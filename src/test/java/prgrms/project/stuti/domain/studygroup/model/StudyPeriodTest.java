package prgrms.project.stuti.domain.studygroup.model;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import prgrms.project.stuti.global.error.exception.StudyGroupException;

class StudyPeriodTest {

	@ParameterizedTest(name = "[{index}] 스터디 시작 시간 > 종료 시간")
	@MethodSource("testBeforeStartDateTimeSource")
	@DisplayName("스터디 종료 시간이 시작 시간보다 전의 시간이 입력되면 예외가 발생한다.")
	void testBeforeStartDateTime(TestPeriod testPeriod) {
		assertThrows(StudyGroupException.class,
			() -> new StudyPeriod(testPeriod.startDateTime, testPeriod.endDateTime));
	}

	private static List<TestPeriod> testBeforeStartDateTimeSource() {
		LocalDateTime now = LocalDateTime.now();

		return List.of(
			new TestPeriod(LocalDateTime.MAX, now),
			new TestPeriod(LocalDateTime.MAX, LocalDateTime.MIN),
			new TestPeriod(now, LocalDateTime.MIN),
			new TestPeriod(now, now.minusDays(30)),
			new TestPeriod(now.plusDays(10), now.minusDays(20))
		);
	}

	@ParameterizedTest(name = "[{index}] 1일 미만의 스터디 기간")
	@MethodSource("testLessThanOneDaySource")
	@DisplayName("스터디 기간이 최소 1일이 되지 않으면 예외가 발생한다.")
	void testLessThanOneDay(TestPeriod testPeriod) {
		assertThrows(StudyGroupException.class,
			() -> new StudyPeriod(testPeriod.startDateTime, testPeriod.endDateTime));
	}

	private static List<TestPeriod> testLessThanOneDaySource() {
		LocalDateTime now = LocalDateTime.now();

		return List.of(
			new TestPeriod(now, now.plusSeconds(10)),
			new TestPeriod(now, now.plusSeconds(999)),
			new TestPeriod(now, now.plusHours(10)),
			new TestPeriod(now, now.plusMinutes(10)),
			new TestPeriod(now, now.plusNanos(10000))
		);
	}

	@ParameterizedTest(name = "[{index}] 유효한 스터디 기간")
	@MethodSource("testValidStudyPeriodSource")
	@DisplayName("유효한 스터디 기간이 입력되면 정상적으로 객체를 생성한다.")
	void testValidStudyPeriod(TestPeriod testPeriod) {
		StudyPeriod studyPeriod = new StudyPeriod(testPeriod.startDateTime, testPeriod.endDateTime);

		assertNotNull(studyPeriod);
	}

	private static List<TestPeriod> testValidStudyPeriodSource() {
		LocalDateTime now = LocalDateTime.now();

		return List.of(
			new TestPeriod(now, now.plusDays(10)),
			new TestPeriod(now, now.plusWeeks(4)),
			new TestPeriod(now, now.plusYears(1)),
			new TestPeriod(now, now.plusDays(30)),
			new TestPeriod(now, now.plusMonths(10))
		);
	}

	private static class TestPeriod {
		LocalDateTime startDateTime;
		LocalDateTime endDateTime;

		public TestPeriod(LocalDateTime startDateTime, LocalDateTime endDateTime) {
			this.startDateTime = startDateTime;
			this.endDateTime = endDateTime;
		}
	}
}
