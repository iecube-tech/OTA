package com.iecube.ota.model.product.controller;

import com.iecube.ota.BaseController.BaseController;
import com.iecube.ota.model.product.entity.PNode;
import com.iecube.ota.model.product.qo.NodeQo;
import com.iecube.ota.model.product.service.ProductService;
import com.iecube.ota.utils.JsonResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/product")
public class ProductController extends BaseController {

    @Autowired
    private ProductService productService;


    @GetMapping("/all")
    public JsonResult<PNode> allProduct(){
        PNode pNode = productService.getAllProduct();
        return new JsonResult<>(OK, pNode);
    }

    @GetMapping("/{id}")
    public JsonResult<PNode> productByNode(@PathVariable Long id){
        PNode res = productService.getTreeByNode(id);
        return new JsonResult<>(OK, res);
    }

    @PostMapping("/add")
    public JsonResult<PNode> addProductNode(@RequestBody NodeQo nodeQo){
        PNode pNode = new PNode();
        pNode.setPId(nodeQo.getPId());
        pNode.setName(nodeQo.getName());
        pNode.setType(nodeQo.getType());
        productService.insertPNode(pNode);
        PNode res = productService.getAllProduct();
        return new JsonResult<>(OK, res);
    }

    @PostMapping("/up")
    public JsonResult<PNode> updateProductNode(@RequestBody NodeQo nodeQo){
        System.out.println(nodeQo);
        PNode pNode = new PNode();
        pNode.setId(nodeQo.getId());
        pNode.setPId(nodeQo.getPId());
        pNode.setName(nodeQo.getName());
        pNode.setType(nodeQo.getType());
        PNode res = productService.updatePNode(pNode);
        return new JsonResult<>(OK, res);
    }

    @PostMapping("/del")
    public JsonResult<PNode> delProductNode(@RequestBody NodeQo nodeQo){
        if(nodeQo.getId()==null){
            return new JsonResult<>(OK);
        }
        System.out.println(nodeQo);
        productService.delNode(nodeQo.getId());
        PNode res = productService.getAllProduct();
        return new JsonResult<>(OK, res);
    }
}
