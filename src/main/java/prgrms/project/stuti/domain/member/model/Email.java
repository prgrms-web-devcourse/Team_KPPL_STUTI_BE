package prgrms.project.stuti.domain.member.model;

import static java.text.MessageFormat.*;
import static lombok.AccessLevel.*;

import java.util.regex.Pattern;

import javax.persistence.Embeddable;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Embeddable
@NoArgsConstructor(access = PROTECTED)
public class Email {

	private static final String EMAIL_REGEX = "[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?";
	private String address;

	public Email(String emailAddress) {
		String lowerCasedEmail = emailAddress.toLowerCase();
		validateEmailPattern(lowerCasedEmail);
		this.address = lowerCasedEmail;
	}

	private void validateEmailPattern(String email) {
		boolean matches = Pattern.matches(EMAIL_REGEX, email);
		if (!matches) {
			throw new IllegalArgumentException(
				format("입력값이 이메일 형식에 맞지 않습니다. inputEmail : {0}", email));
		}
	}

}
