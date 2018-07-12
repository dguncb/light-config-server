package net.lightapi.config.server.common.template;

import net.lightapi.config.server.common.paths.ConfigKeyValuePath;

import java.util.List;

public interface TemplateMapping {

  List<ConfigKeyValuePath> transform(String template);

}
