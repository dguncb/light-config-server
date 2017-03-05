package com.networknt.config.server.handler;

import com.networknt.config.Config;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.HttpString;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.lang3.StringEscapeUtils;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.PullResult;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.transport.FetchResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * curl http://localhost:8080/v1/config/1.2.4/dev/com.networknt.petstore-1.0.0
 */
public class ConfigVersionProfileServiceGetHandler implements HttpHandler {
    static final Logger logger = LoggerFactory.getLogger(ConfigVersionProfileServiceGetHandler.class);
    static final String CONFIG_NAME = "config";
    static final String WORKING_FOLDER = "/working";
    static final String REPOSITORIES_FOLDER = "/repositories";
    static final String BASE_FLODER = System.getProperty("user.home");

    static final String ABSOLUTE_WORKING = BASE_FLODER + WORKING_FOLDER;
    static final String ABSOLUTE_REPOSITORIES = BASE_FLODER + REPOSITORIES_FOLDER;
    static final Map<String, Object> config = Config.getInstance().getJsonMapConfig(CONFIG_NAME);

    public ConfigVersionProfileServiceGetHandler() {
        // get the working directories to initialize them if necessary
        if(logger.isDebugEnabled()) logger.debug("initialize working directory " + ABSOLUTE_WORKING);
        initializeDirectory(ABSOLUTE_WORKING);
        if(logger.isDebugEnabled()) logger.debug("initialize repositories directory " + ABSOLUTE_REPOSITORIES);
        initializeDirectory(ABSOLUTE_REPOSITORIES);

        // check if default config repo is there. clone if it not there
        String defaultRepo = (String)config.get("default_repo");
        String repoName = defaultRepo.substring(defaultRepo.indexOf("/"), defaultRepo.indexOf(".git"));
        File file = new File(ABSOLUTE_REPOSITORIES + repoName);
        if(!file.exists()) {
            // clone
            try (Git result = Git.cloneRepository()
                    .setURI(defaultRepo)
                    .setDirectory(file)
                    .call()) {
                // Note: the call() returns an opened repository already which needs to be closed to avoid file handle leaks!
                if(logger.isDebugEnabled()) logger.debug("Having repository: " + result.getRepository().getDirectory());
            } catch (GitAPIException e) {
                logger.error("GitAPIException", e);
                // TODO return error
            }
        } else {
            // pull
            try {
                FileRepositoryBuilder repositoryBuilder = new FileRepositoryBuilder();
                repositoryBuilder.setMustExist( true );
                repositoryBuilder.readEnvironment();
                repositoryBuilder.addCeilingDirectory(file);
                repositoryBuilder.findGitDir();
                try (Repository repository = repositoryBuilder.build()) {
                    if(logger.isDebugEnabled()) logger.debug("Starting fetch" + file.getAbsolutePath());
                    try (Git git = new Git(repository)) {
                        PullResult result = git.pull().call();
                        if(logger.isDebugEnabled()) logger.debug("Messages: " + result.getMergeResult().toString());
                    } catch (GitAPIException e) {
                        logger.error("GitAPIException", e);
                        // TODO
                    }
                }
            } catch (IOException e) {
                logger.error("IOException", e);
                // TODO return error
            }
        }
    }

    private void initializeDirectory(String absPath) {
        // check if it exists
        File file = new File(absPath);
        if (file.exists()) {
            if (file.isFile()) {
                logger.info("path is a file, remove it");
                file.delete();
                createDirectory(absPath);
            }
        } else {
            // create a directory here
            createDirectory(absPath);
        }
    }

    private void createDirectory(String p) {
        // create a directory here
        Path path = Paths.get(p);
        try {
            Files.createDirectory(path);
        } catch (IOException e) {
            logger.error("IOException", e);
        }
    }

    @Override
    public void handleRequest(HttpServerExchange exchange) throws Exception {

        exchange.getResponseHeaders().add(new HttpString("Content-Type"), "application/octet-stream");
        String version = exchange.getQueryParameters().get("version").getFirst();
        String profile = exchange.getQueryParameters().get("profile").getFirst();
        String service = exchange.getQueryParameters().get("service").getFirst();
        if(logger.isDebugEnabled()) logger.debug("version = " + version + " profile = " + profile + " service = " + service);

        // concat file name
        String path = ABSOLUTE_WORKING + "/" + version + "/" + profile + "/" + service + "/config.zip";
        if(logger.isDebugEnabled()) logger.debug("path " + path);
        exchange.getResponseSender().send("OK");

    }
}
