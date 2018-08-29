package io.github.agileluo.qcode.web;


import io.github.agileluo.qcode.mapper.ScanGroupMapper;
import io.github.agileluo.qcode.model.ScanGroup;
import io.github.agileluo.qcode.util.BizException;
import io.github.agileluo.qcode.util.CheckUtils;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("group")
public class ScanGroupController {

    @Autowired
    private ScanGroupMapper mapper;

    @RequestMapping("add")
    public ScanGroup add(@RequestBody ScanGroup scanGroup){
        try{
            CheckUtils.checkEmpty(scanGroup, "name");
            scanGroup.setCreateTime(new Date());
            if(scanGroup.getParentId() != null){
                ScanGroup parentGroup = mapper.selectByPrimaryKey(scanGroup.getParentId());
                if(parentGroup == null){
                    throw new BizException("父级不存在");
                }
            }
            mapper.insert(scanGroup);
            return scanGroup;
        }catch(DuplicateKeyException ex){
            throw new BizException("分类已存在");
        }
    }

    @RequestMapping("groupList")
    public List<ScanGroup> queryGroupList(Long parentId){
        return mapper.queryGroupList(parentId);
    }

    @RequestMapping("delete")
    public void delete(Long id){
        CheckUtils.checkEmpty("id", id);
        ScanGroup query = new ScanGroup();
        query.setParentId(id);
        List<ScanGroup> children = mapper.select(query);
        if(CollectionUtils.isNotEmpty(children)){
            throw new BizException("此分类包括子类信息，不能删除");
        }
        mapper.deleteByPrimaryKey(id);
    }
}
