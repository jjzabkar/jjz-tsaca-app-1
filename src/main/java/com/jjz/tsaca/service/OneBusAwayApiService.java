package com.jjz.tsaca.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.bind.RelaxedPropertyResolver;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

@Service
public class OneBusAwayApiService implements EnvironmentAware {

	public static final String OBA_PROPERTY_PREFIX = "onebusaway.";
	public static final String OBA_API_KEY = "apiKey";

	private final Logger log = LoggerFactory.getLogger(OneBusAwayApiService.class);

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

}
