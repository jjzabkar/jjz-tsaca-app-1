package com.jjz.tsaca.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.codahale.metrics.annotation.Timed;
import com.jjz.tsaca.service.OneBusAwayApiStopService;

@Controller
@RequestMapping(value = "/csv", produces = MediaType.TEXT_PLAIN_VALUE)
public class ArduinoCsvResource {

	private final Logger log = LoggerFactory.getLogger(ArduinoCsvResource.class);

	@Autowired
	private OneBusAwayApiStopService obaStopService;

	/**
	 * WORKY: <code>curl http://localhost:8080/csv/hello</code>
	 */
	@RequestMapping(value = "/hello", method = RequestMethod.GET)
	@Timed
	@ResponseBody
	public String getHello() {
		log.info("/hello");
		return "abc,def\nghi,jkl";
	}
}
