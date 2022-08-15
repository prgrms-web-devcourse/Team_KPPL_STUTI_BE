package prgrms.project.stuti.domain.studygroup.service;

import static org.assertj.core.api.Assertions.*;
import static prgrms.project.stuti.domain.studygroup.model.StudyGroupMemberRole.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import prgrms.project.stuti.config.ServiceTestConfig;
import prgrms.project.stuti.domain.member.model.Career;
import prgrms.project.stuti.domain.member.model.Field;
import prgrms.project.stuti.domain.member.model.Mbti;
import prgrms.project.stuti.domain.member.model.Member;
import prgrms.project.stuti.domain.member.model.MemberRole;
import prgrms.project.stuti.domain.studygroup.model.PreferredMbti;
import prgrms.project.stuti.domain.studygroup.model.Region;
import prgrms.project.stuti.domain.studygroup.model.StudyGroup;
import prgrms.project.stuti.domain.studygroup.model.StudyGroupMember;
import prgrms.project.stuti.domain.studygroup.model.StudyPeriod;
import prgrms.project.stuti.domain.studygroup.model.Topic;
import prgrms.project.stuti.domain.studygroup.repository.studygroup.StudyGroupRepository;
import prgrms.project.stuti.domain.studygroup.repository.studymember.StudyGroupMemberRepository;
import prgrms.project.stuti.domain.studygroup.service.dto.StudyGroupDto;
import prgrms.project.stuti.domain.studygroup.service.response.StudyGroupDetailResponse;
import prgrms.project.stuti.domain.studygroup.service.response.StudyGroupIdResponse;
import prgrms.project.stuti.domain.studygroup.service.response.StudyGroupsResponse;
import prgrms.project.stuti.global.error.exception.MemberException;
import prgrms.project.stuti.global.error.exception.StudyGroupException;
import prgrms.project.stuti.global.page.CursorPageResponse;

class StudyGroupServiceTest extends ServiceTestConfig {

	@Autowired
	private StudyGroupService studyGroupService;

	@Autowired
	private StudyGroupRepository studyGroupRepository;

	@Autowired
	private StudyGroupMemberRepository studyGroupMemberRepository;

	private final String errorMessageOfNotLeader = "스터디 그룹의 리더가 아닙니다";

	private final Long unknownId = -1L;

	@Nested
	@DisplayName("스터디 그룹 생성 기능은")
	class StudyGroupCreateTest {

		@Test
		@DisplayName("정상적인 멤버가 생성을 시도하면 생성에 성공한다.")
		void testCreateStudyGroup() throws IOException {
			//given
			StudyGroupDto.CreateDto createDto = toCreateDto(member.getId());

			//when
			StudyGroupIdResponse idResponse = studyGroupService.createStudyGroup(createDto);
			Optional<StudyGroup> retrievedStudyGroup = studyGroupRepository.findById(idResponse.studyGroupId());

			//then
			assertThat(retrievedStudyGroup).isPresent();
			assertThat(retrievedStudyGroup.get().getId()).isEqualTo(idResponse.studyGroupId());
		}

		@Test
		@DisplayName("존재하지 않는 멤버가 생성을 시도하면 예외가 발생한다.")
		void testFailedToCreateStudyGroup() throws IOException {
			//given
			StudyGroupDto.CreateDto createDto = toCreateDto(unknownId);

			//when, then
			assertThatThrownBy(() -> studyGroupService.createStudyGroup(createDto))
				.isInstanceOf(MemberException.class)
				.hasMessageContaining("회원을 찾을 수 없습니다");
		}
	}

	@Nested
	@DisplayName("스터디 그룹 상세조회 기능은")
	class StudyGroupGetDetailTest {

		@Test
		@DisplayName("스터디 그룹 아이디로 조회하여 상세결과를 반환한다.")
		void testGetStudyGroupDetail() {
			//given
			Member newMember = saveMember();
			StudyGroup studyGroup = saveStudyGroup(newMember, Topic.AI, Region.ONLINE);
			StudyGroupDto.ReadDto readDto = new StudyGroupDto.ReadDto(studyGroup.getId());

			//when
			StudyGroupDetailResponse detailResponse = studyGroupService.getStudyGroupDetail(readDto);

			//then
			assertThat(detailResponse.studyGroupId()).isEqualTo(studyGroup.getId());
			assertThat(detailResponse.leader().memberId()).isEqualTo(newMember.getId());
		}

		@Test
		@DisplayName("스터디 그룹 아이디로 조회에 실패하면 예외가 발생한다.")
		void testFailedToGetStudyGroupDetail() {
			//given
			StudyGroupDto.ReadDto readDto = new StudyGroupDto.ReadDto(unknownId);

			//when, then
			assertThatThrownBy(() -> studyGroupService.getStudyGroupDetail(readDto))
				.isInstanceOf(StudyGroupException.class)
				.hasMessageContaining("스터디 그룹을 찾을 수 없습니다");
		}
	}

	@Nested
	@DisplayName("스터디 그룹 전체 조회 기능은")
	class StudyGroupGetAllTest {

		@Nested
		@DisplayName("조건검색으로 전체조회를 하면")
		class GetStudyGroupsByConditionTest {

			@Test
			@DisplayName("조건에 맞는 스터디 그룹들을 커서 페이징 방식으로 조회한다.")
			void testGetStudyGroups() {
				//given
				for (int i = 0; i < 5; i++) {
					Member newMember = saveMember();
					saveStudyGroup(newMember, Topic.FRONTEND, Region.SEOUL);
				}

				StudyGroupDto.FindCondition findCondition = StudyGroupDto.FindCondition
					.builder()
					.region(Region.SEOUL)
					.size(20L)
					.build();

				//when
				CursorPageResponse<StudyGroupsResponse> pageResponses =
					studyGroupService.getStudyGroups(findCondition);

				//then
				assertThat(pageResponses.contents()).isNotNull();
				assertThat(pageResponses.hasNext()).isFalse();

				List<StudyGroupsResponse> contents = pageResponses.contents();

				contents.forEach(content -> assertThat(content.region()).isEqualTo(Region.SEOUL.getValue()));
			}
		}

		@Nested
		@DisplayName("멤버의 스터디 그룹을 조회하면")
		class GetMembersStudyGroupsTest {

			@Test
			@DisplayName("멤버의 스터디 그룹들을 커서 페이징 방식으로 조회한다.")
			void testGetMembersStudyGroups() {
				//given
				Member newMember = saveMember();

				for (int i = 0; i < 5; i++) {
					saveStudyGroup(newMember, Topic.FRONTEND, Region.SEOUL);
				}

				StudyGroupDto.FindCondition findCondition = StudyGroupDto.FindCondition
					.builder()
					.memberId(newMember.getId())
					.size(20L)
					.build();

				//when
				CursorPageResponse<StudyGroupsResponse> pageResponses =
					studyGroupService.getMemberStudyGroups(findCondition);

				//then
				assertThat(pageResponses.contents()).isNotNull();
				assertThat(pageResponses.hasNext()).isFalse();

				List<StudyGroupsResponse> contents = pageResponses.contents();

				contents.forEach(content -> assertThat(content.memberId()).isEqualTo(newMember.getId()));
			}
		}
	}

	@Nested
	@DisplayName("스터디 그룹 업데이트 기능은")
	class StudyGroupUpdateTest {

		@Test
		@DisplayName("스터디 리더라면 업데이트한다.")
		void testUpdateStudyGroup() {
			//given
			Member newMember = saveMember();
			StudyGroup studyGroup = saveStudyGroup(newMember, Topic.NETWORK, Region.DAEGU);
			String descriptionBeforeUpdate = studyGroup.getDescription();
			String newDescription = "new description";
			StudyGroupDto.UpdateDto updateDto = toUpdateDto(newDescription, newMember.getId(), studyGroup.getId());

			//when
			StudyGroupIdResponse idResponse = studyGroupService.updateStudyGroup(updateDto);
			Optional<StudyGroup> retrievedStudyGroup = studyGroupRepository.findById(idResponse.studyGroupId());

			//then
			assertThat(retrievedStudyGroup).isPresent();
			assertThat(retrievedStudyGroup.get().getDescription()).isNotEqualTo(descriptionBeforeUpdate);
			assertThat(retrievedStudyGroup.get().getDescription()).isEqualTo(newDescription);
		}

		@Test
		@DisplayName("스터디 리더가 아니라면 예외가 발생한다.")
		void testFailedToUpdateStudyGroup() {
			//given
			Member newMember = saveMember();
			StudyGroup studyGroup = saveStudyGroup(newMember, Topic.AI, Region.ONLINE);
			String newDescription = "new description";
			StudyGroupDto.UpdateDto updateDto = toUpdateDto(newDescription, unknownId, studyGroup.getId());

			//when, then
			assertThatThrownBy(() -> studyGroupService.updateStudyGroup(updateDto))
				.isInstanceOf(StudyGroupException.class)
				.hasMessageContaining(errorMessageOfNotLeader);
		}
	}

	@Nested
	@DisplayName("스터디 그룹 삭제 기능은")
	class StudyGroupDeleteTest {

		@Test
		@DisplayName("스터디 리더라면 삭제한다.")
		void testDeleteStudyGroup() {
			//given
			Member newMember = saveMember();
			StudyGroup studyGroup = saveStudyGroup(newMember, Topic.NETWORK, Region.DAEGU);
			StudyGroupDto.DeleteDto deleteDto = toDeleteDto(newMember.getId(), studyGroup.getId());

			//when
			studyGroupService.deleteStudyGroup(deleteDto);
			Optional<StudyGroup> retrievedStudyGroup = studyGroupRepository.findStudyGroupById(studyGroup.getId());

			//then
			assertThat(retrievedStudyGroup).isNotPresent();
		}

		@Test
		@DisplayName("스터디 리더가 아니라면 예외가 발생한다.")
		void testFailedToDeleteStudyGroup() {
			//given
			Member newMember = saveMember();
			StudyGroup studyGroup = saveStudyGroup(newMember, Topic.AI, Region.ONLINE);
			StudyGroupDto.DeleteDto deleteDto = toDeleteDto(unknownId, studyGroup.getId());

			//when, then
			assertThatThrownBy(() -> studyGroupService.deleteStudyGroup(deleteDto))
				.isInstanceOf(StudyGroupException.class)
				.hasMessageContaining(errorMessageOfNotLeader);
		}
	}

	private StudyGroupDto.CreateDto toCreateDto(Long memberId) throws IOException {
		return StudyGroupDto.CreateDto
			.builder()
			.memberId(memberId)
			.imageFile(getMultipartFile())
			.title("title")
			.topic(Topic.AI)
			.isOnline(false)
			.region(Region.SEOUL)
			.preferredMBTIs(Set.of(Mbti.ENFJ))
			.numberOfRecruits(5)
			.startDateTime(LocalDateTime.now().plusDays(10))
			.endDateTime(LocalDateTime.now().plusMonths(3))
			.description("this is new study group")
			.build();
	}

	private StudyGroupDto.UpdateDto toUpdateDto(String newDescription, Long memberId, Long studyGroupId) {
		return StudyGroupDto.UpdateDto
			.builder()
			.memberId(memberId)
			.studyGroupId(studyGroupId)
			.description(newDescription)
			.build();
	}

	private StudyGroupDto.DeleteDto toDeleteDto(Long memberId, Long studyGroupId) {
		return new StudyGroupDto.DeleteDto(memberId, studyGroupId);
	}

	private MultipartFile getMultipartFile() throws IOException {
		File imageFile =
			new File(Paths.get("src", "test", "resources") + File.separator + "test.png");
		FileInputStream inputStream = new FileInputStream(imageFile);
		String[] split = imageFile.getName().split("\\.");

		return new MockMultipartFile(split[0], imageFile.getName(), "image/" + split[1], inputStream);
	}

	private StudyGroup saveStudyGroup(Member member, Topic topic, Region region) {
		StudyGroup newStudyGroup = studyGroupRepository.save(
			StudyGroup
				.builder()
				.imageUrl("image")
				.title("title")
				.topic(topic)
				.isOnline(false)
				.region(region)
				.numberOfRecruits(5)
				.studyPeriod(new StudyPeriod(LocalDateTime.now().plusDays(10), LocalDateTime.now().plusMonths(3)))
				.preferredMBTIs(Set.of(new PreferredMbti(Mbti.ENFJ)))
				.description("this is new study group")
				.build());

		studyGroupMemberRepository.save(new StudyGroupMember(STUDY_LEADER, member, newStudyGroup));

		return newStudyGroup;
	}

	private Member saveMember() {
		String randomString = RandomStringUtils.randomAlphabetic(5);

		return memberRepository.save(Member
			.builder()
			.email(randomString + "@gmail.com")
			.nickName(randomString)
			.career(Career.JUNIOR)
			.field(Field.ANDROID)
			.githubUrl("github.com")
			.blogUrl("blog.com")
			.mbti(Mbti.ENFJ)
			.profileImageUrl("www.s3.com")
			.memberRole(MemberRole.ROLE_MEMBER)
			.build());
	}
}
