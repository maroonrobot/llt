package com.low_light_apps.low.light.texting;

import java.util.ArrayList;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class MessageArrayAdapter extends ArrayAdapter<Message> {

	private final Context context;
	private ArrayList<Message> messages = new ArrayList<Message>();
	//constructor
	public MessageArrayAdapter(Context context, ArrayList<Message> messages) {
		super(context, R.layout.conversation_row, messages);
		this.context = context;
		this.messages = messages;

	}

	 @Override
	  public View getView(int position, View convertView, ViewGroup parent) {
	    LayoutInflater inflater = (LayoutInflater) context
	        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	    View rowView = inflater.inflate(R.layout.conversation_row, parent, false);
	    TextView textView = (TextView) rowView.findViewById(R.id.label_1);
	    TextView textView2 = (TextView) rowView.findViewById(R.id.label_2);
	    TextView textView3 = (TextView) rowView.findViewById(R.id.label_3);
	    TextView textView4 = (TextView) rowView.findViewById(R.id.label_4);

	    Message message = messages.get(position);
	    Log.v("Message Class", message.message);
	    textView.setText(message.message);
	    // set the same value for the second textView
	    textView2.setText(message.address);
	    String str_type = message.type;
	    Log.v("type", str_type);
	    if(str_type.equals("1")){
	    textView3.setText("received");
	    }
	    else {
	    	textView3.setText("sent");
	    }
	    textView4.setText(message.contact);

	    return rowView;
	  }

}
