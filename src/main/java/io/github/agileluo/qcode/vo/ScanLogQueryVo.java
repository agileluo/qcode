package io.github.agileluo.qcode.vo;

import io.github.agileluo.qcode.model.ScanLog;
import lombok.Data;

@Data
public class ScanLogQueryVo extends ScanLog {
    private String groupName;
    private String createTimeStart;
    private String createTimeEnd;
}
