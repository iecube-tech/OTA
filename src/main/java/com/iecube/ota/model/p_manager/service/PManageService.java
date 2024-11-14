package com.iecube.ota.model.p_manager.service;

import com.iecube.ota.model.User.dto.CurrentUserDto;
import com.iecube.ota.model.p_manager.entity.PManage;

import java.util.List;

public interface PManageService {

    String findByNodeId(Long nodeId);

    List<PManage> nodeAddPM(Long nodeId, List<String> pmList);

    List<PManage> nodeAddManager(Long nodeId, List<String> managerList);

    List<PManage> nodeAddDeveloper(Long nodeId, List<String> developerList);

    List<PManage> nodeRemovePManage(Long pManageId);

    String AddPM(Long nodeId, List<String> pmList);

    String AddManager(Long nodeId, List<String> managerList);

    String AddDeveloper(Long nodeId, List<String> developerList);
    String RemovePManage(Long pManageId);

    String assessingOfficerByNodeId(Long nodeId);

    Boolean isDeveloper(Long nodeId, String unionId);
}
