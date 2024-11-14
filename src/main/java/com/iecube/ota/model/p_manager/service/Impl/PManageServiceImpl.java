package com.iecube.ota.model.p_manager.service.Impl;

import com.iecube.ota.model.User.dto.CurrentUserDto;
import com.iecube.ota.model.feishu.service.FeiShuService;
import com.iecube.ota.model.p_manager.entity.PManage;
import com.iecube.ota.model.p_manager.mapper.PManageMapper;
import com.iecube.ota.model.p_manager.service.PManageService;
import com.iecube.ota.model.product.entity.PNode;
import com.iecube.ota.model.product.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

@Service
public class PManageServiceImpl implements PManageService {

    @Autowired
    private PManageMapper pManageMapper;

    @Autowired
    private FeiShuService feiShuService;

    @Autowired
    private ProductService productService;


    @Override
    public String findByNodeId(Long nodeId) {
        List<PManage> pManages =  pManageMapper.selectByNodeId(nodeId);
        List<String> manageIds = new ArrayList<>();
        pManages.forEach(pManage->{
            manageIds.add(pManage.getManageId()==null?null:pManage.getManageId());
            manageIds.add(pManage.getPmId()==null?null:pManage.getPmId());
            manageIds.add(pManage.getDeveloperId()==null?null:pManage.getDeveloperId());
        });
        manageIds.removeIf(s -> s == null || s.trim().isEmpty());
        return manageIds.isEmpty()?"":feiShuService.getUsersBatch(manageIds);
    }

    @Override
    public List<PManage> nodeAddPM(Long nodeId, List<String> pmList) {
        pmList.forEach(pm->{
            PManage pManage = new PManage();
            pManage.setNodeId(nodeId);
            pManage.setPmId(pm);
            pManageMapper.insert(pManage);
        });
        return pManageMapper.selectByNodeId(nodeId);
    }

    @Override
    public List<PManage> nodeAddManager(Long nodeId, List<String> managerList) {
        managerList.forEach(manager->{
            PManage pManage = new PManage();
            pManage.setNodeId(nodeId);
            pManage.setManageId(manager);
            pManageMapper.insert(pManage);
        });
        return pManageMapper.selectByNodeId(nodeId);
    }

    @Override
    public List<PManage> nodeAddDeveloper(Long nodeId, List<String> developerList) {
        developerList.forEach(d->{
            PManage pManage = new PManage();
            pManage.setNodeId(nodeId);
            pManage.setDeveloperId(d);
            pManageMapper.insert(pManage);
        });
        return pManageMapper.selectByNodeId(nodeId);
    }

    @Override
    public List<PManage> nodeRemovePManage(Long pManageId) {
        PManage pManage = pManageMapper.select(pManageId);
        pManageMapper.delete(pManage);
        return pManageMapper.selectByNodeId(pManage.getNodeId());
    }

    @Override
    public String AddPM(Long nodeId, List<String> pmList) {
        List<PManage> pManageList = nodeAddPM(nodeId, pmList);
        List<String> pmIdList = new ArrayList<>();
        pManageList.forEach(manage->{
            pmIdList.add(manage.getPmId());
        });
        return feiShuService.getUsersBatch(pmIdList);
    }

    @Override
    public String AddManager(Long nodeId, List<String> managerList) {
        List<PManage> pManageList = nodeAddManager(nodeId, managerList);
        List<String> managerIdList = new ArrayList<>();
        pManageList.forEach(manage->{
            managerIdList.add(manage.getManageId());
        });
        return feiShuService.getUsersBatch(managerIdList);
    }

    @Override
    public String AddDeveloper(Long nodeId, List<String> developerList) {
        List<PManage> pManageList = nodeAddDeveloper(nodeId, developerList);
        List<String> developerIdList = new ArrayList<>();
        pManageList.forEach(manage->{
            developerIdList.add(manage.getDeveloperId());
        });
        return feiShuService.getUsersBatch(developerIdList);
    }

    @Override
    public String RemovePManage(Long pManageId) {
        List<PManage> pManageList = nodeRemovePManage(pManageId);
        List<String> manageIds = new ArrayList<>();
        pManageList.forEach(pManage->{
            manageIds.add(pManage.getManageId()==null?null:pManage.getManageId());
            manageIds.add(pManage.getPmId()==null?null:pManage.getPmId());
            manageIds.add(pManage.getDeveloperId()==null?null:pManage.getDeveloperId());
        });
        manageIds.removeIf(s -> s == null || s.trim().isEmpty());
        return manageIds.isEmpty()?"":feiShuService.getUsersBatch(manageIds);
    }

    /**
     * 获取一个节点的审批人员：该节点的所有祖先节点的PM & MANAGER
     * @param nodeId 节点id
     * @return 飞书批量查询结果
     */
    @Override
    public String assessingOfficerByNodeId(Long nodeId) {
        List<PNode> AncestorNodeList = productService.getAncestorNode(nodeId);
        List<Long> nodeIds = new ArrayList<>();
        AncestorNodeList.forEach(node->{
            nodeIds.add(node.getId());
        });
        List<PManage> pManageList = pManageMapper.selectByNodeIds(nodeIds);
        List<String> unionIdList = new ArrayList<>();
        pManageList.forEach(p->{
            unionIdList.add(p.getPmId());
            unionIdList.add(p.getManageId());
        });
        List<String> newList = new ArrayList<>(new HashSet<>(unionIdList));
        if(!newList.isEmpty()){
            return feiShuService.getUsersBatch(newList);
        }
        return null;
    }

    @Override
    public Boolean isDeveloper(Long nodeId, String unionId) {
        List<PManage> pManageList = pManageMapper.selectByNodeId(nodeId);
        List<String> developerIdList = new ArrayList<>();
        pManageList.forEach(p->{
            if(p.getDeveloperId()!=null) {
                developerIdList.add(p.getDeveloperId());
            }
        });
        return developerIdList.contains(unionId);
    }

}
