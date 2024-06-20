# infrastructure4j
> 必须使用JDK17以上的版本

基于Spring boot 3.x的Java基础框架.

## 一、引入框架:
maven:

1.1 通过继承的方式引入
```xml
<parent>
    <groupId>com.reopenai</groupId>
    <artifactId>infrastructure4j-bom</artifactId>
    <version>1.0.0-SNAPSHOT</version>
</parent>
```

1.2 通过声明依赖的方式导入
```xml
 <dependencyManagement>
    <dependencies>
        <dependency>
            <groupId>org.apache.logging.log4j</groupId>
            <artifactId>log4j-bom</artifactId>
            <version>${log4j2.version}</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>
    </dependencies>
</dependencyManagement>
```

## 二、用户指南:

- infrastructure4j-etcd: 在Spring boot3中集成etcd.请参考 [使用说明](./infrastructure4j-project/infrastructure4j-etcd/READEM.md)

## 三、开发指南：
- Step1: mvn install build-checkstyle
- Step2: mvn install infrastructure4j-bom