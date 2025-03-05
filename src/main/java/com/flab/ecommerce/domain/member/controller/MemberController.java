package com.flab.ecommerce.domain.member.controller;

import com.flab.ecommerce.domain.member.dto.MemberCreateRequestDTO;
import com.flab.ecommerce.domain.member.dto.MemberResponseDTO;
import com.flab.ecommerce.domain.member.dto.MemberUpdateRequestDTO;
import com.flab.ecommerce.domain.member.service.MemberService;
import com.flab.ecommerce.global.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/members")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @PostMapping
    public ResponseEntity<ApiResponse<MemberResponseDTO>> registerMember(@Valid @RequestBody MemberCreateRequestDTO requestDTO) {
        MemberResponseDTO member = memberService.registerMember(requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.created(member));
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<MemberResponseDTO>> findMyInfo(Principal principal) {
        String email = principal.getName();
        MemberResponseDTO member = memberService.findMyInfo(email);
        return ResponseEntity.ok(ApiResponse.success(member));
    }

    @PatchMapping("/me")
    public ResponseEntity<ApiResponse<MemberResponseDTO>> updateMyInfo(@Valid @RequestBody MemberUpdateRequestDTO requestDTO, Principal principal) {
        String email = principal.getName();
        MemberResponseDTO member = memberService.updateMyInfo(email, requestDTO);
        return ResponseEntity.ok(ApiResponse.success(member));
    }

    @PatchMapping("/me/deactivate")
    public ResponseEntity<ApiResponse<String>> deactivateMyAccount(Principal principal) {
        String email = principal.getName();
        memberService.deactivateMember(email);
        return ResponseEntity.ok(ApiResponse.success("회원 탈퇴가 완료 되었습니다."));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<MemberResponseDTO>>> findAllMembers() {
        List<MemberResponseDTO> members = memberService.findAllMembers();
        return ResponseEntity.ok(ApiResponse.success(members));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<MemberResponseDTO>> findMemberById(@PathVariable Long id) {
        MemberResponseDTO member = memberService.findMemberById(id);
        return ResponseEntity.ok(ApiResponse.success(member));
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<MemberResponseDTO>> updateMemberById(@PathVariable Long id, @Valid @RequestBody MemberUpdateRequestDTO requestDTO) {
        MemberResponseDTO member = memberService.updateMemberById(id, requestDTO);
        return ResponseEntity.ok(ApiResponse.success(member));
    }

    @PatchMapping("/{id}/deactivate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<String>> deactivateMemberById(@PathVariable Long id) {
        MemberResponseDTO member = memberService.findMemberById(id);
        memberService.deactivateMemberById(id);
        String message = String.format("사용자 %s(%s) 계정이 비활성화 되었습니다.", member.getName(), member.getEmail());
        return ResponseEntity.ok(ApiResponse.success(message));
    }

    @PatchMapping("/{id}/activate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<String>> activateMemberById(@PathVariable Long id) {
        MemberResponseDTO member = memberService.findMemberById(id);
        memberService.activateMemberById(id);
        String message = String.format("사용자 %s(%s) 계정이 활성화 되었습니다.", member.getName(), member.getEmail());
        return ResponseEntity.ok(ApiResponse.success(message));
    }

}
