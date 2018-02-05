package com.xinhe.kakaxianjin.Utils;


import android.util.Log;

import com.xinhe.kakaxianjin.MyApplication;


public class LogcatUtil {

	public static void printLogcat(String log) {
		if (MyApplication.isRelease) {
			return;
		} else {
			Log.i("卡卡现金", log);
		}
	}
}
