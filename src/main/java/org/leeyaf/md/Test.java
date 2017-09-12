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
				if(line.length()<1){
					lastLineIsEmpty=true;
					continue;
				}
				int lineLength=line.length();
				Block lastBlock=blocks.size()>0?blocks.get(blocks.size()-1):null;
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
				}else if(line.indexOf("* ")>-1||line.indexOf(". ")>-1){
					if(lastBlock!=null&&lastBlock.getType()==BLOCK_TYPE.UNORDERED_LIST&&!lastLineIsEmpty){
						lastBlock.getSourceLines().add(line);
					}else{
						Block bigBlock=new Block();
						bigBlock.setType(BLOCK_TYPE.UNORDERED_LIST);
						List<String> blockLines=new ArrayList<>();
						blockLines.add(line);
						bigBlock.setSourceLines(blockLines);
						blocks.add(bigBlock);
					}
				}else if(line.charAt(0)=='-'&&line.charAt(1)=='-'&&line.charAt(2)=='-'){
					Block bigBlock=new Block();
					bigBlock.setType(BLOCK_TYPE.HR);
					List<String> blockLines=new ArrayList<>();
					blockLines.add(line);
					bigBlock.setSourceLines(blockLines);
					blocks.add(bigBlock);
				}else if(line.charAt(0)=='>'&&line.charAt(1)==' '){
					if(lastBlock!=null&&lastBlock.getType()==BLOCK_TYPE.BLOCKQUOTES){
						lastBlock.getSourceLines().add(line);
					}else{
						Block bigBlock=new Block();
						bigBlock.setType(BLOCK_TYPE.BLOCKQUOTES);
						List<String> blockLines=new ArrayList<>();
						blockLines.add(line);
						bigBlock.setSourceLines(blockLines);
						blocks.add(bigBlock);
					}
				}else if(lineLength>2&&line.charAt(0)=='`'&&line.charAt(1)=='`'&&line.charAt(2)=='`'){
					if(!codeLock){
						codeLock=true;
						Block bigBlock=new Block();
						bigBlock.setType(BLOCK_TYPE.CODE);
						List<String> blockLines=new ArrayList<>();
						blockLines.add(line);
						bigBlock.setSourceLines(blockLines);
						blocks.add(bigBlock);
					}else{
						codeLock=false;
						lastBlock.getSourceLines().add(line);
					}
				}else if(lineLength>5&&line.charAt(0)=='-'&&line.charAt(1)==' '&&line.charAt(2)=='['&&(line.charAt(3)==' '||line.charAt(3)=='x')&&line.charAt(4)==']'&&line.charAt(5)==' '){
					if(lastBlock.getType()==BLOCK_TYPE.TASK_LIST){
						lastBlock.getSourceLines().add(line);
					}else{
						Block bigBlock=new Block();
						bigBlock.setType(BLOCK_TYPE.TASK_LIST);
						List<String> blockLines=new ArrayList<>();
						blockLines.add(line);
						bigBlock.setSourceLines(blockLines);
						blocks.add(bigBlock);
					}
				}else if(line.indexOf("|")>-1){
					if(lastBlock.getType()==BLOCK_TYPE.TABLES){
						lastBlock.getSourceLines().add(line);
					}else{
						Block bigBlock=new Block();
						bigBlock.setType(BLOCK_TYPE.TABLES);
						List<String> blockLines=new ArrayList<>();
						blockLines.add(line);
						bigBlock.setSourceLines(blockLines);
						blocks.add(bigBlock);
					}
				}else{
					if(codeLock){
						lastBlock.getSourceLines().add(line);
					}else{
						Block bigBlock=new Block();
						List<String> sourceLines=new ArrayList<>();
						bigBlock.setSourceLines(sourceLines);
						sourceLines.add(line);
						bigBlock.setType(BLOCK_TYPE.P);
						blocks.add(bigBlock);
					}
				}
				lastLineIsEmpty=false;
			}
			for (Block bigBlock : blocks) {
				System.out.println("["+bigBlock.getType().name()+"]:");
				for (String line : bigBlock.getSourceLines()) {
					System.out.println(line);
				}
			}
			System.out.println("------------blocks------------");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
