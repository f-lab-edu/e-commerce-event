package com.flab.ecommerce.global.security.provider;

import com.flab.ecommerce.domain.member.exception.MemberNotFoundException;
import com.flab.ecommerce.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.security.Principal;

@Component
@RequiredArgsConstructor
public class LoginMemberProvider {

    private final MemberRepository memberRepository;

    public Long getLoginMemberId(Principal principal) {
        String email = principal.getName();
        return memberRepository.findByEmail(email)
                .orElseThrow(MemberNotFoundException::new)
                .getId();
    }
}
