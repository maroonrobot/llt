package com.low_light_apps.low.light.texting;


import java.text.DateFormat;
import java.util.Date;

import android.os.Bundle;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import android.support.v4.app.NavUtils;
import android.telephony.SmsManager;

public class SendMessage extends Activity {
	private EditText messageNumber;
	private EditText messageBody;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_message);
        messageNumber = (EditText)findViewById(R.id.txtPhoneNo);
        messageBody = (EditText) findViewById(R.id.txtMessage);
        
        Date message_date = new Date();
        Date today = new Date();
        String todayString = DateFormat.getDateTimeInstance().format(today);
        //String myString = DateFormat.getDateInstance().format(date);
        String myString = DateFormat.getDateTimeInstance().format(message_date);
        if(myString.equals(todayString)){
        	Log.v("Equal", "Equal");
        }
        else{
        	Log.v("not equal", "not equal");
        }
        
  
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_send_message, menu);
        return true;
    }
    public void sendSMS(View v) {
    	String _messageNumber = messageNumber.getText().toString();
		String messageText = messageBody.getText().toString();
		String sent = "SMS_SENT";
        PendingIntent sentPI = PendingIntent.getBroadcast(this, 0,
            new Intent(sent), 0);
        //---when the SMS has been sent---
        registerReceiver(new BroadcastReceiver(){
            @Override
            public void onReceive(Context arg0, Intent arg1) {
            	if(getResultCode() == Activity.RESULT_OK)
                {
                  Toast.makeText(getBaseContext(), "SMS sent",
                                Toast.LENGTH_SHORT).show();
                }
                else
                {
                	Toast.makeText(getBaseContext(), "SMS could not sent",
                            Toast.LENGTH_SHORT).show();
                }
            }
        }, new IntentFilter(sent));
        SmsManager sms = SmsManager.getDefault();
        sms.sendTextMessage(_messageNumber, null, messageText, sentPI, null);
	}

    
}
