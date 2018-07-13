package net.lightapi.config.server.common.template;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class TemplatesFileLoader implements  FileLoader{

    static final Logger logger = LoggerFactory.getLogger(TemplatesFileLoader.class);

    @Override
    public List<File> getTemplates (String path) throws IOException {

     //   GenericExtFilter filter = new GenericExtFilter("yml");
        FilenameFilter textFilter = new FilenameFilter() {
            public boolean accept(File dir, String name) {
                String lowercaseName = name.toLowerCase();
                if (lowercaseName.endsWith(".yml")) {
                    return true;
                } else {
                    return false;
                }
            }
        };

        return Arrays.asList((new File(path)).listFiles(textFilter));

      /*  return Files.walk(Paths.get(path))
                .filter(Files::isRegularFile)
                .map(Path::toFile)
                .collect(Collectors.toList());*/
    }

    @Override
    public void saveProcessedTemplate(String path, String templateName, String content) throws  IOException {
        initializeDirectory(path);
        File template = new File (path, templateName);
        BufferedWriter writer = new BufferedWriter(new FileWriter(template));
        writer.write(content);
        writer.close();
    }

    public String loadStringFromFile(File file) {
        String content = null;
        InputStream inStream = null;
        try {
            inStream = new FileInputStream(file);

            if(inStream != null) {
                content = convertStreamToString(inStream);
            }
        } catch (Exception ioe) {
            logger.error("Exception", ioe);
        } finally {
            if(inStream != null) {
                try {
                    inStream.close();
                } catch(IOException ioe) {
                    logger.error("IOException", ioe);
                }
            }
        }
        return content;
    }

    static String convertStreamToString(java.io.InputStream is) {
        java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
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

    // inner class, generic extension filter
    public class GenericExtFilter implements FilenameFilter {

        private String ext;

        public GenericExtFilter(String ext) {
            this.ext = ext;
        }

        public boolean accept(File dir, String name) {
            return (name.endsWith(ext));
        }
    }
}
