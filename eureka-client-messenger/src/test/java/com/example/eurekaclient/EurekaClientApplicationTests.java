package com.example.eurekaclient;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class EurekaClientApplicationTests {

	@BeforeAll
	static void setup() {
		System.setProperty("spring.config.import", "file:../.config/common.properties");
	}

	@Test
	void contextLoads() {
	}

}
