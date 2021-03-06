/*
 * Copyright (c) 2015-2018 SHENZHEN GUIYI SCIENCE AND TECHNOLOGY DEVELOP CO., LTD. All rights reserved.
 *
 * 注意：本内容仅限于深圳市归一科技研发有限公司内部传阅，禁止外泄以及用于其他的商业目的 
 */
package com.gy.hsxt.access.web.aps.services.accountCompany;

import java.util.Map;

import org.springframework.stereotype.Service;

import com.gy.hsxt.ac.bean.AccountBalance;
import com.gy.hsxt.ac.bean.AccountEntry;
import com.gy.hsxt.ac.bean.AccountEntrySum;
import com.gy.hsxt.access.web.common.service.IBaseService;
import com.gy.hsxt.ao.bean.BuyHsb;
import com.gy.hsxt.ao.bean.PvToHsb;
import com.gy.hsxt.bs.bean.order.DividendDetail;
import com.gy.hsxt.bs.bean.order.PointDividend;
import com.gy.hsxt.common.bean.Page;
import com.gy.hsxt.common.bean.PageData;
import com.gy.hsxt.common.exception.HsException;
import com.gy.hsxt.ps.bean.AllocDetail;
import com.gy.hsxt.ps.bean.AllocDetailSum;
import com.gy.hsxt.rp.bean.ReportsAccountEntry;

/**
 * *************************************************************************
 * 
 * <PRE>
 * Description      : 账户余额查询
 * 
 * Project Name     : hsxt-access-web-company 
 * 
 * Package Name     : com.gy.hsxt.access.web.company.services.accountManagement  
 * 
 * File Name        : IBalanceService 
 * 
 * Author           : LiZhi Peter
 * 
 * Create Date      : 2015-10-8 下午4:08:18
 * 
 * Update User      : LiZhi Peter
 * 
 * Update Date      : 2015-10-8 下午4:08:18
 * 
 * UpdateRemark     : 说明本次修改内容
 * 
 * Version          : v1.0
 * 
 * </PRE>
 ************************************************************************** 
 */
@SuppressWarnings("rawtypes")
@Service
public interface IBalanceService extends IBaseService {

	/**
	 * 账户余额单查询
	 * 
	 * @param custId
	 *            客户全局编号
	 * @param accType
	 *            账户类型编码
	 * @return 账户余额实体集合对象
	 * @throws HsException
	 */
	public AccountBalance findAccNormal(String custId, String accType) throws HsException;

	/**
	 * 查询今日个人积分查询
	 * 
	 * @param custId
	 *            客户全局编码
	 * @param accType
	 *            账户类型编码
	 * @return 账务分录汇总信息对象
	 * @throws HsException
	 */
	public AccountEntrySum findPerIntegralByToday(String custId, String accType) throws HsException;

	/**
	 * 正常账户发生汇总额查询接口
	 * 
	 * @param accountEntry
	 *            账户分录实体对象
	 * @return
	 * @throws HsException
	 */
	public AccountEntry findAccSumNormal(AccountEntry accountEntry) throws HsException;

	/**
	 * 客户对应的账户分类查询接口定义
	 * 
	 * @param custId
	 *            客户号
	 * @param accCategory
	 *            分类标识和对应分类名称如下（1 ：积分类型，2 ：互生币类型，3 ：货币类型，5
	 *            ：地区平台银行货币存款/地区平台银行货币现金类型）
	 * @return
	 * @throws HsException
	 */
	public Map<String, AccountBalance> searchAccBalanceByAccCategory(String custId, int accCategory) throws HsException;


	/**
	 * 查询账户明细详单(积分)
	 * 
	 * @Description:
	 * @author: likui
	 * @created: 2016年2月3日 上午9:49:39
	 * @param transNo
	 * @param transType
	 * @return
	 * @throws HsException
	 * @return : Map<String,Object>
	 * @version V3.0.0
	 */
	public Map<String, Object> queryAccOptDetailed(Map map) throws HsException;

	/**
	 * 获取保底积分
	 * 
	 * @return
	 * @throws HsException
	 */
	public String baseIntegral() throws HsException;
	
	/**
     * 企业账户流水查询
     * @param rpEnterprisResource 企业查询条件
     * @return List<ReportsAccountEntry> 企业账户流水数据集合
     * @throws HsException
     */
    public PageData<ReportsAccountEntry> searchEntAccountEntry(Map filterMap, Map sortMap, Page page) throws HsException;
    
    /**
     * 消费者账户流水查询
     * @param rpEnterprisResource 企业查询条件
     * @return List<ReportsAccountEntry> 企业账户流水数据集合
     * @throws HsException
     */
    public PageData<ReportsAccountEntry> searchPerAccountEntry(Map filterMap, Map sortMap, Page page) throws HsException;
    
    /**
     * 分红账户流水查询
     * @param filterMap 查询条件
     * @param page 分页信息（curPage 当前页,pageSize 每页记录数）
     * @return PageData<PointDividend> 分红流水数据集合
     * @throws HsException
     */
    public PageData<PointDividend> searchCarAccountEntryDividend(Map filterMap, Map sortMap, Page page) throws HsException;
    
    /**
     * 投资分红汇总下的流水详情
     * 
     * @param transNo
     * @return
     */
    public PageData<DividendDetail> getDividendDetailList(Map filterMap,Map sortMap, Page page);

	public PageData<AllocDetail> queryPointAllotDetailedList(Map filterMap,
			Map sortMap, Page page) throws HsException;

	public AllocDetailSum queryPointAllotDetailed(String batchNo, String entResNo)
			throws HsException;
}
