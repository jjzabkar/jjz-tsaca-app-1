package com.jjz.tsaca.service;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.bind.RelaxedPropertyResolver;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class OneBusAwayApiService implements EnvironmentAware {

	public static final String OBA_PROPERTY_PREFIX = "onebusaway.";
	public static final String OBA_API_KEY = "apiKey";

	private final Logger log = LoggerFactory.getLogger(OneBusAwayApiService.class);

	private RestTemplate restTemplate = new RestTemplate();

	private String apiKey;

	@Override
	public void setEnvironment(Environment env) {
		RelaxedPropertyResolver propertyResolver = new RelaxedPropertyResolver(env, OBA_PROPERTY_PREFIX);
		this.apiKey = propertyResolver.getProperty(OBA_API_KEY);
		log.info("Created OneBusAwayApiService({})", this.apiKey);
	}

	public final String getApiKey() {
		return this.apiKey;
	}

	public <T> T getForObject(String url, Class<T> responseType, Map<String, ?> urlVariables) {
		final Map<String, Object> myMap = new HashMap<>(urlVariables);
		myMap.put(OBA_API_KEY, apiKey);
		return restTemplate.getForObject(url, responseType, myMap);
	}

}
