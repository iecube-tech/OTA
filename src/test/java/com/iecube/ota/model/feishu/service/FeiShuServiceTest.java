package com.iecube.ota.model.feishu.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class FeiShuServiceTest {
    @Autowired
    private FeiShuService feiShuService;
    @Test
    public void test() {
        feiShuService.getAllDepartments();
    }
}
