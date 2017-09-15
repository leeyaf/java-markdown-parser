package org.leeyaf.md;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Parser {
	private BlockParser blockParser=new BlockParser();
	
	public String parse(File file){
		String parsed=null;
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
			
			parsed=blockParser.parser(lines);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return parsed;
	}
	
	public String parse(String source){
		String parsed=null;
		try {
			String[] ls=source.split("\n");
			List<String> lines=Arrays.asList(ls);
			parsed=blockParser.parser(lines);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return parsed;
	}
}
