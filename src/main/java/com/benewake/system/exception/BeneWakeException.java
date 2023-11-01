package com.benewake.system.exception;

import lombok.Data;

@Data
public class BeneWakeException extends RuntimeException{

    private Integer code = 201;

    public BeneWakeException() {
        super();
    }

    public BeneWakeException(String message) {
        super(message);
    }

    public BeneWakeException(Integer code ,String message) {
        super(message);
        this.code =code;
    }
}
