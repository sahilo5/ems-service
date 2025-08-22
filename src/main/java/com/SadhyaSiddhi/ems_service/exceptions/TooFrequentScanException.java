package com.SadhyaSiddhi.ems_service.exceptions;

public class TooFrequentScanException extends RuntimeException {
    public TooFrequentScanException(String msg) {
        super(msg);
    }
}