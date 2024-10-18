package com.iecube.ota.utils.FeiShu;

import com.iecube.ota.exception.ServiceException;

public class FeiShuException extends ServiceException {
    public FeiShuException() {
        super();
    }

    public FeiShuException(String message) {
        super(message);
    }

    public FeiShuException(String message, Throwable cause) {
        super(message, cause);
    }

    public FeiShuException(Throwable cause) {
        super(cause);
    }

    protected FeiShuException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
