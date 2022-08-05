package prgrms.project.stuti.domain.studygroup.controller;

import static org.springframework.http.HttpHeaders.*;
import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static prgrms.project.stuti.domain.studygroup.controller.CommonStudyGroupTestUtils.CommonField.*;

import org.springframework.http.HttpHeaders;
import org.springframework.restdocs.headers.HeaderDescriptor;
import org.springframework.restdocs.request.ParameterDescriptor;

public class CommonStudyGroupTestUtils {

	public static HeaderDescriptor contentType() {
		return headerWithName(HttpHeaders.CONTENT_TYPE).description("컨텐츠 타입");
	}

	public static HeaderDescriptor host() {
		return headerWithName(HttpHeaders.HOST).description("호스트");
	}

	public static ParameterDescriptor studyGroupIdPath() {
		return parameterWithName(STUDY_GROUP_ID.value()).description("스터디 그룹 아이디");
	}

	public static HeaderDescriptor contentLength() {
		return headerWithName(CONTENT_LENGTH).description("컨텐츠 길이");
	}

	public static HeaderDescriptor location() {
		return headerWithName(HttpHeaders.LOCATION).description("생성된 리소스 주소");
	}

	enum CommonField {
		TITLE("title"), TOPIC("topic"), IS_ONLINE("isOnline"),
		REGION("region"), PREFERRED_MBTIS("preferredMBTIs"), STUDY_GROUP_MEMBER_ID("studyGroupMemberId"),
		NUMBER_OF_RECRUITS("numberOfRecruits"), START_DATE_TIME("startDateTime"),
		END_DATE_TIME("endDateTime"), DESCRIPTION("description"), IMAGE_FILE("imageFile"),
		STUDY_GROUP_ID("studyGroupId"), IMAGE_URL("imageUrl"), NUMBER_OF_MEMBERS("numberOfMembers"),
		MEMBER_ID("memberId"), PROFILE_IMAGE_URL("profileImageUrl"), NICKNAME("nickname"), FIELD("field"),
		CAREER("career"), MBTI("mbti");

		private final String value;

		CommonField(String value) {
			this.value = value;
		}

		public String value() {
			return this.value;
		}
	}
}
