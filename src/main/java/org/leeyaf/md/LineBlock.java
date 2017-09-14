package org.leeyaf.md;

import java.util.List;

public class LineBlock {
	private LINE_BLOCK_TYPE type;
	private String data;
	private List<LineBlock> subBlocks;
	
	public LineBlock() {
	}
	public LineBlock(LINE_BLOCK_TYPE type, String data, List<LineBlock> subBlocks) {
		this.type = type;
		this.data = data;
		this.subBlocks = subBlocks;
	}
	public List<LineBlock> getSubBlocks() {
		return subBlocks;
	}
	public void setSubBlocks(List<LineBlock> subBlocks) {
		this.subBlocks = subBlocks;
	}
	public LineBlock(LINE_BLOCK_TYPE type, String data) {
		this.type = type;
		this.data = data;
	}
	public LINE_BLOCK_TYPE getType() {
		return type;
	}
	public void setType(LINE_BLOCK_TYPE type) {
		this.type = type;
	}
	public String getData() {
		return data;
	}
	public void setData(String data) {
		this.data = data;
	}

	enum LINE_BLOCK_TYPE{
		ITALIC,
		BOLD,
		IMAGE,
		LINK,
		INLINE_CODE,
		EMOJI,
		NORAML;
	}
}
