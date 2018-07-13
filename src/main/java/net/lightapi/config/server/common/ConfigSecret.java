
package net.lightapi.config.server.common;


/**
 * Represents config secret key-value pair.
 */

public class ConfigSecret implements ValueObject<ConfigSecret> {

  private String key;
  private String secret;

  private SecretValue secretValue;

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

  public ConfigSecret(String key,  SecretValue secretValue) {
    this.key = key;
    this.secretValue = secretValue;
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

  public SecretValue getSecretValue() {
    return secretValue;
  }

  public void setSecretValue(SecretValue secretValue) {
    this.secretValue = secretValue;
  }
}
