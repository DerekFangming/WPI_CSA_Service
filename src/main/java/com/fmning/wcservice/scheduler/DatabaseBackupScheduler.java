package com.fmning.wcservice.scheduler;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.fmning.service.manager.HelperManager;

@EnableScheduling
@Component
public class DatabaseBackupScheduler {
	
	private String backupScriptPath;
	@Autowired private HelperManager helperManager;

	@Scheduled(cron = "30 56 15 * * ?")
    public void keepAlive() {
        //log "alive" every hour for sanity checks
        System.out.println("alive");
        
    }

	@PostConstruct
    public void runOnceOnlyOnStartup() {
		ClassLoader classLoader = getClass().getClassLoader();
		File file = new File(classLoader.getResource("dbBackup.sh").getFile());
		backupScriptPath = file.getAbsolutePath();
        
		try {
			Process process = Runtime.getRuntime().exec("chmod 777 " + backupScriptPath);
			
			BufferedReader in = new BufferedReader(new InputStreamReader(process.getErrorStream()));
			String errors = "";
			String line;
			while ((line = in.readLine()) != null) {
			    errors += line + "\n";
			}
			
			int exitValue = process.waitFor();
			if (exitValue != 0)
				errors += "Execution of script failed!";
			
			if (errors.length() > 0){
				String emailList = "fning@wpi.edu";//,sxie@wpi.edu";
				String emailContent = "Error during startup of the service:\n";
				emailContent += errors;
				helperManager.sendEmail("admin@fmning.com", emailList, "WPI CSA server error report", emailContent);
			}
			
		} catch (IOException | InterruptedException e) {
			String emailList = "fning@wpi.edu";//,sxie@wpi.edu";
			String emailContent = "Error during startup of the service:\n";
			emailContent += e.getMessage();
			helperManager.sendEmail("admin@fmning.com", emailList, "WPI CSA server error report", emailContent);
		}

    }
}
