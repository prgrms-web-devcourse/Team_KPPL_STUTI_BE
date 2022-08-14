package prgrms.project.stuti.global.security.cache.repository;

import org.springframework.data.repository.CrudRepository;

import prgrms.project.stuti.global.security.cache.model.RefreshToken;

public interface RefreshTokenRepository extends CrudRepository<RefreshToken, String> {

}
