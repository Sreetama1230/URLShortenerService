package com.api.url.shortener.dto;

import java.time.LocalDateTime;

import com.api.url.shortener.model.URLRecord;

public class URLRequest {

	private String original_url;
	private String custom_alias;

	public String getOriginal_url() {
		return original_url;
	}

	public void setOriginal_url(String original_url) {
		this.original_url = original_url;
	}

	public String getCustom_alias() {
		return custom_alias;
	}

	public void setCustom_alias(String custom_alias) {
		this.custom_alias = custom_alias;
	}



	public URLRequest(String original_url, String custom_alias) {
		super();
		this.original_url = original_url;
		this.custom_alias = custom_alias;
		
	}

	public URLRequest() {
		super();
	}

	public static URLRecord convertUrlRecord(URLRequest urlRequest) {
		URLRecord record = new URLRecord();
		record.setLongURL(urlRequest.getOriginal_url());
		record.setCreateAt(LocalDateTime.now());
		record.setExpiryAt(record.getCreateAt().plusHours(1)); // after 1hour it will get expires
		return record;
	}
}
