package org.leeyaf.md;

public class LineReader {
	private int pos;
	private char[] data;
	public  LineReader(char[] data){
		pos=0;
		this.data=data;
	}
	
	public char[] readLine(){
		int start=pos;
		for (; pos < data.length; pos++) {
			if('\n'==data[pos]){
				int size=pos-start;
				if(size>0){
					char[] res=new char[size];
					System.arraycopy(data, start, res, 0, size);
					pos++;
					return res;
				}else{
					return new char[0];
				}
			}
		}
		return null;
	}
}
