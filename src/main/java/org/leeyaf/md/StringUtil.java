package org.leeyaf.md;

class StringUtil {
	static boolean isEmoji(char c){
		return c==43||c==45||c>47&&c<58||c>96&&c<123;
	}
	
	static boolean isNumber(char c){
		return c>15&&c<26;
	}
	
	static String unescapeHtml(String s){
		char[] cs=s.toCharArray();
		StringBuilder sb=new StringBuilder();
		for (int i = 0; i < cs.length; i++) {
			if(cs[i]=='<'){
				sb.append("&lt;");
			}else if(cs[i]=='>'){
				sb.append("&gt;");
			}else if(cs[i]=='&'){
				sb.append("&amp;");
			}else if(cs[i]=='\"'){
				sb.append("&quot;");
			}else{
				sb.append(cs[i]);
			}
		}
		return sb.toString();
	}
}
