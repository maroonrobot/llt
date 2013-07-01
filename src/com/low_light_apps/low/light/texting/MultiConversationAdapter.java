package com.low_light_apps.low.light.texting;

import java.util.ArrayList;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class MultiConversationAdapter extends ArrayAdapter<String> {

	private final Context context;
	private final ArrayList<String> values;
	private final ArrayList<String> messages;
	private final ArrayList<String> type;
	private final ArrayList<String> date;

	// constructor
	public MultiConversationAdapter(Context context, ArrayList<String> values,
			ArrayList<String> messages, ArrayList<String> type,
			ArrayList<String> date) {
		super(context, R.layout.conversation_row, values);


		Log.v("SS", "ADAPTER CALLED..");
		this.context = context;
		this.values = values;
		this.messages = messages;
		this.type = type;
		this.date = date;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View rowView = inflater.inflate(R.layout.conversation_row, parent,
				false);
		TextView textView = (TextView) rowView.findViewById(R.id.label_1);
		TextView textView2 = (TextView) rowView.findViewById(R.id.label_2);
		TextView textView3 = (TextView) rowView.findViewById(R.id.label_3);
		TextView textView4 = (TextView) rowView.findViewById(R.id.label_4);
		String truncated = messages.get(position);
		if (truncated != null) {
			Log.v("truncated", truncated);
			if (truncated.length() > 30) {
				truncated = truncated.substring(0, 30);
			}
			textView.setText(truncated);
		}
		// set the same value for the second textView
		textView2.setText(values.get(position)); // examples have this setting a
													// imageview with logic.
		// textView3.setText(type.get(position));
		String str_type = type.get(position);
		Log.v("type", str_type);
		if (str_type.equals("1")) {
			textView3.setText("");
		} else {
			textView3.setText("NEW");
		}
		textView4.setText(date.get(position));

		return rowView;
	}

}
