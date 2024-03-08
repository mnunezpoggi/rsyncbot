/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package xyz.kraftwork.rsyncbot.utils;

import com.j256.ormlite.jdbc.JdbcConnectionSource;
import java.sql.SQLException;
import xyz.kraftwork.chatbot.utils.ConfigurationHolder;

/**
 *
 * @author mnunez
 */
public class DbUtils {
    
    private static JdbcConnectionSource datasource;

    public static JdbcConnectionSource getDataSource() throws SQLException {
            if(datasource != null){
                return datasource;
            }
            ConfigurationHolder config = ConfigurationHolder.getInstance();
            for(String s: new String[]{"DB_URL", "DB_USER", "DB_PASSWORD"}){
                if(config.get(s) == null){
                    System.out.println("Missing " + s + ".\nExiting");
                    System.exit(1);
                }
            }
            
            datasource = new JdbcConnectionSource(config.get("DB_URL"), config.get("DB_USER"), config.get("DB_PASSWORD"));
            
        return datasource;
    }
}
