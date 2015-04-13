package com.ye.gdufs.util;


import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

import org.junit.Test;

public class MPQTest {

	@Test
	public void testDhash() throws NoSuchAlgorithmException,
			UnsupportedEncodingException {
		MPQ mpq = MPQ.getInstance();
		int[] ns = { 1, 10, 100, 1000, 10000, 100000};
		int time1 = 100;
		for (int n : ns) {
			UUID uuid = UUID.randomUUID();
			String str = uuid.toString().replaceAll("-", "");
			int time = time1;
			System.out.println("-------------------------" + n
					+ "--------------------------------");
			StringBuilder sb = new StringBuilder();
			while (n-- > 0) {
				sb.append(str);
			}
			String ll = sb.toString();
			System.out.println(ll.length());
			int ct1 = 0, ct2 = 0;
			long t11 = System.currentTimeMillis();
			while (time-- > 0) {
				mpq.dhash(ll);
			}
			long t12 = System.currentTimeMillis();
			time = time1;
			long t21 = System.currentTimeMillis();
			while (time-- > 0) {
				MsgUtil.msgDigest(ll);
			}
			long t22 = System.currentTimeMillis();
			ct1 = (int) (t12 - t11);
			ct2 = (int) (t22 - t21);
			System.out.println(ct1);
			System.out.println(ct2);
		}
	}

	public static void main(String[] args) throws NoSuchAlgorithmException,
			UnsupportedEncodingException {
		new MPQTest().testDhash();
	}
}
