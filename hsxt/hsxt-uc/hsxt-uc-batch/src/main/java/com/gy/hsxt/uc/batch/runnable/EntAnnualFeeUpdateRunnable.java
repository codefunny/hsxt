/*
 * Copyright (c) 2015-2018 SHENZHEN GUIYI SCIENCE AND TECHNOLOGY DEVELOP CO., LTD. All rights reserved.
 *
 * 注意：本内容仅限于深圳市归一科技研发有限公司内部传阅，禁止外泄以及用于其他的商业目的 
 */
package com.gy.hsxt.uc.batch.runnable;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.gy.hsi.ds.api.IDSBatchServiceListener;
import com.gy.hsxt.uc.batch.mapper.BatchMapper;

/**
 * 读取单个文件，根据文件内容，批量更新用户积分时间
 * 
 * @Package: com.gy.hsxt.uc.batch.runnable
 * @ClassName: PvDateUpdateRunnable
 * @Description: TODO
 * 
 * @author: lvyan
 * @date: 2015-10-21 上午10:27:45
 * @version V1.0
 */
public class EntAnnualFeeUpdateRunnable implements Runnable {
    private Log log = LogFactory.getLog(this.getClass());

    String fileAddr; // 文件地址

    BatchMapper batchMapper; // 数据库操作dao

    int batchCount; // 批量入库条数

    CountDownLatch countDownLatch;

    IDSBatchServiceListener listener;
    
//	企业客户号|企业互生号|企业名称|是否欠年费|年费截止日期
//	是否欠年费：0否 1是
//	年费截止日期：YYYY-MM-DD    	
	String[] filds={"entCustId","entResNo","entCustName","isOweFee","expireDate"};
	int fildLen=filds.length;
    /**
     * 读取单个文件，根据文件内容，批量更新用户积分时间
     * 
     * @param fileAddr
     *            文件地址
     * @param batchMapper
     *            数据库处理dao
     * @param batchCount
     *            批处理数量，每batchCount条数据，提交一次数据库操作
     * @param countDownLatch
     *            线程阻塞器
     * @param listener
     *            任务监听器
     */
    public EntAnnualFeeUpdateRunnable(String fileAddr, BatchMapper batchMapper, int batchCount,
            CountDownLatch countDownLatch, IDSBatchServiceListener listener) {
        this.fileAddr = fileAddr;
        this.batchMapper = batchMapper;
        this.batchCount = batchCount;
        this.countDownLatch = countDownLatch;
        this.listener = listener;

    }

    void reportStatus(int code, String msg) {
        if (listener != null)
        {
            log.info(Thread.currentThread().getName() + msg);
            // 发送进度通知
//            listener.reportStatus(code, msg);
        }
        else
        {
            log.info(Thread.currentThread().getName() + code + " IDSBatchServiceListener is null,msg=" + msg);
        }
    }

    public void run() {
        try
        {
            reportStatus(2, "准备读取文件:" + fileAddr);
            doAction();
            reportStatus(2, "单个文件处理完成:" + fileAddr);
        }
        catch (Exception e)
        {
            reportStatus(1, "单个文件处理出错:" + fileAddr);
            e.printStackTrace();
        }
        finally
        {
            countDownLatch.countDown(); // 汇报线程执行完毕
        }
    }

    /**
     * 读取文件内容，并把数据入库
     */
    void doAction() throws Exception {
        String encoding = "UTF-8";
        File file = new File(fileAddr);
        // 读取记账数据文件
        InputStreamReader isReader = null;
        BufferedReader bfReader = null;
        HashMap<String,String> entDataMap= new HashMap<>(8);
        try
        {
            String fileName = file.getName(); // yyyymmdd_n.txt
            String[] fileNameCols = fileName.split("_");
            isReader = new InputStreamReader(new FileInputStream(file), encoding);
            bfReader = new BufferedReader(isReader);
            ArrayList<Map<String,String>> entIdList = new ArrayList<>(1024);
            while (true)
            {
                // 获取一行记账数据( cust_id|cust_type)(type:1持卡人，2成员企业，3托管企业)
                String lineTxt = bfReader.readLine();

                // 读取到END就结束
                if (lineTxt == null || lineTxt.equals("END"))
                {
                    log.info(fileAddr + Thread.currentThread().getName() + "end =" + lineTxt);
                    break;
                }

                String[] dataFilds = lineTxt.split("\\|");
                entDataMap=getMap(dataFilds);
                    entIdList.add(entDataMap);
                    if (entIdList.size() == batchCount)
                    {
                        updateEnt(batchMapper,  entIdList);
                        entIdList = new ArrayList<Map<String,String>>(1024);
                    }
                

            }
            updateEnt(batchMapper,  entIdList);
        }

        catch (FileNotFoundException e)
        {
            reportStatus(1, "执行出错,单个文件处理出错FileNotFoundException:" + fileAddr);
            e.printStackTrace();
            throw e;
        }
        catch (Exception e)
        {
            reportStatus(1, fileAddr + "执行出错,单个文件处理出错:" + e.getMessage());
            e.printStackTrace();
            throw e;
        }
        finally
        {

            // 关闭读取
            if (bfReader != null)
            {
                try
                {
                    bfReader.close();
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
            // 关闭流
            if (isReader != null)
            {
                try
                {
                    isReader.close();
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }

        }

    }
    
    private HashMap<String,String> getMap(String[] entData){
    	
    	HashMap<String,String> ret = new HashMap<String,String>(8);
    	for(int i=0;i<fildLen;i++){
    		ret.put(filds[i], entData[i]);
    	}
    	return ret;
    }

    

    /**
     * 企业年费状态更新 ,同步方法，避免并发进行表更新     * 
     * @param dataList 企业年费数据列表
     */
    private synchronized static void updateEnt(BatchMapper batchMapper, List<Map<String,String>> dataList) {
        if (dataList == null || dataList.isEmpty())
        {
            System.err.println(Thread.currentThread().getName() + "updateEnt, dataList == null || dataList.isEmpty");
            return;
        }
//        HashMap<String, Object> map = new HashMap<String, Object>();
//        map.put("dataList", dataList);
        System.out.println(Thread.currentThread().getName() + " begin updateEnt dataList.size=" + dataList.size());
        int updateCount = batchMapper.updateEntAnnualFee(dataList);
        System.out.println(Thread.currentThread().getName() + "end updateCust updateCount=" + updateCount);
    }
}
