package io.github.agileluo.qcode.web;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import io.github.agileluo.qcode.mapper.ScanLogMapper;
import io.github.agileluo.qcode.model.ScanLog;
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
@RequestMapping("log")
public class ScanLogController {

    @Autowired
    private ScanLogMapper mapper;



    @RequestMapping("add")
    @Transactional
    public ScanLog add(@RequestBody ScanLog scanLog) {
        CheckUtils.checkEmpty(scanLog, "userId", "group1", "group2", "group3", "qrcode");
        try {
            scanLog.setCreateTime(new Date());
            mapper.insert(scanLog);


            return scanLog;
        } catch (DuplicateKeyException ex) {
            ScanLog query = new ScanLog();
            query.setQrcode(scanLog.getQrcode());
            ScanLog dbLog = mapper.selectOne(query);
            if(scanLog.getGroup3().equals(dbLog.getGroup3()) && dbLog.getUserId().equals(scanLog.getUserId())){
                return dbLog;
            }
            throw new BizException("记录已被【" + dbLog.getUserId() + "】于【" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(dbLog.getCreateTime()) + "】 扫描到【" + dbLog.getGroup1() + " - " + dbLog.getGroup2() + " - " + dbLog.getGroup3() + "】");
        }
    }

    @RequestMapping("pageQuery")
    public PageResp<ScanLog> pageQuery(@RequestBody PageReq<ScanLogQueryVo> query) {
        CheckUtils.checkEmpty(query, "page", "pageSize");
        PageHelper.startPage(query.getPage(), query.getPageSize());
        PageResp<ScanLog> result = new PageResp<>();
        List<ScanLog> items = mapper.query(query.getData());
        long totalCount = ((Page) items).getTotal();
        result.setItems(items);
        result.setTotal((int) totalCount);
        return result;
    }


}
