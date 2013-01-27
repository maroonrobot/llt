package com.low_light_apps.low.light.texting;

import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.Data;
import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.TwoLineListItem;
import android.support.v4.app.NavUtils;

public class ContactsActivity extends ListActivity {
	
	private static String ORDER_BY = ContactsContract.Contacts.DISPLAY_NAME;
    public final static String THREAD_ID = "com.low_light_apps.low.light.texting.THREAD_ID";
    public final static String REPLY_ADDRESS = "com.low_light_apps.low.light.texting.REPLY_ADDRESS";
    public final static String PERSON = "com.low_light_apps.low.light.texting.PERSON";



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       // setContentView(R.layout.activity_contacts);
        Cursor mCursor = getContentResolver().query(ContactsContract.Contacts.CONTENT_URI, null, null, null, ORDER_BY);
        startManagingCursor(mCursor);
        Log.v("LLT", ContactsContract.Contacts.CONTENT_URI.toString());
        Log.v("LLT", "StartedManagingCursor");
        Log.e("cursor count is ", String.valueOf(mCursor.getCount()));
        // Now create a new list adapter bound to the cursor.
        // SimpleListAdapter is designed for binding to a Cursor.
        @SuppressWarnings("deprecation")
		ListAdapter adapter = new SimpleCursorAdapter(
                this, // Context.
                android.R.layout.simple_list_item_2,  // Specify the row template to use (here, two columns bound to the two retrieved cursor rows).
                mCursor,                                              // Pass in the cursor to bind to.
                new String[] {ContactsContract.Contacts.DISPLAY_NAME, ContactsContract.Contacts._ID},           // Array of cursor columns to bind to.
                new int[] {android.R.id.text1, android.R.id.text2});  // Parallel array of which template objects to bind to those columns.

        // Bind to our new adapter.
        setListAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_contacts, menu);
        return true;
    }
    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
    	String item;
        Log.e("postion is ", String.valueOf(position));
        Log.v("id is  ", String.valueOf(id));
        Log.v("View", v.toString());
        String selection = l.getItemAtPosition(position).toString();//for testing
        TwoLineListItem list_item = (TwoLineListItem) v;
        TextView text1 = list_item.getText1();
        if (text1 != null){
        item = (String) text1.getText();
        }
        else {
        item = "text1 is null";
        }
        Log.v("LLT", "after_get_text");
        Toast.makeText(this, item + " Selected", Toast.LENGTH_SHORT).show();
        String thread_id = getConversations(String.valueOf(id));
        Intent intent = new Intent(this, Conversation.class);
        intent.putExtra(THREAD_ID, thread_id);
        if (thread_id == "0") {
        	//need to also get a reply number from the contact.
        	String reply_address = getMobileNumber(String.valueOf(id));
        	if(reply_address.length() < 4){
        		Toast.makeText(this, "No Mobile Number for this contact", Toast.LENGTH_SHORT).show();
        		return;
        	}
        	Log.v("Reply Address", reply_address);
        	intent.putExtra(REPLY_ADDRESS, reply_address);
        	intent.putExtra(PERSON, String.valueOf(id));
        }
        startActivity(intent);
        
      }
    
    private String getConversations(String person_id){
    	String thread_id = "0";
        String[] selectionArgs = new String [] {person_id};
    	Cursor cursor = getContentResolver().query(Uri.parse("content://sms/"), null, "person = ?", selectionArgs, "Date");
        Log.v("LLT", "created cursor");
        startManagingCursor(cursor);
        if (cursor.getCount() > 0){
        	cursor.moveToFirst();
        	Log.v("getConversations", "StartedManagingCursor");
        	Log.v("cursor count is ", String.valueOf(cursor.getCount()));
        	thread_id = cursor.getString(cursor.getColumnIndex("thread_id"));
        	Log.v("Thread ID", thread_id);
        	
        }
        return thread_id;
		
    }
    private  String getMobileNumber(String person_id){
    	Log.v("getMobileNumber", "started get mobile number");
    	String reply_to = "xxx";
    	final String[] projection = new String[] {
    			Phone.NUMBER,
    			Phone.TYPE,
    	};
    	final Cursor phone = managedQuery(
				Phone.CONTENT_URI,
				projection,
				Data.CONTACT_ID + "=?",
				new String[]{person_id},
				null);
        startManagingCursor(phone);
        Log.v("Phones", String.valueOf(phone.getCount()));
        if (phone != null) {
            if (phone.moveToFirst()) {
                do {
                	int phoneType = phone.getInt(phone.getColumnIndex(Phone.TYPE));
                    if (phoneType == Phone.TYPE_MOBILE)
                    {
                     String phoneNumber = phone.getString(phone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DATA));
                         reply_to = phoneNumber.replaceAll("\\s", "");
                         break;
                    }
//                phone.getString(phone.getColumnIndex("address")));
//                phone.getString(phone.getColumnIndex("body")));
              //  Log.v("message_id:  ", message);  
                } while (phone.moveToNext());
            }
        }
        	
    	//reply_to = "14802808913";
    	return reply_to;
    }

    
}
