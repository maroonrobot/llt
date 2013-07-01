package com.low_light_apps.low.light.texting;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.MenuItem;
import android.support.v4.app.NavUtils;

public class MessageReceiver extends LowlightActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_message_receiver);
        super.onCreate(savedInstanceState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_message_receiver, menu);
        return true;
    }

    
}
