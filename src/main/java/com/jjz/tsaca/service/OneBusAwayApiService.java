package com.jjz.tsaca.service;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.web.client.RestTemplate;

@Service
public class OneBusAwayApiService implements EnvironmentAware {

	public static final String OBA_PROPERTY_PREFIX = "onebusaway.";
	public static final String OBA_API_KEY = "OBA_API_KEY";

	private final Logger log = LoggerFactory.getLogger(OneBusAwayApiService.class);

	@Inject
	private RestTemplate restTemplate;

	private String apiKey;

	@Override
	public void setEnvironment(Environment env) {
		this.apiKey = env.getProperty(OBA_API_KEY);
		Assert.notNull("Expected environment variable: " + OBA_API_KEY, this.apiKey);
		log.info("Created OneBusAwayApiService({})", this.apiKey);
	}

	public final String getApiKey() {
		return this.apiKey;
	}

	public <T> T getForObject(String url, Class<T> responseType, Map<String, ?> urlVariables) {
		final Map<String, Object> myMap = new HashMap<>(urlVariables);
		myMap.put("apiKey", apiKey);
		return restTemplate.getForObject(url, responseType, myMap);
	}

}
