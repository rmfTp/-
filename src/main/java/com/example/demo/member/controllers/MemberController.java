package com.example.demo.member.controllers;

import com.example.demo.global.exceptions.BadRequestException;
import com.example.demo.global.libs.Utils;
import com.example.demo.member.services.JoinService;
import com.example.demo.member.validators.JoinValidator;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/member")
@Tag(name = "회원 API", description = "회원 가입, 회원 인증 토근 발급 기능 제공")
public class MemberController {
    private final JoinValidator joinValidator;
    private final JoinService joinService;
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
}
