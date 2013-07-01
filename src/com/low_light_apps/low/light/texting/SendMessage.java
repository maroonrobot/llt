package com.low_light_apps.low.light.texting;

import java.text.DateFormat;
import java.util.Date;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.text.Layout;
import android.util.Log;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.widget.EditText;
import android.widget.Toast;

import com.low_light_apps.low.light.utility.Utility;

public class SendMessage extends LowlightActivity implements OnTouchListener,
		OnFocusChangeListener, OnLongClickListener {
	public static EditText messageNumber;
	public static EditText messageBody;
	public static boolean isEditNumber = false, isEditBody = false;
	long offset = -1;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_send_message);
		super.onCreate(savedInstanceState);
		messageNumber = (EditText) findViewById(R.id.txtPhoneNo);
		messageBody = (EditText) findViewById(R.id.txtMessage);

		messageNumber.setOnTouchListener(this);
		messageBody.setOnTouchListener(this);
		mBack.setOnLongClickListener(this);
		messageBody.addTextChangedListener(this);
		messageNumber.addTextChangedListener(this);

		messageNumber.setOnFocusChangeListener(this);
		messageBody.setOnFocusChangeListener(this);
		Date message_date = new Date();
		Date today = new Date();
		String todayString = DateFormat.getDateTimeInstance().format(today);
		// String myString = DateFormat.getDateInstance().format(date);
		String myString = DateFormat.getDateTimeInstance().format(message_date);
		if (myString.equals(todayString)) {
			Log.v("Equal", "Equal");
		} else {
			Log.v("not equal", "not equal");
		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_send_message, menu);
		return true;
	}

	public void sendSMS(View v) {

		try {

			String _messageNumber = messageNumber.getText().toString();
			String messageText = messageBody.getText().toString();
			String sent = "SMS_SENT";
			PendingIntent sentPI = PendingIntent.getBroadcast(this, 0,
					new Intent(sent), 0);
			// ---when the SMS has been sent---
			registerReceiver(new BroadcastReceiver() {
				@Override
				public void onReceive(Context arg0, Intent arg1) {
					if (getResultCode() == Activity.RESULT_OK) {
						Toast.makeText(getBaseContext(), "SMS sent",
								Toast.LENGTH_SHORT).show();
					} else {
						Toast.makeText(getBaseContext(), "SMS could not sent",
								Toast.LENGTH_SHORT).show();
					}
				}
			}, new IntentFilter(sent));
			SmsManager sms = SmsManager.getDefault();
			sms.sendTextMessage(_messageNumber, null, messageText, sentPI, null);

		} catch (Exception e) {
			// TODO: handle exception
			Utility.logger_D("Exception --- " + e);

		}

	}

	public boolean onTouch(View v, MotionEvent event) {

		if (v == messageBody) {

			try {
				messageBody.requestFocus();
				enableKeyboard();
				switch (event.getAction()) {
				case MotionEvent.ACTION_UP:
					Layout layout = ((EditText) v).getLayout();
					float x = event.getX() + v.getScrollX();
					float y = event.getY() + v.getScrollY();
					int line = layout.getLineForVertical((int) y);

					// Here is what you wanted:

					offset = layout.getOffsetForHorizontal(line, x);
					messageBody.setSelection((int) offset);
					hideDefaultKeyboard();
					break;
				}
			} catch (Exception e) {
				// TODO: handle exception
			}

			return true;

		} else if (v == messageNumber) {

			try {
				messageNumber.requestFocus();
				hideDefaultKeyboard();
				enableKeyboard();

				switch (event.getAction()) {
				case MotionEvent.ACTION_UP:
					Layout layout = ((EditText) v).getLayout();
					float x = event.getX() + v.getScrollX();
					float y = event.getY() + v.getScrollY();
					int line = layout.getLineForVertical((int) y);

					// Here is what you wanted:

					offset = layout.getOffsetForHorizontal(line, x);
					messageNumber.setSelection((int) offset);
					hideDefaultKeyboard();
					break;
				}
			} catch (Exception e) {
				// TODO: handle exception
			}

			return true;

		}

		return true;
	}

	public void onFocusChange(View v, boolean hasFocus) {
		if (v == messageNumber && hasFocus == true) {
			isEditNumber = true;
			isEditBody = false;

		}
		if (v == messageBody && hasFocus == true) {
			isEditBody = true;
			isEditNumber = false;

		}
	}

	public boolean onLongClick(View v) {
		// TODO Auto-generated method stub
		if (v == mBack) {

			try {

				if (isEditNumber) {

					messageNumber.setText("");
				} else {

					messageBody.setText("");
				}

			}

			catch (Exception e) {
				// TODO: handle exception
			}

		}
		return false;
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub

		if (mLayout.getVisibility() == View.VISIBLE) {

			Utility.logger_D("Key board visible...");
			mLayout.setVisibility(View.GONE);

		} else {
			super.onBackPressed();
			Utility.logger_D("Key board invisible...");
		}
	}

}
