package org.leeyaf.md;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.leeyaf.md.Block.BLOCK_TYPE;
import org.leeyaf.md.LineBlock.LINE_BLOCK_TYPE;

public class Parser {
	private List<String> lines;
	private List<Block> blocks;
	
	public static void main(String[] args) {
		try {
			Parser parser=new Parser(new File("C:/Users/psylife-it/Desktop/md.md"));
			System.out.println(parser.process());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public Parser(File file){
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
	
	public Parser(String source){
		try {
			String[] ls=source.split("\n");
			List<String> lines=Arrays.asList(ls);
			this.lines=lines;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public String process() throws Exception{
		buildBlock();
		return JacksonUtil.obj2json(this.blocks);
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
				i+=subSource.length()+2;
			}else if(c=='`'){
				String subSource=findUntil(source.substring(i+1), LINE_BLOCK_TYPE.INLINE_CODE);
				List<LineBlock> subBlocks=buildLine(subSource);
				if(subBlocks!=null){
					blocks.add(new LineBlock(LINE_BLOCK_TYPE.INLINE_CODE, null,subBlocks));
				}else{
					blocks.add(new LineBlock(LINE_BLOCK_TYPE.INLINE_CODE, subSource));
				}
				i+=subSource.length()+2;
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
				i+=temp.length();
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
				if(i+1<cs.length){
					if(cs[i]=='*'&&cs[i+1]=='*') break;
					else sb.append(cs[i]);
				}else{
					sb.append(cs[i]);
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
	
	private void buildBlock(){
		List<Block> blocks=new ArrayList<>();
		boolean codeLock=false;
		boolean lastLineIsEmpty=false;
		for (int i = 0; i < this.lines.size(); i++) {
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
				Block b=new Block(line, BLOCK_TYPE.H1);
				b.setLineBlocks(buildLine(line.substring(2)));
				blocks.add(b);
			}else if(line.charAt(0)=='#'&&line.charAt(1)=='#'&&line.charAt(2)==' '){
				Block b=new Block(line, BLOCK_TYPE.H2);
				b.setLineBlocks(buildLine(line.substring(3)));
				blocks.add(b);
			}else if(line.charAt(0)=='#'&&line.charAt(1)=='#'&&line.charAt(2)=='#'&&line.charAt(3)==' '){
				Block b=new Block(line, BLOCK_TYPE.H3);
				b.setLineBlocks(buildLine(line.substring(4)));
				blocks.add(b);
			}else if(line.charAt(0)=='#'&&line.charAt(1)=='#'&&line.charAt(2)=='#'&&line.charAt(3)=='#'&&line.charAt(4)==' '){
				Block b=new Block(line, BLOCK_TYPE.H4);
				b.setLineBlocks(buildLine(line.substring(5)));
				blocks.add(b);
			}else if(line.charAt(0)=='#'&&line.charAt(1)=='#'&&line.charAt(2)=='#'&&line.charAt(3)=='#'&&line.charAt(4)=='#'&&line.charAt(5)==' '){
				Block b=new Block(line, BLOCK_TYPE.H5);
				b.setLineBlocks(buildLine(line.substring(6)));
				blocks.add(b);
			}else if(line.charAt(0)=='#'&&line.charAt(1)=='#'&&line.charAt(2)=='#'&&line.charAt(3)=='#'&&line.charAt(4)=='#'&&line.charAt(5)=='#'&&line.charAt(6)==' '){
				Block b=new Block(line, BLOCK_TYPE.H6);
				b.setLineBlocks(buildLine(line.substring(7)));
				blocks.add(b);
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
								Block b=new Block(line, null,null,tabCount);
								b.setLineBlocks(buildLine(trimedLine.substring(2)));
								subBlocks.add(b);
								break;
							}else{
								rootBlock=lastSubBlock;
							}
						}else{
							rootBlock.setType(blockType);
							rootBlock.setSubBlock(new ArrayList<Block>());
							Block b=new Block(line,null,null,tabCount);
							b.setLineBlocks(buildLine(trimedLine.substring(2)));
							rootBlock.getSubBlock().add(b);
							break;
						}
					}
				}else{
					Block rootBlock=new Block(null,blockType,new ArrayList<Block>(),-1);
					Block b=new Block(line, null,null,0);
					b.setLineBlocks(buildLine(trimedLine.substring(2)));
					rootBlock.getSubBlock().add(b);
					blocks.add(rootBlock);
				}
			}else if(line.charAt(0)=='-'&&line.charAt(1)=='-'&&line.charAt(2)=='-'){
				blocks.add(new Block(line,BLOCK_TYPE.HR));
			}else if(line.charAt(0)=='>'&&line.charAt(1)==' '){
				blockType=BLOCK_TYPE.BLOCKQUOTES;
				if(lastBlock!=null&&lastBlock.getType()==blockType){
					Block b=new Block(line,null);
					b.setLineBlocks(buildLine(line.substring(2)));
					lastBlock.getSubBlock().add(b);
				}else{
					Block rootBlock=new Block(null, BLOCK_TYPE.BLOCKQUOTES, new ArrayList<Block>());
					Block b=new Block(line,null);
					b.setLineBlocks(buildLine(line.substring(2)));
					rootBlock.getSubBlock().add(b);
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
					Block b=new Block(line, null);
					b.setLineBlocks(buildLine(line.substring(5)));
					lastBlock.getSubBlock().add(b);
				}else{
					Block rootBlock=new Block(null,blockType,new ArrayList<Block>());
					Block b=new Block(line, null);
					b.setLineBlocks(buildLine(line.substring(5)));
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
							if(td.length()>0){
								Block b=new Block(td,BLOCK_TYPE.TD);
								b.setLineBlocks(buildLine(line));
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
						if(th.length()>0){
							Block b=new Block(th,BLOCK_TYPE.TH);
							b.setLineBlocks(buildLine(line));
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
					Block b=new Block(line, BLOCK_TYPE.P);
					b.setLineBlocks(buildLine(line));
					blocks.add(b);
				}
			}
			lastLineIsEmpty=false;
		}
		this.blocks=blocks;
	}
}
