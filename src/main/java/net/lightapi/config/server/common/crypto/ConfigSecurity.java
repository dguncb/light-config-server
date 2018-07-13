package net.lightapi.config.server.common.crypto;


import net.lightapi.config.server.common.SecretValue;

/**
 * Interface for checking and encrypting config value.
 */
public interface ConfigSecurity {

  /**
   * decrypt the given {@code secretValue} .
   *
   * @param secretValue Encrypted secret Value
   * @return Cleartext secret value
   */
  String decrypt(SecretValue secretValue);

  /**
   * Encrypts the given {@code secret}.
   *
   * @param secret Cleartext secret value
   * @return encrypted secret value
   */
  SecretValue ecrypt(String secret);

}
