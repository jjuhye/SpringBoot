package com.jpa.shop.config;

import com.p6spy.engine.logging.Category;
import com.p6spy.engine.spy.P6SpyOptions;
import com.p6spy.engine.spy.appender.MessageFormattingStrategy;
import org.hibernate.engine.jdbc.internal.FormatStyle;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Locale;

/**
 * query multi line custom configure
 */
@Configuration
public class P6spyLogMessageFormatConfiguration {
    @PostConstruct
    public void setLogMessageFormat() {
        P6SpyOptions.getActiveInstance().setLogMessageFormat(CustomP6spySqlFormat.class.getName());
    }

    @Component
    public static class CustomP6spySqlFormat implements MessageFormattingStrategy {
        @Override
        public String formatMessage(int connectionId, String now, long elapsed, String category, String prepared, String sql, String url) {
            sql = formatSql(category, sql);
            return now + "|" + elapsed + "ms|" + category + "|connection " + connectionId + "|" + sql;
        }

        private String formatSql(String category,String sql) {
            if(sql ==null || sql.trim().equals("")) return sql;

            // Only format Statement, distinguish DDL And DML
            if (Category.STATEMENT.getName().equals(category)) {
                String tmpsql = sql.trim().toLowerCase(Locale.ROOT);
                if(tmpsql.startsWith("create") || tmpsql.startsWith("alter") || tmpsql.startsWith("comment")) {
                    sql = FormatStyle.DDL.getFormatter().format(sql);
                }else {
                    sql = FormatStyle.BASIC.getFormatter().format(sql);
                }
            }

            return sql;
        }
    }
}