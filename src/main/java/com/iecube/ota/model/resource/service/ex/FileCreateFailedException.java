package com.iecube.ota.model.resource.service.ex;

import com.iecube.ota.exception.ServiceException;

public class FileCreateFailedException extends ServiceException {
    public FileCreateFailedException() {
        super();
    }

    public FileCreateFailedException(String message) {
        super(message);
    }

    public FileCreateFailedException(String message, Throwable cause) {
        super(message, cause);
    }

    public FileCreateFailedException(Throwable cause) {
        super(cause);
    }

    protected FileCreateFailedException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
