package com.treatsboot;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class TreatDispenserApplication
{
	public static void main(String[] args) {
		SpringApplication.run(TreatDispenserApplication.class, args);
	}

	@Bean
	public ObjectMapper objectMapper()
	{
		ObjectMapper mapper = new ObjectMapper();
		mapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
		return mapper;
	}

	@Bean
	public GpioController gpioController()
	{
		return GpioFactory.getInstance();
	}
}
