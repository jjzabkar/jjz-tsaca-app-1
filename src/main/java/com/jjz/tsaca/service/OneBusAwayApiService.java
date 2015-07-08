package com.jjz.tsaca.service;

import javax.annotation.PostConstruct;

import org.apache.commons.lang.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class OneBusAwayApiService {

	public static final String OBA_API_KEY = "OBA_API_KEY";

	private final Logger log = LoggerFactory.getLogger(OneBusAwayApiService.class);

	private String apiKey;

	@PostConstruct
	public void postConstruct() {
		apiKey = (String) ObjectUtils.defaultIfNull(System.getenv(OBA_API_KEY), "TEST");
	}

	public final String getApiKey() {
		return this.apiKey;
	}

}
