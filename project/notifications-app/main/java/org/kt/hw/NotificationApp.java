package org.kt.hw;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.kt.hw.service.KafkaService;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.util.Properties;

@SpringBootApplication
@Slf4j
@RequiredArgsConstructor
public class NotificationApp {
	private final KafkaService kafkaService;

	public static void main(String[] args) throws IOException {
		log.debug("Starting...");

		String port = System.getenv("APP_PORT");
		if (port != null && !port.isEmpty()) {
			System.setProperty("server.port", port);
		} else {
			Properties prop = new Properties();
			prop.load(Thread.currentThread().getContextClassLoader().getResourceAsStream("application.properties"));
			port = prop.getProperty("server.port");
		}

		SpringApplication.run(NotificationApp.class, args);

		int mb = 1024 * 1024;
		MemoryMXBean memoryBean = ManagementFactory.getMemoryMXBean();
		long xmx = memoryBean.getHeapMemoryUsage().getMax() / mb;
		long xms = memoryBean.getHeapMemoryUsage().getInit() / mb;
		log.info(String.format("Initial Memory (xms) : %dmb", xms));
		log.info(String.format("Max Memory (xmx) : %dmb", xmx));
		//Properties prop = new Properties();
		//prop.load(Thread.currentThread().getContextClassLoader().getResourceAsStream("application.properties"));
		//String port = prop.getProperty("server.port");
		log.debug("Notification App started on " + port + " port");
		//log.debug("с проверкой 500 ошибки на дашборде");
	}

	// http://localhost:8083/v3/api-docs
	// http://localhost:8083/swagger-ui.html
	// https://medium.com/@berktorun.dev/swagger-like-a-pro-with-spring-boot-3-and-java-17-49eed0ce1d2f
	// https://dinakaranramadurai.medium.com/migration-from-swagger-to-open-api-with-spring-boot-3-4a172db66e32

	@Bean
	GroupedOpenApi publicApi() {
		return GroupedOpenApi.builder()
				.group("public-apis")
				.pathsToMatch("/**")
				.build();
	}

/*	@KafkaListener(id="id", topics = "notifications")
	public void listen(String message) {
		log.debug("Received Message in group foo: " + message);
		kafkaService.listen(message);
	}
*/
}
