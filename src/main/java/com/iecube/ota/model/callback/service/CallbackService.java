package com.iecube.ota.model.callback.service;

import com.iecube.ota.model.callback.entity.ReqDto;
import com.iecube.ota.model.callback.entity.ResDto;

public interface CallbackService {
    ResDto fs(ReqDto reqDto);
}
