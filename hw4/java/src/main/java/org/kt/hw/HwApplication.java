package org.kt.hw;

import lombok.extern.slf4j.Slf4j;
//import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
//import org.springframework.context.annotation.Bean;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;

@SpringBootApplication
@Slf4j
@ImportAutoConfiguration(classes = {})
public class HwApplication {

	public static void main(String[] args) {
		log.debug("Starting...");

		SpringApplication.run(HwApplication.class, args);

		int mb = 1024 * 1024;
		MemoryMXBean memoryBean = ManagementFactory.getMemoryMXBean();
		long xmx = memoryBean.getHeapMemoryUsage().getMax() / mb;
		long xms = memoryBean.getHeapMemoryUsage().getInit() / mb;
		log.info(String.format("Initial Memory (xms) : %dmb", xms));
		log.info(String.format("Max Memory (xmx) : %dmb", xmx));
		log.debug("с проверкой 500 ошибки на дашборде");
	}

	// http://localhost:8000/v3/api-docs
	// http://localhost:8000/swagger-ui.html
	// https://medium.com/@berktorun.dev/swagger-like-a-pro-with-spring-boot-3-and-java-17-49eed0ce1d2f
	// https://dinakaranramadurai.medium.com/migration-from-swagger-to-open-api-with-spring-boot-3-4a172db66e32

/*	@Bean
	GroupedOpenApi publicApi() {
		return GroupedOpenApi.builder()
				.group("public-apis")
				.pathsToMatch("/users/**")
				.build();
	}*/
}
