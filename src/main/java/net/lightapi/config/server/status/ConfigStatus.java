
package net.lightapi.config.server.status;

import java.util.Map;

public class ConfigStatus extends com.networknt.status.Status {

    private String severity;
    private Service service;

    private ConfigStatus() {
        super();
    }

    public ConfigStatus(String code) {
        super(code, (Object[])null);
        setSeverity();
    }

    private void setSeverity() {
		@SuppressWarnings("unchecked")
		Map<String, Object> map = (Map<String, Object>)config.get(getCode());
		if (map != null) {
			severity = (String) map.get("severityCode");
		}
	}
    public ConfigStatus(String code, Service service) {
        super(code, (Object[])null);
        this.service = service;
        setSeverity();
    }

    public ConfigStatus(String code, Service service, Object... args) {
        super(code, args);
        this.service = service;
        setSeverity();
    }
    
    public ConfigStatus(String code, String serviceName, String desc) {
       super(code, (Object[])null);
       Service service = new Service(serviceName, desc);
       this.service = service;
       setSeverity();
    }
    
    public ConfigStatus(String code,  String serviceName, String desc, Object... args) {
       super(code, args);
        Service service = new Service(serviceName, desc);
        this.service = service;
       setSeverity();
    }

    /**
     * @return the severity
     */
    public String getSeverity() {
        return severity;
    }

    /**
     * @param severity
     *            the severity to set
     */
    public void setSeverity(String severity) {
        this.severity = severity;
    }

    public Service getService() {
        return service;
    }

    public void setService(Service service) {
        this.service = service;
    }

    public static class Service {
        private String serviceName;
        private String desc;

        /**
         * Default constructor
         */
        public Service() {
            super();
        }

        /**
         * Constructor based values injection
         * 
         * @param serviceName
         *            name of the service
         * @param desc
         *            desc related to source
         */
        public Service(String serviceName, String desc) {
            this.serviceName = serviceName;
            this.desc = desc;
        }

        public String getServiceName() {
            return serviceName;
        }

        public void setServiceName(String serviceName) {
            this.serviceName = serviceName;
        }

        public String getDesc() {
            return desc;
        }

        public void setDesc(String desc) {
            this.desc = desc;
        }
    }

}
