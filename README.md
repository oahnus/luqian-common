# 个人工具包

## config

```yaml
# application.yml
luqian:
  enable: true # true or false required true时, 会加载Config下相关配置类
  # redisson config
  redisson:
    address: 39.98.237.99:6379
    password: chuxuan123sh
    timeout: 3000
    database: 0
    pool:
      max-active: 20
      max-idle: 10
      max-wait: 3000
      min-idle: 4
  qiniu:
    accessKey: 七牛云accesskey
    secretKey: 七牛云secretKey
```