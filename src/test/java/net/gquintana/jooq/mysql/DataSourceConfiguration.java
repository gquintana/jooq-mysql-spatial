/*
 * Default License
 */

package net.gquintana.jooq.mysql;

import java.io.IOException;
import java.util.Properties;
import javax.sql.DataSource;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

/**
 *
 */
public final class DataSourceConfiguration {
    private final Properties CONFIGURATION = new Properties();
    private com.mysql.jdbc.jdbc2.optional.MysqlDataSource dataSource;
    private final Settings settings = new Settings();
    private void loadConfiguration() {
        try {
            CONFIGURATION.load(DataSourceConfiguration.class.getResourceAsStream("/configuration.properties"));
        } catch (IOException e) {
            e.printStackTrace(System.err);
        }
    }
    public DataSource dataSource() {
        if (dataSource == null) {
            loadConfiguration();
            dataSource=new com.mysql.jdbc.jdbc2.optional.MysqlDataSource();
            dataSource.setUrl(CONFIGURATION.getProperty("mysql.jdbc.url"));
            dataSource.setUser(CONFIGURATION.getProperty("mysql.jdbc.user"));
            dataSource.setPassword(CONFIGURATION.getProperty("mysql.jdbc.password"));
            settings.setExecuteLogging(true);
        }
        return dataSource;
    }
    public DSLContext jooq() {
        return  DSL.using(dataSource, SQLDialect.MYSQL, settings);
    }
}
