package com.employee.workwave;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@SpringBootApplication
public class WorkWaveApplication {

	private static final Logger logger = LogManager.getLogger(WorkWaveApplication.class);

	public static void main(String[] args) {
		System.setProperty("java.util.logging.manager", "org.apache.logging.log4j.jul.LogManager");
		SpringApplication.run(WorkWaveApplication.class, args);
		logger.info("Application has successfully loaded");
	}

}
