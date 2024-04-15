/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package xyz.kraftwork.rsyncbot.controllers;

import com.j256.ormlite.dao.CloseableWrappedIterable;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sql.DataSource;
import org.apache.commons.cli.CommandLine;
import xyz.kraftwork.chatbot.ChatInfo;
import xyz.kraftwork.chatbot.Chatbot;
import xyz.kraftwork.rsyncbot.utils.DbUtils;

/**
 *
 * @author mnunez
 */
public abstract class BaseController {

    protected final Chatbot bot;

    public BaseController(Chatbot bot) {
        this.bot = bot;
        try {
            this.setPersistence(DbUtils.getDataSource());
        } catch (SQLException ex) {
            Logger.getLogger(BaseController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    protected void list(ChatInfo info) {
        CloseableWrappedIterable wrappedIterable = getPersistence().getWrappedIterable();
        try {
            for (Object s : wrappedIterable) {
                info.setMessage(s.getClass().getSimpleName() + ": " + s.toString());
                bot.sendMessage(info);
            }
        } finally {
            try {
                wrappedIterable.close();
            } catch (Exception ex) {
                ex.printStackTrace();
                info.setMessage(ex.getMessage());
                bot.sendMessage(info);
            }
        }
    }

    public abstract void create(ChatInfo info, CommandLine cl);

    public abstract void show(ChatInfo info, CommandLine cl);

    protected abstract void setPersistence(JdbcConnectionSource dataSource);

    protected abstract Dao getPersistence();
}
