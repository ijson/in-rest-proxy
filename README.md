# in-restful ![构建状态](https://travis-ci.org/gyk001/beetl2.0.svg)

http://www.ijson.com/

- 封装restful请求,支持POST,GET,PUT,DELETE
- 支持json及xml数据格式
- 单一依赖,支持多个不同服务地址
- 支持配置中心依赖下载

Maven 地址

    <dependency>
      <groupId>com.ijson</groupId>
      <artifactId>in-rest-proxy</artifactId>
      <version>1.0.2</version>
    </dependency>

# 使用方式
`
案例列举以微信支付接口及淘宝接口进行介绍
`

[微信支付-统一下单](https://pay.weixin.qq.com/wiki/doc/api/jsapi.php?chapter=9_1):

1. 根据接口定义自己的model,请求及返回参数定义

```
详见:Unifiedorder.java
其中:
    @INField 注解主要校验当前字段是否为必填字段,可根据此字段生成接口文档
    @XStreamAlias("xml") 定义xml最外层标签
    @CDATA 如果是xml 则会自动添加 <![CDATA[" 开始，由 "]]> 标签
    @Data 自动生成get和set方法,idea的话需要安装lombok插件
```
2. 定义数据拼装解析类

```
详见:WeixinRestCodeC.java
主要是解决不同数据格式带来的问题,由开发者自定义

继承 AbstractRestCodeC.java

encodeArg为拼装数据结构

XmlUtil.toXml(T) 将对象转换成xml

decodeResult(int,map,byte,T) 解析返回的数据结构

XmlUtil.toBean(xml, T); 将xml转换成对象
 
validateResult 校验返回结果是否正确

```

3. 定义接口地址资源
```
详见:WeixinResource.java

@RestResource 定义资源信息

其中
   value : 配置文件中的Key
   desc :描述信息
   codec : 数据拼装解析类
   contentType : 数据返回类型

@POST
    value 地址 ,配置文件中的url+此路径为服务整体地址
    desc 描述

@GET

    value 地址 ,配置文件中的url+此路径为服务整体地址
    desc 描述


```





