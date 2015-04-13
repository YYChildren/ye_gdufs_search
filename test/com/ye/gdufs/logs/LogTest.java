package com.ye.gdufs.logs;


import org.junit.Test;

import com.ye.gdufs.log.Logs;

public class LogTest {

	@Test
	public void test() {
		Logs.info_msg("asdfgsxdfhdgfj");
		Logs.error_msg("asdfgsxdfhdgfj");
		Exception e = new Exception("asdfresvtxzfrvxrf");
		Logs.printStackTrace(e);
	}
}
