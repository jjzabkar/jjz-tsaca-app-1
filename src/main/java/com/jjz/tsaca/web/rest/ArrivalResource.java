package com.jjz.tsaca.web.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.codahale.metrics.annotation.Timed;
import com.jjz.tsaca.domain.Arrival;
import com.jjz.tsaca.service.OneBusAwayApiArrivalsAndDeparturesService;

/**
 * REST controller for managing Arrival.
 */
@RestController
@RequestMapping("/arrivals")
public class ArrivalResource {

	private final Logger log = LoggerFactory.getLogger(getClass());

	@Autowired
	private OneBusAwayApiArrivalsAndDeparturesService service;

	@RequestMapping(value = "/csv", method = RequestMethod.GET, produces = MediaType.TEXT_PLAIN_VALUE)
	@ResponseBody
	@Timed
	public Object getAll() {
		log.debug("REST request to get all Arrivals");
		return service.fetchArduinoCsvData();
	}

	@RequestMapping(value = "/json", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	@Timed
	public Arrival getUnsecuredArrival() {
		return service.getArrival();
	}

	String colors[] = { "red", "yellow", "green", "purple", "white", "off" };
	int colorIndex = 0;

	/**
	 * <code>curl http://localhost:8080/arrivals/test</code>
	 */
	@RequestMapping(value = "/test")
	@Timed
	@ResponseBody
	public String getTest() {
		StringBuffer sb = new StringBuffer();
		sb.append("STATION,abc_123,");
		sb.append(colors[colorIndex % colors.length]);
		sb.append(",");
		colorIndex++;
		sb.append(colors[colorIndex % colors.length]);
		sb.append(",");
		sb.append("\n");
		return sb.toString();
	}

}
