/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package xyz.kraftwork.rsyncbot.models;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 *
 * @author mnunez
 */
@DatabaseTable(tableName = "credentials")
public class Credential {

    @DatabaseField(generatedId = true)
    private int id;
    
    @DatabaseField
    private String name;
    
    @DatabaseField
    private String user;
    
    @DatabaseField
    private String key_path;

    public Credential() {
    }
    
    public Credential(String name, String user, String key_path){
        this.name = name;
        this.user = user;
        this.key_path = key_path;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getKey_path() {
        return key_path;
    }

    public void setKey_path(String key_path) {
        this.key_path = key_path;
    }

    @Override
    public String toString() {
        return "Credential{" + "id=" + id + ", name=" + name + ", user=" + user + ", key_path=" + key_path + '}';
    }
    
    
}
