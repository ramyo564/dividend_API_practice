package com.dayone.exception.impl.ticker;

import com.dayone.exception.AbstractException;
import org.springframework.http.HttpStatus;

public class AlreadyExistTickerException extends AbstractException {

    private final String ticker;

    public AlreadyExistTickerException(String ticker) {
        this.ticker = ticker;
    }

    @Override
    public int getStatusCode() {
        return HttpStatus.BAD_REQUEST.value();
    }

    @Override
    public String getMessage() {
        return "이미 존재하는 티커입니다. -> " + ticker;
    }
}
