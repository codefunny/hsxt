/*
 * Copyright (c) 2015-2018 SHENZHEN GUIYI SCIENCE AND TECHNOLOGY DEVELOP CO., LTD. All rights reserved.
 *
 * 注意：本内容仅限于深圳市归一科技研发有限公司内部传阅，禁止外泄以及用于其他的商业目的 
 */
package com.gy.hsxt.bs.order.interfaces;

import com.gy.hsxt.bs.bean.order.PayInfo;
import com.gy.hsxt.common.exception.HsException;

/**
 * 支付接口
 * 
 * @Package: com.gy.hsxt.bs.api.order
 * @ClassName: IBSPaymentService
 * @Description: TODO
 * 
 * @author: kongsl
 * @date: 2016-7-1 下午6:09:07
 * @version V1.0
 */
public interface IPaymentService {

    /**
     * 获取支付URL
     * 
     * @param payInfo
     *            支付信息
     * @return 支付URL
     * @throws HsException
     */
    public String getPayUrl(PayInfo payInfo) throws HsException;

    /**
     * 获取快捷支付绑定URL
     * 
     * @param custId
     *            客户号
     * @return 快捷支付绑定URL
     * @throws HsException
     */
    public String getQuickPayBindUrl(String custId) throws HsException;

    /**
     * 获取快捷支付短信验证码
     * 
     * @param payInfo
     *            支付信息
     * @throws HsException
     */
    public void getQuickPaySmsCode(PayInfo payInfo) throws HsException;

}
