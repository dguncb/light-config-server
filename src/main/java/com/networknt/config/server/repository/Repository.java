package com.networknt.config.server.repository;

import java.util.Map;

/**
 * Created by stevehu on 2017-03-02.
 */
public interface Repository {
    Map<String, Object> findOne(String service, String profile, String name);
}
