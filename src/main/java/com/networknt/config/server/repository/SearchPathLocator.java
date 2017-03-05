package com.networknt.config.server.repository;

import java.util.Arrays;

/**
 * Created by stevehu on 2017-03-02.
 */
public interface SearchPathLocator {
    Locations getLocations(String service, String profile, String name);

    class Locations {
        private final String service;
        private final String profile;
        private final String name;
        private final String[] locations;
        private final String version;

        public Locations(String service, String profile, String name, String version, String[] locations) {
            this.service = service;
            this.profile = profile;
            this.name = name;
            this.locations = locations;
            this.version = version;
        }

        public String[] getLocations() {
            return locations;
        }

        public String getVersion() {
            return version;
        }

        public String getService() {
            return service;
        }

        public String getProfile() {
            return profile;
        }

        public String getName() {
            return name;
        }

        @Override
        public String toString() {
            return "Locations [service=" + service + ", profile=" + profile
                    + ", name=" + name + ", locations=" + Arrays.toString(locations)
                    + ", version=" + version + "]";
        }
    }
}
