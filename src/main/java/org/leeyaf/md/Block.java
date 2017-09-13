package org.leeyaf.md;

import java.util.List;

public class Block {
	private List<String> source;
	private BLOCK_TYPE type;
	private List<Block> subBlock;
	private Integer tabCount;
	
	public Block(){}
	
	public Block(List<String> sourceLines,BLOCK_TYPE type){
		this.source=sourceLines;
		this.type=type;
	}
	
	public Integer getTabCount() {
		return tabCount;
	}

	public void setTabCount(Integer tabCount) {
		this.tabCount = tabCount;
	}


	public List<Block> getSubBlock() {
		return subBlock;
	}

	public void setSubBlock(List<Block> subBlock) {
		this.subBlock = subBlock;
	}

	public List<String> getSourceLines() {
		return source;
	}

	public void setSourceLines(List<String> sourceLines) {
		this.source = sourceLines;
	}

	public BLOCK_TYPE getType() {
		return type;
	}

	public void setType(BLOCK_TYPE type) {
		this.type = type;
	}
	
	enum BLOCK_TYPE{
		UNORDERED_LIST,
		ORDERED_LIST,
		BLOCKQUOTES,
		CODE,
		TASK_LIST,
		H1,
		H2,
		H3,
		H4,
		H5,
		H6,
		P,
		HR,
		TABLE,
		THEAD,
		TH,
		TBODY,
		TD;
	}
}
