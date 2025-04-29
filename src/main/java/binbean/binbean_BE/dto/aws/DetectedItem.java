package binbean.binbean_BE.dto.aws;

public class DetectedItem {
    private String key;
    private Integer value;
    private Float confidence;

    public void setKey(String key) {
        this.key = key;
    }

    public void setValue(Integer value) {
        this.value = value;
    }

    public void setConfidence(Float confidence) {
        this.confidence = confidence;
    }

    public String getKey() {
        return key;
    }

    public Integer getValue() {
        return value;
    }

    public Float getConfidence() {
        return confidence;
    }
}
