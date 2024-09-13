package com.iecube.ota.model.Terminal.service;

import com.iecube.ota.model.Terminal.entity.TerminalEntity;

import java.util.List;

public interface TerminalService {

    void updateTerminalStatus(String mqttMessage);

    List<TerminalEntity> getTerminalListByProductId(Integer productId);

    TerminalEntity devicePassiveUpgrade(String did, Long firmwareId);
}
