package com.iecube.ota.model.production.service.Impl.ex;

import com.iecube.ota.exception.ServiceException;

public class ProductionException extends ServiceException {
    public ProductionException() {
        super();
    }

    public ProductionException(String message) {
        super(message);
    }

    public ProductionException(String message, Throwable cause) {
        super(message, cause);
    }

    public ProductionException(Throwable cause) {
        super(cause);
    }

    protected ProductionException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
