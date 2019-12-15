package com.treatsboot.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class GTFOException extends RuntimeException
{
    public GTFOException(String message)
    {
        super(message);
    }
}
