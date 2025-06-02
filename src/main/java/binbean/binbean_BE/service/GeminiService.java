package binbean.binbean_BE.service;

import binbean.binbean_BE.gemini.GeminiProVisionModel;
import org.springframework.stereotype.Service;

@Service
public class GeminiService {

    private final GeminiProVisionModel model;

    public GeminiService(GeminiProVisionModel model) {
        this.model = model;
    }

    public String askGeminiWithImage(String prompt, String base64Image) {
        return model.call(prompt, base64Image);
    }
}
