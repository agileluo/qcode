package io.github.agileluo.qcode.web;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import io.github.agileluo.qcode.mapper.ScanLogMapper;
import io.github.agileluo.qcode.mapper.ScanUserMapper;
import io.github.agileluo.qcode.model.ScanLog;
import io.github.agileluo.qcode.model.ScanUser;
import io.github.agileluo.qcode.util.BizException;
import io.github.agileluo.qcode.util.CheckUtils;
import io.github.agileluo.qcode.vo.PageReq;
import io.github.agileluo.qcode.vo.PageResp;
import io.github.agileluo.qcode.vo.ScanLogQueryVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("user")
public class ScanUserController {

    @Autowired
    private ScanUserMapper mapper;


    @RequestMapping("add")
    @Transactional
    public ScanUser add(@RequestBody ScanUser scanUser) {
        CheckUtils.checkEmpty(scanUser, "userName", "password", "realName");
        scanUser.setCreateTime(new Date());
        mapper.insert(scanUser);
        return scanUser;
    }

    @RequestMapping("delete")
    public void delete(String id){
        CheckUtils.checkEmpty("id", id);
        mapper.deleteByPrimaryKey(id);
    }

    @RequestMapping("getById")
    public ScanUser getById(String id){
        CheckUtils.checkEmpty("id", id);
        return mapper.selectByPrimaryKey(id);
    }

    @RequestMapping("update")
    public void update(@RequestBody ScanUser scanUser){
        CheckUtils.checkEmpty(scanUser, "id");
        mapper.updateByPrimaryKeySelective(scanUser);
    }

    @RequestMapping("pageQuery")
    public PageResp<ScanUser> pageQuery(@RequestBody PageReq<ScanUser> query) {
        CheckUtils.checkEmpty(query, "page", "pageSize");
        PageHelper.startPage(query.getPage(), query.getPageSize());
        PageResp<ScanUser> result = new PageResp<>();
        List<ScanUser> items = mapper.select(query.getData());
        long totalCount = ((Page) items).getTotal();
        result.setItems(items);
        result.setTotal((int) totalCount);
        return result;
    }
    @RequestMapping("list")
    public List<ScanUser> list() {
       return mapper.selectAll();
    }


}
