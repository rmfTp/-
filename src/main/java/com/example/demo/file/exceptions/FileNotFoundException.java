package com.example.demo.file.exceptions;

import com.example.demo.global.exceptions.NotFoundException;

public class FileNotFoundException extends NotFoundException {
    public FileNotFoundException() {
        super("NotFound.file");
        setErrorCode(true);
    }
}
