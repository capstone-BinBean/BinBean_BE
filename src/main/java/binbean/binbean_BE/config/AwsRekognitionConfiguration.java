package binbean.binbean_BE.config;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.rekognition.RekognitionClient;

@Configuration
public class AwsRekognitionConfiguration {

    @Value("${cloud.aws.credentials.access-key}")
    private String accessKey;

    @Value("${cloud.aws.credentials.secret-key}")
    private String secretKey;

    @Bean
    public RekognitionClient amazonRekognition() {
        AwsBasicCredentials credentials = AwsBasicCredentials.create(accessKey, secretKey);
        return RekognitionClient.builder()
            .credentialsProvider(StaticCredentialsProvider.create(credentials))
            .region(Region.AP_NORTHEAST_2)
            .build();
    }


}
