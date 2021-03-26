package com.eyas.framework.config;

import com.eyas.framework.EmptyUtil;
import com.eyas.framework.data.EyasFrameworkDto;
import com.eyas.framework.utils.TenantThreadLocal;
import lombok.extern.slf4j.Slf4j;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.StringValue;
import net.sf.jsqlparser.expression.operators.relational.ExpressionList;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.Statements;
import net.sf.jsqlparser.statement.insert.Insert;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.select.SelectItem;
import net.sf.jsqlparser.statement.update.Update;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.reflection.DefaultReflectorFactory;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;

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
        Statements statements = CCJSqlParserUtil.parseStatements(sql);
        EmptyUtil.dealEmptyDataReturn(statements.getStatements(), "mysql插件异常，statements.getStatements()为空");
        EmptyUtil.dealEmptyDataReturn(statements.getStatements().get(0), "mysql插件异常，statements.getStatements().get(0)为空");
        Statement statement = statements.getStatements().get(0);
        String newSql = this.rooter(statement, boundSql);
        log.info("新sql===>" + newSql);
        metaObject.setValue("delegate.boundSql.sql", newSql);
        return invocation.proceed();
    }

    public String rooter(Statement statement, BoundSql boundSql) throws Throwable{
        if (statement instanceof Select){
            return this.processSelect((Select) statement);
        }else if (statement instanceof Insert){
            return this.processInsert((Insert) statement, boundSql);
        }else if (statement instanceof Update){
//            return this.processUpdate((Update) statement, boundSql);
        }
        return boundSql.getSql();
    }

    public String processSelect(Select select) throws Throwable{
        StringBuffer whereSql = new StringBuffer();
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

        return select.toString();
    }

    public String processInsert(Insert insert, BoundSql boundSql){
        EyasFrameworkDto systemUser = (EyasFrameworkDto)TenantThreadLocal.getSystemUser();
        if (EmptyUtil.isNotEmpty(systemUser)) {
            Long tenantCode = systemUser.getTenantCode();
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
                return insert.toString();
            }
        }
        return boundSql.getSql();
    }

    public String processUpdate(Update update, BoundSql boundSql) throws Throwable{
        EyasFrameworkDto systemUser = (EyasFrameworkDto)TenantThreadLocal.getSystemUser();

        if (EmptyUtil.isNotEmpty(systemUser)){
            Long tenantCode = systemUser.getTenantCode();
            List<Column> columnList = update.getColumns();
            AtomicBoolean flag = new AtomicBoolean(false);
            columnList.forEach(column -> {
                if (column.toString().equals("TENANT_CODE")){
                    flag.set(true);
                }
            });
            if (flag.get()) {
                // 如果包含TENANT_CODE直接赋值
                Expression where = update.getWhere();
                Expression expression;
                StringBuffer whereSql = new StringBuffer();
                whereSql.append("TENANT_CODE =");
                whereSql.append(tenantCode);
                if (where == null) {
                    if (whereSql.length() > 0) {

                    }
                } else {
                    if (whereSql.length() > 0 && EmptyUtil.isEmpty(update.getJoins())) {
                        whereSql.append(" and ( ").append(where.toString()).append(" )");
                    } else {
                        whereSql = new StringBuffer();
                        whereSql.append(where.toString());
                    }
                    expression = CCJSqlParserUtil.parseCondExpression(whereSql.toString());
                    update.setWhere(expression);
                }
            }
        }



        return update.toString();
    }



    public Object plugin(Object o) {
        return Plugin.wrap(o, this);
    }

    public void setProperties(Properties properties) {
    }
}
