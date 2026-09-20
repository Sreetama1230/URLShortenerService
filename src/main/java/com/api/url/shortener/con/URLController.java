package com.api.url.shortener.con;

import java.net.URI;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.api.url.shortener.dto.URLRequest;
import com.api.url.shortener.dto.URLResponse;
import com.api.url.shortener.model.URLRecord;
import com.api.url.shortener.service.URLService;

@RestController
@RequestMapping("/v1/url")
public class URLController {

	@Autowired
	public URLService urlService;
	
	@PostMapping("/shorturl")
	public ResponseEntity<URLResponse> createRecord(@RequestBody URLRequest urlRequest) {
		return new ResponseEntity<URLResponse>( urlService.saveURLRecord(urlRequest) , HttpStatus.CREATED);
	}
	
	@GetMapping("/{shortCode}")
	public ResponseEntity<Void> redirect( @PathVariable String shortCode) {
		String originalURL = urlService.redirect(shortCode);
		return ResponseEntity.status(HttpStatus.FOUND).location(URI.create(originalURL)).build();
	}
}
