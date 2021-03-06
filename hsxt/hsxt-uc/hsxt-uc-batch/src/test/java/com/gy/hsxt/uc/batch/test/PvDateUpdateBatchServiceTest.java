/*
 * Copyright (c) 2015-2018 SHENZHEN GUIYI SCIENCE AND TECHNOLOGY DEVELOP CO., LTD. All rights reserved.
 *
 * 注意：本内容仅限于深圳市归一科技研发有限公司内部传阅，禁止外泄以及用于其他的商业目的 
 */
/*
 * Copyright (c) 2015-2018 SHENZHEN GUIYI SCIENCE AND TECHNOLOGY DEVELOP CO., LTD. All rights reserved.
 *
 * 注意：本内容仅限于深圳市归一科技研发有限公司内部传阅，禁止外泄以及用于其他的商业目的 
 */

package com.gy.hsxt.uc.batch.test;

import java.util.HashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.gy.hsi.ds.param.HsPropertiesConfigurer;
import com.gy.hsi.lc.client.log4j.BizLog;
import com.gy.hsi.lc.client.log4j.SystemLog;
import com.gy.hsxt.uc.batch.service.PvDateUpdateBatchService;

/**
 * 
 * @Package: com.gy.hsxt.uc.batch.test
 * @ClassName: PvDateUpdateBatchServiceTest
 * @Description: TODO
 * 
 * @author: lvyan
 * @date: 2015-10-26 下午2:56:47
 * @version V1.0
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:spring/spring-uc-batch.xml" })
public class PvDateUpdateBatchServiceTest {
	private Log log = LogFactory.getLog(this.getClass());

	@Autowired
	PvDateUpdateBatchService service;

	@Test
	public void excuteBatch() {
		// AC、INTEGRALACTIVE、20150720
		HashMap<String, String> args = new HashMap<String, String>();
		args.put("checkFileAddress", "C:\\gyxt\\testCheckFile.txt");

		boolean ret = service.excuteBatch("1",args);
		log.info(ret);
		Assert.assertTrue(ret);
	}

//	@Test
	public void aa() {
		String aa = HsPropertiesConfigurer.getProperty("uc.batch.aa");
		SystemLog.info("", "", "------------" + aa);
		BizLog.biz("", "","c", "------------" + aa);
	}

}
