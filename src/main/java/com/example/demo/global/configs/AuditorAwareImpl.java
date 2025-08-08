package com.example.demo.global.configs;

import com.example.demo.member.libs.MemberUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.AuditorAware;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class AuditorAwareImpl implements AuditorAware<String> {

    private final MemberUtil memberUtil;

    @Override
    public Optional<String> getCurrentAuditor() {
        return Optional.ofNullable(memberUtil.isLogin() ? memberUtil.getMember().getEmail() : null);
    }
}
