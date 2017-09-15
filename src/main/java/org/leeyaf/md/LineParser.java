package org.leeyaf.md;

import org.leeyaf.md.Block.LINE_BLOCK_TYPE;

class LineParser {
	void parse(String source,StringBuilder sb){
		char[] cs=source.toCharArray();
		for (int i = 0; i < cs.length; i++) {
			char c=cs[i];
			if(i+1<cs.length&&((c=='*'&&cs[i+1]=='*')||(c=='_'&&cs[i+1]=='_'))){
				sb.append("<b>");
				String subSource=findUntil(source.substring(i+2), LINE_BLOCK_TYPE.BOLD);
				parse(subSource,sb);
				sb.append("</b>");
				i+=subSource.length()+3;
			}else if(c=='*'||c=='_'){
				sb.append("<i>");
				String subSource=findUntil(source.substring(i+1), LINE_BLOCK_TYPE.ITALIC);
				parse(subSource,sb);
				sb.append("</i>");
				i+=subSource.length()+1;
			}else if(c=='`'){
				sb.append("<code>");
				String subSource=findUntil(source.substring(i+1), LINE_BLOCK_TYPE.INLINE_CODE);
				parse(subSource,sb);
				sb.append("</code>");
				i+=subSource.length()+1;
			}
			else if(c=='!'){
				String temp=source.substring(i);
				if(temp.indexOf(")")>-1){
					temp=temp.substring(0, temp.indexOf(")")+1);
					String alt=temp.substring(2,temp.indexOf("]"));
					String src=temp.substring(temp.indexOf("(")+1,temp.length()-1);
					sb.append("<img src=\"").append(src).append("\" alt=\"").append(alt).append("\"/>");
					i+=temp.length()-1;
				}else{
					sb.append(c);
				}
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
	
	private String findUntil(String source,LINE_BLOCK_TYPE type){
		StringBuilder sb=new StringBuilder();
		char[] cs=source.toCharArray();
		for (int i = 0; i < cs.length; i++) {
			if(type==LINE_BLOCK_TYPE.BOLD){
				if(i+1==cs.length){
					sb.append(cs[i]);
				}else{
					if((cs[i]=='*'&&cs[i+1]=='*')||(cs[i]=='_'&&cs[i+1]=='_')) break;
					else sb.append(cs[i]);
				}
			}else if(type==LINE_BLOCK_TYPE.ITALIC){
				if(i==0){
					if((cs[i]=='*'&&cs[i+1]!='*')||(cs[i]=='_'&&cs[i+1]!='_')) break;
					else sb.append(cs[i]);
				}else if(i+1==cs.length){
					if((cs[i-1]!='*'&&cs[i]=='*')||(cs[i-1]!='_'&&cs[i]=='_')) break;
					else sb.append(cs[i]);
				}else{
					if((cs[i-1]!='*'&&cs[i]=='*'&&cs[i+1]!='*')||(cs[i-1]!='_'&&cs[i]=='_'&&cs[i+1]!='_')) break;
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
