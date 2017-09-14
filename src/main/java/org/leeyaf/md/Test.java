package org.leeyaf.md;

import java.util.ArrayList;
import java.util.List;

import org.leeyaf.md.LineBlock.LINE_BLOCK_TYPE;

public class Test {
	public static void main(String[] args) {
		String source="*这是斜体中间还有**粗体** 有链接[百度](http://www.baidu.com/) 有图片![图片](http://www.baidu.com/)有代码`代码` 有emoji:emoji:* 这就是全部了";
		try {
//			List<LineBlock> blocks=process(source);
//			System.out.println(JacksonUtil.obj2json(blocks));	
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
