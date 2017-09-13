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
					Block bigBlock=new Block();
					List<String> sourceLines=new ArrayList<>();
					bigBlock.setSourceLines(sourceLines);
					sourceLines.add(line);
					bigBlock.setType(BLOCK_TYPE.H1);
					blocks.add(bigBlock);
				}else if(line.charAt(0)=='#'&&line.charAt(1)=='#'&&line.charAt(2)==' '){
					Block bigBlock=new Block();
					bigBlock.setType(BLOCK_TYPE.H2);
					List<String> blockLines=new ArrayList<>();
					blockLines.add(line);
					bigBlock.setSourceLines(blockLines);
					blocks.add(bigBlock);
				}else if(line.charAt(0)=='#'&&line.charAt(1)=='#'&&line.charAt(2)=='#'&&line.charAt(3)==' '){
					Block bigBlock=new Block();
					bigBlock.setType(BLOCK_TYPE.H3);
					List<String> blockLines=new ArrayList<>();
					blockLines.add(line);
					bigBlock.setSourceLines(blockLines);
					blocks.add(bigBlock);
				}else if(line.charAt(0)=='#'&&line.charAt(1)=='#'&&line.charAt(2)=='#'&&line.charAt(3)=='#'&&line.charAt(4)==' '){
					Block bigBlock=new Block();
					bigBlock.setType(BLOCK_TYPE.H4);
					List<String> blockLines=new ArrayList<>();
					blockLines.add(line);
					bigBlock.setSourceLines(blockLines);
					blocks.add(bigBlock);
				}else if(line.charAt(0)=='#'&&line.charAt(1)=='#'&&line.charAt(2)=='#'&&line.charAt(3)=='#'&&line.charAt(4)=='#'&&line.charAt(5)==' '){
					Block bigBlock=new Block();
					bigBlock.setType(BLOCK_TYPE.H5);
					List<String> blockLines=new ArrayList<>();
					blockLines.add(line);
					bigBlock.setSourceLines(blockLines);
					blocks.add(bigBlock);
				}else if(line.charAt(0)=='#'&&line.charAt(1)=='#'&&line.charAt(2)=='#'&&line.charAt(3)=='#'&&line.charAt(4)=='#'&&line.charAt(5)=='#'&&line.charAt(6)==' '){
					Block bigBlock=new Block();
					bigBlock.setType(BLOCK_TYPE.H6);
					List<String> blockLines=new ArrayList<>();
					blockLines.add(line);
					bigBlock.setSourceLines(blockLines);
					blocks.add(bigBlock);
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
									List<String> sourceLines=new ArrayList<>();
									sourceLines.add(line);
									Block block=new Block(sourceLines, blockType);
									block.setTabCount(tabCount);
									subBlocks.add(block);
									break;
								}else{
									rootBlock=lastSubBlock;
								}
							}else{
								List<Block> childrenBlocks=new ArrayList<>();
								Block block=new Block();
								block.setTabCount(tabCount);
								block.setType(blockType);
								List<String> sourceLines=new ArrayList<>();
								block.setSourceLines(sourceLines);
								sourceLines.add(line);
								rootBlock.setSubBlock(childrenBlocks);
								childrenBlocks.add(block);
								break;
							}
						}
					}else{
						Block rootBlock=new Block();
						rootBlock.setType(blockType);
						rootBlock.setTabCount(-1);
						
						List<String> sourceLines=new ArrayList<>();
						sourceLines.add(line);
						
						Block subBlock=new Block(sourceLines, blockType);
						subBlock.setTabCount(0);
						
						List<Block> subBlocks=new ArrayList<>();
						subBlocks.add(subBlock);
						
						rootBlock.setSubBlock(subBlocks);
						
						blocks.add(rootBlock);
					}
				}else if(line.charAt(0)=='-'&&line.charAt(1)=='-'&&line.charAt(2)=='-'){
					Block bigBlock=new Block();
					bigBlock.setType(BLOCK_TYPE.HR);
					List<String> blockLines=new ArrayList<>();
					blockLines.add(line);
					bigBlock.setSourceLines(blockLines);
					blocks.add(bigBlock);
				}else if(line.charAt(0)=='>'&&line.charAt(1)==' '){
					blockType=BLOCK_TYPE.BLOCKQUOTES;
					if(lastBlock!=null&&lastBlock.getType()==blockType){
						lastBlock.getSourceLines().add(line);
					}else{
						Block bigBlock=new Block();
						bigBlock.setType(blockType);
						List<String> blockLines=new ArrayList<>();
						blockLines.add(line);
						bigBlock.setSourceLines(blockLines);
						blocks.add(bigBlock);
					}
				}else if(lineLength>2&&line.charAt(0)=='`'&&line.charAt(1)=='`'&&line.charAt(2)=='`'){
					blockType=BLOCK_TYPE.CODE;
					if(!codeLock){
						codeLock=true;
						Block bigBlock=new Block();
						bigBlock.setType(blockType);
						List<String> blockLines=new ArrayList<>();
						blockLines.add(line);
						bigBlock.setSourceLines(blockLines);
						blocks.add(bigBlock);
					}else{
						codeLock=false;
						lastBlock.getSourceLines().add(line);
					}
				}else if(lineLength>5&&line.charAt(0)=='-'&&line.charAt(1)==' '&&line.charAt(2)=='['&&(line.charAt(3)==' '||line.charAt(3)=='x')&&line.charAt(4)==']'&&line.charAt(5)==' '){
					blockType=BLOCK_TYPE.TASK_LIST;
					if(lastBlock.getType()==blockType){
						lastBlock.getSourceLines().add(line);
					}else{
						Block bigBlock=new Block();
						bigBlock.setType(blockType);
						List<String> blockLines=new ArrayList<>();
						blockLines.add(line);
						bigBlock.setSourceLines(blockLines);
						blocks.add(bigBlock);
					}
				}else if(line.indexOf("|")>-1){
					blockType=BLOCK_TYPE.TABLE;
					if(lastBlock.getType()==blockType){
						lastBlock.getSourceLines().add(line);
					}else{
						List<String> blockLines=new ArrayList<>();
						blockLines.add(line);
						Block b=new Block(blockLines, blockType);
						blocks.add(b);
					}
				}else{
					if(codeLock){
						lastBlock.getSourceLines().add(line);
					}else{
						Block bigBlock=new Block();
						List<String> sourceLines=new ArrayList<>();
						bigBlock.setSourceLines(sourceLines);
						sourceLines.add(line);
						bigBlock.setType(blockType);
						blocks.add(bigBlock);
					}
				}
				lastLineIsEmpty=false;
			}
//			for (Block bigBlock : blocks) {
//				System.out.println("["+bigBlock.getType().name()+"]:"+(bigBlock.getTabCount()==null?"":bigBlock.getTabCount()));
//				for (String line : bigBlock.getSourceLines()) {
//					System.out.println(line);
//				}
//				while(bigBlock.getSubBlock()!=null){
//					bigBlock=bigBlock.getSubBlock();
//					System.out.println("["+bigBlock.getType().name()+"]:"+(bigBlock.getTabCount()==null?"":bigBlock.getTabCount()));
//					for (String line : bigBlock.getSourceLines()) {
//						System.out.println(line);
//					}
//				}
//			}
			System.out.println("------------blocks------------");
			System.out.println(JacksonUtil.obj2json(blocks));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
