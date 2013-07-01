package com.low_light_apps.low.light.texting;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

public class SMSReceiver extends BroadcastReceiver{

	public void onReceive(Context context, Intent intent) {
        // Parse the SMS.
        //Toast.makeText(context, "onReceive", Toast.LENGTH_SHORT).show();
        Bundle bundle = intent.getExtras();
        SmsMessage[] msgs = null;
        String str = "";
        if (bundle != null)
        {
            // Retrieve the SMS.
            Object[] pdus = (Object[]) bundle.get("pdus");
            msgs = new SmsMessage[pdus.length];
            for (int i=0; i<msgs.length; i++)
            {
                msgs[i] = SmsMessage.createFromPdu((byte[])pdus[i]);
                // In case of a particular App / Service.
                //if(msgs[i].getOriginatingAddress().equals("+91XXX"))
                //{
                str += "SMS from " + msgs[i].getOriginatingAddress();
                str += " :";
                str += msgs[i].getMessageBody().toString();
                str += "\n";
                //}
            }
            // Display the SMS as Toast.
            Toast.makeText(context, str, Toast.LENGTH_SHORT).show();
            Log.v("SMSReceiver", context + str);

           // A custom Intent that will used as another Broadcast
           Intent in = new Intent("SmsMessage.intent.MAIN").
           putExtra("get_msg", str);

           //You can place your check conditions here(on the SMS or the sender)            
           //and then send another broadcast 
           context.sendBroadcast(in);
  
            
        }
    }
}
