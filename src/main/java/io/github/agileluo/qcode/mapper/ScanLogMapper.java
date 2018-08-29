package io.github.agileluo.qcode.mapper;

import io.github.agileluo.qcode.model.ScanLog;
import io.github.agileluo.qcode.vo.PageReq;
import io.github.agileluo.qcode.vo.ScanLogQueryVo;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface ScanLogMapper extends Mapper<ScanLog> {

    List<ScanLog> query(ScanLogQueryVo query);
}