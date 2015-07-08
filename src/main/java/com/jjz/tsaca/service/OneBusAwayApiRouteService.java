package com.jjz.tsaca.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

@Service
@Transactional
public class OneBusAwayApiRouteService {

	private final Logger log = LoggerFactory.getLogger(OneBusAwayApiRouteService.class);

	private RestTemplate restTemplate = new RestTemplate();

	public OneBusAwayApiRouteService() {
		// final String uri = "http://localhost:8080/springrestexample/employees/{id}";
		// http://api.pugetsound.onebusaway.org/api/where/stop/1_75403.json?key=TEST
		final String uri = "http://api.pugetsound.onebusaway.org/api/where/route/{routeId}.json?key={apiKey}";
	}

}
