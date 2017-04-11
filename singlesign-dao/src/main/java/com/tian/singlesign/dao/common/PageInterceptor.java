package com.tian.singlesign.dao.common;

import com.tian.common.other.PageParam;
import org.apache.ibatis.executor.parameter.ParameterHandler;
import org.apache.ibatis.executor.resultset.ResultSetHandler;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;
import org.apache.log4j.Logger;

import java.sql.*;
import java.util.Properties;

/**
 * 在sqlSessionFactory中注入一个默认的拦截器,在每一个sql执行前都会拦截,
 * 在这里我们实现的逻辑是,获取到执行方法的参数,如果是我们自定义的PageParam对象,就说明这个
 * 方法需要进行分页处理,然后获取参数进行组装,那么真正执行的sql语句,就是我们组装后的语句了.
 * 如果参数类型不是PageParam类型,则不处理,直接放行,还是按照原来的逻辑处理
 * Created by Administrator on 2016/12/30 0030.
 */
@Intercepts({
        @Signature(type = StatementHandler.class, method = "prepare", args = {Connection.class}),
        @Signature(type = ResultSetHandler.class, method = "handleResultSets", args = {Statement.class})
})
public class PageInterceptor implements Interceptor {
    Logger logger = Logger.getLogger(PageInterceptor.class);
    //插件运行的代码，它将代替原有的方法
    public Object intercept(Invocation invocation) throws Throwable {
        if (invocation.getTarget() instanceof StatementHandler) {
            StatementHandler statementHandler = (StatementHandler) invocation.getTarget();
            MetaObject metaStatementHandler = SystemMetaObject.forObject(statementHandler);

            BoundSql boundSql = (BoundSql) metaStatementHandler.getValue("delegate.boundSql");
            Object parameter=boundSql.getParameterObject();
            if(parameter instanceof PageParam){
                PageParam pageParam = (PageParam)parameter;
                // 分页参数作为参数对象parameterObject的一个属性
                String sql = boundSql.getSql();

                // 重写sql
                String countSql=concatCountSql(sql);
                logger.debug("组装的countSql: "+countSql);
                String pageSql=concatPageSql(sql,pageParam);
                logger.debug("组装的pageSql: "+pageSql);

                Connection connection = (Connection) invocation.getArgs()[0];
                //利用原始sql语句的方法执行
                PreparedStatement countStmt = connection.prepareStatement(countSql);
                //下面的这句话就是获取查询条件的参数
                ParameterHandler parameterHandler = (ParameterHandler) metaStatementHandler.getValue("delegate.parameterHandler");
                //经过set方法，就可以正确的执行sql语句
                parameterHandler.setParameters(countStmt);
                try {
                    ResultSet  rs = countStmt.executeQuery();
                    if (rs.next()) {
                        pageParam.setTotalNumber(rs.getInt(1));
                        logger.debug("查询的总记录数为: "+rs.getInt(1));
                    }
                } catch (SQLException e) {
                    logger.error(e);
                } finally {
                    try {
                        countStmt.close();
                    } catch (SQLException e) {
                        logger.error(e);
                    }
                }

                metaStatementHandler.setValue("delegate.boundSql.sql", pageSql);
            }
        }

        return invocation.proceed();
    }

    /**
     * 拦截类型StatementHandler
     */
    public Object plugin(Object target) {
        if (target instanceof StatementHandler) {
            return Plugin.wrap(target, this);
        } else {
            return target;
        }
    }

    public void setProperties(Properties properties) {

    }


    public String concatCountSql(String sql){
        StringBuffer sb=new StringBuffer("select count(*) from ");
        sql=sql.toLowerCase();
        sb.append(sql.substring(sql.indexOf("from")+4));
        return sb.toString();
    }

    public String concatPageSql(String sql,PageParam pageParam){
        StringBuffer sb=new StringBuffer();
        sb.append(sql);
        int start = (pageParam.getPageNumber()-1)*pageParam.getPageSize();
        sb.append(" limit ").append(start).append(" , ").append(pageParam.getPageSize());
        return sb.toString();
    }

}
