package com.networknt.config.server;

import com.networknt.config.Config;
import com.networknt.server.HandlerProvider;
import io.undertow.Handlers;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Methods;
import com.networknt.info.ServerInfoGetHandler;
import com.networknt.config.server.handler.*;

public class PathHandlerProvider implements HandlerProvider {
    @Override
    public HttpHandler getHandler() {
        return Handlers.routing()
            .add(Methods.GET, "/v1/config/{version}/{profile}/{service}", new ConfigVersionProfileServiceGetHandler())
            .add(Methods.GET, "/v1/server/info", new ServerInfoGetHandler())
        ;
    }
}

