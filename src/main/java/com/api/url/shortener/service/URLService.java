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
		// for each position 62 possibilities
		long maxValue = (62 * 62 * 62 * 62 * 62 * 62) - 1; // 62^6 - 1
		// 62_  62 _ 62 62 62 62 _ _ _ _

		if (number > maxValue) {
			throw new MaxLimitExceededException("Limit reached!");
		}

		// converting the long string
		StringBuilder result = new StringBuilder();
			// decimal -> binary
		 // 23/2 = 11 23%2 = 1
		//  11 / 2 = 5 11%2=1
		//.. 1 1
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
				throw new ShortCodeNotAvailable("The short code is not available");
			} else {

				urlRecord.setShortCode(urlRequest.getCustom_alias());
			}
		} else {
			// counter ->> redis - 1
			Long currentCounter = redisService.get("counter", Long.class);
			if (currentCounter == null) {
				redisService.set("counter", AppConstant.COUNTER);
				urlRecord.setShortCode(getShotURLCode(AppConstant.COUNTER));
			} else {
				// sending the counter after incrementation
				currentCounter = redisService.increment("counter");
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
				throw new URLExpirationException("shorturl is expired please create a new one");
			}
		}
		return record.get().getLongURL();
	}

}
