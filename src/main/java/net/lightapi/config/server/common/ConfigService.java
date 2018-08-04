
package net.lightapi.config.server.common;


/**
 * Represents config key-value pair.
 */

public class ConfigService implements ValueObject<ConfigService> {

  private String configServiceId;
  private String serviceId;
  private EncryptionAlgorithm encryptionAlgorithm = EncryptionAlgorithm.AES;
  private String encryptionSalt;
  private String templateRepository;
  private String serviceOwner;

  private String version;

  private String profile;
  private boolean refreshed;

  public ConfigService() {
  }

  public String getConfigServiceId() {
    return configServiceId;
  }

  public void setConfigServiceId(String configServiceId) {
    this.configServiceId = configServiceId;
  }

  public String getServiceId() {
    return serviceId;
  }

  public void setServiceId(String serviceId) {
    this.serviceId = serviceId;
  }

  public EncryptionAlgorithm getEncryptionAlgorithm() {
    return encryptionAlgorithm;
  }

  public void setEncryptionAlgorithm(EncryptionAlgorithm encryptionAlgorithm) {
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

  public boolean isRefreshed() {
    return refreshed;
  }

  public void setRefreshed(boolean refreshed) {
    this.refreshed = refreshed;
  }
}
