package com.low_light_apps.low.light.texting;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.app.ListActivity;
import android.database.Cursor;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListAdapter;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;
import android.support.v4.app.NavUtils;

public class MmsConversation extends ListActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       // setContentView(R.layout.activity_mms_conversation);
        String[] selectionArgs = new String [] {"2"}; // on T's phone use 1
        Uri mmsUri = Uri.parse("content://mms/");
        Cursor mms_cur = getContentResolver().query(mmsUri, null, "thread_id = ?", selectionArgs, null);//works!!
        //Cursor mms_cur = getContentResolver().query(mmsUri, null, null, null, null);//all mms's
        Log.v("mms with selection", String.valueOf(mms_cur.getCount()));
        if (mms_cur != null) {
            if (mms_cur.moveToFirst()) {
                do {
                	//message = getMmsText(mms_cur.getString(mms_cur.getColumnIndex("_id")));
                	String mmsId = mms_cur.getString(mms_cur.getColumnIndex("_id"));
                	 String selectionPart = "mid=" + mmsId;
                     Uri uri = Uri.parse("content://mms/part");
                     Cursor cursor = getContentResolver().query(uri, null,
                         selectionPart, null, null);
                     if (cursor.moveToFirst()) {
                         do {
                             String partId = cursor.getString(cursor.getColumnIndex("_id"));
                             String type = cursor.getString(cursor.getColumnIndex("ct"));
                             if ("text/plain".equals(type)) {
                                 String data = cursor.getString(cursor.getColumnIndex("_data"));
                                 String body;
                                 if (data != null) {
                                     // implementation of this method below
                                     body = getMmsText(partId);
                                 } else {
                                     body = cursor.getString(cursor.getColumnIndex("text"));
                                 }
                                 Toast.makeText(this, body, Toast.LENGTH_SHORT).show();
                             }
                         } while (cursor.moveToNext());
                     }
           
              //  Log.v("message_id:  ", message);  
                } while (mms_cur.moveToNext());
            }
        }
        
//        String mmsId = "152";
//        String selectionPart = "mid=" + mmsId;
//        Uri uri = Uri.parse("content://mms/part");
//        Cursor cursor = getContentResolver().query(uri, null,
//            selectionPart, null, null);
//        if (cursor.moveToFirst()) {
//            do {
//                String partId = cursor.getString(cursor.getColumnIndex("_id"));
//                String type = cursor.getString(cursor.getColumnIndex("ct"));
//                if ("text/plain".equals(type)) {
//                    String data = cursor.getString(cursor.getColumnIndex("_data"));
//                    String body;
//                    if (data != null) {
//                        // implementation of this method below
//                        body = getMmsText(partId);
//                    } else {
//                        body = cursor.getString(cursor.getColumnIndex("text"));
//                    }
//                    Toast.makeText(this, body, Toast.LENGTH_SHORT).show();
//                }
//            } while (cursor.moveToNext());
//        }
        
        
       //mms_cur.getString(mms_cur.getColumnIndex("_id"))
        @SuppressWarnings("deprecation")
		ListAdapter adapter = new SimpleCursorAdapter(
                this, // Context.
                android.R.layout.simple_list_item_2,  // Specify the row template to use (here, two columns bound to the two retrieved cursor rows).
                mms_cur,                                              // Pass in the cursor to bind to.
                new String[] {"thread_id", "_id"},           // Array of cursor columns to bind to.
                new int[] {android.R.id.text1, android.R.id.text2});  // Parallel array of which template objects to bind to those columns.

        // Bind to our new adapter.
        setListAdapter(adapter);
       
    }
    
    private void getCursorColumns(Cursor cursor){
        if (cursor != null) {
            int num = cursor.getColumnCount();
            for (int i = 0; i < num; ++i) {
                String colname = cursor.getColumnName(i);
                Log.v("Column_Name:  ", colname);

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
        getMenuInflater().inflate(R.menu.activity_mms_conversation, menu);
        return true;
    }

    
}
