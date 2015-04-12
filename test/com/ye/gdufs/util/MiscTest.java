package com.ye.gdufs.util;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;

import org.junit.Test;

public class MiscTest {

	@Test
	public void testDumpObject() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetDumpObject() {
		fail("Not yet implemented");
	}

	@Test
	public void testWriteObject() {
		
		File file = new File("G:/testW.txt");
		try {
			Misc.writeObject(file , "11111askdfhlsdhfl");
			Misc.writeObject(file , "22222askdfhlsdhfl");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testReadObject() {
		File file = new File("G:/testW.txt");
		try {
			System.out.println(Misc.readObject(file));
			System.out.println(Misc.readObject(file));
		} catch (ClassNotFoundException | IOException e) {
			e.printStackTrace();
		}
		
	}

	@Test
	public void testGetContentMd5() {
		fail("Not yet implemented");
	}

	@Test
	public void testAnalyzeSentence() {
		fail("Not yet implemented");
	}

}
