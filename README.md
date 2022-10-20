<div align="center">
  <p>
    <img src="logo.jpg"  height="200px" />
  </p>
  <p>山海Power - 基于SpringBoot的通用Web权限组件</p>
  <p>ShanHaiPower-based SpringBoot Web Permission components</p>
  <p>
    <a href="https://github.com/SeeMountainSea/shanhai-power-spring-boot-starter/releases/latest"><img alt="GitHub release (latest by date)" src="https://img.shields.io/github/v/release/SeeMountainSea/shanhai-power-spring-boot-starter"/></a>
    <a href="https://github.com/SeeMountainSea/shanhai-power-spring-boot-starter/issues"><img alt="GitHub closed issues" src="https://img.shields.io/github/issues/SeeMountainSea/shanhai-power-spring-boot-starter?color=009688"/></a>
    <a href="https://github.com/topics/java"><img alt="GitHub top language" src="https://img.shields.io/github/languages/top/SeeMountainSea/shanhai-power-spring-boot-starter?color=eb8031"/></a>
    <br>
    <a href="https://github.com/SeeMountainSea/shanhai-power-spring-boot-starter/find/master"><img alt="GitHub Code Size" src="https://img.shields.io/github/languages/code-size/SeeMountainSea/shanhai-power-spring-boot-starter?color=795548"/></a>
    <a href="https://github.com/SeeMountainSea/shanhai-power-spring-boot-starter/find/master"><img alt="GitHub Code Lines" src="https://img.shields.io/tokei/lines/github/SeeMountainSea/shanhai-power-spring-boot-starter?color=37474F"/></a>
    <a href="https://github.com/SeeMountainSea/shanhai-power-spring-boot-starter/blob/master/LICENSE"><img alt="GitHub License" src="https://img.shields.io/github/license/SeeMountainSea/shanhai-power-spring-boot-starter?color=534BAE"/></a>
  </p>
</div>


ShanHaiPower 主要提供以下能力：

- 基于注解模式的权限校验

- 基于路由模式的权限校验

- 支持前后端分离模式下独立用户鉴权和会话数据读写

- 支持自定义权限集合实现和自定义路由集合实现

- 支持自行扩展缓存协议实现数据持久化（组件默认提供基于原生Redis协议的持久化组件）

  Shiro和SpringSecurity集成约束太多？文档太少？太过繁琐？

  看过来，山海Power极度精简，只为需要权限校验的你。

## 1.引入组件

```xml
<dependency>
    <groupId>com.wangshanhai.power</groupId>
    <artifactId>shanhai-power-spring-boot-starter</artifactId>
    <version>${last.version}</version>
</dependency>
```

## 2.启用组件

SpringBoot 2.x 启用方式

```java
@Configuration
@EnableShanHaiPower
public class ShanhaiConfig implements WebMvcConfigurer {
  
}
```

SpringBoot 1.5.x 启用方式

```java
@Configuration
@EnableShanHaiPower
@EnableConfigurationProperties(ShanhaiPowerConfig.class)
@AutoConfigureAfter(WebMvcConfigurationSupport.class)
public class ShanhaiConfig extends WebMvcConfigurationSupport {
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new ShanhaiPowerInterceptor()).addPathPatterns("/**");
        registry.addInterceptor(new ShanhaiPowerAnnotationPermissionsInterceptor()).addPathPatterns("/**");
        registry.addInterceptor(new ShanhaiPowerRoutePermissionsInterceptor()).addPathPatterns("/**");
    }
}
```

SpringBoot 1.5.x 启用方式需要自己手动注册相关组件。

组件说明：

| 组件                                         | 组件名称             | 组件说明                         |
| -------------------------------------------- | :------------------- | -------------------------------- |
| ShanhaiPowerInterceptor                      | 用户身份鉴权组件     | 对用户登录的有效性进行鉴权       |
| ShanhaiPowerAnnotationPermissionsInterceptor | 用户注解权限鉴权组件 | 对用户进行单一资源有效性进行鉴权 |
| ShanhaiPowerRoutePermissionsInterceptor      | 用户路由权限鉴权组件 | 对用户进行路由资源有效性进行鉴权 |

## 3.配置说明

```yaml
shanhai:
  power:
    tokenName: token #Token名称
    tokenAlgorithm: uuid # Token生成算法
    token-prefix: 'shanhai ' #Token前缀
    route-permissions: #路由权限定义
      - path: '/route/**'
        permission: 'user:route'
    exclusive-login: true #同端互斥登录
    route-permission-enable: true #启用路由权限组件
    auth-path-patterns: #鉴权组件拦截范围
      - /**
    permission-path-patterns: #权限组件拦截范围 （路由和注解权限共享）
      - /**
    auto-regist: true #自动注册鉴权和注解校验组件（默认启用|如果自己想自行注册，可以关闭）
```

## 4.用户会话组件

| API说明                                                  | API能力                                 | 备注                                |
| -------------------------------------------------------- | --------------------------------------- | ----------------------------------- |
| ShanhaiPower.login("xxx")                                | 通过用户标识进行登录（xxx为用户名或ID） | 1.默认渠道为Default 2.返回TokenInfo |
| ShanhaiPower.login("xxx","PC")                           | 指定渠道登录                            | 返回TokenInfo                       |
| ShanhaiPower.getCurrentUserToken()                       | 获取当前登录的用户信息                  | 返回TokenInfo                       |
| ShanhaiPower.setTokenSessionData(String key,Object data) | 设置基于Token的会话级数据               |                                     |
| ShanhaiPower.getTokenSessionData(String key)             | 获取基于Token的会话级数据               |                                     |

忽略用户会话校验

```java
@RequestNotNeedAuth
@GetMapping("/login")
public TokenInfo login(){
   return ShanhaiPower.login("xxx");
}
```

## 5.注解权限组件

```java
@RequiresPermissions("user:details")
@GetMapping("/queryUserInfo")
public TokenInfo queryUserInfo(){
    return ShanhaiPower.getCurrentUserToken();
}
```

在对应的方法上添加@RequiresPermissions，并且写入权限编码。

实现PermissionService，动态为当前用户追加权限编码。

```java
@Service
public class PermissionServiceImpl implements PermissionService {
    @Override
    public List<String> queryAllPermission(HttpServletRequest request) {
        List<String> allPermission=new ArrayList<>();
        allPermission.add("user:details");
        allPermission.add("user:route");
        return allPermission;
    }

}
```

## 6.路由权限组件

对于一些简单的系统，可能只想基于路由做一些简单的控制，此时可以考虑使用路由组件。

需要注意的是，路由组件支持通过配置文件进行配置

也支持通过实现PermissionService中的loadRoutePermissionConfig方法来进行。**自定义加载的方式优先级高于配置文件的方式**。

```yaml
shanhai:
  power:
    route-permissions:  #路由权限配置（可以配置多个）
      - path: '/route/**'
        permission: 'user:route'
```

路由权限组件同样需要实现PermissionService，动态为当前用户追加权限编码。

实现方式参考第5章节。

## 7.统一异常管理

| 组件异常类                    | 说明                 |
| ----------------------------- | -------------------- |
| ShanHaiNotLoginException      | 用户会话鉴权相关异常 |
| ShanHaiNotPermissionException | 用户权限相关异常     |
| ShanHaiPowerException         | 异常基类             |

可以使用SpringBoot的统一异常管理进行控制

```java
@ExceptionHandler(value = ShanHaiPowerException.class)
public ResponseEntity<?> shanHaiPowerErrorHandler(Exception e) {
    Map<String, Object> resp=new HashMap<>();
    resp.put("code",((ShanHaiPowerException)e).getCode());
    resp.put("message",e.getMessage());
    HttpHeaders headers = new HttpHeaders();
    MediaType mediaType = new MediaType("application","json", StandardCharsets.UTF_8);
    headers.setContentType(mediaType);
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR.value()).headers(headers).body(resp);
}
```

## 8.扩展-会话持久化

可以通过实现PowerStoreService，来实现自定义的会话持久化。

```java
/**
 * 会话存储服务
 * @author Shmily
 */
public interface PowerStoreService {

    /**
     * 设置缓存失效时间
     * @param key
     * @param time (单位s)
     * @return
     */
    Long expire(String key, int time);

    /**
     * 判断key是否存在
     * @param key
     * @return
     */
    boolean exists(String key);
    /**
     * 查询key过期时间
     * @param key
     * @return
     */
    long ttl(String key);
    /**
     * 删除key
     * @param key
     * @return
     */
    void del(String key);
    /**
     * 读取key对应的值
     * @param key
     * @return
     */
    Object get(String key);
    /**
     * 设置key:value
     * @param key
     * @return
     */
    void set(String key, Object value);
    /**
     * 设置key和过期时间
     * @param key
     * @return
     */
    void set(String key, Object value, long time);

}
```

## 9.扩展-Token生成规则

组件默认集成了uuid和sha512方式的Token生成规则，可以通过配置参数来实现。

同时，也支持自己实现TokenGenerateService，来实现自定义的Token生成规则。

如果需要传入额外参数生成Token，需要在登录的时候使用如下方法登录：

```
ShanhaiPower.login(Object userFlag,String channel, Map<String, Object> extParams)
```
