# 甄嬛传人物管理系统

这是一个基于Spring Boot和Neo4j的角色管理系统，提供了完整的前端界面来管理和查询角色数据。

## 项目功能

### 后端功能
- 基于Spring Boot 3.2.0构建
- 使用Neo4j作为图数据库
- 提供RESTful API接口
- 集成Swagger文档

### 前端功能
- 响应式设计，支持移动端和桌面端
- 角色管理：查看、添加、编辑、删除角色
- 多条件查询：按姓名、阵营、称号查询角色
- 角色关系查询：查看两个角色之间的关系
- 实时反馈：所有操作结果都有清晰的提示信息

## 技术栈

### 后端
- Java 17
- Spring Boot 3.2.0
- Spring Data Neo4j
- SpringDoc OpenAPI (Swagger)

### 前端
- HTML5
- Tailwind CSS v3
- Font Awesome
- 纯JavaScript (原生Fetch API)

## 快速开始

### 前提条件
- JDK 17或更高版本
- Maven 3.6+ 或 Gradle 7.0+
- Neo4j数据库 (默认连接到 `bolt://localhost:7687`)

### 配置Neo4j
在 `application.yml` 中配置Neo4j连接信息：

```yaml
spring:
  neo4j:
    uri: bolt://localhost:7687
    authentication:
      username: neo4j
      password: 你的密码
```

### 运行项目

1. 编译打包
```bash
mvn clean package
```

2. 运行应用
```bash
java -jar target/neo4j-db-1.0.0.jar
```

3. 访问应用
- 前端界面：http://localhost:8081/
- Swagger文档：http://localhost:8081/swagger-ui.html

## 前端界面说明

### 主要页面
1. **所有角色** - 显示所有角色列表，支持搜索过滤
2. **查询角色** - 提供多种查询方式：按姓名、阵营、称号查询，获取角色介绍
3. **角色关系** - 查询两个角色之间的关系

### 角色管理操作
- **查看详情** - 点击角色卡片查看完整信息
- **编辑阵营/称号** - 在详情页可以修改角色的阵营或称号
- **删除角色** - 在详情页可以删除角色

### 操作反馈
所有操作（添加、编辑、删除、查询等）都会有即时的视觉反馈，成功或失败的信息会通过右下角的通知提示。

## API接口

### 角色管理
- `GET /api/persons/all` - 获取所有角色
- `GET /api/persons/name/{name}` - 根据姓名查询角色
- `DELETE /api/persons/deletePerson/{name}` - 删除角色

### 角色查询
- `GET /api/persons/alliance/{alliance}` - 根据阵营查询角色
- `GET /api/persons/title/{title}` - 根据称号查询角色
- `GET /api/persons/introduction/{name}` - 获取角色介绍

### 角色更新
- `PATCH /api/persons/{id}/alliance` - 更新角色阵营
- `PATCH /api/persons/{id}/title` - 更新角色称号

### 关系查询
- `GET /api/persons/relationship/{name1}/{name2}` - 查询两个角色之间的关系

## 开发说明

### 前端文件位置
前端界面文件位于 `src/main/resources/static/index.html`

### CORS配置
为支持跨域请求，项目包含了 `CorsConfig.java` 配置类

### 自定义样式
前端使用Tailwind CSS自定义配置，主要颜色和样式定义在HTML文件的 `<style>` 标签中

## 浏览器兼容性

支持现代浏览器的最新版本，包括：
- Chrome
- Firefox
- Safari
- Edge

## 许可证

本项目仅供学习使用。