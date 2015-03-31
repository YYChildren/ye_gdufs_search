package com.ye.gdufs.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class MsgUtil {
    public static final String defaultCharset = "utf-8";//"ISO-8859-1"
    public static final String defaultDigest = "md5";//"ISO-8859-1"
    
    public static byte[] compress(String str) throws IOException{
        return compress(str,defaultCharset);
    }
    public static byte[] compress(String str,String charset) throws IOException{
    	byte[] bytes = str.getBytes(charset);
        return bytesCompress(bytes);
    }
    public static byte[] bytesCompress(byte[] bytes) throws IOException{
    	ByteArrayOutputStream out = new ByteArrayOutputStream();
        GZIPOutputStream gzip;
        gzip = new GZIPOutputStream(out);
        gzip.write(bytes);
        gzip.close();
        return out.toByteArray();
        
    }

    public static String uncompress(byte[] bytes) throws IOException {
    	return uncompress(bytes,defaultCharset);
    }
    public static String uncompress(byte[] bytes, String charset) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ByteArrayInputStream in = new ByteArrayInputStream(bytes);
        GZIPInputStream gunzip = new GZIPInputStream(in);
        byte[] buffer = new byte[4096];
        int n;
        while ((n = gunzip.read(buffer)) >= 0) {
            out.write(buffer, 0, n);
        }
        String result = out.toString(charset); 
        gunzip.close();
        out.close();
        return result;
    }
    public static byte[] bytesUncompress(byte[] bytes) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ByteArrayInputStream in = new ByteArrayInputStream(bytes);
        GZIPInputStream gunzip = new GZIPInputStream(in);
        byte[] buffer = new byte[4096];
        int n;
        while ((n = gunzip.read(buffer)) >= 0) {
            out.write(buffer, 0, n);
        }
        byte[] result = out.toByteArray(); 
        gunzip.close();
        out.close();
        return result;
    }
	
	public static String msgDigest(String str) throws NoSuchAlgorithmException, UnsupportedEncodingException{
		return msgDigest(str,defaultDigest);
	}
	public static String msgDigest(String str,String digest) throws NoSuchAlgorithmException, UnsupportedEncodingException{
		return msgDigest(str,digest,defaultCharset);
	}
	public static String msgDigest(String str,String digest,String charset) throws NoSuchAlgorithmException, UnsupportedEncodingException{
        byte[] bytes = str.getBytes(charset);
        return bytesDigest(bytes,digest,charset);
	}
	public static String bytesDigest(byte[] bytes) throws NoSuchAlgorithmException, UnsupportedEncodingException{
		return bytesDigest(bytes,defaultDigest);
	}
	public static String bytesDigest(byte[] bytes,String digest) throws NoSuchAlgorithmException, UnsupportedEncodingException{
		return bytesDigest(bytes,digest,defaultCharset);
	}
	public static String bytesDigest(byte[] bytes,String digest,String charset) throws NoSuchAlgorithmException, UnsupportedEncodingException{
		MessageDigest m = MessageDigest.getInstance(digest);  
        m.update(bytes);  
        byte s[] = m.digest(); 
        return hex(s);          
	}
	public static String hex(byte[] arr) {  
        StringBuffer sb = new StringBuffer();  
        for (int i = 0; i < arr.length; ++i) {  
            sb.append(Integer.toHexString((arr[i] & 0xFF) | 0x100).substring(1,3));  
        }  
        return sb.toString(); 
    }
	
	public static byte[] object2Bytes(Object o) throws IOException{
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ObjectOutputStream oos = new ObjectOutputStream(baos);
		oos.writeObject(o);
		byte[] result = baos.toByteArray();
		oos.close();
		return result;
	}
	public static Object bytes2Object(byte[] b) throws IOException, ClassNotFoundException{
		ByteArrayInputStream bais = new ByteArrayInputStream(b);
		ObjectInputStream ois = new ObjectInputStream(bais);
		Object result = ois.readObject();
		ois.close();
		return result;
	}
	
	public static byte[] compressObject2Bytes(Object o) throws IOException{
		return bytesCompress(object2Bytes(o));
	}
	public static Object uncompressBytes2Object(byte[] b) throws IOException, ClassNotFoundException{
		return bytes2Object(bytesUncompress(b));
	}
	
}