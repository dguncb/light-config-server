
package net.lightapi.config.server.common;


/**
 * Represents config Secret value.
 */

public class SecretValue implements ValueObject<SecretValue> {

  private String secretHash;
  private String secretSalt;



  /**
   * Create a new secret value.
   *
   * @param secretHash secret value hash
   * @param secretSalt secret value salt
   */
  public SecretValue(String secretHash, String secretSalt) {
    this.secretHash = secretHash;
    this.secretSalt = secretSalt;
  }


  public String getSecretHash() {
    return secretHash;
  }

  public void setSecretHash(String secretHash) {
    this.secretHash = secretHash;
  }

  public String getSecretSalt() {
    return secretSalt;
  }

  public void setSecretSalt(String secretSalt) {
    this.secretSalt = secretSalt;
  }
}
