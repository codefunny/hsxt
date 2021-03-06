define([ "commSrc/comm" ], function() {
	comm.langConfig["invoice"] = {
		// 后台返回的状态码
			22000 : "操作成功",
			22001 : "操作失败",
			160121:	"该企业互生号不存在",

			35100 : "税率申请编号不能为空",
			
			35101 : "税率申请状态不能为空",
			
			35102 : "审批意见不能为空",
			
			35103 : "审批人不能为空",
			
			35104 : "企业客户号不能为空",
			35105 : "企业互生号不能为空",			
			35106 : "企业名称不能为空",	
			35107 : "发票金额不能为空",
			35108 : "银行支行名称不能为空",			
			35109 : "银行银行账号不能为空",			
			35110 : "地址不能为空",			
			35111 : "电话不能为空",			
			35112 : "纳税人识别号不能为空",			
			35113 : "开票内容不能为空",			
			35114 : "业务类型不能为空",			
			35115 : "快递公司名称不能为空",			
			35116 : "运单号不能为空",			
			35117 : "发票ID不能为空",
			
			35118 : "发票状态不能为空",
			
			35119 : "开票方不能为空",
			
			35120 : "开票金额太大",
			
			35121 : "必须一次性开完发票",
			35122 : "至少录入一条发票记录",
			35123 : "发票类型不能为空",
			35124 : "发票代码不能为空",
			35125 : "发票金额不能为空",
			35126 : "发票号码不能为空",
			
			160101 : "该企业客户号找不到企业信息",
			160147 : "企业不存在",
			160355 : "必传参数为空",
			
			12333 : "除了消费积分扣除，其他发票申请必须与剩余金额相同",
			
			13202:  "字符参数不是数字类型",
			12331 : '发票金额必须大于应开发票金额',
			12339 : '该业务类型的发票金额已开完',
			12340 : '消费积分扣除累计金额至少1000元才可开发票',
			12017:  "参数为空",
			12332 : "该客户没有需开的发票",
			12344 : "发票已签收,请勿重复签收",
			36549 : "审批意见不能为空",
			invoice_apprreason_length:'审批意见不能超过300个字',
		status : {
			0 : "待审核",
			1 : "待审批复核",
			2 : "审批复核通过",
			3 : "审批驳回",
			4 : "审批复核驳回"
		},
		invoice_status : {
			0 : "申请中",
			1 : "审批驳回 ",
			2 : "已开票 ",
			3 : "已配送 ",
			4 : "已签收 ",
			5 : "拒签"
		},
		delivery_status : {
			3 : "已配送",
			4 : "已签收 ",
			5 : "拒签"
		},
		delivery_status2 : {
			0 : "申请中",
			1 : "审批驳回 "
		},
		delivery_status3 : {
			2 : "已开票 ",
			3 : "已配送",
			4 : "已签收 "
		},
		invoice_type : {
			1 : "增值税普通发票",
			0 : "增值税专用发票"
		},
		post_way : {
			0 : "快递配送 ",
			1 : "自由配送 ",
		},
		receive_way : {
			0 : "物流单号确认签收 ",
			1 : "电话确认签收 ",
			2 : "到期自动签收"
		},
		receive_way_select : {
			0 : "物流单号确认签收 ",
			1 : "电话确认签收 "
		},
		custType :{
			2 : '成员企业',
			3 : '托管企业',
			4 : '服务公司'
		},
		creType :{
			1 : '身份证',
			2 : '护照',
			3 : '营业执照'
		},
		bizType :{
			'PC001' : '服务公司系统资源费',
			'PC002' : '申购工具',
			'PC003' : '系统使用年费',
			'PC005' : '消费积分',
			'PC006' : '个性卡制定服务费',
			'PC007' : '成员企业系统资源费',
			'PC008' : '首段托管企业系统资源费',
			'PC009' : '创业托管企业系统资源费',
			'PC010' : '托管企业系统资源费(全部资源)',
			'PC011' : '其他段托管企业系统资源费'
		},
		determine : '确定',
		cancel : '取消',
		query : '查看',
		approval : '审批',
		edit : '修改',
		invoiceRefuse : '拒签原因不能超过300个字',
		refuseRemarkmaxLength : '审批意见不能超过300个字',
		workTaskRefuseAccept : '拒绝受理',
		workTaskRefuseConfir : '您正在拒绝受理扣款复核的工单！',
		workTaskHangUp : '挂起',
		workTaskHangUpConfir : '您正在挂起扣款复核的工单！',
		select_postWay_prompt:"请选择配送方式",
		input_expressName_prompt:"请输入承运公司",
		input_trackingNo_prompt:"请输入运单号"
	}
});