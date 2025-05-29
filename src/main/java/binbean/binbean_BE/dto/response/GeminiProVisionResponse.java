package binbean.binbean_BE.dto.response;

import binbean.binbean_BE.entity.Favorites;
import java.util.List;
import lombok.Builder;

@Builder
public record GeminiProVisionResponse(
    List<Candidate> candidates,
    Content content
) {
    public static class Candidate {
        public Content content;
    }

    public static class Content {
        public List<Part> parts;
    }

    public static class Part {
        public String text;
    }

    public static GeminiProVisionResponse create(List<Candidate> candidates, Content content) {
        return GeminiProVisionResponse.builder()
            .candidates(candidates)
            .content(content)
            .build();
    }
}
