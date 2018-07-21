package com.augurit.awater;

import java.util.UUID;

/**
 * 说    明： 默认的ID生成器
 * 创 建 人： 周卫鹏
 * 创建日期： 2017-10-17 20:51
 * 修改说明：
 */
public class DefaultIdGenerator {

    public static String getIdForStr() {
        return UUID.randomUUID().toString().replace("-", "");
    }

}
