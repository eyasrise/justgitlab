package com.eyas.framework.config;


import com.eyas.framework.EmptyUtil;
import com.eyas.framework.data.EyasFrameworkDto;
import com.eyas.framework.utils.TenantThreadLocal;
import lombok.extern.slf4j.Slf4j;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.parser.CCJSqlParserManager;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.reflection.DefaultReflectorFactory;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;

import java.io.StringReader;
import java.sql.Connection;
import java.util.Properties;

/**
 * @author Created by eyas on 2021/2/22.
 */
@Intercepts({@Signature(type = StatementHandler.class, method = "prepare", args = {Connection.class, Integer.class})})
@Slf4j
public class MySqlInterceptor implements Interceptor {
    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        StatementHandler statementHandler = (StatementHandler) invocation.getTarget();
        MetaObject metaObject = MetaObject.forObject(statementHandler, SystemMetaObject.DEFAULT_OBJECT_FACTORY, SystemMetaObject.DEFAULT_OBJECT_WRAPPER_FACTORY, new DefaultReflectorFactory());
        //先拦截到RoutingStatementHandler，里面有个StatementHandler类型的delegate变量，其实现类是BaseStatementHandler，然后就到BaseStatementHandler的成员变量mappedStatement
        BoundSql boundSql = statementHandler.getBoundSql();

        //获取到原始sql语句
        String sql = boundSql.getSql();

        StringBuffer whereSql = new StringBuffer();
        CCJSqlParserManager parserManager = new CCJSqlParserManager();
        Select select = (Select) parserManager.parse(new StringReader(sql));
        PlainSelect plain = (PlainSelect) select.getSelectBody();
        EyasFrameworkDto systemUser = (EyasFrameworkDto) TenantThreadLocal.getSystemUser();


        if (systemUser !=null){
            whereSql.append("TENANT_CODE =");
            whereSql.append(systemUser.getTenantCode());
        }

        //--todo--需要获取动态租户code的逻辑--

        // 获取当前查询条件
        Expression where = plain.getWhere();
        if (where == null) {
            if (whereSql.length() > 0) {
                Expression expression = CCJSqlParserUtil
                        .parseCondExpression(whereSql.toString());
                Expression whereExpression = (Expression) expression;
                plain.setWhere(whereExpression);
            }
        } else {
            // 判断plain.getJoins()是否为空，用来判定是不是联表查询，因为联表查询比较复杂，所有直接忽略联表查询
            // 链表查询自己写租户关联逻辑
            if (whereSql.length() > 0 && EmptyUtil.isEmpty(plain.getJoins())) {
                //where条件之前存在，需要重新进行拼接
                whereSql.append(" and ( ").append(where.toString()).append(" )");
            } else {
                //新增片段不存在，使用之前的sql
                whereSql = new StringBuffer();
                whereSql.append(where.toString());
            }
            Expression expression = CCJSqlParserUtil
                    .parseCondExpression(whereSql.toString());
            plain.setWhere(expression);
        }
        metaObject.setValue("delegate.boundSql.sql", select.toString());
        log.info("新sql===>" + select.toString());
        return invocation.proceed();
    }

    @Override
    public Object plugin(Object o) {
        return Plugin.wrap(o, this);
    }

    @Override
    public void setProperties(Properties properties) {

    }
}
