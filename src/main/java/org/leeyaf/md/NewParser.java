package org.leeyaf.md;

import java.util.ArrayList;
import java.util.List;

import org.leeyaf.md.LineBlock.LINE_BLOCK_TYPE;

public class NewParser {
	public static void main(String[] args) {
		try {
			NewParser newParser=new NewParser();
			StringBuilder sb=new StringBuilder();
			newParser.buildLine("*这是斜__体__中间还有**粗体** 还有链接[百度](http://www.baidu.com/) 还有![图片](http://www.baidu.com/)还有`代码` 还有:+1:* 这就是全部了",sb);
			System.out.println(sb.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
//		System.out.println("["+newParser.findUntil("*kjda*dsa*k*ljlk****", LINE_BLOCK_TYPE.BOLD)+"]");
//		System.out.println("["+newParser.findUntil("**dasdhjkwqk*****ehjqwkhjkdhka*s", LINE_BLOCK_TYPE.ITALIC)+"]");
//		System.out.println("["+newParser.findUntil("dsajdkl***--saj`kdljasklj`", LINE_BLOCK_TYPE.INLINE_CODE)+"]");
	}
	
	private void buildLine(String source,StringBuilder sb){
		char[] cs=source.toCharArray();
		for (int i = 0; i < cs.length; i++) {
			char c=cs[i];
			if((c=='*'&&cs[i+1]=='*')||(c=='_'&&cs[i+1]=='_')){
				sb.append("<b>");
				String subSource=findUntil(source.substring(i+2), LINE_BLOCK_TYPE.BOLD);
				buildLine(subSource,sb);
				sb.append("</b>");
				i+=subSource.length()+3;
			}else if(c=='*'||c=='_'){
				sb.append("<i>");
				String subSource=findUntil(source.substring(i+1), LINE_BLOCK_TYPE.ITALIC);
				buildLine(subSource,sb);
				sb.append("</i>");
				i+=subSource.length()+1;
			}else if(c=='`'){
				sb.append("<code>");
				String subSource=findUntil(source.substring(i+1), LINE_BLOCK_TYPE.INLINE_CODE);
				buildLine(subSource,sb);
				sb.append("</code>");
				i+=subSource.length()+1;
			}
			else if(c=='!'){
				String temp=source.substring(i);
				temp=temp.substring(0, temp.indexOf(")")+1);
				String alt=temp.substring(2,temp.indexOf("]"));
				String src=temp.substring(temp.indexOf("(")+1,temp.length()-1);
				sb.append("<img src=\"").append(src).append("\" alt=\"").append(alt).append("\"/>");
				i+=temp.length()-1;
			}else if(c=='['){
				String temp=source.substring(i);
				temp=temp.substring(0, temp.indexOf(")")+1);
				String title=temp.substring(1,temp.indexOf("]"));
				String href=temp.substring(temp.indexOf("(")+1,temp.length()-1);
				sb.append("<a href=\"").append(href).append("\">").append(title).append("</a>");
				i+=temp.length()-1;
			}else if(c==':'){
				String temp=source.substring(i+1);
				int ep=0;
				if((ep=temp.indexOf(":"))>-1){
					temp=temp.substring(0, ep);
					sb.append("<em>").append(temp).append("</em>");
					i+=temp.length()+1;
				}else{
					sb.append(c);
				}
			}
			else{
				sb.append(c);
			}
		}
	}
	
	private List<LineBlock> buildLine(String source){
		List<LineBlock> blocks=new ArrayList<>();
		char[] cs=source.toCharArray();
		for (int i = 0; i+1 < cs.length; i++) {
			char c=cs[i];
			if(c=='*'&&cs[i+1]=='*'){
				String subSource=findUntil(source.substring(i+2), LINE_BLOCK_TYPE.BOLD);
				List<LineBlock> subBlocks=buildLine(subSource);
				if(subBlocks!=null){
					blocks.add(new LineBlock(LINE_BLOCK_TYPE.BOLD, null,subBlocks));
				}else{
					blocks.add(new LineBlock(LINE_BLOCK_TYPE.BOLD, subSource));
				}
				i+=subSource.length()+3;
			}else if(c=='*'){
				String subSource=findUntil(source.substring(i+1), LINE_BLOCK_TYPE.ITALIC);
				List<LineBlock> subBlocks=buildLine(subSource);
				if(subBlocks!=null){
					blocks.add(new LineBlock(LINE_BLOCK_TYPE.ITALIC, null,subBlocks));
				}else{
					blocks.add(new LineBlock(LINE_BLOCK_TYPE.ITALIC, subSource));
				}
				i+=subSource.length()+1;
			}else if(c=='`'){
				String subSource=findUntil(source.substring(i+1), LINE_BLOCK_TYPE.INLINE_CODE);
				List<LineBlock> subBlocks=buildLine(subSource);
				if(subBlocks!=null){
					blocks.add(new LineBlock(LINE_BLOCK_TYPE.INLINE_CODE, null,subBlocks));
				}else{
					blocks.add(new LineBlock(LINE_BLOCK_TYPE.INLINE_CODE, subSource));
				}
				i+=subSource.length()+1;
			}
			else if(c=='!'){
				String temp=source.substring(i);
				temp=temp.substring(0, temp.indexOf(")")+1);
				blocks.add(new LineBlock(LINE_BLOCK_TYPE.IMAGE,temp));
				i+=temp.length()-1;
			}else if(c=='['){
				String temp=source.substring(i);
				temp=temp.substring(0, temp.indexOf(")")+1);
				blocks.add(new LineBlock(LINE_BLOCK_TYPE.LINK,temp));
				i+=temp.length()-1;
			}else if(c==':'){
				String temp=source.substring(i+1);
				int ep=0;
				if((ep=temp.indexOf(":"))>-1){
					temp=temp.substring(0, ep);
					blocks.add(new LineBlock(LINE_BLOCK_TYPE.EMOJI,temp));
					i+=temp.length()+1;
				}else{
					if(blocks.size()>0&&blocks.get(blocks.size()-1).getType()==LINE_BLOCK_TYPE.NORAML){
						LineBlock block=blocks.get(blocks.size()-1);
						block.setData(block.getData()+c);
					}else{
						blocks.add(new LineBlock(LINE_BLOCK_TYPE.NORAML,""+c));
					}
				}
			}
			else{
				if(blocks.size()>0&&blocks.get(blocks.size()-1).getType()==LINE_BLOCK_TYPE.NORAML){
					LineBlock block=blocks.get(blocks.size()-1);
					block.setData(block.getData()+c);
				}else{
					blocks.add(new LineBlock(LINE_BLOCK_TYPE.NORAML,""+c));
				}
			}
		}
		if(blocks.size()>0&&blocks.size()<2&&blocks.get(0).getType()==LINE_BLOCK_TYPE.NORAML) return null;
		else return blocks;
	}
	
	private String findUntil(String source,LINE_BLOCK_TYPE type){
		StringBuilder sb=new StringBuilder();
		char[] cs=source.toCharArray();
		for (int i = 0; i < cs.length; i++) {
			if(type==LINE_BLOCK_TYPE.BOLD){
				if(i==0){
					if(cs[i]=='*'&&cs[i+1]=='*') break;
					else sb.append(cs[i]);
				}else if(i+1==cs.length){
					sb.append(cs[i]);
				}else{
					if(cs[i]=='*'&&cs[i+1]=='*') break;
					else sb.append(cs[i]);
				}
			}else if(type==LINE_BLOCK_TYPE.ITALIC){
				if(i==0){
					if(cs[i]=='*'&&cs[i+1]!='*') break;
					else sb.append(cs[i]);
				}else if(i+1==cs.length){
					if(cs[i-1]!='*'&&cs[i]=='*') break;
					else sb.append(cs[i]);
				}else{
					if(cs[i-1]!='*'&&cs[i]=='*'&&cs[i+1]!='*') break;
					else sb.append(cs[i]);
				}
			}else if(type==LINE_BLOCK_TYPE.INLINE_CODE){
				if(cs[i]=='`') break;
				else sb.append(cs[i]);
			}else{
				sb.append(cs[i]);
			}
		}
		return sb.toString();
	}
}
