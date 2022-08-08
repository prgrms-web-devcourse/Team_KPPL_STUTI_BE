package prgrms.project.stuti.domain.studygroup.controller;

import static org.springframework.http.HttpHeaders.*;
import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static prgrms.project.stuti.domain.studygroup.controller.StudyGroupTestUtils.CommonField.*;

import org.springframework.restdocs.headers.HeaderDescriptor;
import org.springframework.restdocs.request.ParameterDescriptor;

public class StudyGroupTestUtils {

	public static HeaderDescriptor contentType() {
		return headerWithName(CONTENT_TYPE).description("컨텐츠 타입");
	}

	public static HeaderDescriptor host() {
		return headerWithName(HOST).description("호스트");
	}

	public static ParameterDescriptor studyGroupIdPath() {
		return parameterWithName(STUDY_GROUP_ID.field()).description(STUDY_GROUP_ID.description());
	}

	public static HeaderDescriptor contentLength() {
		return headerWithName(CONTENT_LENGTH).description("컨텐츠 길이");
	}

	enum CommonField {
		TITLE("title", "스터디 제목"),
		TOPIC("topic", "스터디 주제"),
		IS_ONLINE("isOnline", "온라인 / 오프라인 여부"),
		REGION("region", "스터디 지역"),
		PREFERRED_MBTIS("preferredMBTIs", "선호하는 MBTI 목록"),
		NUMBER_OF_MEMBERS("numberOfMembers", "스터디 현재 인원수"),
		NUMBER_OF_RECRUITS("numberOfRecruits", "스터디 모집 인원수"),
		START_DATE_TIME("startDateTime", "스터디 시작일자"),
		END_DATE_TIME("endDateTime", "스터디 종료일자"),
		DESCRIPTION("description", "스터디 상세설명"),
		IMAGE_FILE("imageFile", "스터디 대표 이미지"),
		IMAGE_URL("imageUrl", "스터디 대표 이미지 url"),
		PROFILE_IMAGE_URL("profileImageUrl", "프로필 이미지 url"),
		NICKNAME("nickname", "닉네임"),
		FIELD("field", "개발분야"),
		CAREER("career", "개발경력"),
		MBTI("mbti", "MBTI"),
		STUDY_GROUP_MEMBER_ROLE("studyGroupMemberRole", "스터디 그룹 멤버 역할"),
		MEMBER_ID("memberId", "멤버 아이디"),
		STUDY_GROUP_MEMBER_ID("studyGroupMemberId", "스터디 그룹 멤버 아이디"),
		STUDY_GROUP_ID("studyGroupId", "스터디 그룹 아이디"),
		STUDY_GROUP_QUESTION_ID("studyGroupQuestionId", "스터디 그룹 문의댓글 아이디"),
		PAGE_CONTENTS("contents", "페이지 컨텐츠"),
		HAS_NEXT("hasNext", "다음 페이지 유무"),
		SIZE("size", "페이지 리스트 사이즈"),
		LAST_STUDY_GROUP_ID("lastStudyGroupId", "마지막으로 본 스터디 그룹 아이디"),
		LAST_STUDY_GROUP_QUESTION_ID("lastStudyGroupQuestionId", "마지막으로 본 스터디 그룹 문의댓글 아이디"),
		STUDY_MEMBERS("studyMembers[*]", "스터디 그룹 멤버 목록"),
		STUDY_APPLICANTS("studyApplicants[*]", "스터디 그룹 신청자 목록");

		private final String field;
		private final String description;

		CommonField(String field, String description) {
			this.field = field;
			this.description = description;
		}

		public String field() {
			return this.field;
		}

		public String description() {
			return this.description;
		}
	}
}
