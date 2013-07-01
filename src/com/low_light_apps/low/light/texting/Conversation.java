package com.low_light_apps.low.light.texting;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
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

public class Conversation extends LowLightListActivity implements
		OnTouchListener, OnFocusChangeListener, OnLongClickListener {
	private String reply_address;
	public static EditText editMessage;
	long offset = -1;

	public static boolean isEditMessage = false;
	private ArrayList<String> addresses = new ArrayList<String>();
	private ArrayList<String> messages = new ArrayList<String>();
	private ArrayList<String> type = new ArrayList<String>();
	private ArrayList<String> contacts = new ArrayList<String>();
	private Cursor sms_cur;
	Context ctx;
	// private Cursor mms_cur;
	// private String person = null;
	String toastMessage = "Error in fatching the data.Please try again.";

	public final static String NEXT_MESSAGE = "com.low_light_apps.low.light.texting.NEXT_MESSAGE";
	String main_message = null;
	String contact_name = null;
	ConversationArrayAdapter myAdapter;
	private BroadcastReceiver mIntentReceiver;
	private int curr_count = 0;
	private Handler mHandler = new Handler();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_conversation);
		super.onCreate(savedInstanceState);
		
		ctx = this;
		Intent intent = getIntent();
		editMessage = (EditText) findViewById(R.id.NewMessageContent);
		editMessage.setOnTouchListener(this);
		editMessage.setOnFocusChangeListener(this);
		mBack.setOnLongClickListener(this);
		editMessage.addTextChangedListener(this);

		try {

			if (intent.getStringExtra(Conversation.NEXT_MESSAGE) != null) {
				main_message = intent.getStringExtra(NEXT_MESSAGE);

			} else if (intent.getStringExtra(MainActivity.EXTRA_MESSAGE) != null) {
				main_message = intent
						.getStringExtra(MainActivity.EXTRA_MESSAGE);
			} else {
				main_message = intent
						.getStringExtra(ContactsActivity.THREAD_ID);
				// if(main_message == "0") {
				// //person = intent.getStringExtra(ContactsActivity.PERSON);
				// }
			}

		} catch (NullPointerException e) {
			// TODO: handle exception
			main_message = intent.getStringExtra(ContactsActivity.THREAD_ID);
		}

		// Log.v("sms_cur on create", String.valueOf(curr_count));
		try {

			Uri selectUri = Uri.parse("content://sms/");

			String[] selectionArgs = new String[] { main_message };

			sms_cur = getContentResolver().query(selectUri, null,
					"thread_id = ?", selectionArgs, "Date");// works!!

			curr_count = sms_cur.getCount();
			getCursorColumns(sms_cur);

			if (sms_cur != null) {
				if (sms_cur.moveToFirst()) {
					do {
						// addresses.add(sms_cur.getString(sms_cur.getColumnIndex("address")));
						// addresses.add("placeholder");
						String dateVal = sms_cur.getString(sms_cur
								.getColumnIndex("date"));
						Date date = new Date(Long.valueOf(dateVal));
						// String myString =
						// DateFormat.getDateInstance().format(date);
						String myString = DateFormat.getDateTimeInstance()
								.format(date);
						addresses.add(myString);
						messages.add(sms_cur.getString(sms_cur
								.getColumnIndex("body")));
						String sent_received = sms_cur.getString(sms_cur
								.getColumnIndex("type"));
						type.add(sent_received);
						if (sent_received.equals("1")) {
							// contact_names.add("Sent by Somebody");
							String number = (sms_cur.getString(sms_cur
									.getColumnIndex("address")));
							String name = getContactName(this, number);
							if (name.equals("Contact Not Found")) {
								contacts.add(number);
							} else {
								contacts.add(name);
							}
						} else {
							contacts.add("Me");
						}
					} while (sms_cur.moveToNext());
				}

				myAdapter = new ConversationArrayAdapter(this, addresses,
						messages, type, contacts);
				setListAdapter(myAdapter);
				if (sms_cur.getCount() == 0) {

					reply_address = intent
							.getStringExtra(ContactsActivity.REPLY_ADDRESS);
					// Log.v("ReplyAddress", reply_address);
				} else {
					sms_cur.moveToLast(); // lazily there can be many user -
											// just
											// get last one
					reply_address = sms_cur.getString(sms_cur
							.getColumnIndex("address"));
				}

			}
			sms_cur.close();
		} catch (Exception e) {

			// TODO: handle exception

			Toast.makeText(ctx, toastMessage, Toast.LENGTH_LONG).show();
		}

	}

	@Override
	protected void onResume() {
		super.onResume();

		try {

			IntentFilter intentFilter = new IntentFilter(
					"SmsMessage.intent.MAIN");
			mIntentReceiver = new BroadcastReceiver() {
				@Override
				public void onReceive(Context context, Intent intent) {
					String msg = intent.getStringExtra("get_msg");
					Log.v("get_msg", msg);

					mHandler.postDelayed(new Runnable() {
						public void run() {
							updateMessages();
							// doStuff();
						}
					}, 3000);

				}
			};
			this.registerReceiver(mIntentReceiver, intentFilter);
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	@Override
	protected void onPause() {

		super.onPause();
		this.unregisterReceiver(this.mIntentReceiver);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_conversation, menu);
		return true;
	}

	// for testing
	private void getCursorColumns(Cursor cursor) {
		try {

			if (cursor != null) {
				int num = cursor.getColumnCount();
				for (int i = 0; i < num; ++i) {
					String colname = cursor.getColumnName(i);
					Log.v("Column_Name:  ", colname);

				}
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	private Cursor getConversations(String person_id) {

		Cursor cursor = null;
		try {

			String str_id = String.valueOf(person_id);
			String[] selectionArgs = new String[] { person_id };
			cursor = getContentResolver().query(Uri.parse("content://sms/"),
					null, "person = ?", selectionArgs, "Date");
			Log.v("LLT", "created cursor");
			startManagingCursor(cursor);
			// cursor.moveToFirst();
			Log.v("getConversations", "StartedManagingCursor");
			Log.v("cursor count is ", String.valueOf(cursor.getCount()));
		} catch (Exception e) {
			// TODO: handle exception
		}

		return cursor;

	}

	public void updateMessages() {

		try {

			Log.v("update Messages", "Started");
			myAdapter.clear();

			Uri selectUri = Uri.parse("content://sms/"); // mms-sms doesn't work
			String[] selectionArgs = new String[] { main_message };
			Log.v("MainMessage", main_message);
			Cursor update_cur = getContentResolver().query(selectUri, null,
					"thread_id = ?", selectionArgs, "Date");// works!!
			addresses.clear();
			messages.clear();
			type.clear();
			contacts.clear();

			Log.v("update_cur on create", String.valueOf(update_cur.getCount()));
			if (update_cur != null) {
				if (update_cur.moveToFirst()) {
					do {
						// addresses.add("placeholder");

						// addresses.add(update_cur.getString(update_cur.getColumnIndex("address")));
						String dateVal = update_cur.getString(update_cur
								.getColumnIndex("date"));
						Date date = new Date(Long.valueOf(dateVal));
						// String myString =
						// DateFormat.getDateInstance().format(date);
						String myString = DateFormat.getDateTimeInstance()
								.format(date);
						addresses.add(myString);

						messages.add(update_cur.getString(update_cur
								.getColumnIndex("body")));
						String sent_received = update_cur.getString(update_cur
								.getColumnIndex("type"));
						type.add(sent_received);
						if (sent_received.equals("1")) {
							// contact_names.add("Sent by Somebody");
							String number = (update_cur.getString(sms_cur
									.getColumnIndex("address")));
							String name = getContactName(this, number);
							if (name.equals("Contact Not Found")) {
								contacts.add(number);
							} else {
								contacts.add(name);
							}
						} else {
							contacts.add("Me");
						}

					} while (update_cur.moveToNext());

					myAdapter = new ConversationArrayAdapter(this, addresses,
							messages, type, contacts);
					setListAdapter(myAdapter);
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	public void prepareSMS(View v) {

		try {

			editMessage = (EditText) findViewById(R.id.NewMessageContent);
			final String messageText = editMessage.getText().toString();
			if (messageText.length() == 0) {
				return;
			}
			if (messageText.length() > 160) {
				Toast.makeText(this, "Message Too Long", Toast.LENGTH_LONG)
						.show();
				return;
			}
			SmsManager smsMgr = SmsManager.getDefault();
			ArrayList<String> messages = smsMgr.divideMessage(messageText);

			for (int i = 0; i < messages.size(); i++) {
				sendSMS(messages.get(i));
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	public void sendSMS(String messsageText) {

		try {

			String _messageNumber = reply_address;
			final String messageText = editMessage.getText().toString();
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
						editMessage.setHint(R.string.new_text_message_hint);
						ContentValues values = new ContentValues();
						values.put("address", reply_address);
						values.put("body", messageText);
						// values.put("person", person);
						Log.v("Values", String.valueOf(values.size()));
						Uri InsertUri = getContentResolver().insert(
								Uri.parse("content://sms/sent"), values);
						// Log.v("sms_cur after send message",
						// String.valueOf(sms_cur.getCount()));
						// String dateVal =
						// sms_cur.getString(sms_cur.getColumnIndex("date"));
						Date date = new Date();
						// String myString =
						// DateFormat.getDateInstance().format(date);
						String myString = DateFormat.getDateTimeInstance()
								.format(date);
						addresses.add(myString);
						// addresses.add("placeholder");
						messages.add(messageText);
						type.add("2");
						contacts.add("Me");
						myAdapter.notifyDataSetChanged(); // updates
															// conversation
															// with sent
															// message!!!
					} else {
						Toast.makeText(getBaseContext(), "SMS could not sent",
								Toast.LENGTH_SHORT).show();
					}

				}
			}, new IntentFilter(sent));
			Log.v("message number", _messageNumber);
			SmsManager sms = SmsManager.getDefault();
			sms.sendTextMessage(_messageNumber, null, messageText, sentPI, null);
			editMessage.setText("");

			editMessage.setHint("Sending message...");
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	public String getContactName(String id) {

		String retval = "Contact not in DB";
		try {
			String[] selectionArgs = new String[] { id };
			Cursor mCursor = getContentResolver().query(
					ContactsContract.Contacts.CONTENT_URI, null, "_id = ?",
					selectionArgs, null);
			Log.v("getContactName", "started");
			// getCursorColumns(mCursor);
			startManagingCursor(mCursor);
			Log.v("cursor", String.valueOf(mCursor.getCount()));
			if (mCursor.getCount() > 0) {
				mCursor.moveToFirst();
				retval = mCursor.getString(mCursor
						.getColumnIndex("display_name"));
				// Toast.makeText(this, retval, Toast.LENGTH_SHORT).show();
				// if(retval == null){
				// retval = "no name to display";
				// }

				Log.v("disp_name", retval);

			}
		} catch (Exception e) {
			// TODO: handle exception
		}

		return retval;
	}

	private void doStuff() {
		Toast.makeText(this, "Delayed Toast!", Toast.LENGTH_SHORT).show();
	}

	private String getContactName(Context context, String number) {

		Log.v("ffnet", "Started uploadcontactphoto...");

		String name = null;
		String contactId = null;

		try {

			// define the columns I want the query to return
			String[] projection = new String[] {
					ContactsContract.PhoneLookup.DISPLAY_NAME,
					ContactsContract.PhoneLookup._ID };

			// encode the phone number and build the filter URI
			Uri contactUri = Uri.withAppendedPath(
					ContactsContract.PhoneLookup.CONTENT_FILTER_URI,
					Uri.encode(number));

			// query time
			Cursor cursor = context.getContentResolver().query(contactUri,
					projection, null, null, null);
			startManagingCursor(cursor);
			if (cursor.moveToFirst()) {

				// Get values from contacts database:
				contactId = cursor.getString(cursor
						.getColumnIndex(ContactsContract.PhoneLookup._ID));
				name = cursor
						.getString(cursor
								.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME));

				// Get photo of contactId as input stream:
				Log.v("ffnet", "Started uploadcontactphoto: Contact Found @ "
						+ number);
				Log.v("ffnet", "Started uploadcontactphoto: Contact name  = "
						+ name);
				Log.v("ffnet", "Started uploadcontactphoto: Contact id    = "
						+ contactId);

			} else {

				Log.v("ffnet",
						"Started uploadcontactphoto: Contact Not Found @ "
								+ number);
				name = "Contact Not Found";

			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		return name;

	}

	public boolean onTouch(View v, MotionEvent event) {
		if (v == editMessage) {
			editMessage.requestFocus();

			Layout layout = ((EditText) v).getLayout();
			float x = event.getX() + v.getScrollX();
			float y = event.getY() + v.getScrollY();
			int line = layout.getLineForVertical((int) y);

			offset = layout.getOffsetForHorizontal(line, x);
			editMessage.setSelection((int) offset);

			hideDefaultKeyboard();
			enableKeyboard();

		}

		return true;
	}

	public void onFocusChange(View v, boolean hasFocus) {
		if (v == editMessage && hasFocus == true) {
			isEditMessage = true;

		} else {
			isEditMessage = false;
		}
	}

	public boolean onLongClick(View v) {
		// TODO Auto-generated method stub

		if (v == mBack) {
			Utility.logger_D("Long press");
			handleBackspace(editMessage);
		}
		return false;
	}

	private void handleBackspace(EditText _txtGiven) {

		try {
			editMessage.setText("");
		} catch (Exception e) {
			// TODO: handle exception
		}

		// String txt = _txtGiven.getText().toString();
		// String value = "";

		// int i = _txtGiven.getSelectionStart();
		// for (int j = 0; j < txt.length(); j++) {
		// char c = txt.charAt(j);
		// if (!((i - 1) == j)) {
		// value += c;
		// }
		// }
		// if (txt.length() == 1 && i == 1) {
		// _txtGiven.setText("");
		// }
		// if (value.length() > 0) {
		// _txtGiven.setText(value);
		// if (!(i == 0)) {
		// _txtGiven.setSelection(i - 1);
		// }
		// }

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

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		// TODO Auto-generated method stub
		super.onConfigurationChanged(newConfig);
		Utility.logger_D("ORIENTATION CHANGED....");

	}

}
