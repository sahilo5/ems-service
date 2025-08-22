package com.SadhyaSiddhi.ems_service.exceptions;


public class InvalidQrTokenException extends RuntimeException {
    public InvalidQrTokenException(String msg) {
        super(msg);
    }
}