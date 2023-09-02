package org.example.grpctutorials.handler;


import org.springframework.stereotype.Component;
import request.RequestHandler;
import request.RequestMeta;
import request.ServerReloadRequest;
import response.ServerReloadResponse;

/**
 * server reload request handler.
 */
@Component
public class ServerReloaderRequestHandler extends RequestHandler<ServerReloadRequest, ServerReloadResponse> {

    @Override
    public ServerReloadResponse handle(ServerReloadRequest request, RequestMeta meta) {
        ServerReloadResponse response = new ServerReloadResponse();
        response.setMessage("ok");
        return response;
    }
}
