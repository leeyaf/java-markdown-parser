package org.leeyaf.md;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Parser {
	private BlockParser blockParser=new BlockParser();
	
	public String parse(File file) throws Exception{
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
			
			return blockParser.parser(lines);
		} catch (Exception e) {
			throw e;
		}
	}
	
	public String parse(String source) throws Exception{
		try {
			String[] ls=source.split("\n");
			List<String> lines=Arrays.asList(ls);
			return blockParser.parser(lines);
		} catch (Exception e) {
			throw e;
		}
	}
}
