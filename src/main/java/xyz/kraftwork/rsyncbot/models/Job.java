/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package xyz.kraftwork.rsyncbot.models;

import com.github.fracpete.processoutput4j.output.CollectingProcessOutput;
import com.github.fracpete.rsync4j.RSync;
import com.github.fracpete.rsync4j.core.Binaries;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import java.io.File;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.IOUtils;
import xyz.kraftwork.rsyncbot.controllers.JobsController;

/**
 *
 * @author mnunez
 */
@DatabaseTable(tableName = "jobs")
public class Job implements Runnable {

    private static final String RSYNC_INIT = "rsync";

    private String buildRemote(Server s, Credential c, String path) {
        return c.getUser() + "@" + s.getHost() + ":" + path;
    }

    @Override
    public void run() {

        controller.notify("Starting job: " + this);
        try {
            ArrayList<String> rsync = new ArrayList();
            rsync.add(RSYNC_INIT);
            rsync.add("-a");
            rsync.add("-v");
            if (credential != null) {
                rsync.add("-e");
                rsync.add("" + Binaries.sshBinary() + " -i " + new File("keys/" + credential.getKey_path()).getAbsolutePath() + "");
                if (source_server != null) {
                    rsync.add(buildRemote(source_server, credential, source_path));
                    rsync.add(destination_path);
                } else {
                    rsync.add(buildRemote(destination_server, credential, destination_path));
                    rsync.add(source_path);
                }
            } else {
                rsync.add(source_path);
                rsync.add(destination_path);
            }
            System.out.println(rsync);
            String[] cmd = new String[rsync.size()];
            rsync.toArray(cmd);
            System.out.println(IOUtils.toString(Runtime.getRuntime().exec("whoami").getInputStream(), Charset.defaultCharset()));
            Process p = Runtime.getRuntime().exec(cmd);
            p.waitFor();
            String output = IOUtils.toString(p.getInputStream(), Charset.defaultCharset());
            String errorOutput = IOUtils.toString(p.getErrorStream(), Charset.defaultCharset());
            System.out.println(output);
            System.out.println(errorOutput);
            if(p.exitValue() == 0){
                controller.notify("Success!: " + output.replace("\n", ". " ));
            } else {
                controller.notify("Fail!: " + errorOutput.replace("\n", ". " ));
            }
        } catch (Exception ex) {
            controller.notify("Error on " + this + ":" + ex.getMessage());
        }
    }

    private JobsController controller;

    @DatabaseField(generatedId = true)
    private int id;

    @DatabaseField(canBeNull = false)
    private String name;

    @DatabaseField(canBeNull = false)
    private String schedule;

    @DatabaseField(canBeNull = true, foreign = true, foreignAutoRefresh = true)
    private Server source_server;

    @DatabaseField(canBeNull = false)
    private String source_path;

    @DatabaseField(canBeNull = true, foreign = true, foreignAutoRefresh = true)
    private Server destination_server;

    @DatabaseField(canBeNull = false)
    private String destination_path;

    @DatabaseField(canBeNull = true, foreign = true, foreignAutoRefresh = true)
    private Credential credential;

    public Job() {
    }

    public Job(JobsController controller) {
        this.controller = controller;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public JobsController getController() {
        return controller;
    }

    public void setController(JobsController controller) {
        this.controller = controller;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSchedule() {
        return schedule;
    }

    public void setSchedule(String schedule) {
        this.schedule = schedule;
    }

    public Server getSource_server() {
        return source_server;
    }

    public void setSource_server(Server source_server) {
        this.source_server = source_server;
    }

    public String getSource_path() {
        return source_path;
    }

    public void setSource_path(String source_path) {
        this.source_path = source_path;
    }

    public Server getDestination_server() {
        return destination_server;
    }

    public void setDestination_server(Server destination_server) {
        this.destination_server = destination_server;
    }

    public Credential getCredential() {
        return credential;
    }

    public void setCredential(Credential credential) {
        this.credential = credential;
    }

    public String getDestination_path() {
        return destination_path;
    }

    public void setDestination_path(String destination_path) {
        this.destination_path = destination_path;
    }

    @Override
    public String toString() {
        return "Job{" + "controller=" + controller + ", id=" + id + ", name=" + name + ", schedule=" + schedule + ", source_server=" + source_server + ", source_path=" + source_path + ", destination_server=" + destination_server + ", destination_path=" + destination_path + ", credential=" + credential + '}';
    }

}
