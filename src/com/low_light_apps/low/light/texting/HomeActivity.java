package com.low_light_apps.low.light.texting;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.support.v4.app.NavUtils;

public class HomeActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_home, menu);
        return true;
    }
    
    public void startActivityContact(View v) {
        Intent intent = new Intent(HomeActivity.this, ContactsActivity.class);
        startActivity(intent);
    }
    public void startActivityCompose(View v) {
        Intent intent = new Intent(HomeActivity.this, SendMessage.class);
        startActivity(intent);
    }
    public void startActivityConversations(View v) {
        Intent intent = new Intent(HomeActivity.this, MainActivity.class);
        startActivity(intent);
    }
    public void startActivitySent(View v){
    	Intent intent = new Intent(HomeActivity.this, SentActivity.class);
    	startActivity(intent);
    }
    public void startActivityConversation(View v){
    	Intent intent = new Intent(HomeActivity.this, Conversation.class);
    	startActivity(intent);
    }
    
    public void startMmsConversation(View v){
    	Intent intent = new Intent(this, MmsConversation.class);
    	startActivity(intent);
    }
    
    public void startIsItMms(View v){
    	Intent intent = new Intent(this, IsItMms.class);
    	startActivity(intent);
    }
}
