package org.leeyaf.md;

public class LineBlock {
	private LINE_BLOCK_TYPE type;
	private String source;
	
	public LINE_BLOCK_TYPE getType() {
		return type;
	}


	public void setType(LINE_BLOCK_TYPE type) {
		this.type = type;
	}


	public String getSource() {
		return source;
	}


	public void setSource(String source) {
		this.source = source;
	}


	enum LINE_BLOCK_TYPE{
		ITALIC,
		BOLD,
		IMAGE,
		LINK,
		INLINE_CODE,
		TH,
		TD;
	}
}
