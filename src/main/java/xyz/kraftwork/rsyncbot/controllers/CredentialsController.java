/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package xyz.kraftwork.rsyncbot.controllers;

import com.j256.ormlite.dao.CloseableWrappedIterable;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sql.DataSource;
import org.apache.commons.cli.CommandLine;
import xyz.kraftwork.chatbot.ChatInfo;
import xyz.kraftwork.chatbot.Chatbot;
import xyz.kraftwork.chatbot.RegistrationListener;
import xyz.kraftwork.rsyncbot.models.Credential;
import xyz.kraftwork.rsyncbot.utils.CommandUtils;

/**
 *
 * @author mnunez
 */
public class CredentialsController extends BaseController implements RegistrationListener {

    private Dao<Credential, Integer> persistence;

    public CredentialsController(Chatbot bot) {
        super(bot);
        bot.addRegistrationListener(this);
    }
    
    @Override
    public Object onRegistration() {
        checkKeys();
        return null;
    }

    @Override
    protected void setPersistence(JdbcConnectionSource dataSource) {
        try {
            this.persistence = DaoManager.createDao(dataSource, Credential.class);
        } catch (SQLException ex) {
            Logger.getLogger(CredentialsController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void create(ChatInfo info, CommandLine cl) {
        Credential cred = buildCredential(cl);
        try {
            persistence.create(cred);
            info.setMessage("Successfully saved credential: " + cred);
        } catch (SQLException ex) {
            info.setMessage("Error saving credential: " + ex.getMessage());
            ex.printStackTrace();
        }
        bot.sendMessage(info);
    }

    @Override
    public void show(ChatInfo info, CommandLine cl) {

    }

    public void checkKeys() {
//        try {
//            Thread.sleep(30000);
//        } catch (InterruptedException ex) {
//        }
        bot.sendMessageAll("Bot initialized, testing keys.");

        try (CloseableWrappedIterable<Credential> wrappedIterable = persistence.getWrappedIterable()) {
            for (Credential cred : wrappedIterable) {
                String result = CommandUtils.execCmd("ssh-keygen -lf keys/" + cred.getKey_path());
                if (result.contains("SHA")) {
                    bot.sendMessageAll(cred.toString() + ": OK");
                } else {
                    bot.sendMessageAll(cred.toString() + ": FAIL");
                }
            }
        } catch (Exception ex) {
            Logger.getLogger(CredentialsController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private Credential buildCredential(CommandLine cl) {
        String name = cl.getOptionValue("name");
        String user = cl.getOptionValue("user");
        String key_path = cl.getOptionValue("key_path");
        Credential cred = new Credential(name, user, key_path);
        return cred;
    }

}
