package prgrms.project.stuti.domain.member.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import prgrms.project.stuti.domain.member.model.Member;

public interface MemberRepository extends JpaRepository<Member, Long>, CustomMemberRepository {
	Optional<Member> findByEmail(String email);
}
