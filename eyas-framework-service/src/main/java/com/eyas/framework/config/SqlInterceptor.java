package com.eyas.framework.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.executor.parameter.ParameterHandler;
import org.apache.ibatis.executor.resultset.ResultSetHandler;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;

import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Properties;


/**
 * 完成插件签名：
 * 告诉MyBatis当前插件用来拦截哪个对象的哪个方法
 * 四大对象（Executor，StatementHandler，ParameterHandler，ResultSetHandler）
 * type  指四大对象拦截哪个对象，
 * method ： 代表拦截哪个方法  ,在StatementHandler 中查看，需要拦截的方法
 * args   ：代表参数
 *
 */
@Intercepts(
        value = {
                @Signature(
                        type = Executor.class,
                        method = "query",
                        args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class}),
                @Signature(
                        type = StatementHandler.class,
                        method = "prepare",
                        args = {Connection.class, Integer.class}),
                @Signature(
                        type = ParameterHandler.class,
                        method = "setParameters",
                        args = {PreparedStatement.class})
        }
)
@Slf4j
public class SqlInterceptor implements Interceptor {


    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        Object target = invocation.getTarget();
        Object result = null;

        if (target instanceof Executor) {
            MappedStatement mappedStatement = (MappedStatement) invocation.getArgs()[0];
            Object parameter = invocation.getArgs()[1];
            BoundSql boundSql = mappedStatement.getBoundSql(parameter);
            String oldsql = boundSql.getSql();
            log.info("old:" +  oldsql);

            long start = System.currentTimeMillis();
            Method method = invocation.getMethod();
            /*执行方法*/
            result = invocation.proceed();
            long end = System.currentTimeMillis();
            //LogUtil.info("[TimerInterceptor] execute [" + method.getName() + "] cost [" + (end - start) + "] ms");
            log.info("xxxxxxx Executor Interceptor, method " + method.getName());
        }else if(target instanceof StatementHandler){
            StatementHandler statementHandler = (StatementHandler)invocation.getTarget();
            Method method = invocation.getMethod();
            /*执行方法*/
            result = invocation.proceed();
            log.info("xxxxxx StatementHandler Interceptor, method " + method.getName());
        }else if(target instanceof ParameterHandler){
            ParameterHandler parameterHandler = (ParameterHandler)invocation.getTarget();

            Method method = invocation.getMethod();
            /*执行方法*/
            result = invocation.proceed();
            log.info("xxxxxx ParameterHandler Interceptor, method " + method.getName());
        }else if(target instanceof ResultSetHandler){
            Method method = invocation.getMethod();
            /*执行方法*/
            result = invocation.proceed();
            log.info("xxxxxx ResultSetHandler Interceptor, method " + method.getName());
        }

        return result;
    }

    public Object plugin(Object o) {
        return Plugin.wrap(o, this);
    }

    public void setProperties(Properties properties) {
    }
}