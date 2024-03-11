/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */
package xyz.kraftwork.rsyncbot;

import org.apache.commons.cli.CommandLine;
import xyz.kraftwork.chatbot.ChatInfo;
import xyz.kraftwork.chatbot.Chatbot;
import xyz.kraftwork.chatbot.Command;
import xyz.kraftwork.chatbot.CommandListener;
import xyz.kraftwork.rsyncbot.controllers.CredentialsController;
import xyz.kraftwork.rsyncbot.controllers.JobsController;
import xyz.kraftwork.rsyncbot.controllers.ServersController;

public class Rsyncbot implements CommandListener {
        
    private static final String CREATE_CREDENTIAL = "rsync:create_credential";
    private static final String SHOW_CREDENTIAL = "rsync:show_credential";
    private static final String REMOVE_CREDENTIAL = "rsync:remove_credential";
    private static final String UPDATE_CREDENTIAL = "rsync:update_credential";

    private static final String CREATE_SERVER = "rsync:create_server";
    private static final String REMOVE_SERVER = "rsync:remove_server";
    private static final String UPDATE_SERVER = "rsync:update_server";
    
    private static final String CREATE_JOB = "rsync:create_job";
    private static final String REMOVE_JOB = "rsync:remove_job";
    private static final String UPDATE_JOB_SCHEDULE = "rsync:update_job_schedule";
    
    private final Chatbot bot;
    private final CredentialsController credentialsController;
    private final ServersController serversController;
    private final JobsController jobsController;

    public Rsyncbot() {
        bot = new Chatbot();
        bot.addCommandListener(this);
        addCommands();
        this.credentialsController = new CredentialsController(bot);
        this.serversController = new ServersController(bot);
        this.jobsController = new JobsController(bot);
    }

    private void addCommands() {
        bot.addCommandOptions(CREATE_CREDENTIAL, true, "n", "name", true, "The name for this credential");
        bot.addCommandOptions(CREATE_CREDENTIAL, true, "u", "user", true, "The user that will use this credential");
        bot.addCommandOptions(CREATE_CREDENTIAL, true,  "p", "key_path", true, "The private key path. This must be a filename only. All keys are searched in keys/");
        
        bot.addCommandOptions(SHOW_CREDENTIAL, true, "n", "name", true, "The name for this credential");
        
        bot.addCommandOptions(CREATE_SERVER, true, "n", "name", true, "Name for this server");
        bot.addCommandOptions(CREATE_SERVER, true, "h", "host", true, "Host");
        
        bot.addCommandOptions(CREATE_JOB, true, "n", "name", true, "Name for this job");
        bot.addCommandOptions(CREATE_JOB, true, "s", "schedule", true, "Schedule for this job");
        bot.addCommandOptions(CREATE_JOB, false, "ss", "source_server", true, "Source server name for this job");
        bot.addCommandOptions(CREATE_JOB, true, "sp", "source_path", true, "Source path for this job");
        bot.addCommandOptions(CREATE_JOB, false, "ds", "destination_server", true, "Destination server name for this job");
        bot.addCommandOptions(CREATE_JOB, true, "dp", "destination_path", true, "Destination path for this job");
        bot.addCommandOptions(CREATE_JOB, false, "c", "credential", true, "Credential to use");
        
        bot.addCommandOptions(REMOVE_JOB, true, "n", "name", true, "Name of the job to be removed");
        // rsync:create_job --name=local_copy --schedule=*_*_*_*_* --source_server=localhost --source_credential=sample --source_path=/tmp/a --destination_server=localhost --destination_credential=sample --destination_path=/tmp/b
        bot.addCommandOptions(UPDATE_JOB_SCHEDULE, true, "n", "name", true, "Name of the job to be updated");
        bot.addCommandOptions(UPDATE_JOB_SCHEDULE, true, "s", "schedule", true, "The schedule to chage");
    }

    @Override
    public Object onCommand(ChatInfo info, CommandLine cl) {
        Command command = info.getCommand();
        switch (command.getName()) {
            case CREATE_CREDENTIAL -> {
                credentialsController.create(info, cl);
            }
            case SHOW_CREDENTIAL -> {
                credentialsController.show(info, cl);
            }
            case REMOVE_CREDENTIAL -> {

            }
            case UPDATE_CREDENTIAL -> {

            }
            
            case CREATE_SERVER -> {
                serversController.create(info, cl);
            }
            
            case CREATE_JOB -> {
                jobsController.create(info, cl);
            }
            case REMOVE_JOB -> {
                jobsController.remove(info, cl);
            }
            case UPDATE_JOB_SCHEDULE -> {
                jobsController.update_job_schedule(info, cl);
            }

        }
        return null;
    }

    public static void main(String[] args) {
        Rsyncbot r = new Rsyncbot();
        
    }
}
