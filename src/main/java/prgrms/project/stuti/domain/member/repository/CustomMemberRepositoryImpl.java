package prgrms.project.stuti.domain.member.repository;

import static prgrms.project.stuti.domain.member.model.QMember.*;

import java.util.Optional;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;
import prgrms.project.stuti.domain.member.model.Member;

@RequiredArgsConstructor
public class CustomMemberRepositoryImpl implements CustomMemberRepository {

	private final JPAQueryFactory jpaQueryFactory;

	@Override
	public Optional<Member> findMemberById(Long memberId) {
		return Optional.ofNullable(
			jpaQueryFactory
				.selectFrom(member)
				.where(member.id.eq(memberId), isNotDeleted())
				.fetchOne()
		);
	}

	@Override
	public Optional<Member> findMemberByEmail(String email) {
		return Optional.ofNullable(
			jpaQueryFactory
				.selectFrom(member)
				.where(member.email.eq(email), isNotDeleted())
				.fetchOne()
		);
	}

	@Override
	public Optional<Member> findMemberByNickName(String nickname) {
		return Optional.ofNullable(
			jpaQueryFactory
				.selectFrom(member)
				.where(member.nickName.eq(nickname), isNotDeleted())
				.fetchOne()
		);
	}

	private BooleanExpression isNotDeleted() {
		return member.isDeleted.isFalse();
	}
}