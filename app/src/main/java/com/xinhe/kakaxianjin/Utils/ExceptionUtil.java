package com.xinhe.kakaxianjin.Utils;


import android.util.Log;

import com.xinhe.kakaxianjin.MyApplication;

/**
 * 异常统一处理
 *
 * @author tarena
 *
 */
public class ExceptionUtil {
	public static void handleException(Exception e) {
		if (MyApplication.isRelease) {
			return;
		} else {
			Log.i("卡卡现金",e.toString());
			e.printStackTrace();
		}
	}

}
