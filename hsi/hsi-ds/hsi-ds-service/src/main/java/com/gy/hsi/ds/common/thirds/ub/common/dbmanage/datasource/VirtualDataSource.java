package com.gy.hsi.ds.common.thirds.ub.common.dbmanage.datasource;

import java.io.Serializable;
import java.util.List;
import java.util.Random;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.InitializingBean;

import com.gy.hsi.ds.common.thirds.ub.common.log.AopLogFactory;

public class VirtualDataSource implements Serializable, BeanNameAware, InitializingBean {

    private static final long serialVersionUID = 2391107997087589405L;
    private static Logger log = AopLogFactory.getLogger(VirtualDataSource.class);

    protected List<String> masterSet;
    protected List<String> slaveSet;

    protected List<String> masterPools;
    protected List<String> slavePools;
    protected int loadbalance = 1; // 1:random 2:polling 3:master-backup

    protected Random randomprovider = new Random();
    protected int current = 0;

    protected String vdbid;

    public List<String> getMasterSet() {
        return masterSet;
    }

    public void setMasterSet(List<String> masterSet) {
        this.masterSet = masterSet;
        this.masterPools = masterSet;
    }

    public List<String> getSlaveSet() {
        return slaveSet;
    }

    public void setSlaveSet(List<String> slaveSet) {
        this.slaveSet = slaveSet;
        this.slavePools = slaveSet;
    }

    public void setMasterPools(List<String> masterPool) {
        this.masterPools = masterPool;
    }

    public List<String> getMasterPools() {
        return masterPools;
    }

    public void setSlavePools(List<String> slavePools) {
        this.slavePools = slavePools;
    }

    public List<String> getSlavePools() {
        return slavePools;
    }

    public void setLoadbalance(int loadbalance) {
        this.loadbalance = loadbalance;
    }

    public String getDataSourceKey(boolean readMaster) {
        if (readMaster) {
            return getMasterDataSourceKey();
        } else {
            return getSlaveDataSourceKey();
        }
    }

    public String getMasterDataSourceKey() {
        if (masterPools == null) {
            throw new IllegalStateException("master datasource is null,can't execute the write sql");
        }
        int idx = chooseDSKeyIndex(masterPools.size());
        return masterPools.get(idx);
    }

    public String getSlaveDataSourceKey() {
        if (slavePools == null || slavePools.size() == 0) {
            log.warn("read null slave datasource!");
            return getMasterDataSourceKey();
        }
        int idx = chooseDSKeyIndex(slavePools.size());
        return slavePools.get(idx);
    }

    private int chooseDSKeyIndex(int N) {
        int idx = 0;
        if (loadbalance == 1) {
            idx = randomprovider.nextInt(N);
            log.debug("use random datasource.");
        } else if (loadbalance == 2) {
            current++;
            if (current >= N) {
                current = 0;
            }
            idx = current;
            log.debug("user polling datasource.");
        } else if (loadbalance == 3) {
            idx = 0;
            log.debug("user master-backup datasource.");
        }
        return idx;
    }

    public void afterPropertiesSet() throws Exception {
        if (masterPools == null) {
            throw new IllegalStateException("visualdatasource initial fail,master pool is null!");
        }
    }

    public void setBeanName(String name) {
        this.vdbid = name;
    }

    public String getVdbid() {
        return this.vdbid;
    }

}
