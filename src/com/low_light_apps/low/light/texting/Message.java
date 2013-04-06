package com.low_light_apps.low.light.texting;

import java.util.Date;

public class Message {
	String address;
	String contact;
	String type;
	String message;
	Date message_date;
	
	public Message(String date, String contact, String message, String type, Date message_date){
		this.address = date;
		this.contact = contact;
		this.type = type;
		this.message = message;
		this.message_date = message_date;
		
	}

}

