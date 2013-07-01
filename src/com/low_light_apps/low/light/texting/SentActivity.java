package com.low_light_apps.low.light.texting;

import android.net.Uri;
import android.os.Bundle;
import android.app.ListActivity;
import android.database.Cursor;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.support.v4.app.NavUtils;

public class SentActivity extends LowLightListActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_sent);
    	super.onCreate(savedInstanceState);
        Cursor cursor = getContentResolver().query(Uri.parse("content://sms/sent"), null, null, null, null);
        startManagingCursor(cursor);
        String[] body;
        String[] number;
        body = new String[cursor.getCount()];
        number = new String[cursor.getCount()];
                        
        if(cursor.moveToFirst()){
                for(int i=0;i<cursor.getCount();i++){
                    body[i]= cursor.getString(cursor.getColumnIndexOrThrow("body")).toString();
                     number[i]=cursor.getString(cursor.getColumnIndexOrThrow("address")).toString();
                      cursor.moveToNext();
                }
       }
       cursor.close();

       ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
    			android.R.layout.simple_list_item_1, android.R.id.text1, body);
       setListAdapter(adapter);
	}
 

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_sent, menu);
        return true;
    }

    
}
