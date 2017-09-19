package org.leeyaf.md;

import java.util.ArrayList;
import java.util.List;

import org.leeyaf.md.LineBlock.LINE_BLOCK_TYPE;

public class LineParser {
	private int i;
	private String source;
	
	public void parse(String source,StringBuilder sb){
		this.i=0;
		this.source=source;
		List<LineBlock> blocks=parse(null);
		process(blocks, sb);
	}
	
	private void process(List<LineBlock> blocks,StringBuilder sb){
		if(blocks==null) return;
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
				}else if(block.getType()==LINE_BLOCK_TYPE.LINK){
					String source=block.getSource();
					if(source!=null) sb.append("<a href=\"").append(source).append("\">");
					process(block.getSubBlock(), sb);
					sb.append("</a>");
				}
			}else{
				if(block.getSource()!=null) sb.append(block.getSource());
			}
		}
	}
	
	List<LineBlock> parse(LINE_BLOCK_TYPE until){
		List<LineBlock> blocks=new ArrayList<>();
		char[] cs=source.toCharArray();
		StringBuilder sb=new StringBuilder();
		LineBlock lastBlock=null;
		for (; i < cs.length; i++) {
			sb.setLength(0);
			char c=cs[i];
			LINE_BLOCK_TYPE type=null;
			if(i+1<cs.length&&((c=='*'&&cs[i+1]=='*')||(c=='_'&&cs[i+1]=='_'))){
				type=LINE_BLOCK_TYPE.BOLD;
				i++;
				if(until==type) break;
				i++;
				LineBlock block=new LineBlock(type);
				block.setSubBlock(parse(type));
				blocks.add(block);
				lastBlock=null;
			}else if(c=='*'||c=='_'){
				type=LINE_BLOCK_TYPE.ITALIC;
				if(until==type) break;
				i++;
				LineBlock block=new LineBlock(type);
				block.setSubBlock(parse(type));
				blocks.add(block);
				lastBlock=null;
			}else if(c=='['&&source.substring(i).indexOf("](")>-1){
				type=LINE_BLOCK_TYPE.LINK;
				LineBlock block=new LineBlock(type);
				i++;
				block.setSubBlock(parse(type));
				blocks.add(block);
				if(source.length()>i+2){
					String temp=source.substring(i+2);
					temp=temp.substring(0,temp.indexOf(")"));
					block.setSource(temp);
					i+=temp.length()+2;
					lastBlock=new LineBlock("", null);
					blocks.add(lastBlock);
				}else{
					lastBlock=new LineBlock(""+c, null);
					blocks.add(lastBlock);
				}
			}else if(c==']'){
				if(until==LINE_BLOCK_TYPE.LINK) break;
				else {
					if(lastBlock==null){
						lastBlock=new LineBlock("", null);
						blocks.add(lastBlock);
					}
					lastBlock.setSource(lastBlock.getSource()+c);
				}
			}else if(c=='`'){
				i++;
				for (; i < cs.length; i++) {
					if(cs[i]!='`') sb.append(cs[i]);
					else break;
				}
				String code=StringUtil.unescapeHtml(sb.toString());
				sb.setLength(0);
				sb.append("<code>");
				sb.append(code);
				sb.append("</code>");
				LineBlock block=new LineBlock(sb.toString(),null);
				blocks.add(block);
				lastBlock=null;
			}else if(c=='!'&&cs[i+1]=='['&&source.substring(i).indexOf("](")>-1){
				String temp=source.substring(i);
				temp=temp.substring(0, temp.indexOf(")")+1);
				String alt=temp.substring(2,temp.indexOf("]"));
				String src=temp.substring(temp.indexOf("(")+1,temp.length()-1);
				sb.append("<img src=\"").append(src).append("\" alt=\"").append(alt).append("\"/>");
				blocks.add(new LineBlock(sb.toString(), null));
				i+=temp.length()-1;
				lastBlock=null;
			}else if((i+1)<cs.length&&c==':'&&StringUtil.isEmoji(cs[i+1])){
				sb.append("<em>");
				i++;
				for (; i < cs.length; i++) {
					if(StringUtil.isEmoji(cs[i])) sb.append(cs[i]);
					else break;
				}
				sb.append("</em>");
				blocks.add(new LineBlock(sb.toString(), null));
				lastBlock=null;
			}else{
				if(lastBlock==null){
					lastBlock=new LineBlock("", null);
					blocks.add(lastBlock);
				}
				lastBlock.setSource(lastBlock.getSource()+c);
			}
		}
		return blocks;
	}
}
