package com.iecube.ota.model.feishu.service;

import com.iecube.ota.model.feishu.vo.Department;

import java.util.List;

public interface FeiShuService {

    String getAllDepartments();

    String getDepartmentMember(String departmentId);

    String getUsersBatch(List<String> userIdList);
}
