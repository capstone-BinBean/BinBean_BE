package binbean.binbean_BE.dto.auth.response;


import binbean.binbean_BE.dto.auth.KakaoAccount;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true) // JSON에 정의되지 않은 필드 무시
public record KakaoResponse(
    String id,
    @JsonProperty("kakao_account")
    KakaoAccount kakaoAcount
) {

}
