package net.lightapi.config.server.common.template;


import java.io.File;
import java.io.IOException;
import java.util.List;

public interface FileLoader {

  List<File> getTemplates (String path) throws IOException;

  void saveProcessedTemplate(String path, String templateName, String content) throws  IOException;



}
