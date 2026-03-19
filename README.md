# 用户代办插件

基于 Spring Boot (JDK 1.8) 开发的用户代办管理插件。

## 功能特性

1. **代办消息管理**
   - 字段：创建人、执行人、优先级、创建时间、到期时间、状态
   - 支持增删改查代办任务
   - 支持修改优先级

2. **用户权限**
   - 用户可以增删改自己的代办
   - 管理员可以选择用户推送代办消息

3. **到期提醒**
   - 代办到期前1小时自动发送提醒消息
   - 调用 `ISendUtil.send(msg)` 接口

4. **数据存储**
   - 使用本地缓存（ConcurrentHashMap）存储数据

5. **预设数据**
   - 1个管理员：admin / admin123
   - 3个普通用户：zhangsan、lisi、wangwu（密码均为123456）

6. **端口配置**
   - 随机端口：11000-12000之间

## 技术栈

- Spring Boot 2.7.18
- JDK 1.8
- Maven
- Lombok

## 项目结构

```
├── src/main/java/com/example/todo/
│   ├── TodoPluginApplication.java    # 启动类
│   ├── config/
│   │   └── RandomPortConfig.java     # 随机端口配置
│   ├── controller/
│   │   ├── TodoController.java       # 代办接口
│   │   └── UserController.java       # 用户接口
│   ├── entity/
│   │   ├── Todo.java                 # 代办实体
│   │   └── User.java                 # 用户实体
│   ├── repository/
│   │   ├── TodoRepository.java       # 代办数据访问
│   │   └── UserRepository.java       # 用户数据访问
│   ├── service/
│   │   ├── TodoService.java          # 代办业务逻辑
│   │   └── UserService.java          # 用户业务逻辑
│   ├── scheduler/
│   │   └── TodoReminderScheduler.java # 定时提醒任务
│   └── util/
│       ├── ISendUtil.java            # 消息发送接口
│       └── SendUtilImpl.java         # 消息发送实现
└── src/test/java/com/example/todo/
    └── TodoPluginApplicationTests.java # 测试用例
```

## API 接口

### 用户接口

| 接口 | 方法 | 说明 |
|------|------|------|
| /api/user/login | POST | 用户登录 |
| /api/user/get/{userId} | GET | 获取用户信息 |
| /api/user/all?adminId=xxx | GET | 获取所有用户（管理员） |
| /api/user/list?userId=xxx | GET | 获取普通用户列表 |

### 代办接口

| 接口 | 方法 | 说明 |
|------|------|------|
| /api/todo/create | POST | 创建代办 |
| /api/todo/update | POST | 更新代办 |
| /api/todo/delete | POST | 删除代办 |
| /api/todo/get/{todoId} | GET | 获取代办详情 |
| /api/todo/my/{userId} | GET | 获取我的代办 |
| /api/todo/executor/{executorId} | GET | 获取执行人的代办 |
| /api/todo/all?userId=xxx | GET | 获取所有代办（管理员） |
| /api/todo/updatePriority | POST | 修改优先级 |
| /api/todo/push | POST | 管理员推送代办 |

## 运行项目

```bash
# 编译
mvn clean compile

# 运行
mvn spring-boot:run

# 测试
mvn test
```

## 使用示例

### 1. 用户登录
```bash
curl -X POST "http://localhost:xxxxx/api/user/login" \
  -d "username=admin" \
  -d "password=admin123"
```

### 2. 创建代办
```bash
curl -X POST "http://localhost:xxxxx/api/todo/create" \
  -d "creatorId=user001" \
  -d "executorId=user002" \
  -d "title=完成报告" \
  -d "content=需要完成本周工作报告" \
  -d "priority=HIGH" \
  -d "dueTime=2024-12-31 18:00:00"
```

### 3. 管理员推送代办
```bash
curl -X POST "http://localhost:xxxxx/api/todo/push" \
  -d "adminId=admin001" \
  -d "executorId=user001" \
  -d "title=紧急任务" \
  -d "content=请立即处理" \
  -d "priority=URGENT" \
  -d "dueTime=2024-12-31 12:00:00"
```

### 4. 修改优先级
```bash
curl -X POST "http://localhost:xxxxx/api/todo/updatePriority" \
  -d "todoId=xxx" \
  -d "userId=user001" \
  -d "priority=MEDIUM"
```

### 5. 获取我的代办
```bash
curl "http://localhost:xxxxx/api/todo/my/user001"
```

## 优先级说明

- `LOW` - 低优先级
- `MEDIUM` - 中优先级
- `HIGH` - 高优先级
- `URGENT` - 紧急

## 状态说明

- `PENDING` - 待处理
- `IN_PROGRESS` - 进行中
- `COMPLETED` - 已完成
- `CANCELLED` - 已取消
