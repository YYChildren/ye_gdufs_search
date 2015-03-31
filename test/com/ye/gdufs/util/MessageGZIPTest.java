package com.ye.gdufs.util;
import java.io.IOException;
public class MessageGZIPTest {
	public static void main(String[] args) throws IOException {
		String s = "abcd";
		byte[] b;
		System.out.println("byte" +(b=MsgUtil.compress(s)));
		System.out.println("Str"+MsgUtil.uncompress(b));
	}
}
