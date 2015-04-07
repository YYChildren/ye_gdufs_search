package com.ye.gdufs.util;

public class MPQ {
	private final int CRYPTO_LEN = 0x500;
	private final int TYPE = 1;
	private long[] cryptTable; 
	private static class MPQIn{
		private static MPQ mpq = new MPQ();
	}
	private MPQ(){
		cryptTable = new long[CRYPTO_LEN];
		prepare_crypt_table();
	}
	public static MPQ getInstance(){
		return MPQIn.mpq;
	}
	
	private void prepare_crypt_table(){
	    long seed = 0x00100001;
	    int index1 = 0, index2 = 0, i;
	    for( index1 = 0; index1 < 0x100; index1++ ){
	        for( index2 = index1, i = 0; i < 5; i++, index2 += 0x100){
	            long temp1, temp2;
	 
	            seed = (seed * 125 + 3) % 0x2AAAAB;
	            temp1 = (seed & 0xFFFF) << 0x10;
	 
	            seed = (seed * 125 + 3) % 0x2AAAAB;
	            temp2 = (seed & 0xFFFF);
	 
	            cryptTable[index2] = ( temp1 | temp2 );
	        }
	    }
	}
	public String dhash(String key){
		return Long.toHexString(hash(key,TYPE)) + Long.toHexString(hash(key,TYPE+1));
	}
	public long hash(String key){
		return hash(key.toCharArray(),TYPE);
	}
	public long hash(String key, int type){
		return hash(key.toCharArray(),type);
	}
	public long hash(char[] key, int type){
	    long seed1 = 0x7FED7FED;
	    long seed2 = 0xEEEEEEEE;
	    int typeL = type << 8;
	    int ch;
	    for(char c : key){
	        ch = Character.toUpperCase(c);
	        seed1 = cryptTable[(typeL + ch) % CRYPTO_LEN] ^ (seed1 + seed2);
	        seed2 = ch + seed1 + seed2 + (seed2 << 5) + 3;
	    }
	    return seed1;
	}
}
