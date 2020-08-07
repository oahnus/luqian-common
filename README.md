# Common

## 配置

```yaml
# application.yml
luqian:
  inject: true # 是否将组建注入到Spring中 true or false required true时, 会加载Config下相关配置类
  # redisson config
  redisson:
    address: 127.0.0.1:6379
    password: root
    timeout: 3000
    connectTimeout: 10000;
    database: 0
    pool:
      pool-size: 50
      min-idle: 5
  zookeeper:
    connectStr: 127.0.0.1:2181
  qiniu:
    urlPrefix: http://cdn.xxx.com  # 用于生成私有空间文件token时，拼接域名前缀
    accessKey: 七牛云accesskey
    secretKey: 七牛云secretKey
```
