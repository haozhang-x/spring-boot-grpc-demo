package org.example.grpcdemo.controller;

import lombok.RequiredArgsConstructor;
import org.example.grpcdemo.service.GrpcClientService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import response.ServerReloadResponse;

@RestController
@RequiredArgsConstructor
public class GrpcClientController {
    private final GrpcClientService grpcClientService;

    @RequestMapping("/")
    public ServerReloadResponse printMessage() {
        return grpcClientService.sendMessage();
    }
}
