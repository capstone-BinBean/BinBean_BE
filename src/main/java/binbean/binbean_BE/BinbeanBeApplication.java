package binbean.binbean_BE;

import binbean.binbean_BE.config.properties.OpenAiChatProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
public class BinbeanBeApplication {

	public static void main(String[] args) {
		SpringApplication.run(BinbeanBeApplication.class, args);
	}

}
