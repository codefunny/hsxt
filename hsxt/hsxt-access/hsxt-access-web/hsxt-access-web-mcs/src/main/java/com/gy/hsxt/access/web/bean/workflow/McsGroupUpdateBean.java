/*
 * Copyright (c) 2015-2018 SHENZHEN GUIYI SCIENCE AND TECHNOLOGY DEVELOP CO., LTD. All rights reserved.
 *
 * 注意：本内容仅限于深圳市归一科技研发有限公司内部传阅，禁止外泄以及用于其他的商业目的 
 */
package com.gy.hsxt.access.web.bean.workflow;

import java.io.Serializable;

import com.gy.hsxt.access.web.bean.AbstractMCSBase;
import com.gy.hsxt.access.web.common.constant.ASRespCode;
import com.gy.hsxt.access.web.common.utils.RequestUtil;

/***
 * 工作组更新bean
 * @Package: com.gy.hsxt.access.web.bean.workflow  
 * @ClassName: McsGroupUpdateBean 
 * @Description: TODO
 *
 * @author: wangjg 
 * @date: 2016-1-29 下午6:42:36 
 * @version V1.0
 */
public class McsGroupUpdateBean extends AbstractMCSBase implements Serializable {
    private static final long serialVersionUID = -929759504997703202L;

    /**
     * 值班组编号
     */
    private String groupId;

    /**
     * 值班计划编号
     */
    //private String scheduleId;

    /**
     * 开关状态
     */
    private Integer opened;

    /**
     * 验证开关状态数据
     */
    public void checkOpenStatusData() {
        RequestUtil.verifyParamsIsNotEmpty(
        // 值班组编号
                new Object[] { this.groupId, ASRespCode.MW_GROUP_ID_NOT_NULL },
                // 开关状态
                new Object[] { this.opened, ASRespCode.MW_OPEND_STATUS_NOT_NULL });
    }

    /**
     * 验证值班计划数据
     */
    public void checkScheduleData() {
        RequestUtil.verifyParamsIsNotEmpty(new Object[] { this.groupId, ASRespCode.MW_GROUP_ID_NOT_NULL });
    }

    public String getGroupId() {
        return groupId;
    }

   /* *//**
     * @return the 值班计划
     *//*
    public String getScheduleId() {
        return scheduleId;
    }

    *//**
     * @param 值班计划
     *            the scheduleId to set
     *//*
    public void setScheduleId(String scheduleId) {
        this.scheduleId = scheduleId;
    }*/

    /**
     * @return the 开关状态
     */
    public Integer getOpened() {
        return opened;
    }

    /**
     * @param 开关状态
     *            the opened to set
     */
    public void setOpened(Integer opened) {
        this.opened = opened;
    }

    /**
     * @param 值班组编号
     *            the groupId to set
     */
    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

}
