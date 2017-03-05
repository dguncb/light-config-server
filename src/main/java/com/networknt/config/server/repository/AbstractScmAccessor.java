package com.networknt.config.server.repository;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;

/**
 * Created by stevehu on 2017-03-02.
 */
public class AbstractScmAccessor {

    private static final String[] DEFAULT_LOCATIONS = new String[] { "/" };

    private Logger logger = LoggerFactory.getLogger(AbstractScmAccessor.class);
    /**
     * Base directory for local working copy of repository.
     */
    private File basedir;
    /**
     * URI of remote repository.
     */
    private String uri;
    /**
     * Username for authentication with remote repository.
     */
    private String username;
    /**
     * Password for authentication with remote repository.
     */
    private String password;
    /**
     * Passphrase for unlocking your ssh private key.
     */
    private String passphrase;
    /**
     * Reject incoming SSH host keys from remote servers not in the known host list.
     */
    private boolean strictHostKeyChecking = true;
    /**
     * Search paths to use within local working copy. By default searches only the root.
     */
    private String[] searchPaths = DEFAULT_LOCATIONS.clone();

    public AbstractScmAccessor() {
        this.basedir = createBaseDir();
    }

    protected File createBaseDir() {
        try {
            final File basedir = Files.createTempDirectory("config-repo-").toFile();
            Runtime.getRuntime().addShutdownHook(new Thread() {
                @Override
                public void run() {
                    try {
                        //Deleting the directory recursively.
                        delete(basedir);
                    }
                    catch (IOException e) {
                        AbstractScmAccessor.this.logger.warn(
                                "Failed to delete temporary directory on exit: " + e);
                    }
                }
            });
            return basedir;
        }
        catch (IOException e) {
            throw new IllegalStateException("Cannot create temp dir", e);
        }
    }

    /**
     * Delete a file or a directory and its children.
     * @param file The directory to delete.
     * @throws IOException Exception when problem occurs during deleting the directory.
     */
    private static void delete(File file) throws IOException {

        for (File childFile : file.listFiles()) {

            if (childFile.isDirectory()) {
                delete(childFile);
            } else {
                if (!childFile.delete()) {
                    throw new IOException();
                }
            }
        }

        if (!file.delete()) {
            throw new IOException();
        }
    }

    public void setUri(String uri) {
        while (uri.endsWith("/")) {
            uri = uri.substring(0, uri.length() - 1);
        }
        int index = uri.indexOf("://");
        if (index > 0 && !uri.substring(index + "://".length()).contains("/")) {
            // If there's no context path add one
            uri = uri + "/";
        }
        this.uri = uri;
    }

    public String getUri() {
        return this.uri;
    }

    public void setBasedir(File basedir) {
        this.basedir = basedir.getAbsoluteFile();
    }

    public File getBasedir() {
        return this.basedir;
    }

    public void setSearchPaths(String... searchPaths) {
        this.searchPaths = searchPaths;
    }

    public String[] getSearchPaths() {
        return this.searchPaths;
    }

    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPassphrase() {
        return passphrase;
    }

    public void setPassphrase(String passphrase) {
        this.passphrase = passphrase;
    }

    public boolean isStrictHostKeyChecking() {
        return strictHostKeyChecking;
    }

    public void setStrictHostKeyChecking(boolean strictHostKeyChecking) {
        this.strictHostKeyChecking = strictHostKeyChecking;
    }

    /*
    protected File getWorkingDirectory() {
        if (this.uri.startsWith("file:")) {
            try {
                return new UrlResource(StringUtils.cleanPath(this.uri)).getFile();
            }
            catch (Exception e) {
                throw new IllegalStateException(
                        "Cannot convert uri to file: " + this.uri);
            }
        }
        return this.basedir;
    }

    protected String[] getSearchLocations(File dir, String application, String profile,
                                          String label) {
        String[] locations = this.searchPaths;
        if (locations == null || locations.length == 0) {
            locations = DEFAULT_LOCATIONS;
        }
        else if (locations != DEFAULT_LOCATIONS) {
            locations = StringUtils.concatenateStringArrays(DEFAULT_LOCATIONS, locations);
        }
        Collection<String> output = new LinkedHashSet<String>();
        for (String location : locations) {
            String[] profiles = new String[] { profile };
            if (profile != null) {
                profiles = StringUtils.commaDelimitedListToStringArray(profile);
            }
            String[] apps = new String[] { application };
            if (application != null) {
                apps = StringUtils.commaDelimitedListToStringArray(application);
            }
            for (String prof : profiles) {
                for (String app : apps) {
                    String value = location;
                    if (app != null) {
                        value = value.replace("{application}", app);
                    }
                    if (prof != null) {
                        value = value.replace("{profile}", prof);
                    }
                    if (label != null) {
                        value = value.replace("{label}", label);
                    }
                    if (!value.endsWith("/")) {
                        value = value + "/";
                    }
                    output.addAll(matchingDirectories(dir, value));
                }
            }
        }
        return output.toArray(new String[0]);
    }

    private List<String> matchingDirectories(File dir, String value) {
        List<String> output = new ArrayList<String>();
        try {
            PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver(
                    this.resourceLoader);
            String path = new File(dir, value).toURI().toString();
            for (Resource resource : resolver.getResources(path)) {
                if (resource.getFile().isDirectory()) {
                    output.add(resource.getURI().toString());
                }
            }
        }
        catch (IOException e) {
        }
        return output;
    }
    */
}
