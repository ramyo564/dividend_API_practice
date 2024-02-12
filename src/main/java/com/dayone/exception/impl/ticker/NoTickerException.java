package com.dayone.exception.impl.ticker;

import com.dayone.exception.AbstractException;
import org.springframework.http.HttpStatus;

public class NoTickerException extends AbstractException {

    private final String ticker;

    public NoTickerException(String ticker){
        this.ticker=ticker;
    }

    @Override
    public int getStatusCode() {
        return HttpStatus.BAD_REQUEST.value();
    }

    @Override
    public String getMessage() {
        return "존재하지 않는 티커명 입니다. -> " + ticker;
    }
}
