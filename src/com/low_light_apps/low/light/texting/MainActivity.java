package com.low_light_apps.low.light.texting;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Date;

import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.Contacts.People;
import android.support.v4.widget.SimpleCursorAdapter;
import java.text.SimpleDateFormat;
import android.app.ListActivity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.TwoLineListItem;

public class MainActivity extends ListActivity {
    public final static String EXTRA_MESSAGE = "com.low_light_apps.low.light.texting.MESSAGE";
	private ArrayList<String> addresses = new ArrayList<String>();
	private ArrayList<String> contact_names = new ArrayList<String>();
	private ArrayList<String> messages =  new ArrayList<String>();
	private ArrayList<String> type =  new ArrayList<String>();
	private ArrayList<String> thread_ids = new ArrayList<String>();
	private ArrayList<String> message_ids = new ArrayList<String>();
	private ArrayList<String> dates = new ArrayList<String>();
	MultiConversationAdapter myAdapter;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
       Cursor cursor = getContentResolver().query(Uri.parse("content://mms-sms/conversations"), null, null, null, "DATE DESC");
       //Cursor cursor = getContentResolver().query(Uri.parse("content://sms"), null, null, null, "Date"); //shows all messages
       // Cursor cursor = getContentResolver().query(Uri.parse("content://sms/conversations"), null, null, null, "Date"); //causes an error bc no _id column

        Log.v("LLT", "created cursor");
        startManagingCursor(cursor);
//        cursor.moveToFirst();
       // getCursorColumns(cursor);
        Log.v("LLT", "StartedManagingCursor");
        Log.v("cursor count is ", String.valueOf(cursor.getCount()));
       // String[] from = new String[] {"person", "body", "read"};  // 1 = read 0 = unread
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do { 
                String dateVal = cursor.getString(cursor.getColumnIndex("date"));
                Date date = new Date(Long.valueOf(dateVal));
                //String myString = DateFormat.getDateInstance().format(date);
                String myString = DateFormat.getDateTimeInstance().format(date);
                dates.add(myString);
                type.add(cursor.getString(cursor.getColumnIndex("read")));
                thread_ids.add(cursor.getString(cursor.getColumnIndex("thread_id")));
                //Log.e("Thread_id", cursor.getString(cursor.getColumnIndex("thread_id")));
                message_ids.add(cursor.getString(cursor.getColumnIndex("_id")));
                //Is it a mms?
            	String string = cursor.getString(cursor.getColumnIndex("ct_t"));
                if ("application/vnd.wap.multipart.related".equals(string)) {
                	//Toast.makeText(this, "Its a mms", Toast.LENGTH_SHORT).show(); //works
                	String mmsId = cursor.getString(cursor.getColumnIndex("_id"));
                	String selectionPart = "mid=" + mmsId;
                	Uri uri = Uri.parse("content://mms/part");
                	Cursor mms = getContentResolver().query(uri, null, selectionPart, null, null);
                    
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
	                                } else {
	                                    body = mms.getString(mms.getColumnIndex("text"));
	                                }
	                                Toast.makeText(this, body, Toast.LENGTH_SHORT).show(); //not firing as expected
	                                //messages.add("body");
	                            }
	                        } while (cursor.moveToNext());
	                    }
	                    messages.add("I am an MMS - 2");
                    
                   // addresses.add(getAddressNumber(cursor.getColumnIndex("address")));
//                    String number = (cursor.getString(cursor.getColumnIndex("address")));
//                    String name = getContactName(this, number);
//                	if(name.equals("Contact Not Found") ){
//                		contact_names.add(number);
//                	}
//                	else {
//                		contact_names.add(name);
//                	}
                    contact_names.add("MMS Contact");
                }//if
                //its an sms
                else {
                	messages.add(cursor.getString(cursor.getColumnIndex("body")));
                	//addresses.add(cursor.getString(cursor.getColumnIndex("address")));
                	String number = (cursor.getString(cursor.getColumnIndex("address")));
                	String name = getContactName(this, number);
                	if(name.equals("Contact Not Found") ){
                		contact_names.add(number);
                	}
                	else {
                		contact_names.add(name);
                	}
//                	contact_names.add("SMS Contact");
                }

                } while (cursor.moveToNext());
            }

           
           // myAdapter = new MultiConversationAdapter(this, addresses, messages, type);
            Log.v("message_count", String.valueOf(messages.size()));
            Log.v("thread_count", String.valueOf(thread_ids.size()));
//            Toast.makeText(this,String.valueOf(messages.size()), Toast.LENGTH_SHORT).show();
//            Toast.makeText(this,String.valueOf(thread_ids.size()), Toast.LENGTH_SHORT).show();
//			
            myAdapter = new MultiConversationAdapter(this, contact_names, messages, type, dates); //contact_names, dates
            setListAdapter(myAdapter);
//            Toast.makeText(this, "Done with Loading ListView", Toast.LENGTH_SHORT).show();
       }
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
	        if(thread_id == null){
	        	return;
	        }
	        String message_read = type.get(position);
	        if(message_read.equals("0"))
	        {
	        	Log.v("message read is ", message_read);
	        	 setMessageRead(id);
	        }
//	        Log.v("id is  ", String.valueOf(id)); //id of the conversation
//	        Log.v("View", v.toString());
	        Intent intent = new Intent(this, Conversation.class);
//	        Cursor message_thread = getLastMessage(id);
//	        startManagingCursor(message_thread);
//	        Log.v("message_thread_count:  ", String.valueOf(message_thread.getCount()));
//	        if (message_thread != null) {
//	            if (message_thread.moveToFirst()) {
//	                do {
//	                thread_id = message_thread.getString(message_thread.getColumnIndex("thread_id"));
//	                Log.v("Thread_id:" , thread_id); 
//	                message = String.valueOf(message_thread.getColumnIndex("_id"));
//	                Log.v("message_id:  ", message);  
//	                } while (message_thread.moveToNext());
//	            }
//	        
//	        }
	        //String message = String.valueOf(message_thread.getColumnIndex("thread_id"));
	        Log.v("thread_id to send is:", thread_id);
	        intent.putExtra(EXTRA_MESSAGE, thread_id);
	        startActivity(intent);
//	        Toast.makeText(this, item + " Selected", Toast.LENGTH_LONG).show();
	      }
	 
	 private Cursor getLastMessage(long id){
		 Uri selectUri = Uri.parse("content://sms/");
	     String message = String.valueOf(id);
	     Log.v("getLastMessage", message);
	     String[] selectionArgs = new String [] {message};
//	      new String[] { "_id", "thread_id", "address", "person", "date",
//	              "body", "type" }
	      Cursor cur = getContentResolver().query(selectUri, null, "_id = ?", selectionArgs, null);
	      return cur;
	      
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
	 
	 private String getAddressNumber(int id) {
		    String selectionAdd = new String("msg_id=" + id);
		    String uriStr = MessageFormat.format("content://mms/{0}/addr", id);
		    Uri uriAddress = Uri.parse(uriStr);
		    Cursor cAdd = getContentResolver().query(uriAddress, null,
		        selectionAdd, null, null);
		    String name = null;
		    if (cAdd.moveToFirst()) {
		        do {
		            String number = cAdd.getString(cAdd.getColumnIndex("address"));
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
		    return name;
		}
	 //329
	 public String getContactName(String id){
		 String retval = "Contact not in DB";
		 String[] selectionArgs = new String [] {id};
		 Cursor mCursor = getContentResolver().query(ContactsContract.Contacts.CONTENT_URI, null, "_id = ?", selectionArgs, null);
		 Log.v("getContactName", "started");
		//getCursorColumns(mCursor);
		 startManagingCursor(mCursor);
		 Log.v("cursor", String.valueOf(mCursor.getCount()));
		 if(mCursor.getCount() > 0){
			 mCursor.moveToFirst();
			 retval = mCursor.getString(mCursor.getColumnIndex("display_name"));
//			 Toast.makeText(this, retval, Toast.LENGTH_SHORT).show();
//			 if(retval == null){
//				 retval = "no name to display";
//			 }
			 
			 
			 Log.v("disp_name", retval);
		
		 }
		 return retval;
	 }
	 
	 public void  setMessageRead(long messageID){
		    try{
		        
		        ContentValues contentValues = new ContentValues();
		            contentValues.put("READ", 1);
		        String selection = null;
		        String[] selectionArgs = null;          
                Uri InsertUri = getContentResolver().insert(Uri.parse("content://sms/sent"), contentValues);

		        
		    }catch(Exception ex){
		       
		    }
		}
	 private String getContactName(Context context, String number) {

		 Log.v("ffnet", "Started uploadcontactphoto...");

		 String name = null;
		 String contactId = null;
		 // define the columns I want the query to return
		 String[] projection = new String[] {
		         ContactsContract.PhoneLookup.DISPLAY_NAME,
		         ContactsContract.PhoneLookup._ID};

		 // encode the phone number and build the filter URI
		 Uri contactUri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(number));

		 // query time
		 Cursor cursor = context.getContentResolver().query(contactUri, projection, null, null, null);

		 if (cursor.moveToFirst()) {

		     // Get values from contacts database:
		     contactId = cursor.getString(cursor.getColumnIndex(ContactsContract.PhoneLookup._ID));
		     name =      cursor.getString(cursor.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME));

		     // Get photo of contactId as input stream:
		     Log.v("ffnet", "Started uploadcontactphoto: Contact Found @ " + number);            
		     Log.v("ffnet", "Started uploadcontactphoto: Contact name  = " + name);
		     Log.v("ffnet", "Started uploadcontactphoto: Contact id    = " + contactId);

		 } else {

		     Log.v("ffnet", "Started uploadcontactphoto: Contact Not Found @ " + number);
		     name = "Contact Not Found";

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
    
}
