package prgrms.project.stuti;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import prgrms.project.stuti.global.cache.repository.BlackListTokenRepository;
import prgrms.project.stuti.global.cache.repository.RefreshTokenRepository;
import prgrms.project.stuti.global.cache.repository.TemporaryMemberRepository;

@SpringBootApplication
public class StutiApplication {

	public static void main(String[] args) {
		SpringApplication.run(StutiApplication.class, args);
	}

	@Bean
	CommandLineRunner run(
		RefreshTokenRepository refreshTokenRedisRepo,
		BlackListTokenRepository blackListTokenRedisRepo,
		TemporaryMemberRepository temporaryMemberRedisRepo) {
		return args -> {
			refreshTokenRedisRepo.deleteAll();
			blackListTokenRedisRepo.deleteAll();
			temporaryMemberRedisRepo.deleteAll();
		};
	}
}
