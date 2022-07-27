package prgrms.project.stuti.domain.member.controller;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import prgrms.project.stuti.global.cache.model.BlackListToken;
import prgrms.project.stuti.global.cache.model.RefreshToken;
import prgrms.project.stuti.global.cache.repository.BlackListTokenRepository;
import prgrms.project.stuti.global.cache.repository.RefreshTokenRepository;

@RestController
@RequestMapping("/redis")
@RequiredArgsConstructor
public class RedisController {

    private final BlackListTokenRepository blackListTokenRedisRepo;
    private final RefreshTokenRepository refreshTokenRedisRepo;


    @GetMapping("/refresh")
    public List<List<String>> refresh() {
        List<List<String>> list = new ArrayList<>();
        if (refreshTokenRedisRepo.count() != 0L) {
            Iterator<RefreshToken> iterator = refreshTokenRedisRepo.findAll().iterator();
            while (iterator.hasNext()) {
                RefreshToken token = iterator.next();
                List<String> tokenDetail = new ArrayList<>();
                tokenDetail.add(token.getAccessTokenValue());
                tokenDetail.add(token.getRefreshTokenValue());
                tokenDetail.add(token.getExpiration().toString());
                tokenDetail.add(token.getCreatedTime().toString());
                tokenDetail.add(token.getExpirationTime().toString());
                list.add(tokenDetail);
            }
        }
        return list;
    }

    @GetMapping("/blackList")
    public List<List<String>> blackList() {
        List<List<String>> list = new ArrayList<>();
        if (blackListTokenRedisRepo.count() != 0L) {
            Iterator<BlackListToken> iterator = blackListTokenRedisRepo.findAll().iterator();
            while (iterator.hasNext()) {
                BlackListToken token = iterator.next();
                List<String> tokenDetail = new ArrayList<>();
                tokenDetail.add(token.getBlackListToken());
                tokenDetail.add(token.getExpiration().toString());
                list.add(tokenDetail);
            }
        }
        return list;
    }
}
