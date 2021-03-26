package com.eyas.framework.config;

import com.eyas.framework.EmptyUtil;
import com.eyas.framework.data.EyasFrameworkDto;
import com.eyas.framework.utils.TenantThreadLocal;
import lombok.extern.slf4j.Slf4j;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.StringValue;
import net.sf.jsqlparser.expression.operators.relational.ExpressionList;
import net.sf.jsqlparser.parser.CCJSqlParserManager;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.statement.Statements;
import net.sf.jsqlparser.statement.insert.Insert;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.select.SelectItem;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.reflection.DefaultReflectorFactory;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;

import java.io.StringReader;
import java.sql.Connection;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Stream;

/**
 * @author Created by eyas on 2021/2/22.
 */
@Intercepts({@Signature(type = StatementHandler.class, method = "prepare", args = {Connection.class, Integer.class})})
@Slf4j
public class MySqlInterceptor implements Interceptor {

    public Object intercept(Invocation invocation) throws Throwable {
        StatementHandler statementHandler = (StatementHandler)invocation.getTarget();
        MetaObject metaObject = MetaObject.forObject(statementHandler, SystemMetaObject.DEFAULT_OBJECT_FACTORY, SystemMetaObject.DEFAULT_OBJECT_WRAPPER_FACTORY, new DefaultReflectorFactory());
        BoundSql boundSql = statementHandler.getBoundSql();
        String sql = boundSql.getSql();
        StringBuffer whereSql = new StringBuffer();
        CCJSqlParserManager parserManager = new CCJSqlParserManager();
        Statements statements = CCJSqlParserUtil.parseStatements(sql);
        if (statements.getStatements().get(0) instanceof Select) {
            Select select = (Select)parserManager.parse(new StringReader(sql));
            PlainSelect plain = (PlainSelect)select.getSelectBody();
            EyasFrameworkDto systemUser = (EyasFrameworkDto)TenantThreadLocal.getSystemUser();
            List<SelectItem> selectItemList = plain.getSelectItems();
            AtomicReference<Boolean> flag = new AtomicReference<>(false);
            selectItemList.stream().forEach(selectItem -> {
                if (selectItem.toString().equals("TENANT_CODE")){
                    flag.set(true);
                }
            });
            if (systemUser != null && flag.get()) {
                whereSql.append("TENANT_CODE =");
                whereSql.append(systemUser.getTenantCode());
            }

            Expression where = plain.getWhere();
            Expression expression;
            if (where == null) {
                if (whereSql.length() > 0) {
                    expression = CCJSqlParserUtil.parseCondExpression(whereSql.toString());
                    plain.setWhere(expression);
                }
            } else {
                if (whereSql.length() > 0 && EmptyUtil.isEmpty(plain.getJoins())) {
                    whereSql.append(" and ( ").append(where.toString()).append(" )");
                } else {
                    whereSql = new StringBuffer();
                    whereSql.append(where.toString());
                }

                expression = CCJSqlParserUtil.parseCondExpression(whereSql.toString());
                plain.setWhere(expression);
            }

            metaObject.setValue("delegate.boundSql.sql", select.toString());
            log.info("新sql===>" + select.toString());
        } else if (statements.getStatements().get(0) instanceof Insert) {
            EyasFrameworkDto systemUser = (EyasFrameworkDto)TenantThreadLocal.getSystemUser();
            if (EmptyUtil.isNotEmpty(systemUser)) {
                Long tenantCode = systemUser.getTenantCode();
                Insert insert = (Insert)parserManager.parse(new StringReader(sql));
                List<Column> columnList = insert.getColumns();
                AtomicBoolean flag = new AtomicBoolean(false);
                columnList.forEach(column -> {
                    if (column.toString().equals("TENANT_CODE")){
                        flag.set(true);
                    }
                });
                if (flag.get()) {
                    AtomicReference<Integer> index= new AtomicReference<>(0);
                    // 获取下标
                    Stream.iterate(0, i -> i + 1).limit(columnList.size()).forEach(i -> {
                        if (columnList.get(i).toString().equals("TENANT_CODE")){
                            index.set(i);
                        }
                    });
                    ((ExpressionList)insert.getItemsList()).getExpressions().set(index.get(), new StringValue(String.valueOf(tenantCode)));
                    AtomicReference<Integer> index2= new AtomicReference<>(0);
                    Stream.iterate(0, i -> i + 1).limit(boundSql.getParameterMappings().size()).forEach(i -> {
                        if (boundSql.getParameterMappings().get(i).toString().contains("tenantCode")){
                            index2.set(i);
                        }
                    });
                    List<ParameterMapping> parameterMappingList = boundSql.getParameterMappings();
                    parameterMappingList.remove(parameterMappingList.get(index2.get()));
                    metaObject.setValue("delegate.boundSql.sql", insert.toString());
                    log.info("新sql===>" + insert.toString());
                }
            }
            return invocation.proceed();
        } else {
            return invocation.proceed();
        }
        return invocation.proceed();
    }

    public Object plugin(Object o) {
        return Plugin.wrap(o, this);
    }

    public void setProperties(Properties properties) {
    }
}
