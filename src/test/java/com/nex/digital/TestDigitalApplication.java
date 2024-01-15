package com.nex.digital;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.context.TestConfiguration;

@TestConfiguration(proxyBeanMethods = false)
public class TestDigitalApplication {

	public static void main(String[] args) {
		SpringApplication.from(DigitalApplication::main).with(TestDigitalApplication.class).run(args);
	}

}
