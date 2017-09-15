package org.leeyaf.md;

import java.io.File;

public class Test {
	public static void main(String[] args) {
		try {
			NewParser parser=new NewParser(new File("C:/Users/it/Desktop/md.md"));
			System.out.println(parser.process());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
