/*
 * Copyright (c) 2015-2018 SHENZHEN GUIYI SCIENCE AND TECHNOLOGY DEVELOP CO., LTD. All rights reserved.
 *
 * 注意：本内容仅限于深圳市归一科技研发有限公司内部传阅，禁止外泄以及用于其他的商业目的 
 */
package com.gy.hsxt.access.web.company.controllers.accountManagement;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.gy.hsxt.ac.bean.AccountBalance;
import com.gy.hsxt.ac.bean.AccountEntrySum;
import com.gy.hsxt.access.web.common.bean.HttpRespEnvelope;
import com.gy.hsxt.access.web.common.controller.BaseController;
import com.gy.hsxt.access.web.common.service.IBaseService;
import com.gy.hsxt.access.web.common.utils.RequestUtil;
import com.gy.hsxt.access.web.company.services.accountManagement.IBalanceService;
import com.gy.hsxt.access.web.company.services.accountManagement.IInvestmentAccountService;
import com.gy.hsxt.access.web.company.services.accountManagement.ITransInnerService;
import com.gy.hsxt.access.web.company.services.common.CompanyConfigService;
import com.gy.hsxt.access.web.company.services.common.IPubParamService;
import com.gy.hsxt.bs.bean.order.CustomPointDividend;
import com.gy.hsxt.bs.bean.order.DividendDetail;
import com.gy.hsxt.bs.bean.order.PointDividend;
import com.gy.hsxt.bs.bean.order.PointInvest;
import com.gy.hsxt.common.bean.Page;
import com.gy.hsxt.common.bean.PageData;
import com.gy.hsxt.common.constant.AccountType;
import com.gy.hsxt.common.exception.HsException;
import com.gy.hsxt.common.utils.DateUtil;
import com.gy.hsxt.common.utils.DoubleUtil;
import com.gy.hsxt.lcs.bean.LocalInfo;

/***************************************************************************
 * <PRE>
 * Description 		: 投资账户
 * 
 * Project Name   	: hsxt-access-web-person 
 * 
 * Package Name     : com.gy.hsxt.access.web.person.controllers.accountManagement  
 * 
 * File Name        : IntegralAccountController 
 * 
 * Author           : LiZhi Peter
 * 
 * Create Date      : 2015-10-26 下午5:22:16
 * 
 * Update User      : LiZhi Peter
 * 
 * Update Date      : 2015-10-26 下午5:22:16
 * 
 * UpdateRemark 	: 说明本次修改内容
 * 
 * Version          : v1.0
 * 
 * </PRE>
 ***************************************************************************/
@Controller
@RequestMapping("investmentAccount")
public class InvestmentAccountController extends BaseController {
	@Resource
	private IInvestmentAccountService investmentAccountService; // 投资账户

	@Resource
	private IBalanceService balanceService; // 账户余额查询服务类

	@Autowired
	private CompanyConfigService companyConfigService; // 全局配置文件

	@Autowired
	private ITransInnerService ransInnerService; // 内部转账

	@Autowired
	private IPubParamService pubParamService;// 平台服务公共信息服务类

	/**
	 * 查询余额信息
	 * 
	 * @param custId
	 *            客户号
	 * @param pointNo
	 *            互生卡号
	 * @param request
	 *            当前请求数据
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = { "/query_investment_blance" }, method = { RequestMethod.GET,
			RequestMethod.POST }, produces = "application/json;charset=UTF-8")
	public HttpRespEnvelope findInvestmentBlance(String hsResNo, HttpServletRequest request) {

		// 变量声明
		HttpRespEnvelope hre = null;
		Map<String, Object> map = null;
		CustomPointDividend customPointDividend = null;

		// 执行查询方法
		try {

			// 获取积分投资分红信息
			customPointDividend = this.investmentAccountService.findInvestDividendInfo(hsResNo,

			DateUtil.getSimpleYear(DateUtil.addYears(new Date(), -1)));

			map = new HashMap<String, Object>();

			if (customPointDividend != null) {
				// 投资分红互生币=计算定向消费币+流通币
				BigDecimal addNum = DoubleUtil.add(customPointDividend.getDirectionalDividend(),
						customPointDividend.getNormalDividend());

				// 保存数据到map
				map.put("dividendsHsb", addNum.toString()); // 投资分红互生币
				map.put("dividendYear", customPointDividend.getDividendYear()); // 年份
				map.put("directionalHsb", customPointDividend.getDirectionalDividend()); // 定向消费币
				map.put("circulationHsb", customPointDividend.getNormalDividend()); // 流通币
				map.put("dividendsRate", customPointDividend.getYearDividendRate()); // 投资分红回报率
				map.put("investmentTotal", customPointDividend.getAccumulativeInvestCount()); // 累计积分投资总数
			}

			hre = new HttpRespEnvelope(map);

		} catch (HsException e) {
			hre = new HttpRespEnvelope(e);
		}

		return hre;
	}

	/**
	 * 查询积分投资记录
	 * 
	 * @param custID
	 *            客户号 必传
	 * @param startDate
	 *            查询条件：投资日期 可选
	 * @param endDate
	 *            查询条件：投资日期 可选
	 * @param page
	 *            分页参数 必传
	 * @param request
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = { "/getPointInvestList" }, method = { RequestMethod.GET,
			RequestMethod.POST }, produces = "application/json;charset=UTF-8")
	public HttpRespEnvelope getPointInvestList(String search_entCustId, String startDate, String endDate,
			Page page, HttpServletRequest request) {
		// 变量声明
		HttpRespEnvelope hre = null;
		try {

			hre = new HttpRespEnvelope();
			// 获取积分投资记录
			PageData<PointInvest> list = this.investmentAccountService.getPointInvestList(search_entCustId,startDate, endDate, page);
			if (list != null) {
				hre.setData(list.getResult());
				hre.setTotalRows(list.getCount());
			}
			hre.setCurPage(page.getCurPage());
		} catch (HsException e) {
			hre = new HttpRespEnvelope(e);
		}
		return hre;
	}

	/**
	 * 查询积分投资分红列表
	 * 
	 * @param custID
	 *            客户号 必传
	 * @param startYear
	 *            开始年份 可选
	 * @param endYear
	 *            结束年份 可选
	 * @param page
	 *            分页参数 必传
	 * @param request
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = { "/getPointDividendList" }, method = { RequestMethod.GET,
			RequestMethod.POST }, produces = "application/json;charset=UTF-8")
	public HttpRespEnvelope getPointDividendList(String startYear, String endYear,
			Page page, HttpServletRequest request) {
		// 变量声明
		HttpRespEnvelope hre = null;
		String entCustId = request.getParameter("search_entCustId");
		try {

			hre = new HttpRespEnvelope();
			// 获取积分投资分红信息
			PageData<PointDividend> list = this.investmentAccountService.getPointDividendList(
					entCustId, startYear, endYear, page);
			if (list != null) {
				hre.setData(list.getResult());
				hre.setTotalRows(list.getCount());
			}
			hre.setCurPage(page.getCurPage());
		} catch (HsException e) {
			hre = new HttpRespEnvelope(e);
		}
		return hre;
	}

	/**
	 * 获取积分投资分红详细记录信息
	 * 
	 * @param resNo
	 *            互生号 必传
	 * @param dividendYear
	 *            分红年份 可选
	 * @param page
	 *            分页参数 必传
	 * @param request
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = { "/getDividendDetailList" }, method = { RequestMethod.GET,
			RequestMethod.POST }, produces = "application/json;charset=UTF-8")
	public HttpRespEnvelope getDividendDetailList(String resNo, String dividendYear, Page page,
			HttpServletRequest request) {
		// 变量声明
		HttpRespEnvelope hre = null;
		try {

			hre = new HttpRespEnvelope();
			// 获取积分投资分红详细记录信息
			PageData<DividendDetail> list = this.investmentAccountService.getDividendDetailList(
					resNo, dividendYear, page);
			if (list != null) {
				hre.setData(list.getResult());
				hre.setTotalRows(list.getCount());
			}
			hre.setCurPage(page.getCurPage());
		} catch (HsException e) {
			hre = new HttpRespEnvelope(e);
		}
		return hre;
	}

	/**
	 * 获取积分投资分红详细记录信息
	 * 
	 * @param resNo
	 *            互生号 必传
	 * @param dividendYear
	 *            分红年份 可选
	 * @param page
	 *            分页参数 必传
	 * @param request
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = { "/getInvestDividendInfo" }, method = { RequestMethod.GET,
			RequestMethod.POST }, produces = "application/json;charset=UTF-8")
	public HttpRespEnvelope getInvestDividendInfo(String resNo, String dividendYear,
			HttpServletRequest request) {
		// 变量声明
		HttpRespEnvelope hre = null;
		try {

			hre = new HttpRespEnvelope();
			// 获取积分投资分红详细记录信息
			CustomPointDividend investDividendInfo = this.investmentAccountService
					.getInvestDividendInfo(resNo, dividendYear);
			hre.setData(investDividendInfo);
		} catch (HsException e) {
			hre = new HttpRespEnvelope(e);
		}
		return hre;
	}

	/**
	 * 初始化积分转互生币
	 * 
	 * @param custId
	 *            客户号
	 * @param pointNo
	 *            互生卡号
	 * @param request
	 *            当前请求数据
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = { "/init_integral_transfer_Hsb" }, method = { RequestMethod.GET,
			RequestMethod.POST }, produces = "application/json;charset=UTF-8")
	public HttpRespEnvelope initIntegralTransferHsb(String custId, String pointNo,
			HttpServletRequest request) {

		// 变量声明
		LocalInfo lcalInfo = null; // 本地平台信息
		Double itnAccBalance = null; // 积分账户余额
		Map<String, Object> map = null; // 返回数据临时结合
		AccountBalance accBalance = null; // 账户余额对象

		// Token验证
		HttpRespEnvelope hre = super.checkSecureToken(request);
		if (hre != null) {
			return hre;
		}

		// 执行查询方法
		try {

			map = new HashMap<String, Object>();

			// 查询积分账户余额
			accBalance = balanceService.findAccNormal(custId,
					AccountType.ACC_TYPE_POINT10110.getCode());

			// 非空验证
			if (accBalance != null) {

				// 积分账户余额
				itnAccBalance = DoubleUtil.parseDouble(accBalance.getAccBalance());

				// 可用积分数
				itnAccBalance = DoubleUtil.sub(itnAccBalance,Double.parseDouble(balanceService.baseIntegral()));
				
				//如果小于零则显示0的积分余额
				itnAccBalance = itnAccBalance<0?0.00D:itnAccBalance;
				// 保存账户余额
				map.put("pointBlance", itnAccBalance);
			}

			// 积分账户的可用积分数可转出，转出积分数为不小于的整数！
			map.put("integrationMin", this.companyConfigService.getIntegrationConvertibleMin());

			// 获取本平台的信息
			lcalInfo = this.pubParamService.findSystemInfo();
			// 货币转换比率
			map.put("exchangeRate", lcalInfo.getExchangeRate());
			// 币种
			map.put("currencyCode", lcalInfo.getCurrencyCode());

			hre = new HttpRespEnvelope(map);

		} catch (HsException e) {
			hre = new HttpRespEnvelope(e);
		}
		return hre;
	}

	// /**
	// * 积分转互生币提交方法
	// * @param transInner 转帐记录实体
	// * @param tradePwd 交易密码
	// * @param integralNum 转出积分数
	// * @param hsbNum 转入互生币
	// * @param request
	// * @return
	// */
	// @ResponseBody
	// @RequestMapping(value = { "/integral_transfer_Hsb" }, method =
	// {RequestMethod.POST }, produces = "application/json;charset=UTF-8")
	// public HttpRespEnvelope integralTransferHsb(@ModelAttribute TransInner
	// transInner, String tradePwd , String integralNum , String hsbNum,
	// HttpServletRequest request) {
	//
	// // 变量声明
	// LocalInfo lcalInfo = null; //本地平台信息
	// Double itnAccBalance = null; //积分账户余额
	// Map<String, Object> map = null; //返回数据临时结合
	// AccountBalance accBalance = null; //账户余额对象
	//
	// // Token验证
	// HttpRespEnvelope hre = super.checkSecureToken(request);
	// if (hre != null)
	// {
	// return hre;
	// }
	//
	// // 执行查询方法
	// try
	// {
	// //非空验证
	// RequestUtil.verifyParamsIsNotEmpty(
	// new Object[] { integralNum,
	// RespCode.PW_INTEGRAL_INVALID.getCode(),RespCode.PW_INTEGRAL_INVALID.getDesc()},
	// //转出积分积分数量
	// new Object[] { hsbNum,
	// RespCode.PW_HSBNUM_INVALID.getCode(),RespCode.PW_HSBNUM_INVALID.getDesc()
	// }, //转入互生币数量
	// new Object[] { transInner.getCustName(),
	// RespCode.PW_CUSTNAME_INVALID.getCode(),RespCode.PW_CUSTNAME_INVALID.getDesc()},
	// //客户名称
	// new Object[] { tradePwd,
	// RespCode.PW_TRADEPWD_INVALID.getCode(),RespCode.PW_TRADEPWD_INVALID.getDesc()}
	// //交易密码非空验证
	// );
	// //
	// map = new HashMap<String, Object>();
	//
	// // 查询积分账户余额
	// accBalance = balanceService.findAccNormal(transInner.getCustId(),
	// AccountType.ACC_TYPE_POINT10110.getCode());
	//
	// //非空验证
	// if (accBalance != null )
	// {
	//
	// //积分账户余额
	// itnAccBalance = DoubleUtil.parseDouble(accBalance.getAccBalance());
	//
	// //可用积分数
	// itnAccBalance = DoubleUtil.sub(itnAccBalance,
	// this.companyConfigService.getPersonLeastIntegration());
	//
	// //转出积分是否大于积分余额
	// if(itnAccBalance < DoubleUtil.parseDouble(integralNum)){
	// hre = new
	// HttpRespEnvelope(RespCode.PW_MAXINTEGRAL_INVALID.getCode(),RespCode.PW_MAXINTEGRAL_INVALID.getDesc());
	// return hre;
	// }
	// }
	//
	// //积分账户的可用积分数可转出，转出积分数为不小于的整数！
	// int itcm = this.companyConfigService.getIntegrationConvertibleMin();
	// if(DoubleUtil.parseDouble(integralNum) < itcm){
	// hre = new
	// HttpRespEnvelope(RespCode.PW_INTEGRATIONCONVERTIBLEMIN_INVALID.getCode(),RespCode.PW_INTEGRATIONCONVERTIBLEMIN_INVALID.getDesc());
	// return hre;
	// }
	//
	// //获取本平台的信息
	// lcalInfo = this.pubParamService.findSystemInfo();
	//
	// //内部转账构造相关属性
	// transInner.setTransType(TransInnerType.PV_TO_HSB.getCode()); //交易类型
	// transInner.setCustType(CustType.PERSON.getCode());//持卡人
	// transInner.setAmount(integralNum); //转账金额
	// transInner.setCurrencyCode(lcalInfo.getCurrencyCode()); //转账金额
	// transInner.setTargetAmount(hsbNum);//收款金额
	// transInner.setTargetCurrencyCode(lcalInfo.getCurrencyCode()); //收款币种
	// transInner.setExchangeRate(String.valueOf(lcalInfo.getExchangeRate()));
	// //货币转换比率
	// transInner.setTransInAccType(AccountType.ACC_TYPE_POINT10110.getCode());
	// transInner.setReqOperator(transInner.getCustName()); //申请操作员
	//
	// //执行内部转账
	// ransInnerService.saveTransInner(transInner);
	//
	// hre = new HttpRespEnvelope(map);
	//
	// }
	// catch (HsException e)
	// {
	// e.printStackTrace();
	// hre = new HttpRespEnvelope(e);
	// }
	// return hre;
	// }

	/**
	 * 查询余额信息
	 * 
	 * @param custId
	 *            客户号
	 * @param pointNo
	 *            互生卡号
	 * @param request
	 *            当前请求数据
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = { "/init_integral_investment" }, method = { RequestMethod.POST }, produces = "application/json;charset=UTF-8")
	public HttpRespEnvelope initIntegralInvestment(String custId, String pointNo,
			HttpServletRequest request) {

		// 变量声明
		Double itnAccBalance = null;
		Map<String, Object> map = null;
		AccountBalance accBalance = null;
		AccountEntrySum accEntrySum = null;
		// Token验证
		HttpRespEnvelope hre = super.checkSecureToken(request);
		if (hre != null) {
			return hre;
		}

		// 执行查询方法
		try {
			map = new HashMap<String, Object>();

			// 查询积分账户余额
			accBalance = balanceService.findAccNormal(custId,
					AccountType.ACC_TYPE_POINT10110.getCode());

			// 非空验证
			if (accBalance != null) {

				// 积分账户余额
				itnAccBalance = DoubleUtil.parseDouble(accBalance.getAccBalance());

				// 获取保底积分数
				int pliNumber = Integer.parseInt(balanceService.baseIntegral());

				// 可用积分数 =积分数-保底积分数
				itnAccBalance = DoubleUtil.sub(itnAccBalance, pliNumber);
				
				//如果小于零则显示0的积分余额
				itnAccBalance = itnAccBalance<0?0.00D:itnAccBalance;
				// 保存账户余额
				map.put("pointBlance", itnAccBalance);
			}

			// 积分投资整数倍！
			map.put("integrationInvIntMult", this.companyConfigService.getIntegrationInvIntMult());

			hre = new HttpRespEnvelope(map);

		} catch (HsException e) {
			hre = new HttpRespEnvelope(e);
		}

		return hre;
	}

	// /**
	// * 积分转互生币提交方法
	// * @param transInner 转帐记录实体
	// * @param tradePwd 交易密码
	// * @param integralNum 转出积分数
	// * @param hsbNum 转入互生币
	// * @param request
	// * @return
	// */
	// @ResponseBody
	// @RequestMapping(value = { "/integral_ investment" }, method =
	// {RequestMethod.POST }, produces = "application/json;charset=UTF-8")
	// public HttpRespEnvelope integralInvestment(@ModelAttribute TransInner
	// transInner, String tradePwd , String integralNum , HttpServletRequest
	// request) {
	//
	// // 变量声明
	// LocalInfo lcalInfo = null; //本地平台信息
	// Double itnAccBalance = null; //积分账户余额
	// Map<String, Object> map = null; //返回数据临时结合
	// AccountBalance accBalance = null; //账户余额对象
	//
	// // Token验证
	// HttpRespEnvelope hre = super.checkSecureToken(request);
	// if (hre != null)
	// {
	// return hre;
	// }
	//
	// // 执行查询方法
	// try
	// {
	// //非空验证
	// RequestUtil.verifyParamsIsNotEmpty(
	// new Object[] { integralNum,
	// RespCode.PW_INTEGRAL_INVALID.getCode(),RespCode.PW_INTEGRAL_INVALID.getDesc()},
	// //转出积分积分数量
	// new Object[] { transInner.getCustName(),
	// RespCode.PW_CUSTNAME_INVALID.getCode(),RespCode.PW_CUSTNAME_INVALID.getDesc()},
	// //客户号非空验证
	// new Object[] { tradePwd,
	// RespCode.PW_TRADEPWD_INVALID.getCode(),RespCode.PW_TRADEPWD_INVALID.getDesc()}
	// //交易密码非空验证
	// );
	// //
	// map = new HashMap<String, Object>();
	//
	// // 查询积分账户余额
	// accBalance = balanceService.findAccNormal(transInner.getCustId(),
	// AccountType.ACC_TYPE_POINT10110.getCode());
	//
	// //非空验证
	// if (accBalance != null )
	// {
	//
	// //积分账户余额
	// itnAccBalance = DoubleUtil.parseDouble(accBalance.getAccBalance());
	//
	// //可用积分数
	// itnAccBalance = DoubleUtil.sub(itnAccBalance,
	// this.companyConfigService.getPersonLeastIntegration());
	//
	// //转出积分是否大于积分余额
	// if(itnAccBalance < DoubleUtil.parseDouble(integralNum)){
	// hre = new
	// HttpRespEnvelope(RespCode.PW_MAXINTEGRAL_INVALID.getCode(),RespCode.PW_MAXINTEGRAL_INVALID.getDesc());
	// return hre;
	// }
	// }
	//
	// //积分账户的可用积分数可转出，转出积分数为不小于的整数！
	// int itim = this.companyConfigService.getIntegrationInvIntMult();
	// if(DoubleUtil.parseDouble(integralNum) < itim){
	// hre = new
	// HttpRespEnvelope(RespCode.PW_INTEGRATIONINVINTMULT_INVALID.getCode(),RespCode.PW_INTEGRATIONINVINTMULT_INVALID.getDesc());
	// return hre;
	// }
	//
	// //获取本平台的信息
	// lcalInfo = this.pubParamService.findSystemInfo();
	//
	// //内部转账构造相关属性
	// transInner.setTransType(TransInnerType.PV_TO_HSB.getCode()); //交易类型
	// transInner.setCustType(CustType.PERSON.getCode());//持卡人
	// transInner.setAmount(integralNum); //转账金额
	// transInner.setCurrencyCode(lcalInfo.getCurrencyCode()); //转账金额
	// transInner.setTargetAmount(integralNum);//收款金额
	// transInner.setTargetCurrencyCode(lcalInfo.getCurrencyCode()); //收款币种
	// transInner.setExchangeRate(String.valueOf(lcalInfo.getExchangeRate()));
	// //货币转换比率
	// transInner.setTransInAccType(AccountType.ACC_TYPE_POINT10110.getCode());
	// transInner.setReqOperator(transInner.getCustName()); //申请操作员
	//
	// //执行内部转账
	// ransInnerService.saveTransInner(transInner);
	//
	// hre = new HttpRespEnvelope(map);
	//
	// }
	// catch (HsException e)
	// {
	// e.printStackTrace();
	// hre = new HttpRespEnvelope(e);
	// }
	// return hre;
	// }

	/**
	 * 分页查询的条件约束
	 * 
	 * @param request
	 *            当前请求对象
	 * @param filterMap
	 *            查询条件Map
	 * @param sortMap
	 * @return
	 * @see com.gy.hsxt.access.web.common.controller.BaseController#beforeList(javax.servlet.http.HttpServletRequest,
	 *      java.util.Map, java.util.Map)
	 */
	@Override
	protected HttpRespEnvelope beforeList(HttpServletRequest request, Map filterMap, Map sortMap) {
		// 变量声明
		HttpRespEnvelope hre = null;

		hre = RequestUtil.checkParamIsNotEmpty(
				new Object[] { filterMap.get("beginDate"), "30001" }, // 开始时间非空验证
				new Object[] { filterMap.get("endDate"), "30002" }, // 结束时间非空验证
				new Object[] { filterMap.get("subject"), "3000" }); // 业务类别

		// 返回验证信息
		return hre;
	}

	/**
	 * @return
	 * @see com.gy.hsxt.access.web.common.controller.BaseController#getEntityService()
	 */
	@Override
	protected IBaseService getEntityService() {
		return investmentAccountService;
	}
}
