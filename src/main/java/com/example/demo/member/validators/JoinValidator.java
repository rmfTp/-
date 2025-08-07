package com.example.demo.member.validators;

import com.example.demo.global.validators.MobileValidator;
import com.example.demo.global.validators.PasswordValidator;
import com.example.demo.member.repositories.MemberRepository;
import com.example.demo.member.controllers.RequestJoin;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Lazy
@Component
@RequiredArgsConstructor
public class JoinValidator implements Validator, PasswordValidator, MobileValidator {
    private final MemberRepository repository;

    @Override
    public boolean supports(Class<?> clazz) {
        return clazz.isAssignableFrom(RequestJoin.class);
    }

    @Override
    public void validate(Object target, Errors errors) {
        if (errors.hasErrors()) return;
        /**
         * 이메일 중복여부
         * 비밀번호 복잡성
         * 비밀번호 확인 일치
         * 모바일 형식 검증
         */
        RequestJoin form = (RequestJoin) target;

        //1.이메일 중복여부
        if (repository.existsByEmail(form.getEmail()))
            errors.rejectValue("email", "Duplicate");
        //2. 비밀번호 복장성
        String password = form.getPassword();
        if (!checkAlpha(password, false) || !checkNumber(password) || !checkSpecialChars(password))
            errors.rejectValue("password", "Complexity");
        //3. 비밀번호 확인 일치
        String confirmPassword = form.getConfirmPassword();
        if (!password.equals(confirmPassword))
            errors.rejectValue("confirmPassword", "Mismatch");
        //4. 모바일 형식 검증
        String mobile = form.getMobile();
        if (!checkMobile(mobile))
            errors.rejectValue("mobile","Mobile");
    }
}
