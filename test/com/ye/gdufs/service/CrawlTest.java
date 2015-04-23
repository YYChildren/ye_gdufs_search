package com.ye.gdufs.service;

import org.junit.Test;

public class CrawlTest {

	@Test
	public void testStart() {
		CrawlDataPro c = CrawlDataPro.getInstance();
		c.start();
	}
//	@Test
//	public void testGetQueue(){
//		CrawlDataPro c = CrawlDataPro.getInstance();
//		System.out.println(c.getUrlsReady());
//	}
	public static void main(String[] args) {
		CrawlTest t = new CrawlTest();
		t.testStart();
	}
}
