package com.jjz.tsaca.service;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

@Service
@Transactional
public class OneBusAwayApiStopService {

	private final Logger log = LoggerFactory.getLogger(OneBusAwayApiStopService.class);

	private RestTemplate restTemplate = new RestTemplate();

	@Autowired
	private OneBusAwayApiService obaApiService;

	@SuppressWarnings("rawtypes")
	public Map getTestStop() {
		final String uri = "http://api.pugetsound.onebusaway.org/api/where/stop/{stopId}.json?key={apiKey}";

		Map<String, String> params = new HashMap<String, String>();
		params.put("stopId", "1_75403");
		params.put("apiKey", obaApiService.getApiKey());

		log.info("query params={}", params);
		Map result = restTemplate.getForObject(uri, Map.class, params);
		log.info("result={}", result);
		return result;
	}

}
