package com.api.url.shortener.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class URLRecord {

	

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column( nullable = false)
	private String longURL;
	@Column(unique = true, nullable = false)
	private String shortCode;
	@Column
	private LocalDateTime createAt;
	@Column
	private LocalDateTime expiryAt;
	

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	public String getLongURL() {
		return longURL;
	}

	public void setLongURL(String longURL) {
		this.longURL = longURL;
	}

	

	public LocalDateTime getCreateAt() {
		return createAt;
	}

	public void setCreateAt(LocalDateTime createAt) {
		this.createAt = createAt;
	}

	public LocalDateTime getExpiryAt() {
		return expiryAt;
	}

	public void setExpiryAt(LocalDateTime expiryAt) {
		this.expiryAt = expiryAt;
	}


	public URLRecord() {
		super();
		// TODO Auto-generated constructor stub
	}

	public String getShortCode() {
		return shortCode;
	}

	public void setShortCode(String shortCode) {
		this.shortCode = shortCode;
	}

	public URLRecord(String longURL, String shortCode, LocalDateTime createAt, LocalDateTime expiryAt) {
		super();
		this.longURL = longURL;
		this.shortCode = shortCode;
		this.createAt = createAt;
		this.expiryAt = expiryAt;
	}

	

}
