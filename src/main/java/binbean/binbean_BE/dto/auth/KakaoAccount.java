package binbean.binbean_BE.dto.auth;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class KakaoAccount {
    String email;
    @JsonProperty("profile")
    Profile profile;

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Profile {
        @JsonProperty("nickname")
        String nickname;
        @JsonProperty("thumbnail_image_url")
        String thumbnailImage;
    }
}
