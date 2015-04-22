package com.ye.gdufs.log;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import com.ye.gdufs.global.GlobalArgs;

import java.time.LocalDateTime;

public class Logs {
	private static PrintWriter log_writer; 
	static{
		String log_path;
		String[] errorLogsInfo = GlobalArgs.getErrorLogsinfo();
		log_path = errorLogsInfo[0] + "/" + errorLogsInfo[1] + "." + errorLogsInfo[2]; 
		System.out.println("LogPath:" + log_path);
		try {
			log_writer = new PrintWriter(new FileWriter(log_path,false));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public synchronized static void printStackTrace(Exception e){
		try {
			log_writer.print("--------------------------");
			printNow();
			log_writer.println(" Exception--------------------------");
			e.printStackTrace(log_writer);
			log_writer.flush();
		} catch (Exception e1) {
			e1.printStackTrace();
			e.printStackTrace();
		}
	}
	public synchronized static void error_msg(String format,Object... args){
		try {
			log_writer.print("--------------------------");
			printNow();
			log_writer.println(" error--------------------------");
			log_writer.printf(format, args);
			log_writer.println();
			log_writer.flush();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public synchronized static void info_msg(String format,Object... args){
		try {
			log_writer.print("--------------------------");
			printNow();
			log_writer.println(" info--------------------------");
			log_writer.printf(format, args);
			log_writer.println();
			log_writer.flush();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	private static void printNow(){
		log_writer.print(LocalDateTime.now());
	}
}
