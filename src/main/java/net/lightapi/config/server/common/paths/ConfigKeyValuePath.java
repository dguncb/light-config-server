package net.lightapi.config.server.common.paths;


import net.lightapi.config.server.common.ConfigValue;

import java.util.Arrays;

import static java.util.stream.Collectors.joining;

public class ConfigKeyValuePath {

  final String[] splits;
  final String value;

  public ConfigKeyValuePath(String[] splits, String value) {
    this.splits = splits;
    this.value = value;
  }

  public ConfigKeyValuePath(ConfigValue configValue) {
    if (!configValue.getKey().contains("/")) {
      throw new IllegalArgumentException("Should include  with / " + configValue.getKey());
    }

    this.splits = splitPath(configValue.getKey());
    this.value = configValue.getValue();
  }

  private String[] splitPath(String path) {
    return path.split("/");
  }

  public static ConfigKeyValuePath parse(ConfigValue configValue) {
    return new ConfigKeyValuePath(configValue);
  }

  public int length() {
    return splits.length;
  }

  public String toPath() {
    return Arrays.stream(splits).collect(joining("/"));
  }

  public String getTemplate() {
    return splits[0];
  }


  public String getValue() {
    return this.value;
  }
}
