/***************************************************************************
 *
 * This document contains confidential and proprietary information 
 * subject to non-disclosure agreements with GUIYI Technology, Ltd. 
 * This information shall not be distributed or copied without written 
 * permission from GUIYI technology, Ltd.
 *
 ***************************************************************************/

package com.gy.hsxt.gpf.gcs.common;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Properties;

import javax.xml.bind.PropertyException;

import org.apache.ibatis.executor.ErrorContext;
import org.apache.ibatis.executor.ExecutorException;
import org.apache.ibatis.executor.statement.BaseStatementHandler;
import org.apache.ibatis.executor.statement.RoutingStatementHandler;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.mapping.ParameterMode;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.property.PropertyTokenizer;
import org.apache.ibatis.scripting.xmltags.ForEachSqlNode;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.type.TypeHandler;
import org.apache.ibatis.type.TypeHandlerRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/***************************************************************************
 * <PRE>
 *  Project Name    : hsxt-gpf-gcs-service
 * 
 *  Package Name    : com.gy.hsxt.gpf.gcs.common
 * 
 *  File Name       : PagePlugin.java
 * 
 *  Creation Date   : 2015-7-2
 * 
 *  Author          : xiaofl
 * 
 *  Purpose         : 分页插件，对SQL ID匹配 ListPage的SQL进行物理分页
 * 
 * 
 *  History         : TODO
 * 
 * </PRE>
 ***************************************************************************/

@Intercepts({ @Signature(type = StatementHandler.class, method = "prepare", args = { Connection.class }) })
public class PagePlugin implements Interceptor {
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private static String dialect = "";
    private static String pageSqlId = "";

    public Object intercept(Invocation ivk) throws Throwable {

        if (ivk.getTarget() instanceof RoutingStatementHandler) {
            RoutingStatementHandler statementHandler = (RoutingStatementHandler) ivk
                    .getTarget();
            BaseStatementHandler delegate = (BaseStatementHandler) ReflectHelper
                    .getValueByFieldName(statementHandler, "delegate");
            MappedStatement mappedStatement = (MappedStatement) ReflectHelper
                    .getValueByFieldName(delegate, "mappedStatement");

            if (mappedStatement.getId().matches(pageSqlId)) {
                BoundSql boundSql = delegate.getBoundSql();
                Object parameterObject = boundSql.getParameterObject();
                if (parameterObject == null) {
                    throw new NullPointerException("parameterObject error");
                } else {
                    Connection connection = (Connection) ivk.getArgs()[0];
                    String sql = boundSql.getSql();
                    String countSql = "select count(0) from (" + sql + ") myCount";
                    logger.debug("总数sql 语句:"+countSql);
                    PreparedStatement countStmt = connection
                            .prepareStatement(countSql);
                    BoundSql countBS = new BoundSql(
                            mappedStatement.getConfiguration(), countSql,
                            boundSql.getParameterMappings(), parameterObject);
                    setParameters(countStmt, mappedStatement, countBS,
                            parameterObject);
                    ResultSet rs = countStmt.executeQuery();
                    int count = 0;
                    if (rs.next()) {
                        count = rs.getInt(1);
                    }
                    rs.close();
                    countStmt.close();
                    
                   //分页参数--上下文传参
                    PageInfo page = null;
        			PageContext context=PageContext.getContext();
        			page = context;
        			page.setTotalResults(count);
        			int totalPages = 0;
        			if(page.getPageSize() <= 0 ){
        				page.setPageSize(Constant.PAGE_SIZE);
        			}
        			if(count%page.getPageSize()==0){
        				totalPages = count / page.getPageSize();
        			}else{
        				totalPages = count / page.getPageSize() + 1;
        			}
        			page.setTotalPages(totalPages);

                    String pageSql = generatePageSql(sql, page);
                    logger.debug("page sql:"+pageSql);
                    ReflectHelper.setValueByFieldName(boundSql, "sql", pageSql);
                }
            }
        }
        return ivk.proceed();
    }

    private void setParameters(PreparedStatement ps,
            MappedStatement mappedStatement, BoundSql boundSql,
            Object parameterObject) throws SQLException {
        ErrorContext.instance().activity("setting parameters")
                .object(mappedStatement.getParameterMap().getId());
        List<ParameterMapping> parameterMappings = boundSql
                .getParameterMappings();
        if (parameterMappings != null) {
            Configuration configuration = mappedStatement.getConfiguration();
            TypeHandlerRegistry typeHandlerRegistry = configuration
                    .getTypeHandlerRegistry();
            MetaObject metaObject = parameterObject == null ? null
                    : configuration.newMetaObject(parameterObject);
            for (int i = 0; i < parameterMappings.size(); i++) {
                ParameterMapping parameterMapping = parameterMappings.get(i);
                if (parameterMapping.getMode() != ParameterMode.OUT) {
                    Object value;
                    String propertyName = parameterMapping.getProperty();
                    PropertyTokenizer prop = new PropertyTokenizer(propertyName);
                    if (parameterObject == null) {
                        value = null;
                    } else if (typeHandlerRegistry
                            .hasTypeHandler(parameterObject.getClass())) {
                        value = parameterObject;
                    } else if (boundSql.hasAdditionalParameter(propertyName)) {
                        value = boundSql.getAdditionalParameter(propertyName);
                    } else if (propertyName
                            .startsWith(ForEachSqlNode.ITEM_PREFIX)
                            && boundSql.hasAdditionalParameter(prop.getName())) {
                        value = boundSql.getAdditionalParameter(prop.getName());
                        if (value != null) {
                            value = configuration.newMetaObject(value)
                                    .getValue(
                                            propertyName.substring(prop
                                                    .getName().length()));
                        }
                    } else {
                        value = metaObject == null ? null : metaObject
                                .getValue(propertyName);
                    }
                    TypeHandler typeHandler = parameterMapping.getTypeHandler();
                    if (typeHandler == null) {
                        throw new ExecutorException(
                                "There was no TypeHandler found for parameter "
                                        + propertyName + " of statement "
                                        + mappedStatement.getId());
                    }
                    typeHandler.setParameter(ps, i + 1, value,
                            parameterMapping.getJdbcType());
                }
            }
        }
    }


    private String generatePageSql(String sql, PageInfo page) {
        if (page != null && (dialect !=null && !"".equals(dialect))) {
        	int currentResultIndex = 0;
        	if(page.getPageNo()>1){
        		currentResultIndex = (page.getPageNo()-1)*page.getPageSize();
        	}
        	
            StringBuffer pageSql = new StringBuffer();
            if ("mysql".equals(dialect)) {
                pageSql.append(sql);
                pageSql.append(" limit " + currentResultIndex + ","
                        + page.getPageSize());
            } else if ("oracle".equals(dialect)) {
                pageSql.append("select * from (select tmp_tb.*,ROWNUM row_id from (");
                pageSql.append(sql);
                pageSql.append(")  tmp_tb where ROWNUM<=");
                pageSql.append(currentResultIndex + page.getPageSize());
                pageSql.append(") where row_id>");
                pageSql.append(currentResultIndex);
            }
            return pageSql.toString();
        } else {
            return sql;
        }
    }

    public Object plugin(Object arg0) {
        return Plugin.wrap(arg0, this);
    }

    public void setProperties(Properties p) {
        dialect = p.getProperty("dialect");
        if (dialect ==null || dialect.equals("")) {
            try {
                throw new PropertyException("dialect property is not found!");
            } catch (PropertyException e) {
                e.printStackTrace();
            }
        }
        pageSqlId = p.getProperty("pageSqlId");
        if (pageSqlId ==null || pageSqlId.equals("")) {
            try {
                throw new PropertyException("pageSqlId property is not found!");
            } catch (PropertyException e) {
                e.printStackTrace();
            }
        }
    }

}