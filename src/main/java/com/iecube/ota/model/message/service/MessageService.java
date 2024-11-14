package com.iecube.ota.model.message.service;

import com.iecube.ota.model.message.dto.MessageDto;

import java.util.List;

public interface MessageService {
    void sendToUser(String to, MessageDto message);


    void sendToDepartmentAndUserList(List<String> departmentList, List<String> userList, MessageDto message);

    String sendCardToUser(String to, String cardMsg);


}
