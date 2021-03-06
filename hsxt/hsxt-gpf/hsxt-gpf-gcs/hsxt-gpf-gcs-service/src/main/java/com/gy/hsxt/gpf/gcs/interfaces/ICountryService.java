package com.gy.hsxt.gpf.gcs.interfaces;

import java.util.List;

import com.gy.hsxt.gpf.gcs.bean.Country;

/***************************************************************************
 * <PRE>
 *  Project Name    : hsxt-gpf-gcs-service
 * 
 *  Package Name    : com.gy.hsxt.gpf.gcs.interfaces.ICountryService
 * 
 *  File Name       : ICountryService.java
 * 
 *  Creation Date   : 2015-7-11
 * 
 *  Author          : liuhq
 * 
 *  Purpose         : 国家代码ICountryService接口
 * 
 * 
 *  History         : TODO
 * 
 * </PRE>
 ***************************************************************************/
public interface ICountryService {
    /**
     * 插入记录
     * 
     * @param country
     *            实体类 必须，非null
     * @return 返int类型 1成功，其他失败
     */
    public int insert(Country country);

    /**
     * 读取某条记录
     * 
     * @param countryNo
     *            国家代码 必须，非null
     * @return 返回实体类Country
     */
    public Country getCountry(String countryNo);

    /**
     * 获取数据分页列表
     * 
     * @param Country实体类
     *            必须，非null
     * @return 返回List<Country>,数据为空，返回空List<Country>
     */
    public List<Country> getCountryListPage(Country country);

    /**
     * 获取全部有效国家
     * 
     * @return 返回List<Country>,数据为空，返回空List<Country>
     */
    public List<Country> getCountryAll();

    /**
     * 获取查询国家全部字段列表
     * 
     * @return 返回List<Country>,数据为空，返回空List<Country>
     */
    public List<Country> findContryAll();

    /**
     * 更新某条记录
     * 
     * @param Country实体类
     *            必须，非null
     * @return 返回int类型 1成功，其他失败
     */
    public int update(Country country);

    /**
     * 删除某条记录
     * 
     * @param country
     *            实体类 必须，非null
     * @return 返int类型 1成功，其他失败
     */
    public int delete(String countryNo);

    /**
     * 判断是否已存在相同
     * 
     * @param countryNo
     *            国家代码 必须，非null
     * @return 返回String 大于等于1存在，0不存在
     */
    public boolean existCountry(String cityNo);

    /**
     * 读取大于某个版本号的数据
     * 
     * @param version
     *            版本号 必须，非null
     * @return 返回List<Country>,数据为空，返回空List<Country>
     */
    public List<Country> queryCountrySyncData(Long version);

}
