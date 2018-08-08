
package net.lightapi.config.server.common;


import java.util.List;

/**
 * Represents config values for service.
 */

public class ServiceConfigValues implements ValueObject<ServiceConfigValues> {

  private String configServiceId;
  private List<ConfigValue> values;



  /**
   * Create a new ConfigValue.
   *
   * @param configServiceId config service Id
   * @param values config values
   */
  public ServiceConfigValues(String configServiceId, List<ConfigValue> values) {
    this.configServiceId = configServiceId;
    this.values = values;
  }

  public ServiceConfigValues() {

  }

  public String getConfigServiceId() {
    return configServiceId;
  }

  public void setConfigServiceId(String configServiceId) {
    this.configServiceId = configServiceId;
  }

  public List<ConfigValue> getValues() {
    return values;
  }

  public void setValues(List<ConfigValue> values) {
    this.values = values;
  }
}
