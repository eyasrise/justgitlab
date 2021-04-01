package com.eyas.framework.config;

import com.eyas.framework.EmptyUtil;
import com.eyas.framework.data.EyasFrameworkDto;
import com.eyas.framework.utils.TenantThreadLocal;
import lombok.extern.slf4j.Slf4j;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.StringValue;
import net.sf.jsqlparser.expression.operators.relational.ExpressionList;
import net.sf.jsqlparser.expression.operators.relational.MultiExpressionList;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.Statements;
import net.sf.jsqlparser.statement.insert.Insert;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.select.SelectItem;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.reflection.DefaultReflectorFactory;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;

import java.sql.Connection;
import java.text.DateFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author Created by eyas on 2021/2/22.
 */
@Intercepts({@Signature(type = StatementHandler.class, method = "prepare", args = {Connection.class, Integer.class})})
@Slf4j
public class MySqlInterceptor implements Interceptor {

    @Override
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
//        log.info("新sql===>" + newSql);
        metaObject.setValue("delegate.boundSql.sql", newSql);
        MappedStatement mappedStatement = (MappedStatement)invocation.getArgs()[0];
//        Object parameter =null;
//        if (invocation.getArgs().length>1){
//            parameter = invocation.getArgs()[1];
//        }
//        BoundSql boundSql = mappedStatement.getBoundSql(parameter);
//        Configuration configuration = mappedStatement.getConfiguration();
//
//
//        List<ParameterMapping> parameterMappings = boundSql.getParameterMappings();
//        Object parameterObject = boundSql.getParameterObject();
//
//        MetaObject newMetaObject = configuration.newMetaObject(parameterObject);
//        HashMap<String, String> hashMap = new HashMap<>();
//        if (parameterObject !=null &&parameterMappings.size() >0){
//            for (ParameterMapping parameterMapping : parameterMappings) {
//                String propertyName = parameterMapping.getProperty();
//
//                String parameterValue = getParameterValue(newMetaObject.getValue(propertyName));
//                hashMap.put(propertyName,parameterValue);
//            }
//        }
//        log.info("param===>" + hashMap);
        return invocation.proceed();
    }

    private static String getParameterValue(Object obj) {
        String value = null;
        if (obj instanceof String) {
            value = "'" + obj.toString() + "'";
        } else if (obj instanceof Date) {
            DateFormat formatter = DateFormat.getDateTimeInstance(DateFormat.DEFAULT, DateFormat.DEFAULT, Locale.CHINA);
            value = "'" + formatter.format(new Date()) + "'";
        } else {
            if (obj != null) {
                value = obj.toString();
            } else {
                value = "";
            }
        }
        return value;
    }


    public String rooter(Statement statement, BoundSql boundSql) throws Throwable{
        if (statement instanceof Select){
            return this.processSelect((Select) statement);
        }else if (statement instanceof Insert){
            return this.processInsert((Insert) statement, boundSql);
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
            if ("TENANT_CODE".equals(selectItem.toString())){
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
                if ("TENANT_CODE".equals(column.toString())){
                    flag.set(true);
                }
            });
            if (flag.get()) {
                // 获取租户code坐标
                int index = 0;
                for (int i=0;i<columnList.size();i++){
                    if ("TENANT_CODE".equals(columnList.get(i).toString())){
                        index = i;
                    }
                }
                // 单条数据跟批量数据不一样
                // 给指定坐标index的租户code赋值
                if (insert.getItemsList() instanceof ExpressionList){
                    ExpressionList expressionList = (ExpressionList)insert.getItemsList();
                    expressionList.getExpressions().set(index, new StringValue(String.valueOf(tenantCode)));
                    //
                }else if(insert.getItemsList() instanceof MultiExpressionList){
                    MultiExpressionList multiExpressionList = (MultiExpressionList) insert.getItemsList();
                    for (ExpressionList expression:
                            multiExpressionList.getExprList()) {
                        expression.getExpressions().set(index, new StringValue(String.valueOf(tenantCode)));
                    }
                }
                // 移除问号影响
                List<ParameterMapping> parameterMappingList = boundSql.getParameterMappings();
                List<ParameterMapping> parameterMappingList1 = new ArrayList<>();

                for (int i=0;i<boundSql.getParameterMappings().size();i++){
                    if (boundSql.getParameterMappings().get(i).toString().contains("tenantCode")){
                        parameterMappingList1.add(parameterMappingList.get(i));
                    }
                }
                if (!EmptyUtil.dealListForceEmpty(parameterMappingList1)) {
                    parameterMappingList.removeAll(parameterMappingList1);
                }
                return insert.toString();
            }
        }
        return boundSql.getSql();
    }



    @Override
    public Object plugin(Object o) {
        return Plugin.wrap(o, this);
    }

    @Override
    public void setProperties(Properties properties) {
    }
}
