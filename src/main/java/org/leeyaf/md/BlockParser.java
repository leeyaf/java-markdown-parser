package org.leeyaf.md;

import java.util.ArrayList;
import java.util.List;

import org.leeyaf.md.Block.BLOCK_TYPE;

class BlockParser {
	private final char WRAP='\n';
	private LineParser lineParser=new LineParser();
	
	String parser(List<String> lines){
		List<Block> blocks=buildBlock(lines);
		StringBuilder sb=new StringBuilder();
		processSub(null, blocks, sb);
		return sb.toString();
	}
	
	private List<Block> buildBlock(List<String> lines){
		List<Block> blocks=new ArrayList<>();
		boolean codeLock=false;
		boolean lastLineIsEmpty=false;
		StringBuilder sb=new StringBuilder();
		int lastTabCount=0;
		for (int i = 0; i < lines.size(); i++) {
			sb.setLength(0);
			String line=lines.get(i);
			String trimedLine=line.trim();
			if(line.length()<1||trimedLine.length()<1){
				lastLineIsEmpty=true;
				continue;
			}
			BLOCK_TYPE blockType=BLOCK_TYPE.P;
			Block lastBlock=blocks.size()>0?blocks.get(blocks.size()-1):null;
			int lineLength=line.length();
			try {
				if(lineLength>2&&line.charAt(0)=='#'&&line.charAt(1)==' '){
					sb.append("<h1>");
					lineParser.parse(line.substring(2), sb);
					sb.append("</h1>");
					blocks.add(new Block(sb.toString(), BLOCK_TYPE.H1));
				}else if(lineLength>3&&line.charAt(0)=='#'&&line.charAt(1)=='#'&&line.charAt(2)==' '){
					sb.append("<h2>");
					lineParser.parse(line.substring(3), sb);
					sb.append("</h2>");
					blocks.add(new Block(sb.toString(), BLOCK_TYPE.H2));
				}else if(lineLength>4&&line.charAt(0)=='#'&&line.charAt(1)=='#'&&line.charAt(2)=='#'&&line.charAt(3)==' '){
					sb.append("<h3>");
					lineParser.parse(line.substring(4), sb);
					sb.append("</h3>");
					blocks.add(new Block(sb.toString(), BLOCK_TYPE.H3));
				}else if(lineLength>5&&line.charAt(0)=='#'&&line.charAt(1)=='#'&&line.charAt(2)=='#'&&line.charAt(3)=='#'&&line.charAt(4)==' '){
					sb.append("<h4>");
					lineParser.parse(line.substring(5), sb);
					sb.append("</h4>");
					blocks.add(new Block(sb.toString(), BLOCK_TYPE.H4));
				}else if(lineLength>6&&line.charAt(0)=='#'&&line.charAt(1)=='#'&&line.charAt(2)=='#'&&line.charAt(3)=='#'&&line.charAt(4)=='#'&&line.charAt(5)==' '){
					sb.append("<h5>");
					lineParser.parse(line.substring(6), sb);
					sb.append("</h5>");
					blocks.add(new Block(sb.toString(), BLOCK_TYPE.H5));
				}else if(lineLength>7&&line.charAt(0)=='#'&&line.charAt(1)=='#'&&line.charAt(2)=='#'&&line.charAt(3)=='#'&&line.charAt(4)=='#'&&line.charAt(5)=='#'&&line.charAt(6)==' '){
					sb.append("<h6>");
					lineParser.parse(line.substring(7), sb);
					sb.append("</h6>");
					blocks.add(new Block(sb.toString(), BLOCK_TYPE.H6));
				}else if(lineLength>2
						&&((trimedLine.charAt(0)=='*'&&trimedLine.charAt(1)==' ')
								||(trimedLine.charAt(0)=='-'&&trimedLine.charAt(1)==' ')
								||(isNumber(trimedLine.charAt(0))&&trimedLine.charAt(1)=='.'&&trimedLine.charAt(2)==' '))){
					blockType=isNumber(trimedLine.charAt(0))?BLOCK_TYPE.ORDERED_LIST:BLOCK_TYPE.UNORDERED_LIST;
					char[] lineChars=line.toCharArray();
					int spaceCount=0;
					for (char c : lineChars) {
						if(c==' ') spaceCount++;
						else break;
					}
					int tabCount=spaceCount/2;
					
					if(lastBlock!=null&&!lastLineIsEmpty&&lastBlock.getType()==blockType){
						Block rootBlock=lastBlock;
						for (int j = 0; j <= (lastTabCount-tabCount); j++) {
							List<Block> subBlocks=rootBlock.getSubBlock();
							if(subBlocks==null){
								rootBlock.setType(blockType);
								subBlocks=new ArrayList<>();
								rootBlock.setSubBlock(subBlocks);
							}else if(subBlocks.get(subBlocks.size()-1).getTabCount()!=tabCount){
								rootBlock=subBlocks.get(subBlocks.size()-1);
								continue;
							}
							lineParser.parse(trimedLine.substring(2), sb);
							subBlocks.add(new Block(sb.toString(), null,null,tabCount));
						}
					}else{
						Block rootBlock=new Block(null,blockType,new ArrayList<Block>(),-1);
						lineParser.parse(trimedLine.substring(2), sb);
						rootBlock.getSubBlock().add(new Block(sb.toString(), null,null,tabCount));
						blocks.add(rootBlock);
					}
					lastTabCount=tabCount;
				}else if(lineLength>3&&line.charAt(0)=='-'&&line.charAt(1)=='-'&&line.charAt(2)=='-'){
					sb.append("<hr/>");
					blocks.add(new Block(sb.toString(),BLOCK_TYPE.HR));
				}else if(lineLength>0&&line.charAt(0)=='>'){
					blockType=BLOCK_TYPE.BLOCKQUOTES;
					if(lastBlock==null||lastBlock.getType()!=blockType){
						Block rootBlock=new Block(null, BLOCK_TYPE.BLOCKQUOTES, new ArrayList<Block>());
						blocks.add(rootBlock);
						lastBlock=rootBlock;
					}
					lineParser.parse(line.substring(1), sb);
					Block b=new Block(sb.toString(),null);
					lastBlock.getSubBlock().add(b);
				}else if(lineLength>2&&line.charAt(0)=='`'&&line.charAt(1)=='`'&&line.charAt(2)=='`'){
					blockType=BLOCK_TYPE.CODE;
					if(!codeLock){
						codeLock=true;
						blocks.add(new Block(null,blockType,new ArrayList<Block>()));
					}else{
						codeLock=false;
					}
				}else if(lineLength>6&&line.charAt(0)=='-'&&line.charAt(1)==' '&&line.charAt(2)=='['&&(line.charAt(3)==' '||line.charAt(3)=='x')&&line.charAt(4)==']'&&line.charAt(5)==' '){
					blockType=BLOCK_TYPE.TASK_LIST;
					char check=line.charAt(3);
					sb.append("<label><input type=\"checkbox\" ");
					if(check=='x') sb.append("checked");
					sb.append(">");
					if(lastBlock.getType()!=blockType){
						Block rootBlock=new Block(null,blockType,new ArrayList<Block>());
						blocks.add(rootBlock);
						lastBlock=rootBlock;
					}
					lineParser.parse(line.substring(5), sb);
					sb.append("</label>");
					lastBlock.getSubBlock().add(new Block(sb.toString(), null));
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
									lineParser.parse(td,sb);
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
								lineParser.parse(th,sb);
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
						lastBlock.getSubBlock().add(new Block(line, null));
					}else{
						sb.append("<p>");
						lineParser.parse(line, sb);
						sb.append("</p>");
						blocks.add(new Block(sb.toString(), BLOCK_TYPE.P));
					}
				}
				lastLineIsEmpty=false;
			} catch (Exception e) {
				System.out.println(line);
				throw e;
			}
		}
		return blocks;
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
	
	private boolean isNumber(char c){
		return c>15&&c<26;
	}
}
