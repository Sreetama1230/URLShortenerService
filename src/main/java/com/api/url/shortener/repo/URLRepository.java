package com.api.url.shortener.repo;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.api.url.shortener.model.URLRecord;

@Repository
public interface URLRepository  extends JpaRepository<URLRecord, Long>{

	Optional<URLRecord> findByShortCode(String shortURL);
}
