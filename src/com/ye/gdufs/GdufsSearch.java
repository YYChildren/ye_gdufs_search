package com.ye.gdufs;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.AbstractMap.SimpleEntry;

public class GdufsSearch {
	private SimpleEntry<String,SimpleEntry<String,String>> urlTitleContent;
	{
		SimpleEntry<String,String> titleContent = new SimpleEntry<>( "外交部部长助理郑泽光作国际形势报告", "本网讯 日前，外交部部长助理郑泽光作客我校广东国际战略研究院，就2015年的国际形势做了专题报告......");
		urlTitleContent = new SimpleEntry<>("http://www.gdufs.edu.cn/info/1106/43754.htm",titleContent);
	}
	private String req;
	public GdufsSearch(){
	}

	public GdufsSearch(String req) {
		this.req = req;
	}
	public void setReq(String req) {
		this.req = req;
	}
	public List<SimpleEntry<String, SimpleEntry<String, String>>> get(){
		//utl 对应 {标题 ,内容} 
		if(req == "郑泽光作国际形势报告"){
			List<SimpleEntry<String,SimpleEntry<String,String>>> urlTitleContents = new ArrayList<>();
			for(int i = 0;i<100;++i){
				urlTitleContents.add(urlTitleContent);
			}
			return urlTitleContents; 
		}
		else{
			return null;
		}
	}
	//下面是用法
	public static void main(String[] args){
		GdufsSearch gs= new GdufsSearch();
		gs.setReq("郑泽光作国际形势报告");
		System.out.println("------------------------------------以下是网页------------------------------------\n");
		Iterator<SimpleEntry<String, SimpleEntry<String, String>>> it= gs.get().iterator();
		while(it.hasNext()){
			SimpleEntry<String, SimpleEntry<String, String>> urlTitleContent = it.next();
			System.out.println( "网址: " + urlTitleContent.getKey() );
			System.out.println("标题：" + urlTitleContent.getValue().getKey());
			System.out.println("部分内容：" + urlTitleContent.getValue().getValue());
			System.out.println();
		}
	}
}
