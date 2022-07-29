package prgrms.project.stuti;

import java.util.Calendar;
import java.util.Date;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import prgrms.project.stuti.domain.member.model.MemberRole;
import prgrms.project.stuti.global.cache.model.RefreshToken;
import prgrms.project.stuti.global.cache.service.RefreshTokenService;
import prgrms.project.stuti.global.token.TokenGenerator;
import prgrms.project.stuti.global.token.TokenService;
import prgrms.project.stuti.global.token.Tokens;

@Component
@RequiredArgsConstructor
public class DbInit {
	private final TokenGenerator tokenGenerator;
	private final RefreshTokenService refreshTokenService;
	private final TokenService tokenService;

	@PostConstruct
	private void init(){
		Tokens tokens = tokenGenerator.generateTokens(String.valueOf(1), MemberRole.ROLE_MEMBER.name());
		Date date = getDate(2022, 7, 30);
		System.out.println(date);
		refreshTokenService.save(RefreshToken.builder()
			.accessTokenValue(tokens.accessToken())
			.refreshTokenValue(tokens.refreshToken())
			.memberId(1L)
			.createdTime(date)
			.expirationTime(new Date(date.getTime() + tokenService.getRefreshPeriod()))
			.expiration(tokenService.getRefreshPeriod())
			.build());
		System.out.println("====================================");
		System.out.println("accessToken");
		System.out.println(tokens.accessToken());
		System.out.println("====================================");
	}

	private Date getDate(int year, int month, int date){
		Calendar cal = Calendar.getInstance();
		cal.set(year, month-1, date);
		return new Date(cal.getTimeInMillis());
	}
}
