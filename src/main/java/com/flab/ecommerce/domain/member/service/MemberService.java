package com.flab.ecommerce.domain.member.service;

import com.flab.ecommerce.domain.member.dto.MemberCreateRequestDTO;
import com.flab.ecommerce.domain.member.dto.MemberResponseDTO;
import com.flab.ecommerce.domain.member.dto.MemberUpdateRequestDTO;
import com.flab.ecommerce.domain.member.entity.Member;
import com.flab.ecommerce.domain.member.enums.MemberRole;
import com.flab.ecommerce.domain.member.exception.EmailAlreadyExistsException;
import com.flab.ecommerce.domain.member.exception.MemberNotFoundException;
import com.flab.ecommerce.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public MemberResponseDTO registerMember(MemberCreateRequestDTO requestDTO) {
        if (memberRepository.existsByEmail(requestDTO.getEmail())) {
            throw new EmailAlreadyExistsException();
        }

        String encodedPassword = passwordEncoder.encode(requestDTO.getPassword());

        Member member = Member.builder()
                .email(requestDTO.getEmail())
                .name(requestDTO.getName())
                .password(encodedPassword)
                .role(MemberRole.USER)
                .isActive(true)
                .build();
        memberRepository.save(member);
        return new MemberResponseDTO(member);
    }

    public MemberResponseDTO findMyInfo(String email) {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(MemberNotFoundException::new);
        return new MemberResponseDTO(member);
    }

    @Transactional
    public MemberResponseDTO updateMyInfo(String email, MemberUpdateRequestDTO requestDTO) {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(MemberNotFoundException::new);

        if (requestDTO.getName() != null && !requestDTO.getName().isEmpty()
                && !requestDTO.getName().equals(member.getName())) {
            member.updateName(requestDTO.getName());
        }

        if (requestDTO.getPassword() != null
                && !requestDTO.getPassword().isEmpty()
                && !passwordEncoder.matches(requestDTO.getPassword(), member.getPassword())) {
            member.changePassword(requestDTO.getPassword(), passwordEncoder);
            }

        return new MemberResponseDTO(member);
    }


    @Transactional
    public void deactivateMember(String email) {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(MemberNotFoundException::new);
        member.deactivate();
    }

    public List<MemberResponseDTO> findAllMembers() {
        return memberRepository.findAll()
                .stream()
                .map(MemberResponseDTO::new)
                .toList();
    }

    public MemberResponseDTO findMemberById(Long id) {
        Member member = memberRepository.findById(id)
                .orElseThrow(MemberNotFoundException::new);
        return new MemberResponseDTO(member);
    }

    @Transactional
    public MemberResponseDTO updateMemberById(Long id, MemberUpdateRequestDTO requestDTO) {
        Member member = memberRepository.findById(id)
                .orElseThrow(MemberNotFoundException::new);

        if (requestDTO.getName() != null && !requestDTO.getName().isEmpty()
                && !requestDTO.getName().equals(member.getName())) {
            member.updateName(requestDTO.getName());
        }

        if (requestDTO.getPassword() != null
                && !requestDTO.getPassword().isEmpty()
                && !passwordEncoder.matches(requestDTO.getPassword(), member.getPassword())) {

            member.changePassword(requestDTO.getPassword(), passwordEncoder);
        }

        return new MemberResponseDTO(member);
    }

    @Transactional
    public void deactivateMemberById(Long id) {
        Member member = memberRepository.findById(id)
                .orElseThrow(MemberNotFoundException::new);
        member.deactivate();
    }

    @Transactional
    public void activateMemberById(Long id) {
        Member member = memberRepository.findById(id)
                .orElseThrow(MemberNotFoundException::new);
        member.activate();
    }

}

