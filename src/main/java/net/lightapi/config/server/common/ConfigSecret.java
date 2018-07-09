
package net.lightapi.config.server.common;


/**
 * Represents config secret key-value pair.
 */

public class ConfigSecret implements ValueObject<ConfigSecret> {

  private String key;
  private String secret;



  /**
   * Create a new ConfigValue.
   *
   * @param key key value
   * @param secret config value
   */
  public ConfigSecret(String key, String secret) {
    this.key = key;
    this.secret = secret;
  }

  public String getKey() {
    return key;
  }

  public void setKey(String key) {
    this.key = key;
  }

  public String getSecret() {
    return secret;
  }

  public void setSecret(String secret) {
    this.secret = secret;
  }
}
