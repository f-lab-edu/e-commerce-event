package com.flab.ecommerce.domain.member.controller;

import com.flab.ecommerce.domain.member.dto.MemberCreateRequestDTO;
import com.flab.ecommerce.domain.member.dto.MemberResponseDTO;
import com.flab.ecommerce.domain.member.dto.MemberUpdateRequestDTO;
import com.flab.ecommerce.domain.member.service.MemberService;
import com.flab.ecommerce.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@Tag(name = "회원", description = "회원 정보 조회 및 관리 기능을 제공합니다. ")
@RestController
@RequestMapping("/members")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @Operation(summary = "회원 가입", description = "신규 회원을 등록합니다.")
    @PostMapping
    public ResponseEntity<ApiResponse<MemberResponseDTO>> registerMember(@RequestBody @Valid MemberCreateRequestDTO requestDTO) {
        MemberResponseDTO member = memberService.registerMember(requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.created(member));
    }

    @Operation(summary = "내 정보 조회", description = "현재 로그인한 회원의 정보를 조회합니다.")
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<MemberResponseDTO>> findMyInfo(Principal principal) {
        String email = principal.getName();
        MemberResponseDTO member = memberService.findMyInfo(email);
        return ResponseEntity.ok(ApiResponse.success(member));
    }

    @Operation(summary = "내 정보 수정", description = "현재 로그인한 회원의 정보를 수정합니다.")
    @PatchMapping("/me")
    public ResponseEntity<ApiResponse<MemberResponseDTO>> updateMyInfo(@RequestBody @Valid MemberUpdateRequestDTO requestDTO, Principal principal) {
        String email = principal.getName();
        MemberResponseDTO member = memberService.updateMyInfo(email, requestDTO);
        return ResponseEntity.ok(ApiResponse.success(member));
    }

    @Operation(summary = "회원 탈퇴", description = "현재 로그인한 회원의 계정을 탈퇴 처리 합니다. (실제로는 데이터가 삭제되지 않고 비활성화 상태로 변경됩니다.)")
    @PatchMapping("/me/deactivate")
    public ResponseEntity<ApiResponse<String>> deactivateMyAccount(Principal principal) {
        String email = principal.getName();
        memberService.deactivateMember(email);
        return ResponseEntity.ok(ApiResponse.success("회원 탈퇴가 완료 되었습니다."));
    }

    @Operation(summary = "전체 회원 목록 조회 (관리자 전용)", description = "관리자가 전체 회원의 목록을 조회합니다.")
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<MemberResponseDTO>>> findAllMembers() {
        List<MemberResponseDTO> members = memberService.findAllMembers();
        return ResponseEntity.ok(ApiResponse.success(members));
    }

    @Operation(summary = "회원 단건 조회 (관리자 전용)", description = "관리자가 특정 회원의 정보를 조회합니다.")
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<MemberResponseDTO>> findMemberById(@PathVariable Long id) {
        MemberResponseDTO member = memberService.findMemberById(id);
        return ResponseEntity.ok(ApiResponse.success(member));
    }

    @Operation(summary = "회원 정보 수정 (관리자 전용)", description = "관리자가 특정 회원 정보를 수정합니다.")
    @PatchMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<MemberResponseDTO>> updateMemberById(@PathVariable Long id, @RequestBody @Valid MemberUpdateRequestDTO requestDTO) {
        MemberResponseDTO member = memberService.updateMemberById(id, requestDTO);
        return ResponseEntity.ok(ApiResponse.success(member));
    }

    @Operation(summary = "회원 비활성화 처리 (관리자 전용)", description = "관리자가 특정 회원의 계정을 비활성화 상태로 변경합니다.")
    @PatchMapping("/{id}/deactivate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<String>> deactivateMemberById(@PathVariable Long id) {
        MemberResponseDTO member = memberService.findMemberById(id);
        memberService.deactivateMemberById(id);
        String message = String.format("사용자 %s(%s) 계정이 비활성화 되었습니다.", member.getName(), member.getEmail());
        return ResponseEntity.ok(ApiResponse.success(message));
    }

    @Operation(summary = "회원 활성화 처리 (관리자 전용)", description = "관리자가 특정 회원의 계정을 다시 활성화합니다.")
    @PatchMapping("/{id}/activate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<String>> activateMemberById(@PathVariable Long id) {
        MemberResponseDTO member = memberService.findMemberById(id);
        memberService.activateMemberById(id);
        String message = String.format("사용자 %s(%s) 계정이 활성화 되었습니다.", member.getName(), member.getEmail());
        return ResponseEntity.ok(ApiResponse.success(message));
    }

}
