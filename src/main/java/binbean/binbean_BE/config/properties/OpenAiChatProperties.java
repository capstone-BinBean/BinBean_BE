package binbean.binbean_BE.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "spring.ai.openai")
public class OpenAiChatProperties {
    private String apiKey;
    private String chatBaseUrl;

//    public OpenAiChatProperties(String apiKey, Chat chat) {
//        this.apiKey = apiKey;
//        this.chat = chat;
//    }
//
//    public String getApiKey() {
//        return apiKey;
//    }
//
//    public Chat getChat() {
//        return chat;
//    }
//
//    public static class Chat {
//        private final String completionsPath;
//        private final String baseUrl;
//
//        public Chat(String completionsPath, String baseUrl) {
//            this.completionsPath = completionsPath;
//            this.baseUrl = baseUrl;
//        }
//
//        public String getCompletionsPath() {
//            return completionsPath;
//        }
//
//        public String getBaseUrl() {
//            return baseUrl;
//        }
//    }
    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public String getChatBaseUrl() {
        return chatBaseUrl;
    }

    public void setChatBaseUrl(String chatBaseUrl) {
        this.chatBaseUrl = chatBaseUrl;
    }
}
