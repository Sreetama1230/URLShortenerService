package com.api.url.shortener.dto;

import java.time.LocalDateTime;

import com.api.url.shortener.model.URLRecord;

public class URLResponse {

	private String longURL;
	private String shortURL;
	private LocalDateTime expires_at;

	public String getLongURL() {
		return longURL;
	}

	public void setLongURL(String longURL) {
		this.longURL = longURL;
	}

	public String getShortURL() {
		return shortURL;
	}

	public void setShortURL(String shortURL) {
		this.shortURL = shortURL;
	}

	public LocalDateTime getExpires_at() {
		return expires_at;
	}

	public void setExpires_at(LocalDateTime expires_at) {
		this.expires_at = expires_at;
	}

	public URLResponse(String longURL, String shortURL, LocalDateTime expires_at) {
		super();
		this.longURL = longURL;
		this.shortURL = shortURL;
		this.expires_at = expires_at;
	}

	public URLResponse() {
		super();
	}
	
	public static URLResponse convertToURLResponse(URLRecord urlRecord) {
		return new  URLResponse (urlRecord.getLongURL() , "http://localhost:8080/v1/url/"+urlRecord.getShortCode(),urlRecord.getExpiryAt());
	}

}
