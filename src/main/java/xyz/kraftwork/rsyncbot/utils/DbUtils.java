/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package xyz.kraftwork.rsyncbot.utils;

import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.jdbc.JdbcPooledConnectionSource;
import java.sql.SQLException;
import org.flywaydb.core.Flyway;
import xyz.kraftwork.chatbot.utils.ConfigurationHolder;

/**
 *
 * @author mnunez
 */
public class DbUtils {

    private static JdbcPooledConnectionSource datasource;

    public static JdbcPooledConnectionSource getDataSource() throws SQLException {
        if (datasource != null) {
            return datasource;
        }
        ConfigurationHolder config = ConfigurationHolder.getInstance();
        checkConfig();
        datasource = new JdbcPooledConnectionSource(config.get("DB_URL"), config.get("DB_USER"), config.get("DB_PASSWORD"));
        return datasource;
    }

    public static void migrate() {
        checkConfig();
        ConfigurationHolder config = ConfigurationHolder.getInstance();
        Flyway flyway = Flyway.configure()
                .baselineOnMigrate(true)
                .dataSource(config.get("DB_URL"), config.get("DB_USER"), config.get("DB_PASSWORD"))
                .load();

        // Start the migration
        flyway.migrate();
    }

    private static void checkConfig() {
        ConfigurationHolder config = ConfigurationHolder.getInstance();
        for (String s : new String[]{"DB_URL", "DB_USER", "DB_PASSWORD"}) {
            if (config.get(s) == null) {
                System.out.println("Missing " + s + ".\nExiting");
                System.exit(1);
            }
        }
    }
}
