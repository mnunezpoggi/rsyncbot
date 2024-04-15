/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package xyz.kraftwork.rsyncbot.controllers;

import com.j256.ormlite.dao.CloseableWrappedIterable;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.cli.CommandLine;
import xyz.kraftwork.chatbot.ChatInfo;
import xyz.kraftwork.chatbot.Chatbot;
import xyz.kraftwork.rsyncbot.models.Server;

/**
 *
 * @author mnunez
 */
public class ServersController extends BaseController {

    private Dao<Server, Integer> persistence;

    public ServersController(Chatbot bot) {
        super(bot);
    }

    @Override
    protected Dao getPersistence() {
        return this.persistence;
    }

    @Override
    public void create(ChatInfo info, CommandLine cl) {
        String name = cl.getOptionValue("name");
        String host = cl.getOptionValue("host");
        Server server = new Server();
        server.setName(name);
        server.setHost(host);
        try {
            persistence.create(server);
            info.setMessage("Successfully saved server: " + server);
        } catch (SQLException ex) {
            info.setMessage("Error saving server: " + ex.getMessage());
            ex.printStackTrace();
        }
        bot.sendMessage(info);
    }

    @Override
    public void show(ChatInfo info, CommandLine cl) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    protected void setPersistence(JdbcConnectionSource dataSource) {
        try {
            this.persistence = DaoManager.createDao(dataSource, Server.class);
        } catch (SQLException ex) {
            Logger.getLogger(ServersController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void list(ChatInfo info) {
        super.list(info);
    }

}
