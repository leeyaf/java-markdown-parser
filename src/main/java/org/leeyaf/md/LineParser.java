package org.leeyaf.md;

import java.util.ArrayList;
import java.util.List;

import org.leeyaf.md.LineBlock.LINE_BLOCK_TYPE;

class LineParser {
	private int i;
	
	public void parse(String source,StringBuilder sb){
		i=0;
		List<LineBlock> blocks=parse(source, LINE_BLOCK_TYPE.NORAML);
		process(blocks, sb);
	}
	
	private void process(List<LineBlock> blocks,StringBuilder sb){
		for (LineBlock block : blocks) {
			if(block.getType()!=null){
				if(block.getType()==LINE_BLOCK_TYPE.ITALIC){
					sb.append("<i>");
					process(block.getSubBlock(), sb);
					sb.append("</i>");
					if(block.getSource()!=null) sb.append(block.getSource());
				}else if(block.getType()==LINE_BLOCK_TYPE.BOLD){
					sb.append("<b>");
					process(block.getSubBlock(), sb);
					sb.append("</b>");
					if(block.getSource()!=null) sb.append(block.getSource());
				}else if(block.getType()==LINE_BLOCK_TYPE.INLINE_CODE){
					sb.append("<code>");
					process(block.getSubBlock(), sb);
					sb.append("</code>");
					if(block.getSource()!=null) sb.append(block.getSource());
				}else if(block.getType()==LINE_BLOCK_TYPE.LINK){
					String source=block.getSource();
					if(source!=null&&source.length()>1) source=source.substring(1, source.length()-1);
					sb.append("<a href=\"").append(source).append("\">");
					process(block.getSubBlock(), sb);
					sb.append("</a>");
				}
			}else{
				sb.append(block.getSource());
			}
		}
	}
	
	List<LineBlock> parse(String source,LINE_BLOCK_TYPE until){
		List<LineBlock> blocks=new ArrayList<>();
		char[] cs=source.toCharArray();
		StringBuilder sb=new StringBuilder();
		LineBlock lastBlock=null;
		for (; i < cs.length; i++) {
			sb.setLength(0);
			char c=cs[i];
			if(i+1<cs.length&&((c=='*'&&cs[i+1]=='*')||(c=='_'&&cs[i+1]=='_'))){
				if(until==LINE_BLOCK_TYPE.BOLD) break;
				LineBlock block=new LineBlock(null,LINE_BLOCK_TYPE.BOLD,null);
				i+=2;
				List<LineBlock> subBlock=parse(source, LINE_BLOCK_TYPE.BOLD);
				block.setSubBlock(subBlock);
				blocks.add(block);
				lastBlock=block;
			}else if(c=='*'||c=='_'){
				if(until==LINE_BLOCK_TYPE.ITALIC) break;
				LineBlock block=new LineBlock(null,LINE_BLOCK_TYPE.ITALIC,null);
				i++;
				List<LineBlock> subBlock=parse(source, LINE_BLOCK_TYPE.ITALIC);
				block.setSubBlock(subBlock);
				blocks.add(block);
				lastBlock=block;
			}else if(c=='`'){
				if(until==LINE_BLOCK_TYPE.INLINE_CODE) break;
				LineBlock block=new LineBlock(null,LINE_BLOCK_TYPE.INLINE_CODE,null);
				i++;
				List<LineBlock> subBlock=parse(source, LINE_BLOCK_TYPE.INLINE_CODE);
				block.setSubBlock(subBlock);
				blocks.add(block);
				lastBlock=block;
			}else if(c=='['&&source.substring(i).indexOf("](")>-1){
				LineBlock block=new LineBlock(null,LINE_BLOCK_TYPE.LINK,null);
				i++;
				List<LineBlock> subBlock=parse(source, LINE_BLOCK_TYPE.LINK);
				block.setSubBlock(subBlock);
				blocks.add(block);
				lastBlock=block;
			}else if(c==']'){
				if(until==LINE_BLOCK_TYPE.LINK) break;
				else sb.append(c);
			}else if(c=='!'&&cs[i+1]=='['&&source.substring(i).indexOf("](")>-1){
				String temp=source.substring(i);
				temp=temp.substring(0, temp.indexOf(")")+1);
				String alt=temp.substring(2,temp.indexOf("]"));
				String src=temp.substring(temp.indexOf("(")+1,temp.length()-1);
				sb.append("<img src=\"").append(src).append("\" alt=\"").append(alt).append("\"/>");
				LineBlock block=new LineBlock(sb.toString(), null, null);
				blocks.add(block);
				i+=temp.length()-1;
				lastBlock=block;
			}else if((i+1)<cs.length&&c==':'&&isEmoji(cs[i+1])){
				sb.append("<em>");
				for (; i < cs.length; i++) {
					if(isEmoji(cs[i])){
						sb.append(cs[i]);
					}else break;
				}
				sb.append("</em>");
				LineBlock block=new LineBlock(sb.toString(), null, null);
				blocks.add(block);
				i+=2;
				lastBlock=block;
			}else{
				if(lastBlock==null){
					lastBlock=new LineBlock(null, null, null);
					blocks.add(lastBlock);
				}
				if(lastBlock.getSource()==null){
					lastBlock.setSource(""+c);
				}else{
					lastBlock.setSource(lastBlock.getSource()+c);
				}
			}
		}
		return blocks;
	}
	
	private boolean isEmoji(char c){
		return c==45||c>47&&c<58||c>96&&c<123;
	}
}
