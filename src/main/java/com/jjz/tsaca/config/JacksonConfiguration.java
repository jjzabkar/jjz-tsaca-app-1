package com.jjz.tsaca.config;

import java.util.List;

import javax.xml.bind.annotation.XmlTransient;

import org.joda.time.DateTime;
import org.onebusaway.gtfs.model.Stop;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.datetime.joda.DateTimeFormatterFactory;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.joda.JodaModule;
import com.fasterxml.jackson.datatype.joda.ser.DateTimeSerializer;
import com.fasterxml.jackson.datatype.joda.ser.JacksonJodaFormat;

@Configuration
public class JacksonConfiguration {

	private final Logger log = LoggerFactory.getLogger(JacksonConfiguration.class);

	@Autowired
	private ObjectMapper objectMapper;

	@Bean
	public JodaModule jacksonJodaModule() {
		JodaModule module = new JodaModule();
		DateTimeFormatterFactory formatterFactory = new DateTimeFormatterFactory();
		formatterFactory.setIso(DateTimeFormat.ISO.DATE);
		module.addSerializer(DateTime.class, new DateTimeSerializer(new JacksonJodaFormat(formatterFactory.createDateTimeFormatter()
				.withZoneUTC())));
		return module;
	}

	@Bean
	public RestTemplate restTemplate() {
		final RestTemplate restTemplate = new RestTemplate();
		List<HttpMessageConverter<?>> converters = restTemplate.getMessageConverters();
		for (HttpMessageConverter<?> converter : converters) {
			if (converter instanceof MappingJackson2HttpMessageConverter) {
				ObjectMapper objectMapper = ((MappingJackson2HttpMessageConverter) converter).getObjectMapper();
				/* per: https://stackoverflow.com/questions/16652830/how-should-i-set-a-feature-on-a-spring-message-converters-object-mapper */
				objectMapper.registerModule(new MyIgnoreWheelChairBoardingMixInModule());
				log.info("registered mixin on RestTemplate converter={}", converter);
			}
		}
		return restTemplate;
	}

	public class MyIgnoreWheelChairBoardingMixInModule extends SimpleModule {

		private static final long serialVersionUID = 1725717440594615591L;

		@SuppressWarnings("deprecation")
		public MyIgnoreWheelChairBoardingMixInModule() {
			super("MyIgnoreWheelChairBoardingMixInModule", new Version(0, 0, 1, null));
		}

		@Override
		public void setupModule(SetupContext context) {
			context.setMixInAnnotations(Stop.class, IgnoreStopWheelChairBoardingMixIn.class);
			log.info("Configured Jackson2 MixIn to @JsonIgnore field 'Stop.wheelchairBoarding'");
		}
	}

	/** @see http://wiki.fasterxml.com/JacksonMixInAnnotations */
	abstract class IgnoreStopWheelChairBoardingMixIn {
		@XmlTransient
		@JsonIgnore
		public void setWheelchairBoarding(int wheelchairBoarding) {
		};
	}

}
