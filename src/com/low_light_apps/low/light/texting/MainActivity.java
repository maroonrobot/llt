package com.low_light_apps.low.light.texting;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Date;

import com.low_light_apps.low.light.utility.Utility;

import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

public class MainActivity extends LowLightListActivity {
	public final static String EXTRA_MESSAGE = "com.low_light_apps.low.light.texting.MESSAGE";
	private ArrayList<String> addresses = new ArrayList<String>();
	private ArrayList<String> contact_names = new ArrayList<String>();
	private ArrayList<String> messages = new ArrayList<String>();
	private ArrayList<String> type = new ArrayList<String>();
	private ArrayList<String> thread_ids = new ArrayList<String>();
	private ArrayList<String> message_ids = new ArrayList<String>();
	private ArrayList<String> dates = new ArrayList<String>();
	MultiConversationAdapter myAdapter;
	private int curr_count = 0;
	private Handler mHandler = new Handler();
	private BroadcastReceiver mIntentReceiver;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// Cursor cursor = null, smsCursor, mmsCursor;

		setContentView(R.layout.activity_main);
		super.onCreate(savedInstanceState);

		try {
			Utility.logger_D("First try block-- ");

			setSmsMmsDataForLessThenJellyBean();
			// setAdapter();

		} catch (Exception e) {
			// TODO: handle exception

			// try {
			//
			// Utility.logger_D("second try block-- ");
			//
			// if (cursor == null) {
			//
			// smsCursor = getContentResolver().query(
			// Uri.parse("content://sms"), null, null, null, null);
			// mmsCursor = getContentResolver().query(
			// Uri.parse("content://sms"), null, null, null, null);
			//
			// getSmsDataForJellyBean();
			// setAdapter();
			//
			// }
			// } catch (Exception e2) {
			// // TODO: handle exception
			// Utility.logger_D("Second Exception-- " + e);
			// }
			 Toast.makeText(this, "Error in setSmsMms try block",
			 Toast.LENGTH_SHORT).show();
		}

	}

	@Override
	protected void onResume() {
		super.onResume();

		IntentFilter intentFilter = new IntentFilter("SmsMessage.intent.MAIN");
		mIntentReceiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				String msg = intent.getStringExtra("get_msg");
				Log.v("main_activity_get_msg", msg);
				// Process the sms format and extract body &amp; phoneNumber
				// msg = msg.replace("\n", "");
				// String body = msg.substring(msg.lastIndexOf(":")+1,
				// msg.length());
				// String pNumber = msg.substring(0,msg.lastIndexOf(":"));

				// Add it to the list or do whatever you wish to
				mHandler.postDelayed(new Runnable() {
					public void run() {
						setSmsMmsDataForLessThenJellyBean();
					}
				}, 3000);

			}
		};
		this.registerReceiver(mIntentReceiver, intentFilter);
	}

	@Override
	protected void onPause() {

		super.onPause();
		this.unregisterReceiver(this.mIntentReceiver);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	protected void onListItemClick(ListView l, View v, int position, long id) {
		String item;
		String message = null;
		String thread_id = null;
		Log.e("postion is ", String.valueOf(position));
		thread_id = thread_ids.get(position);
		Log.e("thread_id is ", String.valueOf(thread_id));
		if (thread_id == null) {
			return;
		}
		String message_read = type.get(position);
		if (message_read.equals("0")) {
			Log.v("message read is ", message_read);
			setMessageRead(id);
		}

		Intent intent = new Intent(this, Conversation.class);

		Log.v("thread_id to send is:", thread_id);
		intent.putExtra(EXTRA_MESSAGE, thread_id);
		startActivity(intent);
		// Toast.makeText(this, item + " Selected", Toast.LENGTH_LONG).show();
	}

	private Cursor getLastMessage(long id) {
		Cursor cur = null;
		try {

			Uri selectUri = Uri.parse("content://sms/");
			String message = String.valueOf(id);
			Log.v("getLastMessage", message);
			String[] selectionArgs = new String[] { message };
			// new String[] { "_id", "thread_id", "address", "person", "date",
			// "body", "type" }
			cur = getContentResolver().query(selectUri, null, "_id = ?",
					selectionArgs, null);
		} catch (Exception e) {
			// TODO: handle exception
			Utility.logger_D("getLastMessage--  " + e);

		}
		return cur;

	}

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
			Utility.logger_D("getCursorColumns--  " + e);
		}
	}

	private String getAddressNumber(int id) {

		String selectionAdd = new String("msg_id=" + id);
		Log.v("mms_addressNumber-1", selectionAdd);
		String uriStr = MessageFormat.format("content://mms/{0}/addr", id);
		Log.v("mms_addressNumber-2", uriStr);
		Uri uriAddress = Uri.parse(uriStr);
		Cursor cAdd = getContentResolver().query(uriAddress, null,
				selectionAdd, null, null);
		String name = null;

		try {

			if (cAdd.moveToFirst()) {
				do {
					String number = cAdd.getString(cAdd
							.getColumnIndex("address"));
					if (number != null) {
						try {
							Long.parseLong(number.replace("-", ""));
							name = number;
						} catch (NumberFormatException nfe) {
							if (name == null) {
								name = number;
							}
						}
					}
				} while (cAdd.moveToNext());
			}
			if (cAdd != null) {
				cAdd.close();
			}
		} catch (Exception e) {
			// TODO: handle exception
			Utility.logger_D("getAddressNumber--  " + e);
		}
		return name;
	}

	// 329
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
			Utility.logger_D("getContactName--  " + e);
		}
		return retval;
	}

	public void setMessageRead(long messageID) {
		try {

			ContentValues contentValues = new ContentValues();
			contentValues.put("READ", 1);
			String selection = null;
			String[] selectionArgs = null;
			Uri InsertUri = getContentResolver().insert(
					Uri.parse("content://sms/sent"), contentValues);

		} catch (Exception ex) {

			Utility.logger_D("setMessageRead-- " + ex);
		}
	}

	private String getContactName(Context context, String number) {

		// Log.v("ffnet", number);
		Utility.logger_D("getContactName-- ");

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
			Log.v("SS", "contactUri--- " + contactUri);
			Log.v("SS", "projection--- " + projection);

			// query time
			Cursor cursor = context.getContentResolver().query(contactUri,
					projection, null, null, null);
			Log.v("SS", "Cursor--- " + cursor);

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
			Utility.logger_D("getContactName- Exception- " + e);
		}
		return name;
	}

	private String getMmsText(String id) {
		Uri partURI = Uri.parse("content://mms/part/" + id);
		InputStream is = null;
		StringBuilder sb = new StringBuilder();
		try {
			is = getContentResolver().openInputStream(partURI);
			if (is != null) {
				InputStreamReader isr = new InputStreamReader(is, "UTF-8");
				BufferedReader reader = new BufferedReader(isr);
				String temp = reader.readLine();
				while (temp != null) {
					sb.append(temp);
					temp = reader.readLine();
				}
			}
		} catch (IOException e) {
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
				}
			}
		}
		return sb.toString();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		// TODO Auto-generated method stub
		super.onConfigurationChanged(newConfig);
		Utility.logger_D("ORIENTATION CHANGED  MAIN....");

	}

	public void setSmsMmsDataForLessThenJellyBean() {

		Cursor cursor;
		Cursor smsCursor, mmsCursor;
		contact_names.clear();
		messages.clear();
		type.clear();
		thread_ids.clear();
		message_ids.clear();
		dates.clear();

		Utility.logger_D("setSmsMmsDataForLessThenJellyBean-- ");


		try {

			cursor = getContentResolver().query(
					Uri.parse("content://mms-sms/conversations"), null, null,
					null, "normalized_date desc");
			startManagingCursor(cursor);
			getCursorColumns(cursor);
			if (cursor != null) {
				if (cursor.moveToFirst()) {

					do {
						String dateVal = cursor.getString(cursor
								.getColumnIndex("date"));
						Date date = new Date(Long.valueOf(dateVal));
						Log.v("Date Value", dateVal);
						Log.d("SS", " MY TEST ");
						String myString = DateFormat.getDateInstance().format(
								date);

						dates.add(myString);
						type.add(cursor.getString(cursor.getColumnIndex("read")));
						thread_ids.add(cursor.getString(cursor
								.getColumnIndex("thread_id")));

						message_ids.add(cursor.getString(cursor
								.getColumnIndex("_id")));
						// Is it a mms?
						String string = cursor.getString(cursor
								.getColumnIndex("ct_t"));
						if ("application/vnd.wap.multipart.related"
								.equals(string)) {

							String mmsId = cursor.getString(cursor
									.getColumnIndex("_id"));
							String selectionPart = "mid=" + mmsId;
							// get the text part of the mms
							Uri uri = Uri.parse("content://mms/part");
							Cursor mms = getContentResolver().query(uri, null,
									selectionPart, null, null);

							if (mms.moveToLast()) {
								do {
									String partId = mms.getString(mms
											.getColumnIndex("_id"));
									String type = mms.getString(mms
											.getColumnIndex("ct"));
									if ("text/plain".equals(type)) {
										String data = mms.getString(mms
												.getColumnIndex("_data"));
										String body;
										if (data != null) {
											// implementation of this method
											// below
											body = getMmsText(partId);
										} else {
											body = mms.getString(mms
													.getColumnIndex("text"));
										}

										messages.add(body);
									} else {
										messages.add("View with Android Texting App");
									}
								} while (mms.moveToNext());
							}

							Integer i = Integer.valueOf(mmsId);

							String number = getAddressNumber(i);

							String name = getContactName(this, number);

							if (name.equals("Contact Not Found")) {
								contact_names.add(number);
							} else {
								contact_names.add(name);
							}

						} else {
							messages.add(cursor.getString(cursor
									.getColumnIndex("body")));
							String number = (cursor.getString(cursor
									.getColumnIndex("address")));
							String name = getContactName(this, number);
							if (name.equals("Contact Not Found")) {
								contact_names.add(number);
							} else {
								contact_names.add(name);
							}

						}

					} while (cursor.moveToNext());
				}

			}
			setAdapter();
		} catch (NullPointerException e) {
			// TODO: handle exception
			 Toast.makeText(this, "Error in get conversations try block",
			 Toast.LENGTH_SHORT).show();
			try {

				Utility.logger_D("second try block-- ");

				smsCursor = getContentResolver().query(
						Uri.parse("content://sms"), null, null, null, null);
				mmsCursor = getContentResolver().query(
						Uri.parse("content://sms"), null, null, null, null);

				getSmsDataForJellyBean();
				setAdapter();

			} catch (Exception e2) {
				// TODO: handle exception
				Utility.logger_D("Second Exception-- " + e);
			}
		} catch (Exception e) {
			// TODO: handle exception
			Toast.makeText(this,
					"Problem to getting the data.Please try again.",
					Toast.LENGTH_SHORT).show();
		}

	}

	public void setAdapter() {

		Utility.logger_D("setAdapter-- ");

		myAdapter = new MultiConversationAdapter(this, contact_names, messages,
				type, dates); // contact_names, dates
		setListAdapter(myAdapter);
	}

	public void getSmsDataForJellyBean() {

		Utility.logger_D("getSmsDataForJellyBean-- ");

		try {

			Cursor cursor = getContentResolver().query(
					Uri.parse("content://sms"), null, null, null, null);

			if (cursor != null) {

				if (cursor.moveToFirst()) {

					do {

						String dateVal = cursor.getString(cursor
								.getColumnIndex("date"));
						Date date = new Date(Long.valueOf(dateVal));
						Utility.logger_E("Date Value-- " + dateVal);
						String myString = DateFormat.getDateInstance().format(
								date);
						dates.add(myString);

						type.add(cursor.getString(cursor.getColumnIndex("read")));

						thread_ids.add(cursor.getString(cursor
								.getColumnIndex("thread_id")));

						message_ids.add(cursor.getString(cursor
								.getColumnIndex("_id")));

						String otype = cursor.getString(cursor
								.getColumnIndex("type"));

						Utility.logger_E("SMS TYPE --- " + otype);

						if (otype != null) {
							// its sms

							messages.add(cursor.getString(cursor
									.getColumnIndex("body")));
							String number = (cursor.getString(cursor
									.getColumnIndex("address")));
							String name = getContactName(this, number);
							if (name.equals("Contact Not Found")) {
								contact_names.add(number);
							} else {
								contact_names.add(name);
							}

						} else {

							String mmsId = cursor.getString(cursor
									.getColumnIndex("_id"));
							String selectionPart = "mid=" + mmsId;
							// get the text part of the mms
							Uri uri = Uri.parse("content://mms/part");
							Cursor mms = getContentResolver().query(uri, null,
									selectionPart, null, null);

							if (mms.moveToLast()) {
								do {
									String partId = mms.getString(mms
											.getColumnIndex("_id"));
									String type = mms.getString(mms
											.getColumnIndex("ct"));
									if ("text/plain".equals(type)) {
										String data = mms.getString(mms
												.getColumnIndex("_data"));
										String body;
										if (data != null) {
											// implementation of this method
											// below
											body = getMmsText(partId);
										} else {
											body = mms.getString(mms
													.getColumnIndex("text"));
										}
										// Toast.makeText(this, body,
										// Toast.LENGTH_SHORT).show(); //not
										// firing as expected
										messages.add(body);
									} else {
										messages.add("View with Android Texting App");
									}
								} while (mms.moveToNext());
							}
							// get the senders address

							Integer i = Integer.valueOf(mmsId);

							String number = getAddressNumber(i);

							String name = getContactName(this, number);

							if (name.equals("Contact Not Found")) {
								contact_names.add(number);
							} else {
								contact_names.add(name);
							}

						}

					} while (cursor.moveToNext());
				}

				Utility.logger_E("SMS ID LIST LENGTH --- " + message_ids.size());

			}
			setAdapter();
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

}
