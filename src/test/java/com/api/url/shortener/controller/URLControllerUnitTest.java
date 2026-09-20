package com.api.url.shortener.controller;

import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.assertj.MockMvcTester.MockMvcRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.api.url.shortener.con.URLController;
import com.api.url.shortener.dto.URLRequest;
import com.api.url.shortener.dto.URLResponse;
import com.api.url.shortener.exception.ShortCodeNotAvailable;
import com.api.url.shortener.model.URLRecord;
import com.api.url.shortener.service.URLService;

import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

@WebMvcTest(URLController.class)
public class URLControllerUnitTest {

	@MockitoBean
	URLService urlService;

	@Autowired
	MockMvc mockMvc;

	@Autowired
	ObjectMapper objectMapper;

	URLRecord urlRecord = new URLRecord();
	URLResponse response = new URLResponse();;

	@BeforeEach
	public void setUp() {
		urlRecord.setCreateAt(LocalDateTime.now());
		urlRecord.setExpiryAt(urlRecord.getCreateAt().plusHours(1));
		urlRecord.setLongURL("https://web.whatsapp.com/");
		urlRecord.setShortCode("wp");

		response = URLResponse.convertToURLResponse(urlRecord);
	}

	@Test
	public void testCreateURLRecord() throws JacksonException, Exception {

		URLRequest urlRequest = new URLRequest();
		urlRequest.setOriginal_url("https://web.whatsapp.com/");
		urlRequest.setCustom_alias("wp");

		when(urlService.saveURLRecord(any(URLRequest.class))).thenReturn(response);

		mockMvc.perform(MockMvcRequestBuilders.post("/v1/url/shorturl")
				.content(objectMapper.writeValueAsString(urlRequest)).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isCreated()).andDo(print()).andDo(print())
				.andExpect(jsonPath("$.shortURL").value("http://localhost:8080/v1/url/wp"))
				.andExpect(jsonPath("$.longURL").value("https://web.whatsapp.com/"))

		;
	}

	@Test
	public void testCreateURLRecord_WithOoutCustomAlias() throws JacksonException, Exception {

		URLRequest urlRequest = new URLRequest();
		urlRequest.setOriginal_url("https://web.whatsapp.com/");

		when(urlService.saveURLRecord(any(URLRequest.class))).thenReturn(response);

		mockMvc.perform(MockMvcRequestBuilders.post("/v1/url/shorturl")
				.content(objectMapper.writeValueAsString(urlRequest)).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isCreated()).andDo(print()).andDo(print())
				.andExpect(jsonPath("$.shortURL").value("http://localhost:8080/v1/url/wp"))
				.andExpect(jsonPath("$.longURL").value("https://web.whatsapp.com/"))

		;
	}

	@Test
	public void testCreateURLRecord_FailureWithShortCodeNotAvailable() throws JacksonException, Exception {

		URLRequest urlRequest = new URLRequest();
		urlRequest.setOriginal_url("https://web.whatsapp.com/");
		urlRequest.setCustom_alias("wp");

		when(urlService.saveURLRecord(any(URLRequest.class)))
				.thenThrow(new ShortCodeNotAvailable("The short code is available"));

		mockMvc.perform(MockMvcRequestBuilders.post("/v1/url/shorturl")
				.content(objectMapper.writeValueAsString(urlRequest)).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isConflict()).andDo(print()).andDo(print())
				.andExpect(jsonPath("$.message").value("The short code is available"))
				.andExpect(jsonPath("$.statusCode").value("409 CONFLICT"))

		;
	}

	@Test
	public void testRedirect() throws JacksonException, Exception {

		when(urlService.redirect("wp")).thenReturn("https://web.whatsapp.com/");

		mockMvc.perform(MockMvcRequestBuilders.get("/v1/url/wp")

				.contentType(MediaType.APPLICATION_JSON)).andExpect(status().isFound()).andDo(print()).andDo(print())

		;
	}
}
