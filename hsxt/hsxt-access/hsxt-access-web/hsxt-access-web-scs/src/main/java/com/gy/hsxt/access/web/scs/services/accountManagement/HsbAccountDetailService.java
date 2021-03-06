package com.gy.hsxt.access.web.scs.services.accountManagement;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.gy.hsxt.ac.api.IAccountSearchService;
import com.gy.hsxt.access.web.common.service.BaseServiceImpl;
import com.gy.hsxt.ao.api.IAOCurrencyConvertService;
import com.gy.hsxt.ao.api.IAOExchangeHsbService;
import com.gy.hsxt.ao.bean.BuyHsb;
import com.gy.hsxt.ao.bean.HsbToCash;
import com.gy.hsxt.ao.bean.PvToHsb;
import com.gy.hsxt.bs.api.annualfee.IBSAnnualFeeService;
import com.gy.hsxt.bs.api.order.IBSInvestProfitService;
import com.gy.hsxt.bs.api.order.IBSOrderService;
import com.gy.hsxt.bs.bean.annualfee.AnnualFeeOrder;
import com.gy.hsxt.bs.bean.order.OrderCustom;
import com.gy.hsxt.bs.bean.order.PointDividend;

@Service("hsbAccountDetailService")
public class HsbAccountDetailService extends
		BaseServiceImpl<HsbAccountDetailService> implements
		IHsbAccountDetailService {
	@Autowired
	private IAccountSearchService accountSearch;

	@Autowired
	private IBSInvestProfitService ibsInvestProfitService;// 积投资分红

	@Autowired
	private IBSAnnualFeeService ibsAnnualFeeService;// 获取年费

	@Autowired
	private IBSOrderService ibsOrderService;// 订单详情

	@Autowired
	private IAOCurrencyConvertService iaoCurrencyConvertService;// 货币转换 积分转互生币

	@Autowired
	private IAOExchangeHsbService iaoExchangeHsbService;// 兑换互生币

	/**
	 * 查询积分投资分红详情
	 * 
	 * @param transNo
	 *            : 分红流水号
	 * @return PointDividend:积分投资分红实体类
	 */
	@Override
	public PointDividend getPointDividendInfo(String transNo) {
		return ibsInvestProfitService.getPointDividendInfo(transNo);
	}

	/**
	 * 获取年费订单详情
	 * 
	 * @param transNo
	 *            :订单编号
	 * @return AnnualFeeOrder: 年费订单实体类
	 */
	@Override
	public AnnualFeeOrder queryAnnualFeeOrder(String transNo) {
		return ibsAnnualFeeService.queryAnnualFeeOrder(transNo);
	}

	/**
	 * 线下兑换互生币 查询订单详情
	 * 
	 * @param orderNo
	 * @return
	 */
	@Override
	public OrderCustom getOrder(String transNo) {
		return ibsOrderService.getOrder(transNo);
	}

	/**
	 * 查询互生币转货币详情
	 * 
	 * @param transNo
	 * @return
	 */
	@Override
	public HsbToCash getHsbToCashInfo(String transNo) {
		return iaoCurrencyConvertService.getHsbToCashInfo(transNo);
	}

	/**
	 * 查询积分转互生币详情
	 * 
	 * @param transNo
	 * @return
	 */
	@Override
	public PvToHsb getPvToHsbInfo(String transNo) {
		return iaoCurrencyConvertService.getPvToHsbInfo(transNo);
	}

	/**
	 * 查询兑换互生币详情
	 * 
	 * @param transNo
	 * @return
	 */
	@Override
	public BuyHsb getBuyHsbInfo(String transNo) {
		return iaoExchangeHsbService.getBuyHsbInfo(transNo);
	}
}
