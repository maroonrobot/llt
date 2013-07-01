package com.low_light_apps.low.light.texting;

import android.app.Activity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TableLayout;

public class TestKeyboard extends Activity implements OnClickListener,
		OnTouchListener, OnFocusChangeListener {
	EditText editText1;
	WindowManager.LayoutParams lp;
	private TableLayout keyboardLayout;

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

	private Button mBSpace, mBdone, mBack, mNum, mBShift, mBEnter;
	private String mUpper = "upper", mLower = "lower";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		initView();
	}

	public void initView() {
		setContentView(R.layout.testkeyboard);
		editText1 = (EditText) findViewById(R.id.editText1);
		// editText1.setOnClickListener(this);
		setKeys();
		editText1.setOnTouchListener(this);
		editText1.setOnFocusChangeListener(this);

		lp = getWindow().getAttributes();
		keyboardLayout = (TableLayout) findViewById(R.id.keyboardLayout);

	}

	private void setKeys() {

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

		// mB[27] = (Button) findViewById(R.id.xS2);
		// mB[28] = (Button) findViewById(R.id.xS3);
		// mB[29] = (Button) findViewById(R.id.xS4);
		// mB[30] = (Button) findViewById(R.id.xS5);
		// mB[31] = (Button) findViewById(R.id.xS6);
		mBSpace = (Button) findViewById(R.id.xSpace);
		mBdone = (Button) findViewById(R.id.xDone);

		mBack = (Button) findViewById(R.id.xBack);
		mNum = (Button) findViewById(R.id.xNum);
		mBEnter = (Button) findViewById(R.id.xEnter);
		mBShift = (Button) findViewById(R.id.xShift);
		for (int i = 0; i < mB.length; i++)
			mB[i].setOnClickListener(this);
		mBSpace.setOnClickListener(this);
		mBdone.setOnClickListener(this);
		mBack.setOnClickListener(this);
		mBEnter.setOnClickListener(this);

		mNum.setOnClickListener(this);
		mBShift.setOnClickListener(this);

	}

	public void onClick(View v) {
		// TODO Auto-generated method stub

		if (v == mBShift) {

			if (mBShift.getTag().equals(mUpper)) {

				changeSmallLetters();
				changeSmallTags();
			} else if (mBShift.getTag().equals(mLower)) {

				changeCapitalLetters();
				changeCapitalTags();
			}

		} else if (v != mBdone && v != mBack && v != mNum && v != mBShift) {

			addText(v);

		} else if (v == mBdone) {

			disableKeyboard();

		} else if (v == mBack) {
			isBack(v);
		} else if (v == mNum) {

			String nTag = (String) mNum.getTag();
			if (nTag.equals("num")) {

				changeSyNuLetters();
				changeSyNuTags();

				mBShift.setVisibility(Button.INVISIBLE);

			}
			if (nTag.equals("ABC")) {

				changeCapitalLetters();
				changeCapitalTags();
			}

		}

	}

	public void onFocusChange(View v, boolean hasFocus) {
		// TODO Auto-generated method stub

	}

	public boolean onTouch(View v, MotionEvent event) {
		if (v == editText1) {
			editText1.requestFocus();

			hideDefaultKeyboard();
			enableKeyboard();

		}

		return true;
	}

	public void hideDefaultKeyboard() {
		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

	}

	public void enableKeyboard() {

		keyboardLayout.setVisibility(RelativeLayout.VISIBLE);

	}

	private void disableKeyboard() {
		keyboardLayout.setVisibility(RelativeLayout.GONE);

	}

	private void addText(View v) {
		if (Conversation.isEditMessage == true) {
			String b = "";
			b = (String) v.getTag();
			if (b != null) {
				// adding text in Edittext
				Conversation.editMessage.append(b);

			}
		}

	}

	private void isBack(View v) {
		if (Conversation.isEditMessage == true) {
			CharSequence cc = Conversation.editMessage.getText();
			if (cc != null && cc.length() > 0) {
				{
					Conversation.editMessage.setText("");
					Conversation.editMessage.append(cc.subSequence(0,
							cc.length() - 1));
				}

			}
		}

	}

	private void changeSmallLetters() {

		mBShift.setVisibility(Button.VISIBLE);
		for (int i = 0; i < sL.length; i++)
			mB[i].setText(sL[i]);
		mNum.setTag("123");
	}

	private void changeSmallTags() {
		for (int i = 0; i < sL.length; i++)
			mB[i].setTag(sL[i]);

		mBShift.setTag("lower");

		mNum.setTag("num");
	}

	private void changeCapitalLetters() {

		mBShift.setVisibility(Button.VISIBLE);
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
}
