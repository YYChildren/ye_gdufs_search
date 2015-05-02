package com.ye.gdufs.util;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class SearchExecutor {
	private static SearchExecutor se = SearchExecutorIn.se;
	private static class SearchExecutorIn{
		private static SearchExecutor se = new SearchExecutor(); 
	}
	public static SearchExecutor getInstance(){
		return se;
	}
	private final int THREAD_NUM = 32;
	private ThreadPoolExecutor executor;
	private SearchExecutor(){
		executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(THREAD_NUM);
	}
	public ThreadPoolExecutor getExecutor(){
		return executor;
	}
}
