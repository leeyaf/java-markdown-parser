# java-markdown-parser

Markdown文档解析器，使用Java实现。

实现参照[GitHub Markdown](https://guides.github.com/features/mastering-markdown/)。

## 使用

```java
Parser parser=new Parser();
String res=parser.parse(new File("/example.md"));
System.out.println(res);
```

## 语法支持

### 基础

* 标题
* 粗体/斜体
* 无序列表
* 有序列表
* 图片
* 链接
* 引用
* 行内代码

### GitHub风格

* 代码块
* 任务列表
* 表格
* Emoji

## 另外

支持行内语法嵌套

* 标题项
* 粗体/斜体块
* 列表项
* 任务列表项
* 表格项

## 欢迎star/fork
