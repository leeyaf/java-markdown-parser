package org.leeyaf.md;

import java.util.List;

public class LineBlock {
	private String source;
	private LINE_BLOCK_TYPE type;
	private List<LineBlock> subBlock;
	
	public LineBlock(String source, LINE_BLOCK_TYPE type, List<LineBlock> subBlock) {
		this.source = source;
		this.type = type;
		this.subBlock = subBlock;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public LINE_BLOCK_TYPE getType() {
		return type;
	}

	public void setType(LINE_BLOCK_TYPE type) {
		this.type = type;
	}

	public List<LineBlock> getSubBlock() {
		return subBlock;
	}

	public void setSubBlock(List<LineBlock> subBlock) {
		this.subBlock = subBlock;
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
