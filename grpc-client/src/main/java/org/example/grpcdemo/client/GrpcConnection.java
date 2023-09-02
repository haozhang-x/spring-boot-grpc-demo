
package org.example.grpcdemo.client;


import com.example.grpctutorials.grpc.lib.api.Payload;
import com.example.grpctutorials.grpc.lib.api.RequestGrpc;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.springframework.stereotype.Service;
import registry.PayloadRegistry;
import request.Request;
import request.RequestCallBack;
import request.RequestFuture;
import response.ErrorResponse;
import response.Response;
import response.ResponseCode;
import util.GrpcUtils;

import java.util.concurrent.CancellationException;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * gRPC Client.
 */
@Service
public class GrpcConnection {

    static {
        //加载payLoad
        PayloadRegistry.init();
    }

    @GrpcClient("grpc-server")
    private RequestGrpc.RequestFutureStub grpcFutureServiceStub;

    public Response request(Request request, long timeouts) {
        Payload grpcRequest = GrpcUtils.convert(request);
        ListenableFuture<Payload> requestFuture = grpcFutureServiceStub.request(grpcRequest);
        Payload grpcResponse;
        try {
            grpcResponse = requestFuture.get(timeouts, TimeUnit.SECONDS);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
        Response response = (Response) GrpcUtils.parse(grpcResponse);
        if (response instanceof ErrorResponse) {
            throw new RuntimeException(response.getErrorCode() + response.getMessage());
        }
        return response;
    }


    public RequestFuture requestFuture(Request request) {
        Payload grpcRequest = GrpcUtils.convert(request);

        final ListenableFuture<Payload> requestFuture = grpcFutureServiceStub.request(grpcRequest);
        return new RequestFuture() {

            @Override
            public boolean isDone() {
                return requestFuture.isDone();
            }

            @Override
            public Response get() throws Exception {
                Payload grpcResponse = requestFuture.get();
                Response response = (Response) GrpcUtils.parse(grpcResponse);
                if (response instanceof ErrorResponse) {
                    throw new RuntimeException(response.getErrorCode() + response.getMessage());
                }
                return response;
            }

            @Override
            public Response get(long timeout) throws Exception {
                Payload grpcResponse = requestFuture.get(timeout, TimeUnit.MILLISECONDS);
                Response response = (Response) GrpcUtils.parse(grpcResponse);
                if (response instanceof ErrorResponse) {
                    throw new RuntimeException(response.getErrorCode() + response.getMessage());
                }
                return response;
            }
        };
    }

    public void asyncRequest(Request request, final RequestCallBack requestCallBack) {
        Payload grpcRequest = GrpcUtils.convert(request);
        ListenableFuture<Payload> requestFuture = grpcFutureServiceStub.request(grpcRequest);

        //set callback .
        Futures.addCallback(requestFuture, new FutureCallback<Payload>() {
            @Override
            public void onSuccess(@Nullable Payload grpcResponse) {
                Response response = (Response) GrpcUtils.parse(grpcResponse);

                if (response != null) {
                    if (response instanceof ErrorResponse) {
                        requestCallBack.onException(new RuntimeException(response.getErrorCode() + response.getMessage()));
                    } else {
                        requestCallBack.onResponse(response);
                    }
                } else {
                    requestCallBack.onException(new RuntimeException(ResponseCode.FAIL.getCode() + "response is null"));
                }
            }

            @Override
            public void onFailure(Throwable throwable) {
                if (throwable instanceof CancellationException) {
                    requestCallBack.onException(
                            new TimeoutException("Timeout after " + requestCallBack.getTimeout() + " milliseconds."));
                } else {
                    requestCallBack.onException(throwable);
                }
            }
        }, requestCallBack.getExecutor());
        // set timeout future.
        ListenableFuture<Payload> payloadListenableFuture = Futures
                .withTimeout(requestFuture, requestCallBack.getTimeout(), TimeUnit.MILLISECONDS,
                        new ScheduledThreadPoolExecutor(1));
    }
}



