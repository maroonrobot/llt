package com.low_light_apps.low.light.texting;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.util.Date;

import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.database.Cursor;
import android.util.Log;
import android.view.Menu;
import android.widget.Toast;

public class IsItMms extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_is_it_mms);
		 Cursor cursor = getContentResolver().query(Uri.parse("content://mms-sms/conversations"), null, null, null, "DATE DESC");
	       //Cursor cursor = getContentResolver().query(Uri.parse("content://sms"), null, null, null, "Date"); //shows all messages
	       // Cursor cursor = getContentResolver().query(Uri.parse("content://sms/conversations"), null, null, null, "Date"); //causes an error bc no _id column

	        Log.v("ISITMMS", "created cursor");
	        startManagingCursor(cursor);
//	        cursor.moveToFirst();
	       // getCursorColumns(cursor);
	        Log.v("ISITMMS", "StartedManagingCursor");
	        Log.v("ISITMMS cursor count is ", String.valueOf(cursor.getCount()));
	       // String[] from = new String[] {"person", "body", "read"};  // 1 = read 0 = unread
	        if (cursor != null) {
	            if (cursor.moveToFirst()) {
	                do {
	                	String string = cursor.getString(cursor.getColumnIndex("ct_t"));
	                	String number = (cursor.getString(cursor.getColumnIndex("address"))); //when its a mms the address is null
	                	if (number != null){
	                		Toast.makeText(this, number, Toast.LENGTH_SHORT).show();
	                		
	                	}
	                	//is it an mms?
	                    if ("application/vnd.wap.multipart.related".equals(string)) {
	                        // it's MMS
	                    	//Toast.makeText(this, string, Toast.LENGTH_SHORT).show();
	                    	
	                    	
	                    	String mmsId = cursor.getString(cursor.getColumnIndex("_id"));
	                   	 	String selectionPart = "mid=" + mmsId;
	                        Uri uri = Uri.parse("content://mms/part");
	                        Cursor mms = getContentResolver().query(uri, null,
	                            selectionPart, null, null);
	                        if (mms.moveToFirst()) {
	                            do {
	                            	String partId = mms.getString(mms.getColumnIndex("_id"));
	                                String type = mms.getString(mms.getColumnIndex("ct"));
	                                if ("text/plain".equals(type)) {
	                                    String data = mms.getString(mms.getColumnIndex("_data"));
	                                    String body;
	                                    if (data != null) {
	                                        // implementation of this method below
	                                    	body = getMmsText(partId);
//	                                    	body = "Method";
	                                    } else {
	                                        body = mms.getString(mms.getColumnIndex("text"));
	                                    }
	                                    Toast.makeText(this, body, Toast.LENGTH_SHORT).show();
	                                }
	                            } while (mms.moveToNext());
	                        }
	                    } else {
	                        // it's SMS
	                    }


	                } while (cursor.moveToNext());
	            }
		}
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
        } catch (IOException e) {}
        finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {}
            }
        }
        return sb.toString();
    }
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_is_it_mms, menu);
		return true;
	}
}
