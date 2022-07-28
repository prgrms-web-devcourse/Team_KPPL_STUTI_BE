package prgrms.project.stuti.global.cache.service;

import java.util.Optional;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import prgrms.project.stuti.global.cache.model.TemporaryMember;
import prgrms.project.stuti.global.cache.repository.TemporaryMemberRepository;

@Service
@RequiredArgsConstructor
public class TemporaryMemberService {

    private final TemporaryMemberRepository temporaryMemberRepository;

    public void save(TemporaryMember temporaryMember){
        temporaryMemberRepository.save(temporaryMember);
    }

    public Optional<TemporaryMember> findById(String id) {
        return temporaryMemberRepository.findById(id);
    }
}
