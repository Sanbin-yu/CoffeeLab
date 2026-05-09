# CoffeeLab 精品咖啡 DIY 网站

CoffeeLab 是一个面向客户的精品咖啡 DIY 配方网站。第一版已经搭好可运行骨架：前端使用 Vue3 + Vite，后端使用 Spring Boot + MyBatis 结构，数据库提供 MySQL 8 建表和种子数据脚本。

当前产品重点是“客户自己 DIY 咖啡饮品”：左侧选择原料，右侧杯子实时展示分层、冰块、糖浆、奶泡和顶料动态效果；同时保留经典咖啡、我的配方、分享广场和 Top20 灵感榜。

## 当前状态

- 前端：已实现页面骨架和 ins 风视觉重构，包含首页、DIY 实验台、经典咖啡、我的配方、分享广场、Top20、登录页。
- 后端：已实现 Spring Boot + MyBatis API，覆盖用户、经典咖啡、DIY 配方、公开分享、评分、尝试、收藏、复刻和排行；MySQL 不可用时会短时间熔断并回落到内存演示数据。
- 数据库：已提供 MySQL 8 `schema.sql` 和 `seed.sql`，后端可通过 `COFFEELAB_DB_*` 环境变量连接真实 MySQL。
- 接口文档：详见 [docs/api.md](E:/OnlyTest/CoffeeLab/docs/api.md)。

## 技术栈

- 前端：Vue3、Vite、Vue Router、Pinia、Axios、CSS/SVG 动画。
- 后端：Java 17、Spring Boot 3.3.5、MyBatis 结构、Spring Validation、springdoc-openapi。
- 数据库：MySQL 8，JSON 字段用于配方灵活数据。

## 项目结构

```text
CoffeeLab/
  frontend/        Vue3 + Vite 前端
  backend/         Spring Boot 后端
  database/        MySQL schema、seed 和数据库说明
  docs/            API、架构、运行和交接文档
  AGENTS.md        后续 AI 协作规则
  README.md        快速接入说明
```

## 快速运行

前端：

```powershell
cd E:\OnlyTest\CoffeeLab\frontend
npm.cmd install
npm.cmd run dev
```

后端：

```powershell
cd E:\OnlyTest\CoffeeLab\backend
mvn spring-boot:run
```

数据库初始化：

```powershell
mysql -u <user> -p <database_name> < database/schema.sql
mysql -u <user> -p <database_name> < database/seed.sql
```

## 验证命令

```powershell
cd E:\OnlyTest\CoffeeLab\frontend
npm.cmd run build
```

```powershell
cd E:\OnlyTest\CoffeeLab\backend
mvn -DskipTests package
```

## 重要文档

- [接口文档](E:/OnlyTest/CoffeeLab/docs/api.md)：前后端统一接口、字段、响应结构和错误码。
- [架构说明](E:/OnlyTest/CoffeeLab/docs/architecture.md)：模块结构、数据流、当前实现边界。
- [运行手册](E:/OnlyTest/CoffeeLab/docs/runbook.md)：启动、验证、常见问题。
- [交接说明](E:/OnlyTest/CoffeeLab/docs/handoff.md)：已完成内容、未完成风险和推荐下一步。
- [数据库说明](E:/OnlyTest/CoffeeLab/database/README.md)：表关系、索引、热度分计算和初始化顺序。

## 当前边界

- 第一版不做真实下单、支付、库存、配送和门店管理。
- 前端经典咖啡、我的配方、分享广场、Top20、登录和 DIY 保存已优先请求真实 API，接口不可用时回落到 mock 数据保证页面可演示。
- 后端默认会优先访问 MySQL；如果本机没有 MySQL、账号密码不对或库未初始化，会自动使用内存兜底数据。内存兜底数据重启后会丢失。
- 第一版密码仍是 `{mock}` 演示级校验，不适合生产环境。

## 推荐下一步

1. 初始化 MySQL 后做一次真实数据库端到端联调，重点验证 JSON 字段、评分统计和 Top20 排名。
2. 补齐前端公开配方评分、尝试、收藏、复刻按钮交互。
3. 将演示级 `{mock}` 密码替换为 BCrypt/JWT 等更接近真实项目的认证方案。
4. 为关键后端 service 和前端状态管理补测试。
