package org.leeyaf.md;

import java.util.List;

class Block {
	private String source;
	private BLOCK_TYPE type;
	private List<Block> subBlock;
	private Integer tabCount;
	
	Block(){}
	
	Block(String source, BLOCK_TYPE type, List<Block> subBlock) {
		this.source = source;
		this.type = type;
		this.subBlock = subBlock;
	}

	Block(String source, BLOCK_TYPE type) {
		this.source = source;
		this.type = type;
	}

	Block(String source, BLOCK_TYPE type, List<Block> subBlock, Integer tabCount) {
		this.source = source;
		this.type = type;
		this.subBlock = subBlock;
		this.tabCount = tabCount;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public BLOCK_TYPE getType() {
		return type;
	}

	public void setType(BLOCK_TYPE type) {
		this.type = type;
	}

	public List<Block> getSubBlock() {
		return subBlock;
	}

	public void setSubBlock(List<Block> subBlock) {
		this.subBlock = subBlock;
	}

	public Integer getTabCount() {
		return tabCount;
	}

	public void setTabCount(Integer tabCount) {
		this.tabCount = tabCount;
	}

	enum BLOCK_TYPE{
		P,
		H1,
		H2,
		H3,
		H4,
		H5,
		H6,
		HR,
		CODE,
		BLOCKQUOTES,
		UNORDERED_LIST,
		ORDERED_LIST,
		TASK_LIST,
		TABLE,
		TR,
		THEAD,
		TH,
		TBODY,
	}
	
	enum LINE_BLOCK_TYPE{
		ITALIC,
		BOLD,
		IMAGE,
		LINK,
		INLINE_CODE,
		EMOJI,
		NORAML,
	}
}
