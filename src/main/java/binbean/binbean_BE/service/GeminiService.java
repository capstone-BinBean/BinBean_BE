package binbean.binbean_BE.service;

import binbean.binbean_BE.gemini.GeminiProVisionModel;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class GeminiService {

    private final GeminiProVisionModel model;

    public GeminiService(GeminiProVisionModel model) {
        this.model = model;
    }

    public Mono<String> askGeminiWithImage(String question, String base64Image) {
        return model.call(question, base64Image);
    }
}
