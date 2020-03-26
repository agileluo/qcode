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
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
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
    @RequestMapping("delete")
    public void delete(String id){
        CheckUtils.checkEmpty("id", id);
        mapper.deleteByPrimaryKey(id);
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
    @RequestMapping({"downLoad"})
    public void download(HttpServletResponse response, ScanLogQueryVo query) throws Exception {
        List<ScanLog> items = this.mapper.query(query);
        HSSFWorkbook wb = this.genExecl(items);
        String fileName = query.getGroup1() + "-" + query.getGroup2() + "-" + query.getGroup3();
        response.setContentType("application/x-download");
        response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName + ".xls", "utf-8"));
        wb.write(response.getOutputStream());
    }

    public HSSFWorkbook genExecl(List<ScanLog> logs) {
        HSSFWorkbook wb = new HSSFWorkbook();
        HSSFSheet sheet = wb.createSheet("扫描记录");
        HSSFCellStyle cellStyle = wb.createCellStyle();
        cellStyle.setAlignment(HorizontalAlignment.CENTER);
        int rowNum = 0;
        int colNum = 0;
        HSSFRow row = sheet.createRow(rowNum);
        List<String> titles = Arrays.asList("扫描时间", "扫描人员", "一级分类", "二级分类", "三级分类", "内容");
        HSSFCell cell = null;

        for(Iterator var10 = titles.iterator(); var10.hasNext(); ++colNum) {
            String title = (String)var10.next();
            cell = row.createCell(colNum);
            cell.setCellValue(title);
            cell.setCellStyle(cellStyle);
            row.setHeightInPoints(20.0F);
        }

        rowNum = rowNum + 1;
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        for(ScanLog log : logs){
            row = sheet.createRow(rowNum);
            colNum = 0;
            this.createCell(sheet, cellStyle, row, colNum++, dateFormat.format(log.getCreateTime()));
            this.createCell(sheet, cellStyle, row, colNum++, log.getUserId());
            this.createCell(sheet, cellStyle, row, colNum++, log.getGroup1());
            this.createCell(sheet, cellStyle, row, colNum++, log.getGroup2());
            this.createCell(sheet, cellStyle, row, colNum++, log.getGroup3());
            this.createCell(sheet, cellStyle, row, colNum++, log.getQrcode());
            ++rowNum;
        }

        return wb;
    }

    private void createCell(HSSFSheet sheet, HSSFCellStyle cellStyle, HSSFRow row, int colNum, Object value) {
        HSSFCell cell = row.createCell(colNum);
        sheet.autoSizeColumn(colNum, true);
        cell.setCellStyle(cellStyle);
        cell.setCellValue(value.toString());
    }


}
