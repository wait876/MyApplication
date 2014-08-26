package com.zjut.bluetoothle;

public class Constants {
	public static boolean DEBUG=true;
	//public static boolean isWrite=false;
	public static final int DELAY_TIME=800;
	//public static String device_id=null;
	//public static String device_sum=null;
	
	public static int operationType=0;
	public static final int READ_DEVICE=101;
	public static final int SET_DEVICE=102;
	public static final int SYNC_DEVICE=103;
	
	// 10->16
	public static String toHEXString(String string)
	{
		String result=null;
		long l=Long.parseLong(string);
		result=Long.toHexString(l);
		return result;
	}
	
	// 16->10
	public static String toValueString(String string)
	{
		String result=null;	
		result=Long.valueOf(string, 16).toString();
		return result;
	}
	
	public static int toValueInt(String string)
	{
		return Integer.valueOf(string, 16);
	}
}
