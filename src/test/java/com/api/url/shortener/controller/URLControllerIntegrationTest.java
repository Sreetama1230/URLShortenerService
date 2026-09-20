package com.api.url.shortener.controller;

import static org.junit.Assert.assertEquals;

import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.TestRestTemplate;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureTestRestTemplate;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import com.api.url.shortener.dto.URLRequest;
import com.api.url.shortener.dto.URLResponse;
import com.api.url.shortener.model.URLRecord;
import com.api.url.shortener.repo.URLRepository;

import tools.jackson.databind.ObjectMapper;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestRestTemplate
public class URLControllerIntegrationTest {

	@LocalServerPort
	private int port;

	@Autowired
	private TestRestTemplate testRestTemplate;

	@Autowired
	private URLRepository urlRepository;

	private HttpHeaders headers;

	private final ObjectMapper objectMapper = new ObjectMapper();

	URLRecord urlRecord = new URLRecord();
	URLResponse response = new URLResponse();;

	@BeforeEach
	public void setUp() {
		urlRepository.deleteAll();

		urlRecord.setCreateAt(LocalDateTime.now());
		urlRecord.setExpiryAt(urlRecord.getCreateAt().plusHours(1));
		urlRecord.setLongURL("https://web.whatsapp.com/");
		urlRecord.setShortCode("wp");

	}

	public String createURLWithPort() {
		return "http://localhost:" + port + "/v1/url";
	}

	@Test
	public void testURLCreateRecord() {
		headers = new HttpHeaders();

		headers.setContentType(MediaType.APPLICATION_JSON);

		URLRequest urlRequest = new URLRequest();
		urlRequest.setOriginal_url("https://web.whatsapp.com/");
		urlRequest.setCustom_alias("wp");

		HttpEntity<String> entity = new HttpEntity<>(objectMapper.writeValueAsString(urlRequest), headers);

		ResponseEntity<URLResponse> resp = testRestTemplate.exchange(createURLWithPort() + "/shorturl", HttpMethod.POST,
				entity, URLResponse.class);

		URLResponse response = resp.getBody();

		assertEquals("https://web.whatsapp.com/", response.getLongURL());
		assertEquals("http://localhost:8080/v1/url/wp", response.getShortURL());

	}

	@Test
	public void testRedirect() {

		URLRequest urlRequest = new URLRequest();
		urlRequest.setOriginal_url("https://web.whatsapp.com/");
		urlRequest.setCustom_alias("wp");

		urlRepository.save(urlRecord);

		ResponseEntity<Void> resp = testRestTemplate.exchange(createURLWithPort() + "/wp", HttpMethod.GET, null,
				Void.class);

		assertEquals(HttpStatus.OK, resp.getStatusCode());

	}

}
