package prgrms.project.stuti.global.cache.repository;

import org.springframework.data.repository.CrudRepository;

import prgrms.project.stuti.global.cache.model.RefreshToken;

public interface RefreshTokenRepository extends CrudRepository<RefreshToken, String> {

}
