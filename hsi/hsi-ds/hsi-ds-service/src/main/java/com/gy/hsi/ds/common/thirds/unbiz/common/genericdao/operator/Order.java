/**
 * adx-common#com.gy.hsi.ds.common.thirds.ub.generic.dao.operator.Order.java
 * 下午2:53:44 created by Darwin(Tianxin)
 */
package com.gy.hsi.ds.common.thirds.unbiz.common.genericdao.operator;

/**
 * 排序
 *
 * @author Darwin(Tianxin)
 */
public class Order implements Pair {

    private String column;

    private boolean asc;

    /**
     * @param column
     * @param asc
     */
    public Order(String column, boolean asc) {
        this.column = column;
        this.asc = asc;
    }

    public boolean isAsc() {
        return asc;
    }

    public String getColumn() {
        return column;
    }

    public Object getValue() {
        return asc;
    }

}
