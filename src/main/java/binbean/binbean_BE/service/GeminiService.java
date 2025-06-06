package binbean.binbean_BE.service;

import binbean.binbean_BE.integration.gemini.GeminiMultimodalClient;
import org.springframework.stereotype.Service;

@Service
public class GeminiService {

    private final GeminiMultimodalClient model;

    public GeminiService(GeminiMultimodalClient model) {
        this.model = model;
    }

    public String askGeminiWithImage(String prompt, String base64Image) {
        return model.call(prompt, base64Image);
    }
}
