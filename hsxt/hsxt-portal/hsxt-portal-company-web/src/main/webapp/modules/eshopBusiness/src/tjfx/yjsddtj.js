﻿define(['text!eshopBusinessTpl/tjfx/yjsddtj.html'], function(tpl){
	return {
		showPage : function(){
			$('#busibox').html(tpl);
			
			var self = this,
			searchTable = null;
			
			/*下拉列表*/
			$("#yjsddtj_tjlx").selectList({
				options:[
					{name:'日统计',value:'1'},
					{name:'月统计',value:'2'},
					{name:'企业互生号',value:'4'}
				]
			}).change(function(e){
				var v = $(this).attr('optionValue');
				
			});
			$("#yjsddtj_zffs").selectList({
				options:[
					{name:'全部',value:''},
					{name:'现金支付',value:'1'},
					{name:'互生币支付',value:'2'},
					{name:'网银支付',value:'3'}
				]	
			}).change(function(e){
				var v = $(this).attr('optionValue');
				
			});
			/*日期控件*/
			$("#yjsddtj_beginDate").datepicker({dateFormat:'yy-mm-dd'});
			$("#yjsddtj_endDate").datepicker({dateFormat:'yy-mm-dd'});
			
			//查询单击事件
			$('#btn_cx').click(function(){
				if (!self.validateData()) {
					return;
				}
				
				searchTable = $.fn.bsgrid.init('yjsddtj_table', {
					//url: '',
					localData: [{qyhsh:'06186010000',jsrq:'2014-08-28 10:11:53',ddzje:3162.00,jsje:3382.00,ddzjf:372.00},{qyhsh:'06186010000',jsrq:'2014-08-27 10:11:53',ddzje:3160.00,jsje:3380.00,ddzjf:370.00}],
					otherParames: {
						beginDate : $("#yjsddtj_beginDate").val(),
						endDate : $("#yjsddtj_endDate").val()
					},
					//不显示空白行
					displayBlankRows: false,
					//不显示无页可翻的提示
					pageIncorrectTurnAlert: false,
					//隔行变色
					stripeRows: true,
					//不显示选中行背景色
					rowSelectedColor: false,
					pageSize: 10
				});
			});
			//这一行需要去掉，此处用于显示数据
			$('#btn_cx').click();
		},
		validateData : function(){
			return comm.valid({
				formID : '#yjsddtj_form',
				rules : {
					
				},
				messages : {
					
				}
			});
		}
	};
});