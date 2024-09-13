package com.iecube.ota.model.Terminal.service.ex;

import com.iecube.ota.exception.ServiceException;

public class SendMqttMessageException extends ServiceException {
    public SendMqttMessageException() {
        super();
    }

    public SendMqttMessageException(String message) {
        super(message);
    }

    public SendMqttMessageException(String message, Throwable cause) {
        super(message, cause);
    }

    public SendMqttMessageException(Throwable cause) {
        super(cause);
    }

    protected SendMqttMessageException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
