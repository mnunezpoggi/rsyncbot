/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package xyz.kraftwork.rsyncbot.controllers;

import com.coreoz.wisp.JobStatus;
import com.coreoz.wisp.Scheduler;
import com.coreoz.wisp.schedule.Schedule;
import com.coreoz.wisp.schedule.Schedules;
import com.coreoz.wisp.schedule.cron.CronExpressionSchedule;
import com.j256.ormlite.dao.CloseableWrappedIterable;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.jdbc.JdbcPooledConnectionSource;
import fc.cron.CronExpression;
import java.sql.SQLException;
import java.time.Duration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.cli.CommandLine;
import xyz.kraftwork.chatbot.ChatInfo;
import xyz.kraftwork.chatbot.Chatbot;
import xyz.kraftwork.chatbot.RegistrationListener;
import xyz.kraftwork.rsyncbot.models.Credential;
import xyz.kraftwork.rsyncbot.models.Job;
import xyz.kraftwork.rsyncbot.models.Server;

/**
 *
 * @author mnunez
 */
public class JobsController extends BaseController implements RegistrationListener {

    private Dao<Job, Integer> persistence;
    private Dao<Server, Integer> servers;
    private Dao<Credential, Integer> credentials;
    private final Scheduler scheduler;

    private static final String[] REQUIRED_PARAMS = {"name", "schedule", "source_path", "destination_path"};

    public JobsController(Chatbot bot) {
        super(bot);
        scheduler = new Scheduler();
        bot.addRegistrationListener(this);
    }

    @Override
    public Object onRegistration() {
        scheduleJobs();
        return null;
    }
    
    @Override
    protected Dao getPersistence(){
        return this.persistence;
    }

    @Override
    public void create(ChatInfo info, CommandLine cl) {
        String message = "";
        boolean failed = false;
        try {
            String name = cl.getOptionValue("name");
            // TODO: check schedule
            // TODO: Check destination_path
            // TODO: Check cron
            String schedule = cl.getOptionValue("schedule").replace('_', ' ');
            String source_path = cl.getOptionValue("source_path");
            String destination_path = cl.getOptionValue("destination_path");
            String source_server = cl.getOptionValue("source_server");
            String destination_server = cl.getOptionValue("destination_server");
            String credential = cl.getOptionValue("credential");

            CronExpression.createWithoutSeconds(schedule);

            if (source_server != null && destination_server != null) {
                message += "Source and destination can't be remote. Sorry. ";
                failed = true;
            }
            if ((source_server != null || destination_server != null) && credential == null) {
                message += "Remote server found but no credential specified. ";
                failed = true;
            }
            List<Server> found_source_server = null;
            List<Server> found_destination_server = null;
            List<Credential> found_credential = null;
            String failed_message = "Couldn't find ";
            if (!failed && source_server != null) {
                found_source_server = servers.queryForEq("name", source_server);
                if (found_source_server.isEmpty()) {
                    message += failed_message + "source_server " + source_server;
                    failed = true;
                }
            }
            if (!failed && destination_server != null) {
                found_destination_server = servers.queryForEq("name", destination_server);
                if (found_destination_server.isEmpty()) {
                    message += failed_message + "destination_server " + destination_server;
                    failed = true;
                }
            }

            if (!failed && credential != null) {
                found_credential = credentials.queryForEq("name", credential);
                if (found_credential.isEmpty()) {
                    message += "Couldn't find credential " + credential;
                    failed = true;
                }
            }

            if (!failed) {
                Job job = new Job();
                job.setName(name);
                job.setSource_path(source_path);
                job.setDestination_path(destination_path);
                job.setSchedule(schedule);

                if (credential != null) {
                    job.setCredential(found_credential.get(0));
                    if (source_server != null) {
                        job.setSource_server(found_source_server.get(0));
                    }
                    if (destination_server != null) {
                        job.setDestination_server(found_destination_server.get(0));
                    }
                }
                persistence.create(job);
                info.setMessage("Successfully saved job: " + job);
                persistence.update(job);
                this.scheduleJob(job);
            } else {
                info.setMessage(message);
            }

        } catch (Exception ex) {
            info.setMessage("Error: " + ex.getMessage());
            ex.printStackTrace();
        } finally {
            bot.sendMessage(info);
        }

    }

    @Override
    public void show(ChatInfo info, CommandLine cl) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    protected void setPersistence(JdbcPooledConnectionSource dataSource) {
        try {
            this.persistence = DaoManager.createDao(dataSource, Job.class);
            this.servers = DaoManager.createDao(dataSource, Server.class);
            this.credentials = DaoManager.createDao(dataSource, Credential.class);
        } catch (SQLException ex) {
            Logger.getLogger(JobsController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void remove(ChatInfo info, CommandLine cl) {
        try {
            String jobName = cl.getOptionValue("name");
            List<Job> job = persistence.queryForEq("name", jobName);
            if (job.isEmpty()) {
                info.setMessage("Couldn't find job " + jobName + " to remove.");
            } else {
                //scheduler.remove(jobName);
                Optional<com.coreoz.wisp.Job> j = scheduler.findJob(jobName);
                if (j.isPresent()) {
                    com.coreoz.wisp.Job sj = j.get();
                    if (sj.status() != JobStatus.DONE) {
                        info.setMessage("WARNING: Job " + jobName + " is currently running, it will be fully removed once completed");
                        scheduler.cancel(jobName);
                    }
                }
                persistence.delete(job.get(0));
                info.setMessage("Successfully removed " + jobName);
            }
        } catch (Exception ex) {
            info.setMessage(ex.getMessage());
            ex.printStackTrace();
        }
//        finally {
//          
//        }
        bot.sendMessage(info);

    }

    private void scheduleJob(Job job) {
        job.setController(this);
        bot.sendMessageAll("Scheduling: " + job);
        CronExpressionSchedule sc = CronExpressionSchedule.parse(job.getSchedule());
        scheduler.schedule(job.getName(), job, sc);

    }

    private void scheduleJobs() {
        bot.sendMessageAll("Starting jobs cleaner");
        scheduler.schedule(
                "jobs_cleaner",
                () -> scheduler
                        .jobStatus()
                        .stream()
                        .filter(job -> job.status() == JobStatus.DONE)
                        .filter(job -> job.lastExecutionEndedTimeInMillis() < (System.currentTimeMillis() - 10000))
                        .forEach(job -> {
                            scheduler.remove(job.name());
                        }),
                Schedules.fixedDelaySchedule(Duration.ofMinutes(10))
        );
        Iterator<Job> i = persistence.iterator();
        while (i.hasNext()) {
            scheduleJob(i.next());
        }

    }

    public void notify(String message) {
        bot.sendMessageAll(message);
    }

    public void updateJobSchedule(ChatInfo info, CommandLine cl) {
        try {
            String schedule = cl.getOptionValue("schedule").replace('_', ' ');
            CronExpression.createWithoutSeconds(schedule);
            String jobName = cl.getOptionValue("name");
            List<Job> job = persistence.queryForEq("name", jobName);
            if (job.isEmpty()) {
                info.setMessage("Couldn't find job " + jobName + " to remove.");
            } else {
                Job found = job.get(0);
                found.setSchedule(schedule);
                persistence.update(found);
                Optional<com.coreoz.wisp.Job> j = scheduler.findJob(jobName);
                scheduler.cancel(jobName);
                this.scheduleJob(found);
                info.setMessage("Successfully updated " + jobName);
            }
        } catch (Exception ex) {
            info.setMessage(ex.getMessage());
            ex.printStackTrace();
        }
        bot.sendMessage(info);
    }

    public void runJob(ChatInfo info, CommandLine cl) {
        try {
            String jobName = cl.getOptionValue("name");
            List<Job> job = persistence.queryForEq("name", jobName);
            if (job.isEmpty()) {
                info.setMessage("Couldn't find job " + jobName + " to run.");
                bot.sendMessage(info);
            } else {
                Job found = job.get(0);
                found.setController(this);
                new Thread(found).start();
            }
        } catch (Exception ex) {
            info.setMessage(ex.getMessage());
            ex.printStackTrace();
        }

    }
    
    @Override
    public void list(ChatInfo info){
        super.list(info);
    }
}
