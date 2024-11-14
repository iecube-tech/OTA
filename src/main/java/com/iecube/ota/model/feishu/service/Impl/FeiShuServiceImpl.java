package com.iecube.ota.model.feishu.service.Impl;

import com.iecube.ota.config.AppAccessTokenConfig;
import com.iecube.ota.exception.ServiceException;
import com.iecube.ota.model.feishu.service.FeiShuService;
import com.iecube.ota.utils.FeiShu.CheckFSResponseUtil;
import com.iecube.ota.utils.FeiShu.FeiShuException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@Service
public class FeiShuServiceImpl implements FeiShuService {


    private AppAccessTokenConfig appAccessTokenConfig;

    private static final String BATCH_DEPARTMENT_LIST="https://open.feishu.cn/open-apis/contact/v3/departments";
    private static final String DEPARTMENT_MEMBER="https://open.feishu.cn/open-apis/contact/v3/users/find_by_department";
    private static final String BATCH_USERS="https://open.feishu.cn/open-apis/contact/v3/users/batch";
    @Autowired
    public void appAccessToken(AppAccessTokenConfig appAccessTokenConfig) {
        this.appAccessTokenConfig = appAccessTokenConfig;
    }


    @Override
    public String getAllDepartments() {
        String url = UriComponentsBuilder.fromHttpUrl(BATCH_DEPARTMENT_LIST)
                .queryParam("department_id_type", "open_department_id")
                .queryParam("parent_department_id", "0")
                .queryParam("fetch_child", true)
                .toUriString();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer "+ appAccessTokenConfig.getAppAccessToken());
        HttpEntity<Object> httpEntity = new HttpEntity<>(headers);
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, httpEntity, String.class);
        try{
            CheckFSResponseUtil.checkErrorResponse(response);
            return response.getBody();
        }catch (FeiShuException e){
            throw new ServiceException(e.getMessage());
        }

    }

    @Override
    public String getDepartmentMember(String departmentId) {
        String url= UriComponentsBuilder.fromHttpUrl(DEPARTMENT_MEMBER)
                .queryParam("department_id_type", "open_department_id")
                .queryParam("department_id", departmentId)
                .toUriString();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer "+ appAccessTokenConfig.getAppAccessToken());
        HttpEntity<Object> httpEntity = new HttpEntity<>(headers);
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, httpEntity, String.class);
        try{
            CheckFSResponseUtil.checkErrorResponse(response);
            return response.getBody();
        }catch (FeiShuException e){
            throw new ServiceException(e.getMessage());
        }
    }

    @Override
    public String getUsersBatch(List<String> userIdList) {
        String url = UriComponentsBuilder.fromHttpUrl(BATCH_USERS)
                    .queryParam("user_id_type","union_id")
                    .queryParam("department_id_type", "open_department_id")
                    .toUriString();
        for(String userId : userIdList){
            url = UriComponentsBuilder.fromHttpUrl(url).queryParam("user_ids",userId).toUriString();
        }
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer "+ appAccessTokenConfig.getAppAccessToken());
        HttpEntity<Object> httpEntity = new HttpEntity<>(headers);
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, httpEntity, String.class);
        try{
            CheckFSResponseUtil.checkErrorResponse(response);
            return response.getBody();
        }catch (FeiShuException e){
            throw new ServiceException(e.getMessage());
        }
    }
}
