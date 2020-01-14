package com.github.smartcommit.client;

import com.github.smartcommit.model.Group;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;

import java.util.Map;

public class Main {
  public static void main(String[] args) {
    // use basic configuration when packaging
    BasicConfigurator.configure();
    org.apache.log4j.Logger.getRootLogger().setLevel(Level.INFO);
    //      PropertyConfigurator.configure("log4j.properties");
    try {
      SmartCommit smartCommit =
          new SmartCommit(Config.REPO_ID, Config.REPO_NAME, Config.REPO_PATH, Config.TEMP_DIR);
      Map<String, Group> groups = smartCommit.analyzeWorkingTree();
//            Map<String, Group> groups = smartCommit.analyzeCommit(Config.COMMIT_ID);
      if (groups != null) {
        for (Map.Entry<String, Group> entry : groups.entrySet()) {
          System.out.println(entry.getKey());
          System.out.println(entry.getValue().toString());
        }
      } else {
        System.out.println("No Changes.");
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    return;
  }
}