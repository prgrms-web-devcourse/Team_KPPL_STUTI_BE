package prgrms.project.stuti.global.security;

import java.util.HashMap;
import java.util.Map;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import prgrms.project.stuti.global.error.exception.MemberException;

@Builder(access = AccessLevel.PRIVATE)
@Getter
public class OAuth2Attribute {

	private Map<String, Object> attributes;
	private String attributeKey;
	private String email;
	private String name;
	private String picture;

	public static OAuth2Attribute of(String provider, String attributeKey, Map<String, Object> attributes) {
		return switch (provider) {
			case "google" -> ofGoogle(attributeKey, attributes);
			case "github" -> ofGithub(attributeKey, attributes);
			default -> throw MemberException.notValidAuthType(provider);
		};
	}

	private static OAuth2Attribute ofGithub(String attributeKey, Map<String, Object> attributes) {
		int id = (int)attributes.get("id");
		Object nullableEmail = attributes.get("email");
		String email = nullableEmail == null ? id + "@github.com" : (String)nullableEmail;

		return OAuth2Attribute.builder()
			.name((String)attributes.get("name"))
			.email(email)
			.picture((String)attributes.get("avatar_url"))
			.attributes(attributes)
			.attributeKey(attributeKey)
			.build();

	}

	private static OAuth2Attribute ofGoogle(String attributeKey, Map<String, Object> attributes) {
		return OAuth2Attribute.builder()
			.name((String) attributes.get("name"))
			.email((String) attributes.get("email"))
			.picture((String) attributes.get("picture"))
			.attributes(attributes)
			.attributeKey(attributeKey)
			.build();
	}

	public Map<String, Object> convertToMap() {
		Map<String, Object> map = new HashMap<>();
		map.put("id", attributeKey);
		map.put("key", attributeKey);
		map.put("name", name);
		map.put("email", email);
		map.put("picture", picture);

		return map;
	}

}
