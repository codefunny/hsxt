/*
 * Copyright (c) 2015-2018 SHENZHEN GUIYI SCIENCE AND TECHNOLOGY DEVELOP CO., LTD. All rights reserved.
 *
 * 注意：本内容仅限于深圳市归一科技研发有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
package com.gy.hsxt.ao.enumtype;

/**
 * 银行转帐原因枚举定义
 * 
 * @Package: com.gy.hsxt.bs.order.enumtype
 * @ClassName: TransReason
 * @Description: TODO
 * 
 * @author: kongsl
 * @date: 2015-10-22 下午5:19:15
 * @version V3.0.0
 */
public enum TransReason {

    // 主动提现
    ACCORD_CASH(1),
    // 销户退款
    CLOSE_ACCOUNT(2);
    private Integer code;

    TransReason(Integer code) {
        this.code = code;
    }

    public Integer getCode() {
        return code;
    }
}
