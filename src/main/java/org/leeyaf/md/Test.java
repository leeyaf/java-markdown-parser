package org.leeyaf.md;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import org.leeyaf.md.Block.BLOCK_TYPE;

public class Test {
	public static void main(String[] args) {
		try {
			FileReader fr=new FileReader("C:/Users/psylife-it/Desktop/md.md");
			BufferedReader br=new BufferedReader(fr);
			String readed=null;
			List<String> lines=new ArrayList<>();
			while(true){
				readed=br.readLine();
				if(readed!=null){
					lines.add(readed);
				}else{
					break;
				}
			}
			System.out.println("------------lines------------");
			for (String line : lines) {
				System.out.println(line);
			}
			System.out.println("------------lines------------");
			
			System.out.println("------------blocks------------");
			List<Block> blocks=new ArrayList<>();
			boolean codeLock=false;
			boolean lastLineIsEmpty=false;
			for (int i = 0; i < lines.size(); i++) {
				String line=lines.get(i);
				String trimedLine=line.trim();
				if(line.length()<1||trimedLine.length()<1){
					lastLineIsEmpty=true;
					continue;
				}
				BLOCK_TYPE blockType=BLOCK_TYPE.P;
				Block lastBlock=blocks.size()>0?blocks.get(blocks.size()-1):null;
				int lineLength=line.length();
				if(line.charAt(0)=='#'&&line.charAt(1)==' '){
					blocks.add(new Block(line, BLOCK_TYPE.H1));
				}else if(line.charAt(0)=='#'&&line.charAt(1)=='#'&&line.charAt(2)==' '){
					blocks.add(new Block(line, BLOCK_TYPE.H2));
				}else if(line.charAt(0)=='#'&&line.charAt(1)=='#'&&line.charAt(2)=='#'&&line.charAt(3)==' '){
					blocks.add(new Block(line, BLOCK_TYPE.H3));
				}else if(line.charAt(0)=='#'&&line.charAt(1)=='#'&&line.charAt(2)=='#'&&line.charAt(3)=='#'&&line.charAt(4)==' '){
					blocks.add(new Block(line, BLOCK_TYPE.H4));
				}else if(line.charAt(0)=='#'&&line.charAt(1)=='#'&&line.charAt(2)=='#'&&line.charAt(3)=='#'&&line.charAt(4)=='#'&&line.charAt(5)==' '){
					blocks.add(new Block(line, BLOCK_TYPE.H5));
				}else if(line.charAt(0)=='#'&&line.charAt(1)=='#'&&line.charAt(2)=='#'&&line.charAt(3)=='#'&&line.charAt(4)=='#'&&line.charAt(5)=='#'&&line.charAt(6)==' '){
					blocks.add(new Block(line, BLOCK_TYPE.H6));
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
									subBlocks.add(new Block(line, null,null,tabCount));
									break;
								}else{
									rootBlock=lastSubBlock;
								}
							}else{
								rootBlock.setType(blockType);
								rootBlock.setSubBlock(new ArrayList<Block>());
								rootBlock.getSubBlock().add(new Block(line,null,null,tabCount));
								break;
							}
						}
					}else{
						Block rootBlock=new Block(null,blockType,new ArrayList<Block>(),-1);
						rootBlock.getSubBlock().add(new Block(line, null,null,0));
						blocks.add(rootBlock);
					}
				}else if(line.charAt(0)=='-'&&line.charAt(1)=='-'&&line.charAt(2)=='-'){
					blocks.add(new Block(line,BLOCK_TYPE.HR));
				}else if(line.charAt(0)=='>'&&line.charAt(1)==' '){
					blockType=BLOCK_TYPE.BLOCKQUOTES;
					if(lastBlock!=null&&lastBlock.getType()==blockType){
						lastBlock.getSubBlock().add(new Block(line,null));
					}else{
						Block rootBlock=new Block(null, BLOCK_TYPE.BLOCKQUOTES, new ArrayList<Block>());
						rootBlock.getSubBlock().add(new Block(line,null));
						blocks.add(rootBlock);
					}
				}else if(lineLength>2&&line.charAt(0)=='`'&&line.charAt(1)=='`'&&line.charAt(2)=='`'){
					blockType=BLOCK_TYPE.CODE;
					if(!codeLock){
						codeLock=true;
						Block rootBlock=new Block(null,blockType,new ArrayList<Block>());
						rootBlock.getSubBlock().add(new Block(line, null));
						blocks.add(rootBlock);
					}else{
						codeLock=false;
						lastBlock.getSubBlock().add(new Block(line, null));
					}
				}else if(lineLength>5&&line.charAt(0)=='-'&&line.charAt(1)==' '&&line.charAt(2)=='['&&(line.charAt(3)==' '||line.charAt(3)=='x')&&line.charAt(4)==']'&&line.charAt(5)==' '){
					blockType=BLOCK_TYPE.TASK_LIST;
					if(lastBlock.getType()==blockType){
						lastBlock.getSubBlock().add(new Block(line, null));
					}else{
						Block rootBlock=new Block(null,blockType,new ArrayList<Block>());
						rootBlock.getSubBlock().add(new Block(line, null));
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
								if(td.length()>0){
									trBlock.getSubBlock().add(new Block(td,BLOCK_TYPE.TD));	
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
							if(th.length()>0){
								trBlock.getSubBlock().add(new Block(th,BLOCK_TYPE.TH));	
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
						blocks.add(new Block(line,BLOCK_TYPE.P));
					}
				}
				lastLineIsEmpty=false;
			}
			System.out.println("------------blocks------------");
			System.out.println(JacksonUtil.obj2json(blocks));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
