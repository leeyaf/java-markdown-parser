package org.leeyaf.md;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.leeyaf.md.Block.BLOCK_TYPE;
import org.leeyaf.md.LineBlock.LINE_BLOCK_TYPE;

public class NewParser {
	private List<String> lines;
	private List<Block> blocks;
	private final char WRAP='\n';
	
	public NewParser(File file){
		try {
			List<String> lines=new ArrayList<>();
			FileReader fr=new FileReader(file);
			BufferedReader br=new BufferedReader(fr);
			String readed=null;
			while((readed=br.readLine())!=null){
				lines.add(readed);
			}
			br.close();
			fr.close();
			this.lines=lines;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public NewParser(String source){
		try {
			String[] ls=source.split("\n");
			List<String> lines=Arrays.asList(ls);
			this.lines=lines;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public String process(){
		buildBlock();
		StringBuilder sb=new StringBuilder();
		processSub(null, blocks, sb);
		return sb.toString();
	}
	
	private void processSub(BLOCK_TYPE parentBlockType,List<Block> blocks,StringBuilder sb){
		for (Block b : blocks) {
			if(b.getType()!=null){
				if(b.getType()==BLOCK_TYPE.UNORDERED_LIST||b.getType()==BLOCK_TYPE.TASK_LIST){
					if(b.getSource()!=null) sb.append("<li>").append(b.getSource()).append(WRAP);
					sb.append("<ul>").append(WRAP);
					processSub(BLOCK_TYPE.UNORDERED_LIST,b.getSubBlock(), sb);
					sb.append("</ul>").append(WRAP);
					if(b.getSource()!=null) sb.append("</li>");
				}else if(b.getType()==BLOCK_TYPE.ORDERED_LIST){
					if(b.getSource()!=null) sb.append("<li>").append(b.getSource()).append(WRAP);
					sb.append("<ol>").append(WRAP);
					processSub(BLOCK_TYPE.ORDERED_LIST,b.getSubBlock(), sb);
					sb.append("</ol>").append(WRAP);
					if(b.getSource()!=null) sb.append("</li>");
				}else if(b.getType()==BLOCK_TYPE.BLOCKQUOTES){
					sb.append("<blockquote>").append(WRAP);
					processSub(BLOCK_TYPE.BLOCKQUOTES,b.getSubBlock(), sb);
					sb.append("</blockquote>").append(WRAP);
				}else if(b.getType()==BLOCK_TYPE.CODE){
					sb.append("<pre>").append(WRAP);
					processSub(BLOCK_TYPE.CODE,b.getSubBlock(), sb);
					sb.append("</pre>").append(WRAP);
				}else if(b.getType()==BLOCK_TYPE.TABLE){
					sb.append("<table>").append(WRAP);
					processSub(BLOCK_TYPE.TABLE,b.getSubBlock(), sb);
					sb.append("</table>").append(WRAP);
				}else if(b.getType()==BLOCK_TYPE.THEAD){
					sb.append("<thead>").append(WRAP);
					processSub(BLOCK_TYPE.THEAD,b.getSubBlock(), sb);
					sb.append("</thead>").append(WRAP);
				}else if(b.getType()==BLOCK_TYPE.TBODY){
					sb.append("<tbody>").append(WRAP);
					processSub(BLOCK_TYPE.TBODY,b.getSubBlock(), sb);
					sb.append("</tbody>").append(WRAP);
				}else if(b.getType()==BLOCK_TYPE.TR){
					sb.append("<tr>").append(WRAP);
					processSub(parentBlockType,b.getSubBlock(), sb);
					sb.append("</tr>").append(WRAP);
				}else{
					sb.append(b.getSource()).append(WRAP);
				}
			}else{
				if(parentBlockType==BLOCK_TYPE.UNORDERED_LIST||parentBlockType==BLOCK_TYPE.ORDERED_LIST){
					sb.append("<li>").append(b.getSource()).append("</li>").append(WRAP);
				}else if(parentBlockType==BLOCK_TYPE.THEAD){
					sb.append("<th>").append(b.getSource()).append("</th>").append(WRAP);
				}else if(parentBlockType==BLOCK_TYPE.TBODY){
					sb.append("<td>").append(b.getSource()).append("</td>").append(WRAP);
				}else{
					sb.append(b.getSource()).append(WRAP);
				}
			}
		}
	}
	
	private void buildBlock(){
		List<Block> blocks=new ArrayList<>();
		boolean codeLock=false;
		boolean lastLineIsEmpty=false;
		StringBuilder sb=new StringBuilder();
		for (int i = 0; i < this.lines.size(); i++) {
			sb.setLength(0);
			String line=this.lines.get(i);
			String trimedLine=line.trim();
			if(line.length()<1||trimedLine.length()<1){
				lastLineIsEmpty=true;
				continue;
			}
			BLOCK_TYPE blockType=BLOCK_TYPE.P;
			Block lastBlock=blocks.size()>0?blocks.get(blocks.size()-1):null;
			int lineLength=line.length();
			if(line.charAt(0)=='#'&&line.charAt(1)==' '){
				sb.append("<h1>");
				buildLine(line.substring(2), sb);
				sb.append("</h1>");
				blocks.add(new Block(sb.toString(), BLOCK_TYPE.H1));
			}else if(line.charAt(0)=='#'&&line.charAt(1)=='#'&&line.charAt(2)==' '){
				sb.append("<h2>");
				buildLine(line.substring(3), sb);
				sb.append("</h2>");
				blocks.add(new Block(sb.toString(), BLOCK_TYPE.H2));
			}else if(line.charAt(0)=='#'&&line.charAt(1)=='#'&&line.charAt(2)=='#'&&line.charAt(3)==' '){
				sb.append("<h3>");
				buildLine(line.substring(4), sb);
				sb.append("</h3>");
				blocks.add(new Block(line, BLOCK_TYPE.H3));
			}else if(line.charAt(0)=='#'&&line.charAt(1)=='#'&&line.charAt(2)=='#'&&line.charAt(3)=='#'&&line.charAt(4)==' '){
				sb.append("<h4>");
				buildLine(line.substring(5), sb);
				sb.append("</h4>");
				blocks.add(new Block(sb.toString(), BLOCK_TYPE.H4));
			}else if(line.charAt(0)=='#'&&line.charAt(1)=='#'&&line.charAt(2)=='#'&&line.charAt(3)=='#'&&line.charAt(4)=='#'&&line.charAt(5)==' '){
				sb.append("<h5>");
				buildLine(line.substring(6), sb);
				sb.append("</h5>");
				blocks.add(new Block(sb.toString(), BLOCK_TYPE.H5));
			}else if(line.charAt(0)=='#'&&line.charAt(1)=='#'&&line.charAt(2)=='#'&&line.charAt(3)=='#'&&line.charAt(4)=='#'&&line.charAt(5)=='#'&&line.charAt(6)==' '){
				sb.append("<h6>");
				buildLine(line.substring(7), sb);
				sb.append("</h6>");
				blocks.add(new Block(sb.toString(), BLOCK_TYPE.H6));
			}else if((trimedLine.charAt(0)=='*'&&trimedLine.charAt(1)==' ')||(trimedLine.charAt(0)=='1'&&trimedLine.charAt(1)=='.'&&trimedLine.charAt(2)==' ')){
				blockType=trimedLine.charAt(0)=='*'?BLOCK_TYPE.UNORDERED_LIST:BLOCK_TYPE.ORDERED_LIST;
				char[] lineChars=line.toCharArray();
				int spaceCount=0;
				for (char c : lineChars) {
					if(c==' ') spaceCount++;
					else break;
				}
				int tabCount=spaceCount/2;
				
				if(lastBlock!=null&&!lastLineIsEmpty&&lastBlock.getType()==blockType){
					Block rootBlock=lastBlock;
					for (int j = 0; j <= tabCount; j++) {
						List<Block> subBlocks=rootBlock.getSubBlock();
						if(subBlocks!=null){
							Block lastSubBlock=subBlocks.get(subBlocks.size()-1);
							if(lastSubBlock.getTabCount()==tabCount){
								buildLine(trimedLine.substring(2), sb);
								Block b=new Block(sb.toString(), null,null,tabCount);
								subBlocks.add(b);
								break;
							}else{
								rootBlock=lastSubBlock;
							}
						}else{
							rootBlock.setType(blockType);
							rootBlock.setSubBlock(new ArrayList<Block>());
							buildLine(trimedLine.substring(2), sb);
							Block b=new Block(sb.toString(),null,null,tabCount);
							rootBlock.getSubBlock().add(b);
							break;
						}
					}
				}else{
					Block rootBlock=new Block(null,blockType,new ArrayList<Block>(),-1);
					buildLine(trimedLine.substring(2), sb);
					Block b=new Block(sb.toString(), null,null,0);
					rootBlock.getSubBlock().add(b);
					blocks.add(rootBlock);
				}
			}else if(line.charAt(0)=='-'&&line.charAt(1)=='-'&&line.charAt(2)=='-'){
				sb.append("<hr/>");
				blocks.add(new Block(sb.toString(),BLOCK_TYPE.HR));
			}else if(line.charAt(0)=='>'&&line.charAt(1)==' '){
				blockType=BLOCK_TYPE.BLOCKQUOTES;
				if(lastBlock!=null&&lastBlock.getType()==blockType){
					buildLine(line.substring(2), sb);
					Block b=new Block(sb.toString(),null);
					lastBlock.getSubBlock().add(b);
				}else{
					Block rootBlock=new Block(null, BLOCK_TYPE.BLOCKQUOTES, new ArrayList<Block>());
					buildLine(line.substring(2), sb);
					Block b=new Block(sb.toString(),null);
					rootBlock.getSubBlock().add(b);
					blocks.add(rootBlock);
				}
			}else if(lineLength>2&&line.charAt(0)=='`'&&line.charAt(1)=='`'&&line.charAt(2)=='`'){
				blockType=BLOCK_TYPE.CODE;
				if(!codeLock){
					codeLock=true;
					Block rootBlock=new Block(null,blockType,new ArrayList<Block>());
					blocks.add(rootBlock);
				}else{
					codeLock=false;
				}
			}else if(lineLength>5&&line.charAt(0)=='-'&&line.charAt(1)==' '&&line.charAt(2)=='['&&(line.charAt(3)==' '||line.charAt(3)=='x')&&line.charAt(4)==']'&&line.charAt(5)==' '){
				blockType=BLOCK_TYPE.TASK_LIST;
				char check=line.charAt(3);
				sb.append("<label><input type=\"checkbox\" ");
				if(check=='x') sb.append("checked");
				sb.append(">");
				if(lastBlock.getType()==blockType){
					buildLine(line.substring(5), sb);
					sb.append("</label>");
					Block b=new Block(sb.toString(), null);
					lastBlock.getSubBlock().add(b);
				}else{
					Block rootBlock=new Block(null,blockType,new ArrayList<Block>());
					buildLine(line.substring(5), sb);
					sb.append("</label>");
					Block b=new Block(sb.toString(), null);
					rootBlock.getSubBlock().add(b);
					blocks.add(rootBlock);
				}
			}else if(line.indexOf("|")>-1){
				blockType=BLOCK_TYPE.TABLE;
				if(lastBlock.getType()==blockType){
					if(line.indexOf("---")>-1){
						Block tbodyBlock=new Block(null,BLOCK_TYPE.TBODY,new ArrayList<Block>());
						lastBlock.getSubBlock().add(tbodyBlock);
					}else{
						Block rootBlock=lastBlock.getSubBlock().get(lastBlock.getSubBlock().size()-1);
						Block trBlock=new Block(null,BLOCK_TYPE.TR,new ArrayList<Block>());
						
						String[] tds=line.split("\\|");
						for (String td : tds) {
							sb.setLength(0);
							if(td.length()>0){
								buildLine(td,sb);
								Block b=new Block(sb.toString(),null);
								trBlock.getSubBlock().add(b);	
							}
						}
						rootBlock.getSubBlock().add(trBlock);
					}
				}else{
					Block tableBlock=new Block(null, blockType,new ArrayList<Block>());
					Block theadBlock=new Block(null,BLOCK_TYPE.THEAD,new ArrayList<Block>());
					Block trBlock=new Block(null, BLOCK_TYPE.TR,new ArrayList<Block>());
					
					String[] ths=line.split("\\|");
					for (String th : ths) {
						sb.setLength(0);
						if(th.length()>0){
							buildLine(th,sb);
							Block b=new Block(sb.toString(),null);
							trBlock.getSubBlock().add(b);	
						}
					}
					theadBlock.getSubBlock().add(trBlock);
					tableBlock.getSubBlock().add(theadBlock);
					
					blocks.add(tableBlock);
				}
			}else{
				if(codeLock){
					Block subBlock=new Block(line, null);
					lastBlock.getSubBlock().add(subBlock);
				}else{
					sb.append("<p>");
					buildLine(line, sb);
					sb.append("</p>");
					blocks.add(new Block(sb.toString(), BLOCK_TYPE.P));
				}
			}
			lastLineIsEmpty=false;
		}
		this.blocks=blocks;
	}
	
	private void buildLine(String source,StringBuilder sb){
		char[] cs=source.toCharArray();
		for (int i = 0; i < cs.length; i++) {
			char c=cs[i];
			if(i+1<cs.length&&((c=='*'&&cs[i+1]=='*')||(c=='_'&&cs[i+1]=='_'))){
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
