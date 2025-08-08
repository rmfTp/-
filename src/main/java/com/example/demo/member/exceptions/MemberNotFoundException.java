package com.example.demo.member.exceptions;

import com.example.demo.global.exceptions.NotFoundException;

public class MemberNotFoundException extends NotFoundException {
    public MemberNotFoundException() {
        super("NotFound.Member");
        setErrorCode(true);
    }
}
