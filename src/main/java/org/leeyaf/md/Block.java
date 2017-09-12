package org.leeyaf.md;

import java.util.List;

public class Block {
	private List<String> sourceLines;
	private BLOCK_TYPE type;
	private Block subBlock;

	public Block getSubBlock() {
		return subBlock;
	}

	public void setSubBlock(Block subBlock) {
		this.subBlock = subBlock;
	}

	public List<String> getSourceLines() {
		return sourceLines;
	}

	public void setSourceLines(List<String> sourceLines) {
		this.sourceLines = sourceLines;
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
		TABLES,
		H1,
		H2,
		H3,
		H4,
		H5,
		H6,
		P,
		HR;
	}
}
