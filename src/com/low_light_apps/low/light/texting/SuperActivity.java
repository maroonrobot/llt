package com.low_light_apps.low.light.texting;


import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

public class SuperActivity extends Activity implements OnSeekBarChangeListener {

	SeekBar seekBar;
	public static float progressValue;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		Log.e("SS", "Super Activity on create --  ");
		seekBar.setOnSeekBarChangeListener(this);
	}

	@Override
	public void onProgressChanged(SeekBar seekBar, int progress,
			boolean fromUser) {
		// TODO Auto-generated method stub
		Log.e("SS", "progress --  " + progress);

	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
		// TODO Auto-generated method stub

	}

	public float getProgress() {
		Log.e("SS", "Get Progress --  " + progressValue);
		return progressValue;

	}

	public void setProgress() {

		progressValue = seekBar.getProgress();
		Log.e("SS", "Set Progress --  " + progressValue);

	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		Log.e("SS", "On Resume Super --  " + progressValue);
		seekBar.setProgress((int) progressValue);
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		Log.e("SS", "On Destroy Super --  ");

	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		Log.e("SS", "On OnPause Super --  ");
		setProgress();
	}


}
