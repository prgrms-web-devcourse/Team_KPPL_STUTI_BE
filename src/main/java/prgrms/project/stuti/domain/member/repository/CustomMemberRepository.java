package prgrms.project.stuti.domain.member.repository;

import java.util.Optional;


import prgrms.project.stuti.domain.member.model.Member;

public interface CustomMemberRepository {

	Optional<Member> findMemberById(Long memberId);

	Optional<Member> findMemberByEmail(String email);

	Optional<Member> findMemberByNickName(String nickname);
}
