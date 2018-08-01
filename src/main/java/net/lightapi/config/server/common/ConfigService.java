
package net.lightapi.config.server.common;


/**
 * Represents config key-value pair.
 */

public class ConfigService implements ValueObject<ConfigService> {

  private String serviceId;
  private String encryptionAlgorithm;
  private String encryptionSalt;
  private String templateRepository;
  private String serviceOwner;

  private String version;

  private String profile;





  public ConfigService() {
  }

  public String getServiceId() {
    return serviceId;
  }

  public void setServiceId(String serviceId) {
    this.serviceId = serviceId;
  }

  public String getEncryptionAlgorithm() {
    return encryptionAlgorithm;
  }

  public void setEncryptionAlgorithm(String encryptionAlgorithm) {
    this.encryptionAlgorithm = encryptionAlgorithm;
  }

  public String getEncryptionSalt() {
    return encryptionSalt;
  }

  public void setEncryptionSalt(String encryptionSalt) {
    this.encryptionSalt = encryptionSalt;
  }

  public String getTemplateRepository() {
    return templateRepository;
  }

  public void setTemplateRepository(String templateRepository) {
    this.templateRepository = templateRepository;
  }

  public String getServiceOwner() {
    return serviceOwner;
  }

  public void setServiceOwner(String serviceOwner) {
    this.serviceOwner = serviceOwner;
  }

  public String getVersion() {
    return version;
  }

  public void setVersion(String version) {
    this.version = version;
  }

  public String getProfile() {
    return profile;
  }

  public void setProfile(String profile) {
    this.profile = profile;
  }
}
