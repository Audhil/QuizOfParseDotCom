package com.mns.quiz;

public class Debug {
	static boolean debug = true;
	
	public static void print(String string) {
		if (debug) System.out.println(string);
	}

}
