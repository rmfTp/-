package com.example.demo.global.advicers;

import com.example.demo.global.exceptions.CommonException;
import com.example.demo.global.libs.Utils;
import com.example.demo.global.rests.JSONError;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@RestControllerAdvice(annotations = RestController.class)
public class CommonRestControllerAdvice {

    private final Utils utils;

    @ExceptionHandler(Exception.class)
    public ResponseEntity<JSONError> errorHandler(Exception e) {
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR; // 기본 에러 코드는 500
        Object message = e.getMessage();

        if (e instanceof CommonException commonException) {
            status = commonException.getStatus();
            Map<String, List<String>> errorMessages = commonException.getErrorMessages(); // 커맨드 객체 검증 실패 메세지
            if (errorMessages != null) {
                message = errorMessages;
            } else {
                // 에러 코드로 관리되는 문구인 경우
                if (commonException.isErrorCode()) {
                    message = utils.getMessage((String)message);
                }
            }
        }

        e.printStackTrace();

        return ResponseEntity.status(status).body(new JSONError(status, message));
    }
}