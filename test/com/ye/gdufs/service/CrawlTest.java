package com.ye.gdufs.service;

import org.junit.Test;

public class CrawlTest {

	@Test
	public void testStart() {
		CrawlDataPro c = CrawlDataPro.getInstance();
		c.start();
	}
	public static void main(String[] args) {
		CrawlTest t = new CrawlTest();
		t.testStart();
	}
}
