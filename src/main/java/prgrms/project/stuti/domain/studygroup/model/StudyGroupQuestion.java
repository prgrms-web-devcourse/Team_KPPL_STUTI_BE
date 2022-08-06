package prgrms.project.stuti.domain.studygroup.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import prgrms.project.stuti.domain.member.model.Member;
import prgrms.project.stuti.global.base.BaseEntity;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class StudyGroupQuestion extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "study_group_question_id", unique = true, nullable = false, updatable = false)
	private Long id;

	@Column(name = "contents", length = 500, nullable = false)
	private String contents;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "parent_id")
	private StudyGroupQuestion parent;

	@OneToMany(mappedBy = "parent", orphanRemoval = true)
	private final List<StudyGroupQuestion> children = new ArrayList<>();

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "member_id")
	private Member member;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "study_group_id")
	private StudyGroup studyGroup;

	public StudyGroupQuestion(String contents, StudyGroupQuestion parent, Member member, StudyGroup studyGroup) {
		this.contents = contents;
		this.parent = parent;
		this.member = member;
		this.studyGroup = studyGroup;
	}

	public void updateContents(String contents) {
		this.contents = contents;
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this,
			ToStringStyle.SHORT_PREFIX_STYLE)
			.append("id", id)
			.append("contents", contents)
			.append("member", member)
			.append("studyGroup", studyGroup)
			.toString();
	}
}
