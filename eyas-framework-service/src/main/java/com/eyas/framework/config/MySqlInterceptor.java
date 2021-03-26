package com.eyas.framework.config;

import com.eyas.framework.EmptyUtil;
import com.eyas.framework.data.EyasFrameworkDto;
import com.eyas.framework.utils.TenantThreadLocal;
import lombok.extern.slf4j.Slf4j;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.LongValue;
import net.sf.jsqlparser.expression.operators.relational.ExpressionList;
import net.sf.jsqlparser.expression.operators.relational.ItemsList;
import net.sf.jsqlparser.expression.operators.relational.MultiExpressionList;
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
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.reflection.DefaultReflectorFactory;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;

import java.io.StringReader;
import java.sql.Connection;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicReference;

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
            return invocation.proceed();
        } else if (statements.getStatements().get(0) instanceof Insert) {
            EyasFrameworkDto systemUser = (EyasFrameworkDto)TenantThreadLocal.getSystemUser();
            if (EmptyUtil.isEmpty(systemUser)) {
                return invocation.proceed();
            } else {
                Long tenantCode = systemUser.getTenantCode();
                Insert insert = (Insert)parserManager.parse(new StringReader(sql));
                insert.getColumns().add(new Column("TENANT_CODE"));
                if (insert.getItemsList() != null) {
                    ItemsList itemsList = insert.getItemsList();
                    if (itemsList instanceof MultiExpressionList) {
                        ((MultiExpressionList)itemsList).getExprList().forEach((dto) -> {
                            dto.getExpressions().add(new LongValue(tenantCode));
                        });
                    } else {
                        ((ExpressionList)insert.getItemsList()).getExpressions().add(new LongValue(tenantCode));
                    }
                }

                return invocation.proceed();
            }
        } else {
            return invocation.proceed();
        }
    }

    public Object plugin(Object o) {
        return Plugin.wrap(o, this);
    }

    public void setProperties(Properties properties) {
    }
}
