package org.leeyaf.md.test;

import java.io.File;

import org.leeyaf.md.Parser;

public class Test {
	public static void main(String[] args) {
		try {
			Parser parser=new Parser();
			System.out.println(parser.parse(new File("C:/Users/psylife-it/Desktop/md.md")));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
