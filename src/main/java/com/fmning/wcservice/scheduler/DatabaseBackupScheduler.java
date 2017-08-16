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
import com.fmning.wcservice.utils.Utils;

@EnableScheduling
@Component
public class DatabaseBackupScheduler {
	
	private String backupScriptPath;
	@Autowired private HelperManager helperManager;

	@Scheduled(cron = "0 0 1 * * ?")
    public void keepAlive() {
		try {
			Process process = Runtime.getRuntime().exec(backupScriptPath + " " + Utils.dbBackupFolder);
			
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
				String emailList = "fning@wpi.edu,sxie@wpi.edu";
				String emailContent = "Error during naclup of database:\n";
				emailContent += errors;
				helperManager.sendEmail("admin@fmning.com", emailList, "WPI CSA server error report", emailContent);
			}
			
		} catch (IOException | InterruptedException e) {
			String emailList = "fning@wpi.edu,sxie@wpi.edu";
			String emailContent = "Error during naclup of database:\n";
			emailContent += e.getMessage();
			helperManager.sendEmail("admin@fmning.com", emailList, "WPI CSA server error report", emailContent);
		}
        
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
				String emailList = "fning@wpi.edu,sxie@wpi.edu";
				String emailContent = "Error during startup of the service:\n";
				emailContent += errors;
				helperManager.sendEmail("admin@fmning.com", emailList, "WPI CSA server error report", emailContent);
			}
			
		} catch (IOException | InterruptedException e) {
			String emailList = "fning@wpi.edu,sxie@wpi.edu";
			String emailContent = "Error during startup of the service:\n";
			emailContent += e.getMessage();
			helperManager.sendEmail("admin@fmning.com", emailList, "WPI CSA server error report", emailContent);
		}

    }
}
