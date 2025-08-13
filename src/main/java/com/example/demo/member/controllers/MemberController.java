package com.example.demo.member.controllers;

import com.example.demo.global.exceptions.BadRequestException;
import com.example.demo.global.libs.Utils;
import com.example.demo.member.jwt.TokenService;
import com.example.demo.member.libs.MemberUtil;
import com.example.demo.member.services.JoinService;
import com.example.demo.member.validators.JoinValidator;
import com.example.demo.member.validators.TokenValidator;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/member")
@Tag(name = "회원 API", description = "회원 가입, 회원 인증 토근 발급 기능 제공")
public class MemberController {
    private final JoinValidator joinValidator;
    private final JoinService joinService;
    private final TokenValidator tokenValidator;
    private final TokenService tokenService;
    private final MemberUtil memberUtil;
    private final Utils utils;
    // 회원가입
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "회원가입처리", method = "POST")
    @ApiResponse(responseCode = "201", description = "회원가입 성공시 201로 응답, 검증 실패시 400")
    public void join(@Valid @RequestBody RequestJoin form, Errors errors) {
        joinValidator.validate(form, errors);
        if (errors.hasErrors())
            throw new BadRequestException(utils.getErrorMessages(errors));
        joinService.process(form);
    }

    /**
     * 회원 계정으로 JWT 토큰 발급
     * @return
     */
    @Operation(summary = "회원 인증 처리", description = "이메일과 비밀번호로 인증한 후 회원 전용 요청을 보낼수 있는 토큰(JWT)을 발급")
    @Parameters({
            @Parameter(name="email", required = true, description = "이메일"),
            @Parameter(name="password", required = true, description = "비밀번호")
    })
    @ApiResponse(responseCode = "200", description = "인증 성공시 토큰(JWT)발급")
    @PostMapping("/token")
    public String token(@Valid @RequestBody RequestToken form, Errors errors) {
        tokenValidator.validate(form, errors);
        if (errors.hasErrors())
            throw new BadRequestException(utils.getErrorMessages(errors));
        return tokenService.create(form.getEmail());
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/test1")
    public void test1() {
        System.out.println("로그인시 접근가능");
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/test2")
    public void test2() {
        System.out.println("관리자만 접근가능");
    }
}
