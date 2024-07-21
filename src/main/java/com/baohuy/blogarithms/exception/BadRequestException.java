package com.baohuy.blogarithms.exception;

import org.springframework.http.HttpStatus;

public class BadRequestException extends BaseException {
    public BadRequestException(HttpStatus statusCode, String message) {
        super(HttpStatus.BAD_REQUEST, message);
    }
}
