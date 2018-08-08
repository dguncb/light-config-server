
package net.lightapi.config.server.common;


/**
 * Represents config key-value pair.
 */

public class ConfigValue implements ValueObject<ConfigValue> {

  private String key;
  private String value;



  /**
   * Create a new ConfigValue.
   *
   * @param key key value
   * @param value config value
   */
  public ConfigValue(String key, String value) {
    this.key = key;
    this.value = value;
  }

  public ConfigValue() {

  }

  public String getKey() {
    return key;
  }

  public void setKey(String key) {
    this.key = key;
  }

  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }
}
