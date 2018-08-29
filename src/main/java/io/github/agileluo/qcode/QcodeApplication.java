package io.github.agileluo.qcode;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ImportResource;

@ImportResource(locations = {"classpath:spring-context.xml"})
@SpringBootApplication
public class QcodeApplication {

	public static void main(String[] args) {
		SpringApplication.run(QcodeApplication.class, args);
	}
}
