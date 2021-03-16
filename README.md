# whale-rpc
## 项目简介
whale-rpc是一个基于Netty的高性能、高可用Java RPC框架。
## 主要特性
- 支持多种系列化协议，包括Protobuf、Kryo、Hessian、MsgPack
- 支持多种软负载均衡和全局服务、单个服务负载均衡配置
- 基于Netty支持心跳检测机制和断连重连
- 基于Zookeeper实现服务注册与服务发现
- 支持服务多版本和分组
- 支持客户端异步调用
## 项目结构
~~~
whale
├── whale-rpc -- 核心模块
|        ├── whale-rpc-common -- 工具类及通用代码
|        ├── whale-rpc-load-balance -- 负载均衡模块
|        ├── whale-rpc-protocol -- 通信协议和序列化协议
|        └── whale-rpc-register -- 注册中心模块
├── whale-rpc-boot-starter -- 自定义spring-boot-starter
└── whale-rpc-example -- 示例
         ├── example-client -- 客户端
         ├── example-server -- 服务端
         └── example-server1 -- 服务端1
~~~

## 如何运行
1. 克隆代码
2. 安装Zookeeper
3. 配置修改
4. 运行服务端
5. 运行客户端

## TODO
1. 服务端异步多线程处理客户端请求
2. 多注册中心支持
3. 添加LRU、LFU、一致性HASH负载均衡
4. 支持自定义Filter