# CoffeeLab 交接说明

## 已完成

- 创建项目级 README、接口文档、数据库脚本和可运行前后端骨架。
- 前端完成 Vue3 + Vite 项目结构，包含路由、Pinia、Axios、页面组件和 mock 数据。
- 前端完成 ins 风视觉重构：首页、DIY 实验台和经典咖啡页已从普通卡片风升级为咖啡灵感杂志风。
- DIY 页面已实现左侧原料实验台和右侧杯子实时预览，包含冷热杯、液体、糖浆、冰块、奶泡、顶料等视觉状态。
- 后端完成 Spring Boot API 骨架，覆盖 `docs/api.md` 中的主要接口。
- 后端完成统一响应、分页结构、mock token、CORS、OpenAPI 配置、MyBatis mapper、JSON 类型处理器和内存兜底业务实现。
- 后端默认优先连接 MySQL，数据库不可用时通过 `PersistenceGuard` 短时间熔断并回落到 `InMemoryStore`。
- 前端经典咖啡、我的配方、分享广场、Top20、登录和 DIY 保存已优先请求真实 API，接口不可用时回落到 mock 数据。
- 数据库完成 MySQL 8 schema、seed 和数据库说明。

## 验证记录

已通过：

```powershell
cd E:\OnlyTest\CoffeeLab\frontend
npm.cmd run build
```

已通过：

```powershell
cd E:\OnlyTest\CoffeeLab\backend
mvn -DskipTests package
```

浏览器已查看：

- `http://127.0.0.1:5173/`
- `http://127.0.0.1:5173/diy`
- `http://127.0.0.1:5173/classic`

接口冒烟已通过：

- `GET /api/classic-coffees`
- `POST /api/auth/login`
- `POST /api/recipes`
- `GET /api/public-recipes`
- `GET /api/rankings/top-recipes`

验证时间：2026-05-09 13:09:43 Asia/Shanghai。

## 未完成风险

- 前端公开配方的评分、尝试、收藏、复刻按钮交互尚未完全接入页面。
- 后端在 MySQL 不可用时会使用内存兜底数据，兜底数据重启后丢失。
- 后端密码处理是演示级 mock，不适合生产。
- 真实 MySQL 端到端联调仍需在本机初始化库后验证 JSON 字段映射和统计 SQL。
- 尚未编写自动化测试。

## 推荐下一步

1. 初始化 MySQL 并执行 `database/schema.sql`、`database/seed.sql`，做真实数据库端到端联调。
2. 前端补齐公开配方评分、尝试、收藏、复刻按钮和反馈提示。
3. 登录态联调：补充 401 跳转、注册入口和更友好的错误提示。
4. 认证升级：将 `{mock}` 密码校验升级为 BCrypt/JWT。
5. 测试补齐：后端 controller/service 测试，前端 store 和关键页面交互测试。
