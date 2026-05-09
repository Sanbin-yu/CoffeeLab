# CoffeeLab 架构说明

## 产品模块

CoffeeLab 第一版围绕五个核心体验：

- DIY 实验台：客户选择杯型、咖啡基底、浓缩份数、奶类、甜度、冰量、糖浆、奶泡和顶料。
- 经典咖啡：提供拿铁、美式、卡布奇诺、摩卡、焦糖玛奇朵、冷萃、澳白等传统饮品模板。
- 我的配方：保存用户自己的 DIY 配方。
- 分享广场：公开展示用户分享的 DIY 配方。
- Top20 灵感榜：按综合热度展示前 20 名公开配方。

## 前端架构

前端位于 `frontend/`，使用 Vue3 + Vite。

主要目录：

```text
frontend/src/
  api/             Axios client 和接口封装
  components/      通用组件、咖啡杯预览、原料实验台、配方卡片
  data/            当前 mock 数据
  router/          Vue Router 页面路由
  stores/          Pinia 状态
  styles/          全局视觉和动画样式
  views/           页面组件
```

关键实现：

- `DiyLabView.vue` 负责 DIY 页面组合。
- `IngredientWorkbench.vue` 负责左侧原料分类选择。
- `CoffeeCupPreview.vue` 负责右侧杯子视觉预览。
- `stores/diyRecipe.js` 负责当前配方状态、风味标签和味觉雷达计算。
- `styles/main.css` 定义当前 ins 风视觉系统、动态效果、响应式布局。

当前前端视觉方向：

- 奶油色背景、胶片颗粒、柔和渐变和咖啡色主标题。
- 首页使用杂志式大标题、贴纸标签和拍立得杯子舞台。
- DIY 右侧杯子预览使用 story 标签、拍立得卡片、倒液、冰块、奶泡、糖浆和漂浮光影动画。
- 经典咖啡页使用 coffee moodboard 风格，不再是普通卡片墙。

## 后端架构

后端位于 `backend/`，使用 Spring Boot 3.3.5 + Java 17。

主要目录：

```text
backend/src/main/java/com/coffeelab/backend/
  common/          统一响应和分页结构
  config/          CORS、mock token、OpenAPI
  controller/      REST API 控制器
  dto/             请求对象
  exception/       业务异常和全局异常处理
  mapper/          MyBatis mapper
  mybatis/         JSON 类型处理器
  model/           领域模型
  service/         业务实现、MySQL 优先读写、内存兜底和熔断保护
  vo/              响应对象
```

当前后端状态：

- API 路径与 `docs/api.md` 对齐。
- 统一响应结构为 `code`、`message`、`data`。
- 通过 `Authorization: Bearer <token>` 解析 mock 登录态。
- MyBatis mapper 已接入用户、经典咖啡、配方、公开配方、评分、收藏、尝试、复刻和排行查询。
- `mybatis/` 中提供 JSON 类型处理器，用于 `List<String>`、`FlavorRadar` 和经典咖啡默认配方模板。
- `InMemoryStore` 仍提供演示数据和内存读写兜底。
- `PersistenceGuard` 在数据库连接或 SQL 失败后短时间切换到内存兜底，避免本地未启动 MySQL 时接口长时间阻塞。
- MySQL 连接配置在 `application.yml` 中，支持 `COFFEELAB_DB_HOST`、`COFFEELAB_DB_PORT`、`COFFEELAB_DB_NAME`、`COFFEELAB_DB_USER`、`COFFEELAB_DB_PASSWORD`。

## 数据库架构

数据库脚本位于 `database/`，目标 MySQL 8。

核心表：

- `users`：用户。
- `classic_coffees`：经典咖啡和默认配方模板。
- `recipes`：用户 DIY 配方。
- `recipe_ingredients`：配方原料明细，预留给后续分析和原料管理。
- `public_recipes`：公开分享配方和热度缓存。
- `recipe_ratings`：评分。
- `recipe_favorites`：收藏。
- `recipe_try_records`：尝试记录。
- `recipe_fork_records`：复刻记录。

Top20 热度分公式：

```text
hotScore = averageRating * 40 + ratingCount * 2 + triedCount * 1.5 + favoriteCount * 2 + forkCount * 2
```

## API 数据流

典型 DIY 保存流程：

```text
用户在 DIY 页面选择原料
  -> Pinia 更新 recipe 状态
  -> CoffeeCupPreview 实时更新杯子动画
  -> 用户填写配方名称和备注
  -> 前端调用 POST /api/recipes
  -> 后端保存配方
  -> 我的配方页通过 GET /api/recipes/my 展示
```

典型分享评分流程：

```text
用户发布配方
  -> POST /api/recipes/{id}/publish
  -> 分享广场 GET /api/public-recipes 展示
  -> 其他用户查看、尝试、收藏、评分、复刻
  -> 后端更新 averageRating、ratingCount、triedCount、favoriteCount、forkCount、hotScore
  -> Top20 GET /api/rankings/top-recipes 展示排行
```
