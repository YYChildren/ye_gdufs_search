package com.ye.gdufs.service;


import org.junit.Test;

public class IndexProTest {

	@Test
	public void test() {
		IndexPro.getInstance().start();
	}
	public static void main(String[] args) {
		new IndexProTest().test();
	}
}
