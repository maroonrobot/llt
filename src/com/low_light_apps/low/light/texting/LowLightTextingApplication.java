package com.low_light_apps.low.light.texting;

import android.app.Application;
import android.util.Log;

public class LowLightTextingApplication extends Application {
	
	private int brightness;
	private int startBrightness = 0;

	public int getBrightness() {
		return brightness;
	}

	public void setBrightness(int brightness) {
		this.brightness = brightness;
	}

	public int getStartBrightness() {
		return startBrightness;
	}

	public void setStartBrightness(int startBrightness) {
		if (this.startBrightness==0)
		{
			Log.d("Initial Brighness", startBrightness + "");
			this.startBrightness = startBrightness;
		}
	}
}
