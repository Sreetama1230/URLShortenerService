package com.api.url.shortener.service;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.api.url.shortener.constants.AppConstant;
import com.api.url.shortener.dto.URLRequest;
import com.api.url.shortener.dto.URLResponse;
import com.api.url.shortener.exception.MaxLimitExceededException;
import com.api.url.shortener.exception.ResourceNotFound;
import com.api.url.shortener.exception.ShortCodeNotAvailable;
import com.api.url.shortener.exception.URLExpirationException;
import com.api.url.shortener.model.URLRecord;
import com.api.url.shortener.repo.URLRepository;

@Service
public class URLService {

	@Autowired
	private URLRepository urlRepository;
	@Autowired
	private RedisService redisService;
	private static final String BASE62 = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

	public String getShotURLCode(Long number) {

//		if(number > Math.pow(62, 6)) {
//			throw new MaxLimitExceededException("Limit reached!");
//		}

		long maxValue = 62L * 62 * 62 * 62 * 62 * 62 - 1; // 62^6 - 1 

		if (number < 0 || number > maxValue) {
			throw new MaxLimitExceededException("Limit reached!");
		}

		// converting the long string
		StringBuilder result = new StringBuilder();

		while (number > 0) {
			int remainder = (int) (number % 62);
			result.append(BASE62.charAt(remainder));
			number = number / 62;
		}
		return result.reverse().toString();

	}

	public URLResponse saveURLRecord(URLRequest urlRequest) {

		URLRecord urlRecord = URLRequest.convertUrlRecord(urlRequest);

		if (urlRequest.getCustom_alias() != null) {
			if (urlRepository.findByShortCode(urlRequest.getCustom_alias()).isPresent()) {
				throw new ShortCodeNotAvailable("The short code is available");
			} else {

				urlRecord.setShortCode(urlRequest.getCustom_alias());
			}
		} else {
			Long currentCounter = redisService.get("counter", Long.class);
			if (currentCounter == null) {
				redisService.set("counter", AppConstant.COUNTER);
				urlRecord.setShortCode(getShotURLCode(AppConstant.COUNTER));
			} else {
				currentCounter = redisService.increment("counter");
//				redisService.set("counter", currentCounter);
				urlRecord.setShortCode(getShotURLCode(currentCounter));
			}

		}

		return URLResponse.convertToURLResponse(urlRepository.save(urlRecord));

	}

	public String redirect(String shortCode) {
		Optional<URLRecord> record = urlRepository.findByShortCode(shortCode);
		if (record.isEmpty()) {
			throw new ResourceNotFound("No record found!");
		} else {
			if (record.get().getExpiryAt().isBefore(LocalDateTime.now())) {
				throw new URLExpirationException("shorturl is expired please create new one");
			}
		}
		return record.get().getLongURL();
	}

}
