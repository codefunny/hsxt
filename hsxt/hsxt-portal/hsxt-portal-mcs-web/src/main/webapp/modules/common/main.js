﻿/*
 *加载全局对象
 * 1.含有jquery ,jquery plugins , underscore,  项目的公用方法
 * 2.使用公用方法：comm.functionName(XXX)...
 * 3.引用公用的路径 
       公用src :   'commSrc'
	   公用css:    'commCss'
	   公用模板：'commTpl'
	   公用数据：'commDat'
 * 4.模板自动填充数据，局部刷新
 */

if(document.cookie.match(new RegExp("(^| )custId=([^;]*)(;|$)")) == null){
	window.location = "/quitUrl";
}

reqConfig.setLocalPath("common");
reqConfig.setLocalPath("callCenter"); 
reqConfig.setLocalPath("accountManage");
reqConfig.setLocalPath("companyInfo");
reqConfig.setLocalPath("coDeclaration");
reqConfig.setLocalPath("resouceManage");
reqConfig.setLocalPath("messageCenter");
reqConfig.setLocalPath("systemManage");
reqConfig.setLocalPath("safeSet");
reqConfig.setLocalPath("contractManage");
reqConfig.setLocalPath("workflow");
reqConfig.setLocalPath("workopt");

require.config(reqConfig);



define(['GY',"accountManageLan","jquerycharcount",'commonLan'], function(GY) {
	
/*
 *  加载头部 
 *
 */
require(["commSrc/frame/header"],function(tpl){	

});

 
/*
 *  加载左边导航栏
 *
 */
require(["commSrc/frame/sideBar"],function(tpl){	

});
 	 

/*
 *  加载右边服务模块
 *
 */
require(["commSrc/frame/rightBar"],function(tpl){	

});


/*
 *  加载中间菜单
 *
*/
require(["commSrc/frame/nav"],function(tpl){	

}); 


/*
 *  加载页脚
 *
 */
require(["commSrc/frame/footer"],function(src){	
 
});

	  
//IE8 及以下不支持 filter() 20160308李智
if (!Array.prototype.filter) {
	Array.prototype.filter = function(fun /* , thisp */) {
		var len = this.length;
		if (typeof fun != "function") {
			throw new TypeError();
		}
		var res = new Array();
		var thisp = arguments[1];
		for ( var i = 0; i < len; i++) {
			if (i in this) {
				var val = this[i]; // in case fun mutates this
				if (fun.call(thisp, val, i, this)) {
					res.push(val);
				}
			}
		}
		return res;
	};
}

// 对Date的扩展，将 Date 转化为指定格式的String
// 月(M)、日(d)、小时(h)、分(m)、秒(s)、季度(q) 可以用 1-2 个占位符，
// 年(y)可以用 1-4 个占位符，毫秒(S)只能用 1 个占位符(是 1-3 位的数字)
// 例子：
// (new Date()).Format("yyyy-MM-dd hh:mm:ss.S") ==> 2006-07-02 08:09:04.423
// (new Date()).Format("yyyy-M-d h:m:s.S") ==> 2006-7-2 8:9:4.18
Date.prototype.Format = function(fmt) { // author: meizz
	var o = {
		"M+" : this.getMonth() + 1, // 月份
		"d+" : this.getDate(), // 日
		"h+" : this.getHours(), // 小时
		"m+" : this.getMinutes(), // 分
		"s+" : this.getSeconds(), // 秒
		"q+" : Math.floor((this.getMonth() + 3) / 3), // 季度
		"S" : this.getMilliseconds()
	// 毫秒
	};
	if (/(y+)/.test(fmt))
		fmt = fmt.replace(RegExp.$1, (this.getFullYear() + "")
				.substr(4 - RegExp.$1.length));
	for ( var k in o)
		if (new RegExp("(" + k + ")").test(fmt))
			fmt = fmt.replace(RegExp.$1, (RegExp.$1.length == 1) ? (o[k])
					: (("00" + o[k]).substr(("" + o[k]).length)));
	return fmt;
}
	//保存登录token
	$("#oldToken").val(comm.getRequestParams()["token"]);

});
