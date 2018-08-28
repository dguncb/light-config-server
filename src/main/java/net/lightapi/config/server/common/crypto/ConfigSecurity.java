package net.lightapi.config.server.common.crypto;



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
  String decrypt(String secretValue);

  /**
   * Encrypts the given {@code secret}.
   *
   * @param input Cleartext secret value
   * @return encrypted secret value
   */
  String ecrypt(String input);

}
