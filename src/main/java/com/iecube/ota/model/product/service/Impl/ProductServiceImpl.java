package com.iecube.ota.model.product.service.Impl;

import com.iecube.ota.exception.DeleteException;
import com.iecube.ota.exception.InsertException;
import com.iecube.ota.exception.QueryException;
import com.iecube.ota.exception.UpdateException;
import com.iecube.ota.model.product.entity.PNode;
import com.iecube.ota.model.product.mapper.ProductMapper;
import com.iecube.ota.model.product.service.ProductService;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductMapper productMapper;

    private static List<PNode> allNodeList;

    @Override
    public void insertPNode(PNode pNode){
        PNode parentNode = productMapper.pNodeById(pNode.getPId());
        if(parentNode==null){
            throw new InsertException("异常添加操作");
        }
        if(parentNode.getType()==0){
            throw new InsertException("叶子节点无法添加子节点");
        }
        int res = productMapper.insertNode(pNode);
        if(res != 1){
            throw new InsertException("新增数据异常");
        }
    }

    @Override
    public PNode updatePNode(PNode pNode){
        int res = productMapper.updateNode(pNode);
        if(res != 1){
            throw new UpdateException("更新数据异常");
        }
        return pNode;
    }


    @Override
    public void delNode(long id) {
        PNode pNode = productMapper.pNodeById(id);
        System.out.println(pNode);
        if(pNode.getPId()==0){
            throw new DeleteException("根节点无法删除");
        }
        if(pNode.getType()==0){
            this.delPNode(pNode);
        }
        if(pNode.getType()==1){
            PNode tree = this.getPNodeTree(pNode.getId());
            List<PNode> pNodeList = this.getAllNodes(tree);
            System.out.println("cnlmllllllggggg");
            System.out.println(pNodeList);
            this.batchDelPNode(pNodeList);
        }

    }

    @Override
    public PNode getAllProduct(){
        PNode rootNode = productMapper.pNodeByPId(0);
        PNode tree = this.getPNodeTree(rootNode.getId());
        return tree;
    }

    @Override
    public PNode getTreeByNode(Long id){
        PNode tree = this.getPNodeTree(id);
        return tree;
    }

    public void allNode(){
        List<PNode> pNodeList = productMapper.allNode();
        allNodeList = pNodeList;
    }

    /**
     * 根据根节点构建树
     * @param id 根节点id
     * @return
     */
    public PNode getPNodeTree(long id){
        if(id<=0){
            throw new QueryException("请求参数id的值异常");
        }
        // root 信息
        PNode pNode = productMapper.pNodeById(id);
        this.allNode();
        pNode = this.getChildTree(pNode);
        return pNode;
    }

    private PNode getChildTree(PNode parentNode){
        List<PNode> childTree = new ArrayList<>();
        // 在所有的节点中判断其pId与当前的pNode的id是不是相同 相同就是他的子节点
        for(PNode node: allNodeList){
            if(node.getPId()!=null){
                if(node.getPId()==parentNode.getId()){
                    // 递归实现
                    childTree.add(getChildTree(node));
                }
            }
        }
        parentNode.setChildren(childTree);
        return parentNode;
    }

    /**
     * 获取树中的所有节点
     * @param tree
     * @return
     */
    // 递归方法，用来获取树中所有节点
    public List<PNode> getAllNodes(PNode tree) {
        List<PNode> allNodes = new ArrayList<>();
        collectNodes(tree, allNodes);
        return allNodes;
    }

    /**
     * 递归收集节点
     * @param node
     * @param allNodes
     */
    private void collectNodes(PNode node, List<PNode> allNodes) {
        if (node == null) {
            return;
        }
        allNodes.add(node);
        if (node.getChildren() != null) {
            for (PNode child : node.getChildren()) {
                collectNodes(child, allNodes);
            }
        }
    }

    public void delPNode(PNode pNode){
        int res = productMapper.delNode(pNode);
        if(res != 1){
            throw new DeleteException("删除数据异常");
        }
    }

    public int batchDelPNode(@Param("list")  List<PNode> pNodeList){
        System.out.println(pNodeList);
        int res = productMapper.batchDelNode(pNodeList);
        return res;
    }
}
