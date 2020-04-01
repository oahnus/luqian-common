# 个人工具包

## config

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

mybatis 枚举转换 
1. 枚举类型 implements BaseEnum类
2. 创建TypeHandler
    ```java
    @MappedTypes(UserSource.class)
    @MappedJdbcTypes(JdbcType.TINYINT)
    public class MyTypeHandler MyEnumTypeHandler<? extends BaseEnum> {
        
    }
    ```
3. 在application.yml中配置 typeHandler package
    ```yaml
    mybatis:
      type-handlers-package: com.xxx.xxx.typehandler
    
    ```

在Entity中使用枚举类型时，要添加@Column注解，将枚举类型当做表字段（默认情况下只有简单类型才会被当作表中的字段）
