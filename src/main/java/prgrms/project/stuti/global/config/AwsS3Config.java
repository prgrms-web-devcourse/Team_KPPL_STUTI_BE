package prgrms.project.stuti.global.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.amazonaws.auth.InstanceProfileCredentialsProvider;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;

import lombok.Getter;

@Getter
@Configuration
public class AwsS3Config {

	@Value("cloud.aws.region.static")
	String region;

	@Bean
	public AmazonS3 amazonS3() {
		InstanceProfileCredentialsProvider provider =
			new InstanceProfileCredentialsProvider(true);

		return AmazonS3ClientBuilder.standard().withCredentials(provider).withRegion(region).build();
	}
}
