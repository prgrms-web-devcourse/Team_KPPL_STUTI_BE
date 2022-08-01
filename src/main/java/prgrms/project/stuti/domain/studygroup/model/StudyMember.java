package prgrms.project.stuti.domain.studygroup.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

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
public class StudyMember extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "study_member_id", unique = true, nullable = false, updatable = false)
	private Long id;

	@Enumerated(value = EnumType.STRING)
	@Column(name = "study_member_role", length = 16, nullable = false)
	private StudyMemberRole studyMemberRole;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "member_id")
	private Member member;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "study_group_id")
	private StudyGroup studyGroup;

	public StudyMember(StudyMemberRole studyMemberRole, Member member, StudyGroup studyGroup) {
		this.studyMemberRole = studyMemberRole;
		this.member = member;
		this.studyGroup = studyGroup;
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this,
			ToStringStyle.SHORT_PREFIX_STYLE)
			.append("id", id)
			.append("studyMemberRole", studyMemberRole)
			.append("member", member)
			.append("studyGroup", studyGroup)
			.toString();
	}
}
