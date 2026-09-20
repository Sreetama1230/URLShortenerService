package com.api.url.shortener.service;

import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.api.url.shortener.dto.URLRequest;
import com.api.url.shortener.dto.URLResponse;
import com.api.url.shortener.exception.ResourceNotFound;
import com.api.url.shortener.exception.ShortCodeNotAvailable;
import com.api.url.shortener.exception.URLExpirationException;
import com.api.url.shortener.model.URLRecord;
import com.api.url.shortener.repo.URLRepository;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

@ExtendWith(MockitoExtension.class)
public class URLServiceTest {

	@Mock
	private URLRepository urlRepository;
	@Mock
	private RedisService redisService;

	@InjectMocks
	private URLService urlService;

	URLRecord urlRecord = new URLRecord();

	@BeforeEach
	public void setUp() {
		urlRecord.setCreateAt(LocalDateTime.now());
		urlRecord.setExpiryAt(urlRecord.getCreateAt().plusHours(1));
		urlRecord.setLongURL("https://web.whatsapp.com/");
		urlRecord.setShortCode("mywp");
	}

	@Test
	void testSaveURLRecord() {
		URLRequest urlRequest = new URLRequest();
		urlRequest.setOriginal_url("https://web.whatsapp.com/");

		when(urlRepository.save(any(URLRecord.class))).thenReturn(urlRecord);
		URLResponse urlResponse = urlService.saveURLRecord(urlRequest);
		assertEquals("https://web.whatsapp.com/", urlResponse.getLongURL());
	}

	@Test
	void testSaveURLRecord_FailureWithShortCodeNotAvailable() {

		URLRequest urlRequest = new URLRequest();
		urlRequest.setOriginal_url("https://web.whatsapp.com/");
		urlRequest.setCustom_alias("mywp");
		URLRecord record = new  URLRecord();
		when(urlRepository.findByShortCode("mywp")).thenReturn(Optional.of(record));
		assertThrows(ShortCodeNotAvailable.class, ()-> urlService.saveURLRecord(urlRequest));
		

	}
	
	@Test
	void testRedirect_ResourceNotFound() {

		when(urlRepository.findByShortCode("mywp")).thenReturn(Optional.empty());
		assertThrows(ResourceNotFound.class, ()-> urlService.redirect("mywp"));
		

	}
	
	@Test
	void testRedirect_URLExpirationException() {

		urlRecord.setExpiryAt(LocalDateTime.MIN);
		when(urlRepository.findByShortCode("mywp")).thenReturn(Optional.of(urlRecord));
		assertThrows(URLExpirationException.class, ()-> urlService.redirect("mywp"));
		

	}
	
}
