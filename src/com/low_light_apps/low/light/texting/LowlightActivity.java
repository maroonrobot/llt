package com.low_light_apps.low.light.texting;

import java.lang.reflect.Field;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.provider.Settings.SettingNotFoundException;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

import com.low_light_apps.low.light.utility.Utility;

public class LowlightActivity extends Activity implements OnClickListener,
		TextWatcher {

	private Vibrator myVib;

	private Button mBSpace, mBdone;
	protected Button mBack;
	protected Button mNum;
	private Button mBShift, xParentesis;
	private Button mBEnter, mComma;
	private RelativeLayout mKLayout, Conversaton_layout;
	protected RelativeLayout mLayout;

	protected boolean CHAR_STATE_FLAG; // true for caps and false for small
	private static int LATTER_CASE_STATE_FLAG = 1;
	public static final String PREFERENCES_NAME = "Brightness";
	SharedPreferences settings;
	SharedPreferences.Editor prefEditor;

	private boolean isEdit = false, isEdit1 = false, isEdit2 = false;
	private String mUpper = "upper", mLower = "lower";
	private int w, mWindowWidth;
	private String sL[] = { "a", "b", "c", "d", "e", "f", "g", "h", "i", "j",
			"k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w",
			"x", "y", "z" };
	private String cL[] = { "A", "B", "C", "D", "E", "F", "G", "H", "I", "J",
			"K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W",
			"X", "Y", "Z" };

	private String nS[] = { "@", ";", "'", "$", "3", "%", "&", "*", "8", "-",
			"+", "(", "?", "/", "9", "0", "1", "4", "#", "5", "7", ":", "2",
			"\"", "6", "!" };

	private Button mB[] = new Button[26];
	int DefaultBrightnessValue;

	float BackLightValue = 0;
	float AlphaValue = 0;
	int alpha_Val;
	String OPACITY = "briteness";
	boolean flag;

	float backlight_val = 0;

	SeekBar adjustBrightnessSeekBar;
	WindowManager.LayoutParams lp;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		myVib = (Vibrator) this.getSystemService(VIBRATOR_SERVICE);
		lp = getWindow().getAttributes();

		disableSoftKeyBacklight();
		adjustBrightnessSeekBar = (SeekBar) findViewById(R.id.adjust_brightness);
		mLayout = (RelativeLayout) findViewById(R.id.xK1);

		mKLayout = (RelativeLayout) findViewById(R.id.xKeyBoard);
		super.onCreate(savedInstanceState);

		settings = getSharedPreferences(PREFERENCES_NAME, MODE_PRIVATE);

		Conversaton_layout = (RelativeLayout) findViewById(R.id.textBox_sendmessage);

		try {
			DefaultBrightnessValue = android.provider.Settings.System.getInt(
					getContentResolver(),
					android.provider.Settings.System.SCREEN_BRIGHTNESS);
		} catch (SettingNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		setKeys();
		setFrow();
		setSrow();
		setTrow();
		setForow();

		if (savedInstanceState != null) {
			flag = true;
			Utility.logger_D("On Save Instance State not Null...");
			float get_progress = (Float) savedInstanceState.get(OPACITY);

			adjustBrightnessSeekBar.setProgress((int) get_progress);

			setBrightness(get_progress);

		} else {
			flag = false;
			Utility.logger_D("On Save Instance State Null...");

			// restorevalue();
		}
		adjustBrightnessSeekBar.setMax(DefaultBrightnessValue);

		adjustBrightnessSeekBar
				.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

					public void onProgressChanged(SeekBar arg0, int arg1,
							boolean arg2) {
						// TODO Auto-generated method stub
						Utility.logger_D("Listener..." + arg1);

						setBrightness(arg1);
						// setReverseBrightness(arg1);
					}

					public void onStartTrackingTouch(SeekBar arg0) {
						// TODO Auto-generated method stub

					}

					public void onStopTrackingTouch(SeekBar arg0) {
						// TODO Auto-generated method stub

					}
				});

	}

	private void disableSoftKeyBacklight() {

		try {
			WindowManager.LayoutParams localLayoutParams = new WindowManager.LayoutParams();
			WindowManager.LayoutParams.class.getField("buttonBrightness").set(
					localLayoutParams, Integer.valueOf(0));
			changeBtnBacklight(lp,
					WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_OFF);
			getWindow().setAttributes(lp);

		} catch (Exception localException2) {

			Log.w("ScreenFilter", "Cannot set button brightness");
		}

	}

	private void changeBtnBacklight(WindowManager.LayoutParams lp, float value) {

		if (Integer.parseInt(Build.VERSION.SDK) >= 8) {
			try {
				lp.buttonBrightness = value;
			} catch (Exception e) {
				Log.w("SS", "Error changing button brightness");
				e.printStackTrace();
			}
		} else {

			try {
				Field buttonBrightness = lp.getClass().getField(
						"buttonBrightness");
				Log.v("SS", "buttonBrightness " + buttonBrightness);
				buttonBrightness.set(lp, value);
			} catch (Exception e) {
				Log.w("SS", "Error changing button brightness");
				e.printStackTrace();
			}

		}

	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		prefEditor = settings.edit();
		prefEditor.clear();
		prefEditor.commit();
	}

	@Override
	protected void onPause() {
		super.onPause();

		Utility.logger_D("On Pausse ---");
		prefEditor = settings.edit();
		prefEditor.clear();

		prefEditor.putInt("opacity", adjustBrightnessSeekBar.getProgress());
		prefEditor.putFloat("opacity_val", backlight_val);
		prefEditor.commit();
		Utility.logger_D("opacity --- " + adjustBrightnessSeekBar.getProgress());
	}

	@Override
	protected void onResume() {
		super.onResume();

		Utility.logger_D("On Resume ---");

		if (flag) {

		} else {
			restorevalue();
		}

	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		// TODO Auto-generated method stub

		Utility.logger_D("On Save Instance State...");

		super.onSaveInstanceState(outState);

		outState.putFloat(OPACITY, adjustBrightnessSeekBar.getProgress());

	}

	public void restorevalue() {

		Utility.logger_D("restorevalue ---");

		float get_progress;

		float set_brightness;
		get_progress = settings.getInt("opacity", DefaultBrightnessValue);

		set_brightness = settings.getFloat("opacity_val",
				DefaultBrightnessValue);

		Utility.logger_D(" ---------------------- ");

		Utility.logger_D("get_progress--- " + get_progress);
		Utility.logger_D("set_brightness--- " + set_brightness);

		adjustBrightnessSeekBar.setProgress((int) get_progress);
		setBrightness(set_brightness);

	}

	public void setBrightness(float progress) {

		WindowManager.LayoutParams layoutParams = getWindow().getAttributes();

		if (progress <= 1) {
			backlight_val = 1;

		} else if (progress == DefaultBrightnessValue) {

			backlight_val = progress - 1;
		} else {
			backlight_val = progress;
		}
		BackLightValue = (float) backlight_val / DefaultBrightnessValue;

		layoutParams.screenBrightness = BackLightValue;
		layoutParams.alpha = BackLightValue;

		getWindow().setAttributes(layoutParams);

	}

	public void setReverseBrightness(float progress) {

		WindowManager.LayoutParams layoutParams = getWindow().getAttributes();

		if (progress <= 1) {
			backlight_val = 1;

		} else if (progress == DefaultBrightnessValue) {

			backlight_val = progress - 1;
		} else {
			backlight_val = progress;
		}
		BackLightValue = (float) (DefaultBrightnessValue - backlight_val)
				/ DefaultBrightnessValue;

		layoutParams.alpha = BackLightValue;
		layoutParams.screenBrightness = BackLightValue;

		getWindow().setAttributes(layoutParams);

	}

	public void onClick(View v) {
		myVib.vibrate(50);

		if (v == xParentesis) {
			addText(v);
		} else if (v == mBShift) {
			setCapsSmallLatter();

		} else if (v != mBdone && v != mBack && v != mNum && v != mBShift) {
			addText(v);

		} else if (v == mBdone) {

			disableKeyboard();

		} else if (v == mBack) {
			Utility.logger_D("back pressed.....");
			isBack(v);
		} else if (v == mNum) {

			String nTag = (String) mNum.getTag();
			if (nTag.equals("num")) {

				changeSyNuLetters();
				changeSyNuTags();

				mBShift.setVisibility(Button.INVISIBLE);
				xParentesis.setVisibility(Button.VISIBLE);

			} else if (nTag.equals("ABC")) {

				try {

					int start = 0, end = 0;
					String mainText = "";

					if (SendMessage.isEditBody == true) {

						start = SendMessage.messageBody.getSelectionStart();
						end = SendMessage.messageBody.getSelectionEnd();
						mainText = SendMessage.messageBody.getText().toString();

					} else if (SendMessage.isEditNumber == true) {

						start = SendMessage.messageNumber.getSelectionStart();
						end = SendMessage.messageNumber.getSelectionEnd();
						mainText = SendMessage.messageNumber.getText()
								.toString();
					}

					onABCPress(v, start, end, mainText);

					if (CHAR_STATE_FLAG) {
						changeCapitalLetters();
						changeCapitalTags();
					} else {
						changeSmallLetters();
						changeSmallTags();
					}
				} catch (Exception e) {
					// TODO: handle exception
				}
			}
		}

	}

	protected void onABCPress(View v, int start, int end, String mainText) {

		try {

			String b = "";
			b = (String) v.getTag();

			if (b != null) {

				String detectedText = mainText.substring(start - 1, end);

				Utility.logger_E("detectedText @" + detectedText + "@");

				if (detectedText.equals(" ")) {

					Utility.logger_E("2 condition true ...");
					String text1 = "";

					text1 = mainText.substring(start - 2, end);
					Utility.logger_D("text1 ...@" + text1 + "@");

					if (text1.equals(". ")) {

						CHAR_STATE_FLAG = true;
					} else if (text1.equals("? ")) {

						CHAR_STATE_FLAG = true;
					} else if (text1.equals("! ")) {

						CHAR_STATE_FLAG = true;
					}
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	protected void setKeysOnPeriod() {

		changeCapitalLetters();
		changeCapitalTags();

	}

	private void addText(View v) {

		myVib.vibrate(50);
		if (SendMessage.isEditBody == true) {
			String b = "";
			b = (String) v.getTag();
			if (b != null) {

				try {

					int start = SendMessage.messageBody.getSelectionStart();
					int end = SendMessage.messageBody.getSelectionEnd();
					SendMessage.messageBody.getText().replace(
							Math.min(start, end), Math.max(start, end), b, 0,
							b.length());
				} catch (IndexOutOfBoundsException e) {
					// TODO: handle exception

				} catch (NullPointerException e) {
					// TODO: handle exception
				}

			}
		}

		if (SendMessage.isEditNumber == true) {
			String b = "";
			b = (String) v.getTag();
			if (b != null) {

				int start = SendMessage.messageNumber.getSelectionStart();
				int end = SendMessage.messageNumber.getSelectionEnd();
				SendMessage.messageNumber.getText().replace(
						Math.min(start, end), Math.max(start, end), b, 0,
						b.length());
			}
		}

	}

	public String removeCharAt(String s, int pos) {
		return s.substring(0, pos) + s.substring(pos + 1);
	}

	private void isBack(View v) {

		try {

			if (SendMessage.isEditBody == true) {

				Utility.logger_D("back pressed edit body.....");

				CharSequence cc = SendMessage.messageBody.getText();

				int start = SendMessage.messageBody.getSelectionStart();
				int end = SendMessage.messageBody.getSelectionEnd();

				Utility.logger_E(" start -- " + start);
				Utility.logger_E("end -- " + end);

				if (cc != null && cc.length() > 0 && start > 0 && end > 0) {
					{

						Utility.logger_E(" NEW TEXT -- "
								+ removeCharAt(cc.toString(), start - 1));

						SendMessage.messageBody.setText(removeCharAt(
								cc.toString(), start - 1));

						SendMessage.messageBody.setSelection(start - 1);

					}

				}
			}

			if (SendMessage.isEditNumber == true) {

				Utility.logger_D("back pressed edit number.....");

				CharSequence cc = SendMessage.messageNumber.getText();

				int start = SendMessage.messageNumber.getSelectionStart();
				int end = SendMessage.messageNumber.getSelectionEnd();

				Utility.logger_E(" start -- " + start);
				Utility.logger_E("end -- " + end);

				if (cc != null && cc.length() > 0 && start > 0 && end > 0) {
					{

						Utility.logger_E(" NEW TEXT -- "
								+ removeCharAt(cc.toString(), start - 1));

						SendMessage.messageNumber.setText(removeCharAt(
								cc.toString(), start - 1));

						SendMessage.messageNumber.setSelection(start - 1);

					}

				}

			}

		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	protected void changeSmallLetters() {
		mBShift.setBackgroundResource(R.drawable.box_small_letter);
		CHAR_STATE_FLAG = false;
		mBShift.setVisibility(Button.VISIBLE);
		xParentesis.setVisibility(Button.INVISIBLE);

		for (int i = 0; i < sL.length; i++)
			mB[i].setText(sL[i]);
		mNum.setTag("123");
	}

	protected void changeSmallTags() {
		for (int i = 0; i < sL.length; i++)
			mB[i].setTag(sL[i]);

		mBShift.setTag("lower");

		mNum.setTag("num");
	}

	private void changeCapitalLetters() {

		mBShift.setBackgroundResource(R.drawable.box_caps);
		CHAR_STATE_FLAG = true;
		mBShift.setVisibility(Button.VISIBLE);
		xParentesis.setVisibility(Button.INVISIBLE);
		for (int i = 0; i < cL.length; i++)
			mB[i].setText(cL[i]);

		mBShift.setTag("upper");
		mNum.setText("123");

	}

	private void changeCapitalTags() {
		for (int i = 0; i < cL.length; i++)
			mB[i].setTag(cL[i]);
		mNum.setTag("num");

	}

	private void changeSyNuLetters() {

		for (int i = 0; i < nS.length; i++)
			mB[i].setText(nS[i]);
		mNum.setText("ABC");

	}

	private void changeSyNuTags() {
		for (int i = 0; i < nS.length; i++)
			mB[i].setTag(nS[i]);

		mNum.setTag("ABC");

	}

	// enabling customized keyboard
	public void enableKeyboard() {
		Log.v("SS", "enableKeyboard keyboard");
		mLayout.setVisibility(RelativeLayout.VISIBLE);

		mKLayout.setVisibility(RelativeLayout.VISIBLE);

	}

	// Disable customized keyboard
	private void disableKeyboard() {
		mLayout.setVisibility(RelativeLayout.GONE);
		mKLayout.setVisibility(RelativeLayout.GONE);

	}

	public void hideDefaultKeyboard() {

		Log.v("SS", "hide default keyboard");

		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

	}

	private void setFrow() {
		w = (mWindowWidth / 13);
		w = w - 15;
		mB[16].setWidth(w);
		mB[22].setWidth(w + 3);
		mB[4].setWidth(w);
		mB[17].setWidth(w);
		mB[19].setWidth(w);
		mB[24].setWidth(w);
		mB[20].setWidth(w);
		mB[8].setWidth(w);
		mB[14].setWidth(w);
		mB[15].setWidth(w);
		mB[16].setHeight(50);
		mB[22].setHeight(50);
		mB[4].setHeight(50);
		mB[17].setHeight(50);
		mB[19].setHeight(50);
		mB[24].setHeight(50);
		mB[20].setHeight(50);
		mB[8].setHeight(50);
		mB[14].setHeight(50);
		mB[15].setHeight(50);

	}

	private void setSrow() {
		w = (mWindowWidth / 10);
		mB[0].setWidth(w);
		mB[18].setWidth(w);
		mB[3].setWidth(w);
		mB[5].setWidth(w);
		mB[6].setWidth(w);
		mB[7].setWidth(w);
		// mB[26].setWidth(w);
		mB[9].setWidth(w);
		mB[10].setWidth(w);
		mB[11].setWidth(w);
		// mB[26].setWidth(w);

		mB[0].setHeight(50);
		mB[18].setHeight(50);
		mB[3].setHeight(50);
		mB[5].setHeight(50);
		mB[6].setHeight(50);
		mB[7].setHeight(50);
		mB[9].setHeight(50);
		mB[10].setHeight(50);
		mB[11].setHeight(50);
		// mB[26].setHeight(50);
	}

	private void setTrow() {
		w = (mWindowWidth / 12);
		mB[25].setWidth(w);
		mB[23].setWidth(w);
		mB[2].setWidth(w);
		mB[21].setWidth(w);
		mB[1].setWidth(w);
		mB[13].setWidth(w);
		mB[12].setWidth(w);
		// mB[27].setWidth(w);
		// mB[28].setWidth(w);
		mBack.setWidth(w);

		mB[25].setHeight(50);
		mB[23].setHeight(50);
		mB[2].setHeight(50);
		mB[21].setHeight(50);
		mB[1].setHeight(50);
		mB[13].setHeight(50);
		mB[12].setHeight(50);
		// mB[27].setHeight(50);
		// mB[28].setHeight(50);
		mBack.setHeight(50);

	}

	private void setForow() {
		w = (mWindowWidth / 10);
		mBSpace.setWidth(w * 4);
		mBSpace.setHeight(50);
		// mB[29].setWidth(w);
		// mB[29].setHeight(50);
		//
		// mB[30].setWidth(w);
		// mB[30].setHeight(50);
		//
		// mB[31].setHeight(50);
		// mB[31].setWidth(w);
		mBdone.setWidth(w + (w / 1));
		mBdone.setHeight(50);

	}

	private void setKeys() {
		mWindowWidth = getWindowManager().getDefaultDisplay().getWidth(); // getting
		// window
		// height
		// getting ids from xml files
		mB[0] = (Button) findViewById(R.id.xA);
		mB[1] = (Button) findViewById(R.id.xB);
		mB[2] = (Button) findViewById(R.id.xC);
		mB[3] = (Button) findViewById(R.id.xD);
		mB[4] = (Button) findViewById(R.id.xE);
		mB[5] = (Button) findViewById(R.id.xF);
		mB[6] = (Button) findViewById(R.id.xG);
		mB[7] = (Button) findViewById(R.id.xH);
		mB[8] = (Button) findViewById(R.id.xI);
		mB[9] = (Button) findViewById(R.id.xJ);
		mB[10] = (Button) findViewById(R.id.xK);
		mB[11] = (Button) findViewById(R.id.xL);
		mB[12] = (Button) findViewById(R.id.xM);
		mB[13] = (Button) findViewById(R.id.xN);
		mB[14] = (Button) findViewById(R.id.xO);
		mB[15] = (Button) findViewById(R.id.xP);
		mB[16] = (Button) findViewById(R.id.xQ);
		mB[17] = (Button) findViewById(R.id.xR);
		mB[18] = (Button) findViewById(R.id.xS);
		mB[19] = (Button) findViewById(R.id.xT);
		mB[20] = (Button) findViewById(R.id.xU);
		mB[21] = (Button) findViewById(R.id.xV);
		mB[22] = (Button) findViewById(R.id.xW);
		mB[23] = (Button) findViewById(R.id.xX);
		mB[24] = (Button) findViewById(R.id.xY);
		mB[25] = (Button) findViewById(R.id.xZ);
		// mB[26] = (Button) findViewById(R.id.xS5);
		// mB[27] = (Button) findViewById(R.id.xS2);
		// mB[28] = (Button) findViewById(R.id.xS3);
		// mB[29] = (Button) findViewById(R.id.xS4);
		// mB[30] = (Button) findViewById(R.id.xS5);
		// mB[31] = (Button) findViewById(R.id.xS6);
		mBSpace = (Button) findViewById(R.id.xSpace);
		mBdone = (Button) findViewById(R.id.xDone);

		mBack = (Button) findViewById(R.id.xBack);
		mNum = (Button) findViewById(R.id.xNum);

		mBEnter = (Button) findViewById(R.id.xdot);
		mComma = (Button) findViewById(R.id.xS5);
		mBShift = (Button) findViewById(R.id.xShift);
		xParentesis = (Button) findViewById(R.id.xParentesis);

		for (int i = 0; i < mB.length; i++)
			mB[i].setOnClickListener(this);
		mBSpace.setOnClickListener(this);
		mBdone.setOnClickListener(this);
		mBack.setOnClickListener(this);
		// mBack.setOnLongClickListener(this);
		mBEnter.setOnClickListener(this);
		mComma.setOnClickListener(this);

		mNum.setOnClickListener(this);

		mBShift.setOnClickListener(this);
		xParentesis.setOnClickListener(this);

	}

	private void setCapsSmallLatter() {

		try {

			++LATTER_CASE_STATE_FLAG;
			Utility.logger_E("FLAG VALUE--- " + LATTER_CASE_STATE_FLAG);

			switch (LATTER_CASE_STATE_FLAG) {

			case 2:

				Utility.logger_D("FLAG VALUE IN 2--- " + LATTER_CASE_STATE_FLAG);

				changeSmallLetters();
				changeSmallTags();
				LATTER_CASE_STATE_FLAG = 0;
				break;

			case 1:

				Utility.logger_D("FLAG VALUE IN 3--- " + LATTER_CASE_STATE_FLAG);

				changeCapitalLetters();
				changeCapitalTags();

				break;
			default:
				Utility.logger_D("FLAG VALUE IN DEFALUT--- "
						+ LATTER_CASE_STATE_FLAG);

				changeCapitalLetters();
				changeCapitalTags();
				break;

			}
		} catch (Exception e) {
			// TODO: handle exception
		}

	}

	protected void setConditionOnDotPress(CharSequence s) {

		try {

			String getText = s.toString();

			if (mNum.getTag().equals("ABC") || LATTER_CASE_STATE_FLAG == 1) {

			} else {

				if (getText.length() > 0) {

					changeSmallLetters();
					changeSmallTags();
				}

				int start = 0, end = 0;

				if (SendMessage.isEditBody == true) {

					start = SendMessage.messageBody.getSelectionStart();
					end = SendMessage.messageBody.getSelectionEnd();

					setKeyboardOnSpacePress(start, end, getText);

				} else if (SendMessage.isEditNumber == true) {

					start = SendMessage.messageNumber.getSelectionStart();
					end = SendMessage.messageNumber.getSelectionEnd();

					setKeyboardOnSpacePress(start, end, getText);
				}

			}
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	public void setKeyboardOnSpacePress(int start, int end, String getText) {

		try {

			Utility.logger_E("cursor start index..." + start);
			Utility.logger_E("cursor end index..." + end);

			String detectedText = getText.substring(start - 1, end);

			Utility.logger_E("detectedText @" + detectedText + "@");

			if (getText.substring(start - 1, end).equals(" ")) {

				Utility.logger_E("2 condition true ...");
				String text1 = "";

				text1 = getText.substring(start - 2, end);
				Utility.logger_D("text1 ...@" + text1 + "@");

				if (text1.equals(". ")) {

					Utility.logger_E("3 condition true ...");
					setKeysOnPeriod();

				} else if (text1.equals("? ")) {

					Utility.logger_E("3 condition true ...");
					setKeysOnPeriod();

				} else if (text1.equals("! ")) {

					Utility.logger_E("3 condition true ...");
					setKeysOnPeriod();

				}

			} else if (getText.length() > 1) {

				if (getText.substring(getText.lastIndexOf(" ") - 1).length() == 3) {
					Utility.logger_E(" MAKE KEYBOARD SMALL ");
					changeSmallLetters();
					changeSmallTags();
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		// TODO Auto-generated method stub
		Utility.logger_D("On text change..." + s);

		setConditionOnDotPress(s);
	}

	@Override
	public void afterTextChanged(Editable s) {
		// TODO Auto-generated method stub
	}

}
