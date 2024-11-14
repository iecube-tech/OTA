package com.iecube.ota.model.product.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class ProductServiceTests {

    @Autowired
    private ProductService productService;

    @Test
    public void testGetParentNode() {
        System.out.println( productService.getParentTreeByNode(11L));
    }

}
