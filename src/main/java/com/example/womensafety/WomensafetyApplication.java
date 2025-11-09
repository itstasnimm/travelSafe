package com.example.womensafety;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class WomensafetyApplication {

	public static void main(String[] args) {
		SpringApplication.run(WomensafetyApplication.class, args);
	}

}



/*
mvn spring-boot:run

tracking link

button on click---> controller --->service --->return table---> 2 nearest hospital + 2 nearest police station with their distance

service?
send current address + nearest public place using open street api

 */
