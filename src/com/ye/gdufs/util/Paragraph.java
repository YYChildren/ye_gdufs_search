/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ye.gdufs.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Entities;
import org.jsoup.safety.Whitelist;
import org.jsoup.select.Elements;

/**
 *
 * @author Administrator
 */
public class Paragraph {

	public static final HashSet<String> htmlTagDrop = new HashSet<String>();
	public static final HashSet<String> htmlTagExtract = new HashSet<String>();
	public static final HashSet<String> htmlTagRecursion = new HashSet<String>();

	static {
		String[] drop = {// 剔除的标签
		"noscript", "script", "link", "style", "meta", "base", "input", "br",
				"hr", "textarea", "fieldset", "legend", "select", "optgroup",
				"option", "button", "isindex", "form", "applet", "object",
				"embed", "param", "video" };

		String[] extractStr = {// 提取
		"h1", "h2", "h3", "h4", "h5", "h6", "p", "title", "a",// "pre",
				"tr", "td", "dt", "dd", "li" };
		String[] recursion = {// 递归搜索，如果子节点包含recursion，则继续深入搜索，否则提取该文本
		"body", "div", "table", "frameset", "frame", "iframe", "ul", "ol", "dl" };
		htmlTagDrop.addAll(Arrays.asList(drop));
		htmlTagExtract.addAll(Arrays.asList(extractStr));// 提取这些标签
		htmlTagRecursion.addAll(Arrays.asList(recursion));
	}
	static {
		Entities.EscapeMode.base.getMap().clear();
	}
	
	public static List<String> extractSentence(Document doc) {
		String unsafe = doc.toString();
		String safe = Jsoup.clean(unsafe, Whitelist.basic());
		return seg(Jsoup.parse(safe));
	}
	
	public static List<String> extractSentence(String content) {
		String unsafe = content;
		String safe = Jsoup.clean(unsafe, Whitelist.basic());
		return seg(Jsoup.parse(safe));
	}

	private static List<String> seg(Document doc) {
		List<String> bodyParaStr = new ArrayList<String>();;
		Stack<Element> elementStack = new Stack<Element>();
		Element body = doc.body();
		body.data();
		elementStack.push(body);
		Element contentTag;
		while (!elementStack.isEmpty()) {// 深度优先搜索
			if ((contentTag = elementStack.pop()) == null) {
				continue;
			}
			boolean isTagRecursion = false;
			String tagName = contentTag.tagName().toLowerCase();
			boolean containDrop = false;
			StringBuilder txtSb = new StringBuilder("");
			if (Paragraph.htmlTagRecursion.contains(tagName)) {// 如果是递归标签
				Elements childrenTag = contentTag.children();
				Iterator<Element> it = childrenTag.iterator();
				while (it.hasNext()) {
					Element child = it.next();
					String childName = child.tagName();
					if (Paragraph.htmlTagRecursion
							.contains(childName)
							|| Paragraph.htmlTagExtract
									.contains(childName)) {
						// elementStack.addAll(childrenTag);
						int size = childrenTag.size();
						for (int i = size - 1; i >= 0; i--) {
							elementStack.push(childrenTag.get(i));
						}
						isTagRecursion = true;
						break;
					} else if (Paragraph.htmlTagDrop
							.contains(childName)) {
						containDrop = true;
						// it.remove();
					} else {
						txtSb.append(child.text());
					}
				}
			} else if (Paragraph.htmlTagDrop.contains(tagName)
					|| !Paragraph.htmlTagExtract.contains(tagName)) {// 如果是drop标签 或者  不是extract标签
				continue;
			}
			if (isTagRecursion) {
				continue;
			}
			String txt = "";
			if (containDrop) {// div里有要drop
				txt = txtSb.toString();
			} else {
				txt = contentTag.text().trim();// 做了修改 最后ContentTag是纯
												// div..或者 p，h1等
			}
			if ("".equals(txt)) {
				continue;
			}
			bodyParaStr.add(txt);
		}
		return bodyParaStr;
	}
}