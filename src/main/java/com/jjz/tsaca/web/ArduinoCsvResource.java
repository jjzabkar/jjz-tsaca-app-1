package com.jjz.tsaca.web;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.codahale.metrics.annotation.Timed;
import com.jjz.tsaca.domain.ArrivalDeparture;
import com.jjz.tsaca.service.OneBusAwayApiArrivalsAndDeparturesService;

@Controller
@RequestMapping(value = "/csv", method = RequestMethod.GET, produces = MediaType.TEXT_PLAIN_VALUE)
public class ArduinoCsvResource {

	private final Logger log = LoggerFactory.getLogger(getClass());

	@Autowired
	private OneBusAwayApiArrivalsAndDeparturesService service;

	/**
	 * WORKY: <code>curl http://localhost:8080/csv/hello</code>
	 */
	@RequestMapping(value = "/hello")
	@Timed
	@ResponseBody
	public String getHello() {
		log.info("/hello");
		return "abc,def\nghi,jkl";
	}

	@RequestMapping(value = "/map", produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public Map<String, ArrivalDeparture> getUnsecuredMap() {
		return service.getArrivalsMap();
	}

	@RequestMapping(value = "/data")
	@Timed
	@ResponseBody
	public String getData() {
		return service.fetchArduinoCsvData();
	}

}
