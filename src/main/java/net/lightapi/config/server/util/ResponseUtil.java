package net.lightapi.config.server.util;

import com.networknt.config.Config;

import net.lightapi.config.server.status.ConfigStatus;


import java.util.LinkedHashMap;
import java.util.Map;

public class ResponseUtil {

    static String ERROR_NOT_DEFINED = "ERR10042";

    public static String populateErrorResponse(String serviceClass, String error)  {
        String result;
        final Map<String, Object> errorMap = new LinkedHashMap<>();
        ConfigStatus status = new ConfigStatus(ERROR_NOT_DEFINED, serviceClass, error);

        // populate error in response map
        errorMap.put("statusCode", status.getStatusCode());
        errorMap.put("code", status.getCode());
        errorMap.put("message", status.getMessage());
        errorMap.put("description", status.getDescription());
        errorMap.put("hybrid-service", status.getService().getServiceName());
        errorMap.put("hybrid-service-error", status.getService().getDesc());
        try {
            result = Config.getInstance().getMapper().writeValueAsString(errorMap);
        }catch (Exception e) {
            System.out.println("generate error response failed:" + e.getMessage());
            result = "";
        }
        return result;
    }

}
