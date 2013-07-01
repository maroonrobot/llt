package com.low_light_apps.low.light.utility;

import android.util.Log;

public class Utility {

	public static String Tag = "SS";

	public static void logger_D(String msg) {
		Log.d(Tag, msg);
	}

	public static void logger_E(String msg) {
		Log.e(Tag, msg);
	}

}
