# Spring Boot gRPC demo

Spring Boot 使用 gRPC

1. 使用 grpc-server-spring-boot-starter 构建了 `gRPC server`
2. 使用 grpc-client-spring-boot-starter 构建了 `gRPC client`
3. client 和 server 之间，使用双向 tls 认证

# 模块介绍

## grpc-lib

grpc基本模块，存放 proto 文件及 proto 生成的文件等

## grpc-server

grpc 服务端，依赖 grpc-lib，实现 grpc service

## grpc-client

grpc 客户端，依赖 grpc-lib，调用 grpc service

# 生成密钥

注意-subj中的参数，根据自己的情况进行调整，gRPC Server 需要此地址去连接，否则会报 tls 的错

## 生成服务端密钥

```bash
openssl req -x509 -nodes -subj "/CN=macbook-pro.local" -newkey rsa:4096 -sha256 -keyout server.key -out server.crt -days 3650
```

## 生成客户端密钥

```bash
openssl req -x509 -nodes -subj "/CN=macbook-pro.local" -newkey rsa:4096 -sha256 -keyout client.key -out client.crt -days 3650
```

# gRPC 配置

## gRPC Server 配置

```yaml
grpc:
  server:
    #端口
    port: 9090
    security:
      #开启安全配置
      enabled: true
      #证书
      certificate-chain: classpath:certificates/server.crt
      #私钥
      private-key: classpath:certificates/server.key
      #信任证书
      trust-cert-collection: classpath:certificates/trusted-clients.crt.collection
      #客户端认证
      client-auth: require
      #协议
      protocols: TLSv1.2
```

## gRPC Client 配置

```yaml
grpc:
  client:
    # gRPC配置的名字，GrpcClient注解会用到
    grpc-server:
      # gRPC服务端地址，这里要跟证书里面CN的一致，否则会报错
      address: 'static://macbook-pro.local:9090'
      #gRPC传输方式
      negotiationType: TLS
      #安全配置
      security:
        #信任证书
        trust-cert-collection: classpath:certificates/trusted-servers.crt.collection
        #客户端认证
        clientAuthEnabled: true
        #证书地址
        certificateChain: classpath:certificates/client.crt
        #私钥
        private-key: classpath:certificates/client.key
        #协议
        protocols: TLSv1.2
```
