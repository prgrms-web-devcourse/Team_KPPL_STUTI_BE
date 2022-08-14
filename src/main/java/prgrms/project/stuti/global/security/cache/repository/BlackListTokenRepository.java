package prgrms.project.stuti.global.security.cache.repository;

import org.springframework.data.repository.CrudRepository;

import prgrms.project.stuti.global.security.cache.model.BlackListToken;

public interface BlackListTokenRepository extends CrudRepository<BlackListToken, String> {

}