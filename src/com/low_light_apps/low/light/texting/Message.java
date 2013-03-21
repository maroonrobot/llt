package com.low_light_apps.low.light.texting;

public class Message {
	private final String address;
	private final String contact;
	private final String type;
	private final String message;
	
	public Message(String address, String contact, String message, String type){
		this.address = address;
		this.contact = contact;
		this.type = type;
		this.message = message;
		
	}

}

