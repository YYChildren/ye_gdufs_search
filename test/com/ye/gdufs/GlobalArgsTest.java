package com.ye.gdufs;
import org.junit.Test;

import com.ye.gdufs.global.GlobalArgs;

public class GlobalArgsTest {

	@Test
	public void testIsurlfinishupdate(){
		System.out.println(GlobalArgs.isUrlFinishUpdate());
	}
	
	@Test
	public void testGetDoc() {
		System.out.println(GlobalArgs.getDoc());
	}

	@Test
	public void testIsSeedsUpdate() {
		System.out.println(GlobalArgs.isSeedsUpdate());
	}

	@Test
	public void testGetSeeds() {
		System.out.println(GlobalArgs.getSeeds());
	}

	@Test
	public void testIsThreadUpdate() {
		System.out.println(GlobalArgs.isThreadUpdate());
	}

	@Test
	public void testGetThread() {
		System.out.println(GlobalArgs.getCrawlThread());
	}

	@Test
	public void testCwsModelPath() {
		System.out.println(GlobalArgs.getCwsModelPath());
	}

	@Test
	public void testPosModelPath() {
		System.out.println(GlobalArgs.getPosModelPath());
	}

	@Test
	public void testNerModelPath() {
		System.out.println(GlobalArgs.getNerModelPath());
	}
	public static void main(String[] args){
		
//		String a = GlobalArgs.getDoc().body().toString();
//		System.out.println(a);
		
		GlobalArgsTest t = new GlobalArgsTest();
		t.testIsurlfinishupdate();
		t.testIsSeedsUpdate();
		t.testIsThreadUpdate();
		t.testGetSeeds();
		t.testGetThread();
		t.testCwsModelPath();
		t.testNerModelPath();
		t.testPosModelPath();
	}
}
