package org.example.grpcdemo.service;

import lombok.extern.slf4j.Slf4j;
import org.example.grpcdemo.client.GrpcConnection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import request.ServerReloadRequest;
import response.ServerReloadResponse;

@Service
@Slf4j
public class GrpcClientService {
    @Autowired
    GrpcConnection grpcConnection;

    public ServerReloadResponse sendMessage() {
        ServerReloadResponse response = (ServerReloadResponse) grpcConnection.request(new ServerReloadRequest(), 1000);
        return response;
    }

}
