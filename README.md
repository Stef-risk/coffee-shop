# ☕ 咖啡餐吧管理系统

> 一套完整的、可商用的餐饮门店管理后台，采用前后端分离架构。
> 后端基于 **Java 17 + Spring Boot 3.2**，前端基于 **Vue 3 + Element Plus**。

---

## 目录

- [技术栈](#技术栈)
- [功能模块](#功能模块)
- [系统架构](#系统架构)
- [快速启动](#快速启动)
  - [环境要求](#环境要求)
  - [数据库初始化](#数据库初始化)
  - [启动后端](#启动后端)
  - [启动前端](#启动前端)
- [API 接口文档](#api-接口文档)
- [默认账号](#默认账号)
- [项目结构](#项目结构)
- [配置说明](#配置说明)
- [部署指南](#部署指南)

---

## 技术栈

### 后端

| 组件 | 版本 | 说明 |
|------|------|------|
| Java | 17 | LTS 版本 |
| Spring Boot | 3.2.3 | 核心框架 |
| Spring Security | 6.x | 认证鉴权 |
| Spring Data JPA | 3.x | ORM 框架 |
| MySQL | 8.0+ | 主数据库 |
| JJWT | 0.12.3 | JWT 令牌 |
| Lombok | Latest | 消除样板代码 |
| Maven | 3.8+ | 构建工具 |

### 前端

| 组件 | 版本 | 说明 |
|------|------|------|
| Vue | 3.4 | 核心框架（Composition API） |
| Vite | 5.x | 构建工具 |
| Element Plus | 2.6 | UI 组件库 |
| Pinia | 2.x | 状态管理 |
| Vue Router | 4.x | 路由管理 |
| Axios | 1.6 | HTTP 客户端 |
| ECharts | 5.x | 数据可视化 |
| Day.js | 1.11 | 日期处理 |

---

## 功能模块

### 1. 认证与权限
- JWT 无状态认证，Bearer Token 机制
- 四级角色权限体系：

| 角色 | 权限范围 |
|------|----------|
| **ADMIN**（超级管理员） | 全部功能 |
| **MANAGER**（店长） | 除员工创建外的全部功能 |
| **CASHIER**（收银员） | 下单、收款、桌台、客户查询 |
| **KITCHEN**（厨房/吧台） | 厨房看板、菜单查看 |

### 2. 订单管理（核心模块）
- **完整生命周期**：`待确认 → 已确认 → 制作中 → 待取餐/上桌 → 已完成`
- **三种订单类型**：堂食（绑定桌台）、外带、外卖（需填写地址）
- **支付方式**：现金（自动计算找零）、微信、支付宝、银行卡、会员余额
- 折扣金额支持
- 订单取消及桌台状态联动

### 3. 厨房看板
- 实时展示进行中订单（待确认/已确认/制作中/待取餐）
- 按状态颜色区分（黄→蓝→紫→绿）
- 一键流转订单状态
- 支持 30 秒自动刷新

### 4. 菜单管理
- 分类管理（支持排序权重）
- 菜品管理（价格、图片、描述）
- 一键上架/下架
- 分类 + 关键词 + 状态组合筛选

### 5. 桌台管理
- 可视化桌台网格（颜色区分状态）
- 四种状态：空闲 / 使用中 / 已预订 / 清洁中
- 下单时自动占用，结账后自动设为清洁
- 支持按区域、容量管理

### 6. 会员管理
- 会员档案（姓名/手机/邮箱）
- 四级会员等级：普通 / 银卡 / 金卡 / 铂金
- 余额充值，支持会员余额支付
- 自动统计消费次数和累计消费金额

### 7. 预订管理
- 桌位预订，绑定会员
- 状态流转：待确认 → 已确认 → 已到店 / 未到店 / 已取消
- 今日预订快捷视图
- 日历时间选择

### 8. 销售报表
- **日报**：当日营收、订单量、客单价、Top10热销商品
- **月报**：月度汇总 + 逐日营收折线图
- **自定义时段**：任意日期范围报表
- ECharts 可视化图表

### 9. 员工管理
- 员工 CRUD，软删除（离职状态）
- BCrypt 密码加密
- 角色分配与调整

---

## 系统架构

```
┌──────────────────────────────────────────────────────────────┐
│                    浏览器 (Vue 3)                              │
│  ┌──────────┐  ┌──────────┐  ┌──────────┐  ┌──────────────┐  │
│  │ 订单管理  │  │ 厨房看板 │  │ 菜单管理 │  │  报表/其他   │  │
│  └──────────┘  └──────────┘  └──────────┘  └──────────────┘  │
│               Pinia Store │ Vue Router                        │
│               Axios (JWT Bearer Token)                        │
└──────────────────────┬───────────────────────────────────────┘
                       │ HTTP/JSON  (port 3000 → proxy → 8080)
┌──────────────────────▼───────────────────────────────────────┐
│               Spring Boot 3.2 (port 8080, context: /api)     │
│  ┌─────────────┐  ┌─────────────┐  ┌──────────────────────┐  │
│  │ Controllers │→ │  Services   │→ │  Repositories (JPA)  │  │
│  └─────────────┘  └─────────────┘  └──────────────────────┘  │
│  Spring Security 6 + JWT Filter                              │
└──────────────────────┬───────────────────────────────────────┘
                       │ JDBC/Hibernate
┌──────────────────────▼───────────────────────────────────────┐
│                    MySQL 8.0                                  │
│  users · menu_categories · menu_items · dining_tables        │
│  orders · order_items · payments · customers · reservations  │
└──────────────────────────────────────────────────────────────┘
```

---

## 快速启动

### 环境要求

| 软件 | 最低版本 |
|------|----------|
| JDK | 17 |
| Maven | 3.8 |
| MySQL | 8.0 |
| Node.js | 18 |
| npm | 9 |

### 数据库初始化

```sql
-- 1. 创建数据库
CREATE DATABASE coffeeshop CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 2. 执行初始化脚本（包含种子数据）
mysql -u root -p coffeeshop < src/main/resources/db/init.sql
```

> Spring Boot 启动时会通过 `ddl-auto: update` 自动建表。
> `init.sql` 仅用于插入初始数据（员工、分类、菜品、桌台）。

### 启动后端

```bash
# 克隆项目（已在 coffee-shop 目录）
cd /path/to/coffee-shop

# 方式一：使用环境变量（推荐生产环境）
export DB_HOST=localhost
export DB_PORT=3306
export DB_NAME=coffeeshop
export DB_USER=root
export DB_PASSWORD=your_password
export JWT_SECRET=your-super-secret-key-min-32-chars

mvn spring-boot:run

# 方式二：直接修改 application.yml
# 编辑 src/main/resources/application.yml 中的数据库配置后运行：
mvn spring-boot:run

# 打包为 JAR 部署
mvn clean package -DskipTests
java -jar target/coffee-shop-2.0.0.jar \
  --spring.datasource.url=jdbc:mysql://localhost:3306/coffeeshop \
  --spring.datasource.username=root \
  --spring.datasource.password=your_password
```

后端启动后访问：`http://localhost:8080/api/actuator/health`

### 启动前端

```bash
cd frontend

# 安装依赖
npm install

# 开发模式启动（内置代理到后端 8080）
npm run dev
# 访问 http://localhost:3000

# 生产构建
npm run build
# 构建产物在 frontend/dist/
```

> **代理配置**：开发模式下 `/api` 请求自动代理到 `http://localhost:8080`，
> 无需手动修改接口地址。

---

## API 接口文档

所有接口响应格式统一为：

```json
{
  "success": true,
  "message": "success",
  "data": { ... },
  "timestamp": "2024-01-01T12:00:00"
}
```

### 认证

| 方法 | 路径 | 说明 | 权限 |
|------|------|------|------|
| POST | `/api/auth/login` | 登录获取 Token | 公开 |

**登录请求示例：**
```json
{
  "username": "admin",
  "password": "Admin@123456"
}
```

**登录响应示例：**
```json
{
  "success": true,
  "data": {
    "accessToken": "eyJhbGc...",
    "tokenType": "Bearer",
    "expiresIn": 86400000,
    "userId": 1,
    "username": "admin",
    "fullName": "系统管理员",
    "role": "ADMIN"
  }
}
```

后续请求在 Header 中携带：`Authorization: Bearer <accessToken>`

---

### 订单管理

| 方法 | 路径 | 说明 | 权限 |
|------|------|------|------|
| GET | `/api/orders` | 查询订单列表（分页+筛选） | 已登录 |
| GET | `/api/orders/active` | 获取进行中订单 | 已登录 |
| GET | `/api/orders/{id}` | 获取订单详情 | 已登录 |
| POST | `/api/orders` | 新建订单 | 已登录 |
| PATCH | `/api/orders/{id}/status` | 更新订单状态 | 已登录 |
| POST | `/api/orders/{id}/payment` | 订单收款 | 已登录 |
| POST | `/api/orders/{id}/cancel` | 取消订单 | 已登录 |

**新建订单请求示例：**
```json
{
  "orderType": "DINE_IN",
  "tableId": 1,
  "customerId": null,
  "discountAmount": 5.00,
  "notes": "不要辣",
  "items": [
    { "menuItemId": 2, "quantity": 2, "notes": "少冰" },
    { "menuItemId": 5, "quantity": 1 }
  ]
}
```

**收款请求示例：**
```json
{
  "paymentMethod": "WECHAT",
  "paidAmount": 56.00,
  "transactionId": "wx_xxx"
}
```

---

### 菜单管理

| 方法 | 路径 | 说明 | 权限 |
|------|------|------|------|
| GET | `/api/menu/categories` | 获取所有分类 | 已登录 |
| POST | `/api/menu/categories` | 新增分类 | ADMIN/MANAGER |
| PUT | `/api/menu/categories/{id}` | 更新分类 | ADMIN/MANAGER |
| DELETE | `/api/menu/categories/{id}` | 删除分类 | ADMIN/MANAGER |
| GET | `/api/menu/items` | 搜索菜品（分页） | 已登录 |
| GET | `/api/menu/items/category/{id}` | 按分类获取菜品 | 已登录 |
| POST | `/api/menu/items` | 新增菜品 | ADMIN/MANAGER |
| PUT | `/api/menu/items/{id}` | 更新菜品 | ADMIN/MANAGER |
| PATCH | `/api/menu/items/{id}/availability` | 上架/下架 | ADMIN/MANAGER |
| DELETE | `/api/menu/items/{id}` | 删除菜品 | ADMIN/MANAGER |

---

### 桌台管理

| 方法 | 路径 | 说明 | 权限 |
|------|------|------|------|
| GET | `/api/tables` | 获取桌台列表（可按状态筛选） | 已登录 |
| POST | `/api/tables` | 新增桌台 | 已登录 |
| PUT | `/api/tables/{id}` | 更新桌台 | 已登录 |
| PATCH | `/api/tables/{id}/status` | 更新桌台状态 | 已登录 |
| DELETE | `/api/tables/{id}` | 删除桌台 | 已登录 |

---

### 会员管理

| 方法 | 路径 | 说明 | 权限 |
|------|------|------|------|
| GET | `/api/customers` | 查询会员（分页+关键词） | 已登录 |
| GET | `/api/customers/{id}` | 获取会员详情 | 已登录 |
| GET | `/api/customers/phone/{phone}` | 手机号查询会员 | 已登录 |
| POST | `/api/customers` | 新增会员 | 已登录 |
| PUT | `/api/customers/{id}` | 更新会员（含充值） | 已登录 |

---

### 预订管理

| 方法 | 路径 | 说明 | 权限 |
|------|------|------|------|
| GET | `/api/reservations` | 预订列表 | 已登录 |
| GET | `/api/reservations/today` | 今日预订 | 已登录 |
| POST | `/api/reservations` | 新建预订 | 已登录 |
| PATCH | `/api/reservations/{id}/status` | 更新预订状态 | 已登录 |
| DELETE | `/api/reservations/{id}` | 取消预订 | 已登录 |

---

### 报表

| 方法 | 路径 | 参数 | 权限 |
|------|------|------|------|
| GET | `/api/reports/daily` | `?date=2024-01-15` | ADMIN/MANAGER |
| GET | `/api/reports/monthly` | `?year=2024&month=1` | ADMIN/MANAGER |
| GET | `/api/reports/custom` | `?startDate=...&endDate=...` | ADMIN/MANAGER |

---

### 员工管理

| 方法 | 路径 | 说明 | 权限 |
|------|------|------|------|
| GET | `/api/staff` | 员工列表 | ADMIN/MANAGER |
| POST | `/api/staff` | 新增员工 | ADMIN |
| PUT | `/api/staff/{id}` | 更新员工 | ADMIN |
| DELETE | `/api/staff/{id}` | 停用员工（软删除） | ADMIN |

---

## 默认账号

> 初始密码均为 `Admin@123456`，首次登录后请及时修改。

| 用户名 | 角色 | 说明 |
|--------|------|------|
| `admin` | ADMIN | 超级管理员，拥有全部权限 |
| `manager` | MANAGER | 店长，拥有除员工创建外的全部权限 |
| `cashier01` | CASHIER | 收银员 |
| `kitchen01` | KITCHEN | 厨房/吧台员工 |

---

## 项目结构

```
coffee-shop/
├── src/
│   ├── main/
│   │   ├── java/com/stefan/coffeeshop/
│   │   │   ├── CoffeeShopApplication.java   # 启动入口
│   │   │   ├── common/                       # 通用响应、分页、枚举
│   │   │   │   └── enums/                   # OrderStatus, OrderType, ...
│   │   │   ├── config/
│   │   │   │   └── SecurityConfig.java      # Spring Security 配置
│   │   │   ├── controller/                   # REST 控制器
│   │   │   │   ├── AuthController.java
│   │   │   │   ├── OrderController.java
│   │   │   │   ├── MenuItemController.java
│   │   │   │   └── ...
│   │   │   ├── service/                      # 业务逻辑层
│   │   │   ├── repository/                   # JPA 数据访问层
│   │   │   ├── entity/                       # JPA 实体
│   │   │   │   ├── Order.java               # 订单（核心）
│   │   │   │   ├── OrderItem.java
│   │   │   │   ├── Payment.java
│   │   │   │   └── ...
│   │   │   ├── dto/
│   │   │   │   ├── request/                 # 请求 DTO（含校验）
│   │   │   │   └── response/                # 响应 DTO
│   │   │   ├── security/                    # JWT 过滤器、Provider
│   │   │   └── exception/                   # 全局异常处理
│   │   └── resources/
│   │       ├── application.yml              # 应用配置
│   │       └── db/init.sql                  # 数据库初始化脚本
│   └── test/
│       ├── java/com/stefan/coffeeshop/
│       └── resources/application-test.yml   # 测试用 H2 配置
│
├── frontend/                                 # Vue 3 前端
│   ├── src/
│   │   ├── api/                             # 接口请求层
│   │   ├── assets/main.css                  # 全局样式
│   │   ├── components/layout/               # 应用布局（侧边栏+顶栏）
│   │   ├── router/index.js                  # 路由（含导航守卫）
│   │   ├── stores/auth.js                   # Pinia 认证状态
│   │   ├── utils/request.js                 # Axios 封装（拦截器）
│   │   └── views/                           # 页面组件
│   │       ├── auth/Login.vue
│   │       ├── dashboard/Dashboard.vue
│   │       ├── order/
│   │       │   ├── OrderList.vue            # 订单列表+收款
│   │       │   ├── OrderCreate.vue          # 下单页（购物车）
│   │       │   └── KitchenDisplay.vue       # 厨房看板
│   │       ├── menu/                        # 菜单分类+菜品
│   │       ├── table/TableManage.vue        # 桌台网格视图
│   │       ├── customer/CustomerList.vue    # 会员+充值
│   │       ├── reservation/                 # 预订管理
│   │       ├── staff/StaffList.vue          # 员工管理
│   │       └── report/SalesReport.vue       # 销售报表+图表
│   ├── package.json
│   ├── vite.config.js
│   └── index.html
│
├── pom.xml                                  # Maven 构建配置
└── README.md
```

---

## 配置说明

### 后端配置（`src/main/resources/application.yml`）

| 配置项 | 默认值 | 说明 |
|--------|--------|------|
| `server.port` | `8080` | 服务端口 |
| `DB_HOST` | `localhost` | 数据库主机（环境变量） |
| `DB_PORT` | `3306` | 数据库端口（环境变量） |
| `DB_NAME` | `coffeeshop` | 数据库名（环境变量） |
| `DB_USER` | `root` | 数据库用户（环境变量） |
| `DB_PASSWORD` | _(空)_ | 数据库密码（**必须修改**） |
| `JWT_SECRET` | 内置值 | JWT 密钥（**生产必须修改**） |
| `JWT_EXPIRATION` | `86400000` | Token 有效期（ms，默认24小时） |

### 前端配置（`frontend/.env`）

```env
VITE_API_BASE_URL=http://localhost:8080/api
```

生产环境修改 `frontend/.env.production`：
```env
VITE_API_BASE_URL=/api
```

---

## 部署指南

### Docker 部署（推荐）

```yaml
# docker-compose.yml
version: '3.8'
services:
  mysql:
    image: mysql:8.0
    environment:
      MYSQL_DATABASE: coffeeshop
      MYSQL_ROOT_PASSWORD: yourpassword
    volumes:
      - mysql_data:/var/lib/mysql
      - ./src/main/resources/db/init.sql:/docker-entrypoint-initdb.d/init.sql
    ports:
      - "3306:3306"

  backend:
    image: openjdk:17-slim
    working_dir: /app
    command: java -jar coffee-shop-2.0.0.jar
    environment:
      DB_HOST: mysql
      DB_PASSWORD: yourpassword
      JWT_SECRET: your-production-secret-key
    volumes:
      - ./target/coffee-shop-2.0.0.jar:/app/coffee-shop-2.0.0.jar
    ports:
      - "8080:8080"
    depends_on:
      - mysql

  frontend:
    image: nginx:alpine
    volumes:
      - ./frontend/dist:/usr/share/nginx/html
      - ./nginx.conf:/etc/nginx/conf.d/default.conf
    ports:
      - "80:80"
    depends_on:
      - backend

volumes:
  mysql_data:
```

### Nginx 配置（`nginx.conf`）

```nginx
server {
    listen 80;
    server_name your-domain.com;
    root /usr/share/nginx/html;
    index index.html;

    # Vue 路由历史模式支持
    location / {
        try_files $uri $uri/ /index.html;
    }

    # 后端 API 反向代理
    location /api/ {
        proxy_pass http://backend:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
    }
}
```

### 手动构建部署

```bash
# 1. 构建后端
mvn clean package -DskipTests
# 产物：target/coffee-shop-2.0.0.jar

# 2. 构建前端
cd frontend && npm install && npm run build
# 产物：frontend/dist/

# 3. 将 dist/ 部署到 Nginx
# 4. 运行后端 JAR
java -jar target/coffee-shop-2.0.0.jar \
  --spring.datasource.password=prod_password \
  --app.jwt.secret=prod_secret
```

---

## 数据模型

```
users           → 员工账号（角色：ADMIN/MANAGER/CASHIER/KITCHEN）
menu_categories → 菜单分类
menu_items      → 菜品（关联分类）
dining_tables   → 桌台（状态自动流转）
customers       → 会员（含余额、消费统计）
orders          → 订单（关联桌台/会员/员工）
order_items     → 订单明细（价格快照）
payments        → 支付记录（关联订单）
reservations    → 预订（关联桌台/会员）
```

---

## 开发指南

### 添加新的 API 接口

1. 在 `entity/` 中定义 JPA 实体
2. 在 `repository/` 中定义 JpaRepository
3. 在 `dto/request/` 和 `dto/response/` 中定义 DTO
4. 在 `service/` 中实现业务逻辑
5. 在 `controller/` 中定义 REST 接口
6. 在 `config/SecurityConfig.java` 中配置路径权限

### 前端添加新页面

1. 在 `frontend/src/views/` 中创建 Vue 组件
2. 在 `frontend/src/api/` 中添加接口调用
3. 在 `frontend/src/router/index.js` 中注册路由
4. 在 `AppLayout.vue` 的菜单中添加导航项

---

## 常见问题

**Q: 启动报错 `Access denied for user`**
> 检查数据库用户名密码配置，或在 `application.yml` 中直接填写。

**Q: JWT 密钥报错 `Key length must be >= 256 bits`**
> 确保 `JWT_SECRET` 环境变量长度 ≥ 32 个字符。

**Q: 前端接口 401 错误**
> Token 已过期，重新登录即可。默认 Token 有效期 24 小时。

**Q: 前端开发模式跨域**
> `vite.config.js` 已配置代理，确保后端运行在 8080 端口，无需其他配置。

**Q: 如何重置管理员密码**
> 使用 BCrypt 生成新密码哈希，直接更新数据库 `users` 表的 `password` 字段。

---

## License

MIT License — 可自由用于商业项目。

---

*Built with ❤️ on Java 17 + Spring Boot 3.2 + Vue 3*
