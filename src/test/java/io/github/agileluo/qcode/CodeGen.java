package io.github.agileluo.qcode;

import io.github.agileluo.qcode.util.MybatisGeneratorUtil;

public class CodeGen {

    public static void main(String[] args) throws Exception {
        MybatisGeneratorUtil.genCode("scan_log");
    }
}
