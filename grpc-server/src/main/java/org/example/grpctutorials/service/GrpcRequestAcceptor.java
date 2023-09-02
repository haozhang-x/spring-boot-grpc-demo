package org.example.grpctutorials.service;


import com.example.grpcdemo.grpc.lib.api.Payload;
import com.example.grpcdemo.grpc.lib.api.RequestGrpc;
import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
import org.example.grpctutorials.registy.RequestHandlerRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import request.Request;
import request.RequestHandler;
import request.RequestMeta;
import response.ErrorResponse;
import response.Response;
import util.GrpcUtils;

/**
 * rpc request acceptor of grpc.
 */
@Slf4j
@GrpcService
public class GrpcRequestAcceptor extends RequestGrpc.RequestImplBase {

    @Autowired
    private RequestHandlerRegistry requestHandlerRegistry;

    private void trace(Payload grpcRequest, boolean receive) {
        try {
            log.info("Payload {},meta={},body={}", receive ? "receive" : "send",
                    grpcRequest.getMetadata().toByteString().toStringUtf8(),
                    grpcRequest.getBody().toByteString().toStringUtf8());
        } catch (Throwable throwable) {
            log.error("Request error={}",
                    grpcRequest.toByteString().toStringUtf8());
        }

    }

    @Override
    public void request(Payload grpcRequest, StreamObserver<Payload> responseObserver) {
        trace(grpcRequest, true);
        String type = grpcRequest.getMetadata().getType();

        RequestHandler requestHandler = requestHandlerRegistry.getByRequestType(type);
        //no handler found.
        if (requestHandler == null) {
            log.warn(String.format("[%s] No handler for request type : %s :", "grpc", type));
            Payload payloadResponse = GrpcUtils
                    .convert(ErrorResponse.build("RequestHandler Not Found"));
            trace(payloadResponse, false);
            responseObserver.onNext(payloadResponse);
            responseObserver.onCompleted();
            return;
        }

        Object parseObj = null;
        try {
            parseObj = GrpcUtils.parse(grpcRequest);
        } catch (Exception e) {
            log.warn("[grpc] Invalid request receive error={}", e.getMessage(), e);
            Payload payloadResponse = GrpcUtils.convert(ErrorResponse.build(e.getMessage()));
            trace(payloadResponse, false);
            responseObserver.onNext(payloadResponse);
            responseObserver.onCompleted();
            return;
        }

        if (parseObj == null) {
            log.warn("Invalid request receive  ,parse request is null");
            Payload payloadResponse = GrpcUtils
                    .convert(ErrorResponse.build("Invalid request"));
            trace(payloadResponse, false);
            responseObserver.onNext(payloadResponse);
            responseObserver.onCompleted();
            return;
        }

        if (!(parseObj instanceof Request)) {
            log.warn("Invalid request receive  ,parsed payload is not a request,parseObj={}",
                    parseObj);
            Payload payloadResponse = GrpcUtils
                    .convert(ErrorResponse.build("Invalid request"));
            trace(payloadResponse, false);
            responseObserver.onNext(payloadResponse);
            responseObserver.onCompleted();
            return;
        }

        Request request = (Request) parseObj;
        try {
            RequestMeta requestMeta = new RequestMeta();
            Response response = requestHandler.handleRequest(request, requestMeta);
            Payload payloadResponse = GrpcUtils.convert(response);
            trace(payloadResponse, false);
            responseObserver.onNext(payloadResponse);
            responseObserver.onCompleted();
        } catch (Throwable e) {
            log.error("[{}] Fail to handle request,error message :{}", "grpc", e.getMessage(),
                    e);
            Payload payloadResponse = GrpcUtils.convert(ErrorResponse.build(e));
            trace(payloadResponse, false);
            responseObserver.onNext(payloadResponse);
            responseObserver.onCompleted();
        }

    }

}