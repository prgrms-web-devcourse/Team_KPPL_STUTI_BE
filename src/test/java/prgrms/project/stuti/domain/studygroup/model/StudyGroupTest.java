package prgrms.project.stuti.domain.studygroup.model;

import static org.assertj.core.api.Assertions.*;

import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import prgrms.project.stuti.domain.member.model.Mbti;
import prgrms.project.stuti.global.error.exception.StudyGroupException;

class StudyGroupTest {

	private StudyGroup studyGroup;

	@BeforeEach
	public void setup() {
		this.studyGroup = StudyGroup
			.builder()
			.numberOfRecruits(3)
			.preferredMBTIs(Set.of(new PreferredMbti(Mbti.ENFJ)))
			.build();
	}

	@Test
	@DisplayName("스터디 그룹의 지원자수가 증가한다.")
	void testIncreaseNumberOfApplicants() {
		int numberOfBeforeDoAction = studyGroup.getNumberOfApplicants();

		studyGroup.increaseNumberOfApplicants();

		assertThat(studyGroup.getNumberOfApplicants()).isGreaterThan(numberOfBeforeDoAction);
	}

	@Test
	@DisplayName("스터디 그룹의 지원자수가 0 밑으로 감소하지 않는다.")
	void testDecreaseNumberOfApplicants() {
		studyGroup.decreaseNumberOfApplicants();
		studyGroup.decreaseNumberOfApplicants();
		studyGroup.decreaseNumberOfApplicants();
		studyGroup.decreaseNumberOfApplicants();
		studyGroup.decreaseNumberOfApplicants();
		studyGroup.decreaseNumberOfApplicants();

		assertThat(studyGroup.getNumberOfApplicants()).isNotNegative();
	}

	@Test
	@DisplayName("스터디 그룹의 멤버의수가 증가한다.")
	void testIncreaseNumberOfMembers() {
		int numberOfBeforeDoAction = studyGroup.getNumberOfMembers();

		studyGroup.increaseNumberOfMembers();

		assertThat(studyGroup.getNumberOfMembers()).isGreaterThan(numberOfBeforeDoAction);
	}

	@Test
	@DisplayName("스터디 그룹의 멤버수가 0 밑으로 감소하지 않는다.")
	void testDecreaseNumberOfMembers() {
		studyGroup.decreaseNumberOfMembers();
		studyGroup.decreaseNumberOfMembers();
		studyGroup.decreaseNumberOfMembers();
		studyGroup.decreaseNumberOfMembers();
		studyGroup.decreaseNumberOfMembers();
		studyGroup.decreaseNumberOfMembers();

		assertThat(studyGroup.getNumberOfMembers()).isNotNegative();
	}

	@Test
	@DisplayName("스터디 그룹의 모집인원이 다 채워진 상태에서 스터디 멤버의 인원수를 증시키면 예외가 발생한다.")
	void testFullOfNumberOfRecruits() {
		studyGroup.increaseNumberOfMembers();
		studyGroup.increaseNumberOfMembers();

		assertThatThrownBy(() -> studyGroup.increaseNumberOfMembers()).isInstanceOf(StudyGroupException.class);
	}
}
