package binbean.binbean_BE.config.properties;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "spring.ai.openai")
public class OpenAiChatProperties {
    @Value("${spring.ai.openai.api-key}")
    private String apiKey;
    @Value("${spring.ai.openai.chat.base-url}")
    private String baseUrl;
    @Value("${spring.ai.openai.chat.completions-path}")
    private String path;
    @Value("${spring.ai.openai.chat.model}")
    private String model;

    public String getApiKey() {
        return apiKey;
    }

    public String getCompletionsPath() {
        return path;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public String getModel() {
        return model;
    }
}
