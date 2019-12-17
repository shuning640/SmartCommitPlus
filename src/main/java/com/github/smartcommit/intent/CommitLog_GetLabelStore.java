package com.github.smartcommit.intent;

// DataCollector
import com.github.smartcommit.util.GitService;
import com.github.smartcommit.util.GitServiceCGit;
import com.github.smartcommit.util.Utils;

import java.util.List;
import java.util.ArrayList;
import org.apache.commons.io.FileUtils;
import java.io.File;

// Example
import com.github.gumtreediff.gen.jdt.JdtTreeGenerator;
import com.github.gumtreediff.matchers.MappingStore;
import com.github.gumtreediff.matchers.Matcher;
import com.github.gumtreediff.matchers.Matchers;
import com.github.gumtreediff.matchers.SimilarityMetrics;
import com.github.gumtreediff.tree.ITree;
import com.github.gumtreediff.tree.TreeContext;
import org.eclipse.jdt.core.dom.ASTParser;
import java.io.IOException;

// MongoExample
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;


// Commit message:  Get, Label, Store
public class CommitLog_GetLabelStore {
    public static void main(String[] args) {
        String REPO_NAME = "guava";
        String REPO_DIR = "/Users/Chuncen/IdeaProjects/" + REPO_NAME;
        String commitID = "f4b3f611c4e49ecaded58dcb49262f55e56a3322";
        List<String> commitLog =new ArrayList<String>();
        List<String> commitIDs =new ArrayList<String>();
        List<String> commitAuthors =new ArrayList<String>();
        List<String> commitDates =new ArrayList<String>();
        List<String> commitMessages =new ArrayList<String>();
        List<String> commitIntents =new ArrayList<String>();
        System.out.println("Get Succeed? " + getIt(REPO_DIR, commitLog, commitIDs, commitAuthors, commitDates, commitMessages));
        System.out.println("Label Succeed? " + lableIt(REPO_DIR, commitMessages, commitIntents));
        System.out.println("Store Succeed? " + storeIt(REPO_NAME, commitIDs, commitAuthors,
                commitDates, commitMessages, commitIntents));
    }
    // Get All commit Messages
    public static boolean getIt(String REPO_DIR, List<String> commitLog, List<String> commitIDs,
                                List<String> commitAuthors, List<String> commitDates, List<String> commitMessages) {
        GitService gitService = new GitServiceCGit();

        // Just get ID and msg
        // String log = Utils.runSystemCommand(REPO_DIR, "git", "log", "--oneline");
        /*
        String log = Utils.runSystemCommand(REPO_DIR, "git", "log", "--pretty=oneline");
        String lines[] = log.split("\\r?\\n"), body[];
        for (String line : lines) {
            commitLog.add(line);
            body = line.split(" ");
            commitIDs.add(body[0]);
            commitMessages.add(body[1]);
        }

         */
        //System.out.println("Log0: " + commitIDs.get(0));

        // get all the commit details
        String log = Utils.runSystemCommand(REPO_DIR, "git", "log");
        String parts[] = log.split("\\ncommit"), body[];
        for (String part : parts) {
            commitLog.add(part);
            body = part.split("\\nAuthor:|\\nDate:|\\n\\n");
            commitIDs.add(body[0]);
            commitAuthors.add(body[1]);
            commitDates.add(body[2]);
            commitMessages.add(body[3]);
        }
        //System.out.println("Log0: " + commitMessages.get(1));
        return true;
    }
    // Cluster to get label using gumtree
    public static boolean lableIt(String REPO_DIR, List<String> commitMessages, List<String> commitIntents) {
        try {
            List<String> intents;
            intents = FileUtils.readLines(new File(REPO_DIR +File.separator+"intents-selected.txt"));
            for (String msg : commitMessages) {
                for (String intent : intents) {
                    if(msg.contains(intent)) {
                         commitIntents.add(intent);
                         break;
                    }
                }
                commitIntents.add("unknown");
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        //System.out.println("Intent1 : " + commitIntents.get(1));
        return true;
    }

    // Store the label and message into mongodb
    public static boolean storeIt(String REPO_NAME, List<String> commitIDs, List<String> commitAuthors,
                                  List<String> commitDates, List<String> commitMessages, List<String> commitIntents) {
        MongoClientURI connectionString = new MongoClientURI("mongodb://localhost:27017");
        MongoClient mongoClient = new MongoClient(connectionString);
        MongoDatabase sampleDB = mongoClient.getDatabase("samples");
        MongoCollection<Document> repoCol = sampleDB.getCollection(REPO_NAME);
        Integer size = commitIDs.size();
        for(int i = 0 ; i < size ; i++) {
            //System.out.println(commitIDs.get(i));
            // key:value
            Document sampleDoc = new Document("repo_name", REPO_NAME);
            sampleDoc
                    .append("commit_id", commitIDs.get(i))
                    .append("commit_msg", commitMessages.get(i))
                    .append("author", commitAuthors.get(i))
                    .append("date", commitDates.get(i))
                    .append("intent", commitIntents.get(i))
            ;
            repoCol.insertOne(sampleDoc);
        }
        // if simply read from json
        /*
        GitService gitService = new GitServiceCGit();
        // mongoimport -d 数据库名 -c 数据表  --type json --file D:\data.json
        String log = Utils.runSystemCommand(REPO_DIR, "mongoimport", "-d", "sampleDB",
                "-c", "repoCol", "--type", "json", "--file", "commit-all.json");
        */
        mongoClient.close();
        return true;
    }
}

// read from json
// https://www.cnblogs.com/dwb91/p/6726823.html

// read from txt
// https://blog.csdn.net/xuehyunyu/article/details/77873420

// into Mongodb
// https://github.com/Symbolk/IntelliMerge/blob/f4b5166abbd7dffc2040b819670dad31a6b89ae0/src/main/java/edu/pku/intellimerge/evaluation/Evaluator.java#L49
