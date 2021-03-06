/*
 * Copyright (c) 2015-2018 SHENZHEN GUIYI SCIENCE AND TECHNOLOGY DEVELOP CO., LTD. All rights reserved.
 *
 * 注意：本内容仅限于深圳市归一科技研发有限公司内部传阅，禁止外泄以及用于其他的商业目的 
 */
package com.gy.hsxt.access.web.scs.controllers.accountManagement;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.gy.hsxt.ac.bean.AccountBalance;
import com.gy.hsxt.access.web.common.bean.HttpRespEnvelope;
import com.gy.hsxt.access.web.common.constant.ASRespCode;
import com.gy.hsxt.access.web.common.controller.BaseController;
import com.gy.hsxt.access.web.common.service.IBaseService;
import com.gy.hsxt.access.web.common.utils.RequestUtil;
import com.gy.hsxt.access.web.scs.services.accountManagement.IBalanceService;
import com.gy.hsxt.access.web.scs.services.accountManagement.ITransInnerService;
import com.gy.hsxt.access.web.scs.services.common.ICommService;
import com.gy.hsxt.access.web.scs.services.common.IPubParamService;
import com.gy.hsxt.access.web.scs.services.common.SCSConfigService;
import com.gy.hsxt.access.web.scs.services.companyInfo.IBankCardService;
import com.gy.hsxt.access.web.scs.services.safeSet.IPwdSetService;
import com.gy.hsxt.ao.bean.TransOut;
import com.gy.hsxt.ao.enumtype.TransReason;
import com.gy.hsxt.bp.bean.BusinessCustParamItemsRedis;
import com.gy.hsxt.bp.bean.BusinessSysParamItemsRedis;
import com.gy.hsxt.bp.client.api.BusinessParamSearch;
import com.gy.hsxt.common.constant.AccountType;
import com.gy.hsxt.common.constant.BusinessParam;
import com.gy.hsxt.common.constant.Channel;
import com.gy.hsxt.common.constant.CustType;
import com.gy.hsxt.common.constant.RespCode;
import com.gy.hsxt.common.exception.HsException;
import com.gy.hsxt.common.utils.DateUtil;
import com.gy.hsxt.common.utils.DoubleUtil;
import com.gy.hsxt.gp.constant.GPConstant.GPTransSysFlag;
import com.gy.hsxt.lcs.bean.LocalInfo;
import com.gy.hsxt.uc.as.api.enumerate.UserTypeEnum;
import com.gy.hsxt.uc.as.bean.common.AsBankAcctInfo;

/***************************************************************************
 * <PRE>
 * Description 		: 货币账户控制类
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
@RequestMapping("currencyAccount")
public class CurrencyAccountController extends BaseController {

    @Resource
    private IBalanceService balanceService; // 账户余额查询服务类

    @Autowired
    private IPubParamService pubParamService;// 平台服务公共信息服务类

    @Autowired
    private SCSConfigService scsConfigService; // 全局配置文件

    @Resource
    private IBankCardService bankCardService; // 银行卡操作服务类

    @Resource
    private ITransInnerService transInnerService;// 转账操作服务类

    @Autowired
    public BusinessParamSearch businessParamSearch;

    @Resource
    private IPwdSetService pwdSetService;   //交易密码服务类
    
    @Resource
    private ICommService commService;  //公共接口类

	/**
	 * 用户查询积分账户余额，返回积分账户总余额，可用积分数和今日积分数
	 * 
	 * @param custId
	 *            操作员客户号
	 * @param entCustId
	 *            企业客户号
	 * @param request
	 *            当前请求数据
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value =
	{ "/find_currency_blance" }, method =
	{ RequestMethod.POST }, produces = "application/json;charset=UTF-8")
	public HttpRespEnvelope findCurrencyBlance(String custId, String entCustId, String token,
			HttpServletRequest request)
	{

		// 变量声明

		Map<String, Object> map = null;
		AccountBalance accBalance = null;
		HttpRespEnvelope hre = null;

		// 执行查询方法
		try
		{
			// Token验证
			super.checkSecureToken(request);

			map = new HashMap<String, Object>();

			// 非空数据验证
			RequestUtil.verifyParamsIsNotEmpty(new Object[]
			{ entCustId, RespCode.AS_ENT_CUSTID_INVALID.getCode(), RespCode.AS_ENT_CUSTID_INVALID.getDesc() } // 企业客户号
			);

			// 查询用户查询积分账户余额
			accBalance = balanceService.findAccNormal(entCustId, AccountType.ACC_TYPE_POINT30110.getCode());
			if (accBalance != null)
			{
				map.put("currencyBlance", accBalance.getAccBalance());
			}

			hre = new HttpRespEnvelope(map);

		} catch (HsException e)
		{
			hre = new HttpRespEnvelope(e);
		}

		return hre;
	}

	/**
	 * 货币转银行界面数据初始化
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
	@RequestMapping(value =
	{ "/init_currency_transfer_bank" }, method =
	{ RequestMethod.GET, RequestMethod.POST }, produces = "application/json;charset=UTF-8")
	public HttpRespEnvelope initCurrencyTransferBank(String custId, String entCustId, String pointNo, HttpServletRequest request)
	{

		// 变量声明
		LocalInfo lcalInfo = null; // 本地平台信息
		Map<String, Object> map = null; // 返回数据临时结合
		AccountBalance accBalance = null; // 账户余额对象
		HttpRespEnvelope hre = null;
		List<AsBankAcctInfo> bankAcctList = null;// 银行卡集合

		// 执行查询方法
		try
		{
			// Token验证
			super.checkSecureToken(request);

			map = new HashMap<String, Object>();
			// 非空数据验证
			RequestUtil.verifyParamsIsNotEmpty(new Object[] { entCustId, RespCode.AS_ENT_CUSTID_INVALID }); // 企业客户号
			
			// 查询积分账户余额
			accBalance = balanceService.findAccNormal(entCustId, AccountType.ACC_TYPE_POINT30110.getCode());

			// 非空验证
			if (accBalance != null)
			{
				// 保存账户余额
				map.put("pointBlance", accBalance.getAccBalance());
			}

			// 转出货币最小整数！
			map.put("currencyMin", this.scsConfigService.getMonetaryConvertibleMin());

			// 转银行手续费
			// map.put("bankFee", this.scsConfigService.getCurrencyFee());

			// 获取本平台的信息
			lcalInfo = this.pubParamService.findSystemInfo();

			// 币种
			map.put("currencyCode", lcalInfo.getCurrencyNameCn());

			// 查询用户下绑定的银行卡列表
			bankAcctList = this.bankCardService.findBankAccountList(entCustId, UserTypeEnum.ENT.getType());
			map.put("bankList", bankAcctList);
			
			// 获取货币转银行规则参数
	        Map<String, BusinessSysParamItemsRedis> itemMap = businessParamSearch.searchSysParamItemsByGroup(BusinessParam.HB_CHANGE_BANK.getCode());

	        // 获取企业单笔最大转账金额
	        String itemMaxValue = getSysItemsValue(itemMap, BusinessParam.COMAPNY_SINGLE_TRANSFER_MAX);
	        map.put("transferMaxAmount", itemMaxValue);
	        
	        // 获取单日最大限额
	        String dailyLimitAmt = getSysItemsValue(itemMap, BusinessParam.COMAPANY_SINGLE_DAILY_TRANSFER_MAX);
	        map.put("dayTransferMaxAmount", dailyLimitAmt);
	        
	        // 获取单日已发生金额
	        BusinessCustParamItemsRedis dailyHadAmtItem = businessParamSearch.searchCustParamItemsByIdKey(custId,BusinessParam.SINGLE_DAILY_HAD_TRANSFER.getCode());
	        //map.put("dayTransferAmount", dailyHadAmtItem.getSysItemsValue());
	        // 如果累计金额未过期，比较时以累计金额
            if (dailyHadAmtItem != null && DateUtil.getCurrentDateNoSign().equals(dailyHadAmtItem.getDueDate())) {
                map.put("dayTransferAmount", dailyHadAmtItem.getSysItemsValue());
            } else {
                map.put("dayTransferAmount", "0.00");
            }
	        
	        //获取当前企业货币转银行的限时信息
	        Map<String,String> infoMap = this.scsConfigService.getRestrictMap(BusinessParam.CASH_TO_BANK, entCustId);
	        
	        //非空数据验证
			if(null != infoMap && infoMap.size() > 0){
				map.putAll(infoMap);
			}
			
			//获取业务限制数据
            Map<String, String> restrictMap = commService.getBusinessRestrict(entCustId, BusinessParam.CASH_TO_BANK);
            map.put("restrict", restrictMap);

			hre = new HttpRespEnvelope(map);

		} catch (HsException e)
		{
			hre = new HttpRespEnvelope(e);
		}
		return hre;
	}

	/**
     * 获取业务参数中的公共系统参数值,从参数组集合中获取指定参数项
     * 
     * @param itemMap
     *            参数组Map, 其中key为参数项key， value为参数项对象
     * @param itemKey
     *            参数项代码
     * @return
     */
    private String getSysItemsValue(Map<String, BusinessSysParamItemsRedis> itemMap, BusinessParam itemKey) {
        BusinessSysParamItemsRedis item = itemMap.get(itemKey.getCode());
        return item.getSysItemsValue();
    }
    
	/**
	 * 货币转银行界面数据初始化
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
	@RequestMapping(value =
	{ "/getBankTransFee" }, method =
	{ RequestMethod.GET, RequestMethod.POST }, produces = "application/json;charset=UTF-8")
	public HttpRespEnvelope getBankTransFee(@ModelAttribute TransOut transOut, HttpServletRequest request)
	{

		// 变量声明
		HttpRespEnvelope hre = null;
		// 执行查询方法
		try
		{
			// Token验证
			super.checkSecureToken(request);

			// 非空验证
			RequestUtil.verifyParamsIsNotEmpty(new Object[]
			{ transOut.getAmount(), RespCode.PW_AMOUNT_TRANSFERRED_INVALID.getCode(),
					RespCode.PW_AMOUNT_TRANSFERRED_INVALID.getDesc() }, // 交易金额
					new Object[]
			{ transOut.getBankCityNo(), RespCode.PW_REGINFO_CITY_INVALID.getCode(),
					RespCode.PW_REGINFO_CITY_INVALID.getDesc() }, // 收款账户开户城市代码
					new Object[]
			{ transOut.getBankNo(), RespCode.PW_BANK_NO_INVALID.getCode(), RespCode.PW_BANK_NO_INVALID.getDesc() } // 收款人开户银行代码
			);

			// 获取手续费
			String ransFee = this.transInnerService.getBankTransFee(transOut, GPTransSysFlag.URGENT);

			hre = new HttpRespEnvelope(ransFee);

		} catch (HsException e)
		{
			hre = new HttpRespEnvelope(e);
		}
		return hre;
	}

	/**
	 * 提交货币转银行信息
	 * 
	 * @param transOut
	 *            货币转银行账户
	 * @param randomToken
	 *            随机token
	 * @param tradePwd
	 *            交易密码
	 * @param accId
	 *            银行id
	 * @param request
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value =
	{ "/currency_transfer_bank" }, method =
	{ RequestMethod.POST }, produces = "application/json;charset=UTF-8")
	public HttpRespEnvelope currencyTransferBank(TransOut transOut, String randomToken, String entCustId,
			String custEntName, String tradePwd, String accId, HttpServletRequest request)
	{

		// 变量声明
		HttpRespEnvelope hre = null;

		// 执行查询方法
		try
		{
			// Token验证
			super.checkSecureToken(request);

			// 非空数据验证
			RequestUtil.verifyParamsIsNotEmpty(new Object[]
			{ tradePwd, RespCode.PW_TRADEPWD_INVALID.getCode(), RespCode.PW_TRADEPWD_INVALID.getDesc() }, // 交易密码
					new Object[]
			{ randomToken, RespCode.AS_SECURE_TOKEN_INVALID.getCode(), RespCode.AS_SECURE_TOKEN_INVALID.getDesc() }, // 随机token
					new Object[]
			{ transOut.getHsResNo(), RespCode.AS_CUSTID_INVALID.getCode(), RespCode.AS_CUSTID_INVALID.getDesc() }, // 客户互生号
					new Object[]
			{ entCustId, RespCode.AS_ENT_CUSTID_INVALID.getCode(), RespCode.AS_ENT_CUSTID_INVALID.getDesc() }, // 企业客户号
					new Object[]
			{ accId, RespCode.PW_BANK_ACC_NAME_INVALID.getCode(), RespCode.PW_BANK_ACC_NAME_INVALID.getDesc() }, // 银行账号
					new Object[]
			{ transOut.getAmount(), RespCode.PW_TARGETAMOUNT_NULL.getCode(), RespCode.PW_TARGETAMOUNT_NULL.getDesc() }, // 货币金额
					new Object[]
			{ custEntName, ASRespCode.PW_CONTRACT_ENTCUSTNAME_INVALID.getCode(),
					ASRespCode.PW_CONTRACT_ENTCUSTNAME_INVALID.getDesc() } // 企业名称
			);

			// 正整数验证
			RequestUtil.verifyPositiveInteger(transOut.getAmount(), RespCode.PW_TARGETAMOUNT_INVALID);

			// 呼叫中心的特殊处理方法
			tradePwd = RequestUtil.encodeBase64String(request, tradePwd);

			// 交易密码验证
			pwdSetService.checkTradePwd(entCustId, tradePwd, UserTypeEnum.ENT.getType(), randomToken);

			// 转出最小互生币数
			Double itcm = DoubleUtil.parseDouble(this.scsConfigService.getMonetaryConvertibleMin());
			if (DoubleUtil.parseDouble(transOut.getAmount()) < itcm)
			{
				hre = new HttpRespEnvelope(RespCode.PW_CURRENCY_MIN_INVALID.getCode(),
						RespCode.PW_CURRENCY_MIN_INVALID.getDesc());
				return hre;
			}

			// 货币转银行构造相关属性

			transOut.setCustType(CustType.SERVICE_CORP.getCode()); // 服务公司
			transOut.setCustName(custEntName); // 企业名称
			//transOut.setReqOptId(transOut.getCustId());        //操作员编号
			transOut.setReqOptId(request.getParameter("custName"));        //操作员编号
			transOut.setReqOptName(request.getParameter("operName"));
			transOut.setCustId(entCustId);
			transOut.setTransReason(TransReason.ACCORD_CASH.getCode());// 转账原因
			transOut.setChannel(Channel.WEB.getCode()); // 终端渠道

			// 执行货币转银行
			/* Map<Object, Object> retMap= */
			transInnerService.saveTransOut(transOut, Long.parseLong(accId));

			hre = new HttpRespEnvelope();

		} catch (HsException e)
		{
			hre = new HttpRespEnvelope(e);
		}
		return hre;
	}

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
	/*
	 * @Override protected HttpRespEnvelope beforeList(HttpServletRequest
	 * request, Map filterMap, Map sortMap) { // 变量声明 HttpRespEnvelope hre =
	 * null;
	 * 
	 * //开始时间于结束时间验证 RequestUtil.verifyQueryDate(
	 * filterMap.get("beginDate").toString
	 * (),filterMap.get("endDate").toString());
	 * 
	 * // 返回验证信息 return hre; }
	 */

	/**
	 * 货币账户明细查询
	 * 
	 * @param request
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value =
	{ "/detailed_page" }, method =
	{ RequestMethod.GET, RequestMethod.POST }, produces = "application/json;charset=UTF-8")
	public HttpRespEnvelope searchAccEntryPage(HttpServletRequest request, HttpServletResponse response)
	{
		HttpRespEnvelope hre = null;

		try
		{
			// Token验证
			super.verifySecureToken(request);

			// 分页查询
			request.setAttribute("serviceName", balanceService);
			request.setAttribute("methodName", "searchAccEntryPage");
			hre = super.doList(request, response);

		} catch (HsException e)
		{
			hre = new HttpRespEnvelope(e);
		}

		return hre;
	}

	/**
	 * 获取积分账户操作详情
	 * 
	 * @Description:
	 * @author: likui
	 * @created: 2016年1月26日 下午7:40:12
	 * @param request
	 * @param response
	 * @return
	 * @return : HttpRespEnvelope
	 * @version V3.0.0
	 */
	@ResponseBody
	@RequestMapping(value =
	{ "/get_acc_opt_detailed" }, method =
	{ RequestMethod.GET, RequestMethod.POST }, produces = "application/json;charset=UTF-8")
	public HttpRespEnvelope getAccOptDetailed(HttpServletRequest request, HttpServletResponse response)
	{
		HttpRespEnvelope hre = null;

		try
		{
			// Token验证
			super.verifySecureToken(request);
			String transNo = request.getParameter("transNo");
			String transType = request.getParameter("transType");
			hre = new HttpRespEnvelope(balanceService.queryAccOptDetailed(transNo, transType));
		} catch (HsException e)
		{
			hre = new HttpRespEnvelope(e);
		}
		return hre;
	}

	/**
	 * @return
	 * @see com.gy.hsxt.access.web.common.controller.BaseController#getEntityService()
	 */
	@Override
	protected IBaseService getEntityService()
	{
		return null;
	}
}
