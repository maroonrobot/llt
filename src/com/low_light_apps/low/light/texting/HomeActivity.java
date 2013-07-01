package com.low_light_apps.low.light.texting;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;

public class HomeActivity extends LowlightActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_home);
    	super.onCreate(savedInstanceState);
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
