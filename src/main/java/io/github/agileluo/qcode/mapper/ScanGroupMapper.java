package io.github.agileluo.qcode.mapper;

import io.github.agileluo.qcode.model.ScanGroup;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface ScanGroupMapper extends Mapper<ScanGroup> {

    List<ScanGroup> queryGroupList(@Param("parentId") Long parentId);
}