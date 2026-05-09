# CoffeeLab 前后端统一接口开发文档

本文档用于 CoffeeLab 前端、后端、数据库三方统一联调。第一版接口围绕用户登录、经典咖啡、DIY 配方、分享广场、评分、尝试、收藏、复刻和 Top20 排行展开。

## 1. 全局约定

### 1.1 基础信息

- API 前缀：`/api`
- 数据格式：`application/json`
- 字段命名：请求和响应统一使用 `camelCase`
- 时间格式：后端当前直接序列化 `LocalDateTime`，示例为 `2026-05-09T10:00:00`；如果后续需要统一展示格式，再在全局 Jackson 配置中收敛。
- 登录方式：`Authorization: Bearer <token>`
- 第一版鉴权：演示级 mock token，前端登录后保存 token，请求需要登录的接口时放入请求头。
- 演示账号：`latte@example.com / 123456`

### 1.2 统一响应结构

所有接口统一返回：

```json
{
  "code": 200,
  "message": "success",
  "data": {}
}
```

分页接口统一返回：

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "records": [],
    "page": 1,
    "pageSize": 12,
    "total": 0,
    "totalPages": 0
  }
}
```

### 1.3 常用错误码

| code | message 示例 | 说明 |
| --- | --- | --- |
| 200 | success | 成功 |
| 400 | invalid request | 请求参数错误 |
| 401 | unauthorized | 未登录或 token 无效 |
| 403 | forbidden | 无权限操作 |
| 404 | not found | 资源不存在 |
| 409 | conflict | 数据冲突，例如手机号重复 |
| 500 | internal server error | 服务端异常 |

### 1.4 枚举值

#### cupType

| 值 | 含义 |
| --- | --- |
| `coldCup` | 冷杯 |
| `hotCup` | 热杯 |

#### temperatureType

| 值 | 含义 |
| --- | --- |
| `cold` | 冷饮 |
| `hot` | 热饮 |

#### coffeeBase

| 值 | 含义 |
| --- | --- |
| `espresso` | 浓缩咖啡 |
| `americano` | 美式基底 |
| `coldBrew` | 冷萃 |
| `decaf` | 低因咖啡 |

#### milkType

| 值 | 含义 |
| --- | --- |
| `none` | 不加奶 |
| `wholeMilk` | 全脂牛奶 |
| `lowFatMilk` | 低脂牛奶 |
| `oatMilk` | 燕麦奶 |
| `coconutMilk` | 椰奶 |
| `thickMilk` | 厚乳 |

#### sweetness

| 值 | 含义 |
| --- | --- |
| `noSugar` | 无糖 |
| `lowSugar` | 三分糖 |
| `halfSugar` | 半糖 |
| `lessSugar` | 七分糖 |
| `fullSugar` | 全糖 |

#### iceLevel

| 值 | 含义 |
| --- | --- |
| `noIce` | 去冰 |
| `lessIce` | 少冰 |
| `normalIce` | 正常冰 |
| `extraIce` | 多冰 |

#### foam

| 值 | 含义 |
| --- | --- |
| `none` | 不加奶泡 |
| `lightFoam` | 轻奶泡 |
| `thickFoam` | 厚奶泡 |
| `seaSaltCream` | 海盐奶盖 |
| `coconutCloud` | 椰子云朵 |

#### publicRecipeSort

| 值 | 含义 |
| --- | --- |
| `latest` | 最新发布 |
| `rating` | 评分最高 |
| `tried` | 尝试最多 |
| `favorite` | 收藏最多 |
| `hot` | 综合热度 |

### 1.5 通用对象

#### FlavorRadar

```json
{
  "bitterness": 4,
  "sweetness": 3,
  "acidity": 2,
  "milkiness": 5,
  "richness": 4,
  "freshness": 3
}
```

字段范围统一为 `0-5`。

#### RecipePayload

创建和编辑 DIY 配方时使用：

```json
{
  "name": "午夜榛果拿铁",
  "note": "双份浓缩，半糖，榛果香更明显",
  "cupType": "coldCup",
  "temperatureType": "cold",
  "coffeeBase": "espresso",
  "espressoShots": 2,
  "milkType": "oatMilk",
  "sweetness": "halfSugar",
  "iceLevel": "normalIce",
  "syrups": ["hazelnut", "caramel"],
  "foam": "lightFoam",
  "toppings": ["cocoaPowder"],
  "flavorTags": ["奶香浓郁", "榛果风味", "冰爽"],
  "flavorRadar": {
    "bitterness": 4,
    "sweetness": 3,
    "acidity": 1,
    "milkiness": 5,
    "richness": 4,
    "freshness": 3
  },
  "isPublic": false
}
```

#### RecipeVO

```json
{
  "id": 1001,
  "userId": 12,
  "name": "午夜榛果拿铁",
  "note": "双份浓缩，半糖，榛果香更明显",
  "cupType": "coldCup",
  "temperatureType": "cold",
  "coffeeBase": "espresso",
  "espressoShots": 2,
  "milkType": "oatMilk",
  "sweetness": "halfSugar",
  "iceLevel": "normalIce",
  "syrups": ["hazelnut", "caramel"],
  "foam": "lightFoam",
  "toppings": ["cocoaPowder"],
  "flavorTags": ["奶香浓郁", "榛果风味", "冰爽"],
  "flavorRadar": {
    "bitterness": 4,
    "sweetness": 3,
    "acidity": 1,
    "milkiness": 5,
    "richness": 4,
    "freshness": 3
  },
  "isPublic": false,
  "createdAt": "2026-05-09 10:00:00",
  "updatedAt": "2026-05-09 10:00:00"
}
```

## 2. 用户与鉴权接口

### 2.1 用户注册

- 方法：`POST`
- 路径：`/api/auth/register`
- 登录：不需要
- 说明：手机号和邮箱至少填写一个，昵称和密码必填。

请求体：

```json
{
  "nickname": "拿铁研究员",
  "phone": "13800000000",
  "email": "latte@example.com",
  "password": "123456"
}
```

响应：

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 12,
    "nickname": "拿铁研究员",
    "phone": "13800000000",
    "email": "latte@example.com",
    "avatarUrl": null,
    "createdAt": "2026-05-09 10:00:00"
  }
}
```

### 2.2 用户登录

- 方法：`POST`
- 路径：`/api/auth/login`
- 登录：不需要
- 说明：`account` 可以是手机号或邮箱。

请求体：

```json
{
  "account": "latte@example.com",
  "password": "123456"
}
```

响应：

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "token": "mock-1-xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx",
    "user": {
      "id": 12,
      "nickname": "拿铁研究员",
      "phone": "13800000000",
      "email": "latte@example.com",
      "avatarUrl": null
    }
  }
}
```

### 2.3 获取当前用户

- 方法：`GET`
- 路径：`/api/users/me`
- 登录：需要

响应：

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 12,
    "nickname": "拿铁研究员",
    "phone": "13800000000",
    "email": "latte@example.com",
    "avatarUrl": null,
    "createdAt": "2026-05-09 10:00:00"
  }
}
```

### 2.4 更新当前用户

- 方法：`PUT`
- 路径：`/api/users/me`
- 登录：需要

请求体：

```json
{
  "nickname": "冷萃星人",
  "avatarUrl": "https://example.com/avatar.png"
}
```

响应：

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 12,
    "nickname": "冷萃星人",
    "phone": "13800000000",
    "email": "latte@example.com",
    "avatarUrl": "https://example.com/avatar.png"
  }
}
```

## 3. 经典咖啡接口

### 3.1 获取经典咖啡列表

- 方法：`GET`
- 路径：`/api/classic-coffees`
- 登录：不需要

响应：

```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "id": 1,
      "name": "拿铁",
      "imageUrl": "/images/classic/latte.png",
      "description": "浓缩咖啡与牛奶融合，口感顺滑，奶香明显。",
      "caffeineLevel": 3,
      "suitableCrowd": "喜欢柔和奶香的用户",
      "tags": ["奶香", "顺滑", "经典"],
      "adjustable": true
    }
  ]
}
```

### 3.2 获取经典咖啡详情

- 方法：`GET`
- 路径：`/api/classic-coffees/{id}`
- 登录：不需要

响应：

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 1,
    "name": "拿铁",
    "imageUrl": "/images/classic/latte.png",
    "description": "浓缩咖啡与牛奶融合，口感顺滑，奶香明显。",
    "caffeineLevel": 3,
    "suitableCrowd": "喜欢柔和奶香的用户",
    "tags": ["奶香", "顺滑", "经典"],
    "defaultRecipe": {
      "cupType": "hotCup",
      "temperatureType": "hot",
      "coffeeBase": "espresso",
      "espressoShots": 2,
      "milkType": "wholeMilk",
      "sweetness": "noSugar",
      "iceLevel": "noIce",
      "syrups": [],
      "foam": "lightFoam",
      "toppings": []
    },
    "adjustable": true
  }
}
```

### 3.3 获取经典咖啡配方模板

- 方法：`GET`
- 路径：`/api/classic-coffees/{id}/recipe-template`
- 登录：不需要
- 说明：前端点击“一键使用”后，可用该模板预填 DIY 页面。

响应：

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "name": "经典拿铁",
    "note": "来自经典咖啡模板，可继续调整。",
    "cupType": "hotCup",
    "temperatureType": "hot",
    "coffeeBase": "espresso",
    "espressoShots": 2,
    "milkType": "wholeMilk",
    "sweetness": "noSugar",
    "iceLevel": "noIce",
    "syrups": [],
    "foam": "lightFoam",
    "toppings": [],
    "flavorTags": ["奶香", "顺滑", "经典"],
    "flavorRadar": {
      "bitterness": 3,
      "sweetness": 1,
      "acidity": 1,
      "milkiness": 5,
      "richness": 4,
      "freshness": 1
    }
  }
}
```

## 4. DIY 配方接口

### 4.1 创建我的配方

- 方法：`POST`
- 路径：`/api/recipes`
- 登录：需要

请求体：见 `RecipePayload`

响应：

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 1001
  }
}
```

### 4.2 获取我的配方列表

- 方法：`GET`
- 路径：`/api/recipes/my`
- 登录：需要

查询参数：

| 参数 | 类型 | 必填 | 说明 |
| --- | --- | --- | --- |
| keyword | string | 否 | 配方名称关键词 |
| tag | string | 否 | 风味标签 |
| page | number | 否 | 默认 1 |
| pageSize | number | 否 | 默认 12 |

响应：

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "records": [
      {
        "id": 1001,
        "name": "午夜榛果拿铁",
        "cupType": "coldCup",
        "temperatureType": "cold",
        "flavorTags": ["奶香浓郁", "榛果风味", "冰爽"],
        "isPublic": false,
        "createdAt": "2026-05-09 10:00:00",
        "updatedAt": "2026-05-09 10:00:00"
      }
    ],
    "page": 1,
    "pageSize": 12,
    "total": 1,
    "totalPages": 1
  }
}
```

### 4.3 获取配方详情

- 方法：`GET`
- 路径：`/api/recipes/{id}`
- 登录：需要
- 说明：只能查看自己的私有配方；公开配方详情使用 `/api/public-recipes/{id}`。

响应：见 `RecipeVO`

### 4.4 更新配方

- 方法：`PUT`
- 路径：`/api/recipes/{id}`
- 登录：需要

请求体：见 `RecipePayload`

响应：

```json
{
  "code": 200,
  "message": "success",
  "data": true
}
```

### 4.5 删除配方

- 方法：`DELETE`
- 路径：`/api/recipes/{id}`
- 登录：需要

响应：

```json
{
  "code": 200,
  "message": "success",
  "data": true
}
```

### 4.6 复制配方

- 方法：`POST`
- 路径：`/api/recipes/{id}/copy`
- 登录：需要
- 说明：复制自己已有配方，生成一条新的个人配方。

请求体：

```json
{
  "name": "午夜榛果拿铁 Copy"
}
```

响应：

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 1002
  }
}
```

## 5. 分享广场接口

### 5.1 发布配方

- 方法：`POST`
- 路径：`/api/recipes/{id}/publish`
- 登录：需要
- 说明：只能发布自己的配方。

响应：

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "publicRecipeId": 2001
  }
}
```

### 5.2 取消发布配方

- 方法：`POST`
- 路径：`/api/recipes/{id}/unpublish`
- 登录：需要

响应：

```json
{
  "code": 200,
  "message": "success",
  "data": true
}
```

### 5.3 获取公开配方列表

- 方法：`GET`
- 路径：`/api/public-recipes`
- 登录：不需要

查询参数：

| 参数 | 类型 | 必填 | 说明 |
| --- | --- | --- | --- |
| keyword | string | 否 | 配方名称关键词 |
| tag | string | 否 | 风味标签 |
| sort | string | 否 | `latest`、`rating`、`tried`、`favorite`、`hot` |
| page | number | 否 | 默认 1 |
| pageSize | number | 否 | 默认 12 |

响应：

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "records": [
      {
        "id": 2001,
        "recipeId": 1001,
        "name": "午夜榛果拿铁",
        "authorId": 12,
        "authorName": "拿铁研究员",
        "cupType": "coldCup",
        "temperatureType": "cold",
        "flavorTags": ["奶香浓郁", "榛果风味", "冰爽"],
        "averageRating": 4.8,
        "ratingCount": 24,
        "triedCount": 118,
        "favoriteCount": 36,
        "forkCount": 14,
        "hotScore": 334.0,
        "createdAt": "2026-05-09 10:00:00"
      }
    ],
    "page": 1,
    "pageSize": 12,
    "total": 1,
    "totalPages": 1
  }
}
```

### 5.4 获取公开配方详情

- 方法：`GET`
- 路径：`/api/public-recipes/{id}`
- 登录：不需要

响应：

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 2001,
    "recipeId": 1001,
    "name": "午夜榛果拿铁",
    "note": "双份浓缩，半糖，榛果香更明显",
    "authorId": 12,
    "authorName": "拿铁研究员",
    "cupType": "coldCup",
    "temperatureType": "cold",
    "coffeeBase": "espresso",
    "espressoShots": 2,
    "milkType": "oatMilk",
    "sweetness": "halfSugar",
    "iceLevel": "normalIce",
    "syrups": ["hazelnut", "caramel"],
    "foam": "lightFoam",
    "toppings": ["cocoaPowder"],
    "flavorTags": ["奶香浓郁", "榛果风味", "冰爽"],
    "flavorRadar": {
      "bitterness": 4,
      "sweetness": 3,
      "acidity": 1,
      "milkiness": 5,
      "richness": 4,
      "freshness": 3
    },
    "averageRating": 4.8,
    "ratingCount": 24,
    "triedCount": 118,
    "favoriteCount": 36,
    "forkCount": 14,
    "hotScore": 334.0,
    "createdAt": "2026-05-09 10:00:00"
  }
}
```

## 6. 评分接口

### 6.1 提交或修改评分

- 方法：`POST`
- 路径：`/api/public-recipes/{id}/ratings`
- 登录：需要
- 说明：同一用户对同一个公开配方只保留一条评分，再次提交视为修改。

请求体：

```json
{
  "score": 5,
  "comment": "榛果和燕麦奶很搭，半糖刚好。"
}
```

响应：

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "averageRating": 4.8,
    "ratingCount": 24
  }
}
```

### 6.2 获取评分概况

- 方法：`GET`
- 路径：`/api/public-recipes/{id}/ratings/summary`
- 登录：不需要

响应：

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "averageRating": 4.8,
    "ratingCount": 24,
    "scoreDistribution": {
      "1": 0,
      "2": 1,
      "3": 2,
      "4": 5,
      "5": 16
    }
  }
}
```

## 7. 尝试、收藏和复刻接口

### 7.1 记录尝试

- 方法：`POST`
- 路径：`/api/public-recipes/{id}/try`
- 登录：需要

响应：

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "triedCount": 119
  }
}
```

### 7.2 收藏公开配方

- 方法：`POST`
- 路径：`/api/public-recipes/{id}/favorite`
- 登录：需要

响应：

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "favoriteCount": 37
  }
}
```

### 7.3 取消收藏公开配方

- 方法：`DELETE`
- 路径：`/api/public-recipes/{id}/favorite`
- 登录：需要

响应：

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "favoriteCount": 36
  }
}
```

### 7.4 复刻公开配方到我的配方

- 方法：`POST`
- 路径：`/api/public-recipes/{id}/fork`
- 登录：需要

请求体：

```json
{
  "name": "我的榛果拿铁改良版"
}
```

响应：

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "triedCount": null,
    "favoriteCount": null,
    "forkCount": 15,
    "recipeId": 1003,
    "publicRecipeId": null
  }
}
```

## 8. Top20 排行接口

### 8.1 获取综合热度 Top20

- 方法：`GET`
- 路径：`/api/rankings/top-recipes`
- 登录：不需要

查询参数：

| 参数 | 类型 | 必填 | 说明 |
| --- | --- | --- | --- |
| range | string | 否 | `all`、`month`、`week`，默认 `all` |

综合热度默认公式：

```text
hotScore = averageRating * 40 + ratingCount * 2 + triedCount * 1.5 + favoriteCount * 2 + forkCount * 2
```

响应：

```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "rank": 1,
      "publicRecipeId": 2001,
      "recipeName": "午夜榛果拿铁",
      "authorName": "拿铁研究员",
      "cupType": "coldCup",
      "temperatureType": "cold",
      "flavorTags": ["奶香浓郁", "榛果风味", "冰爽"],
      "averageRating": 4.8,
      "ratingCount": 24,
      "triedCount": 118,
      "favoriteCount": 36,
      "forkCount": 14,
      "hotScore": 334.0
    }
  ]
}
```

## 9. 前端联调页面对应接口

| 页面 | 主要接口 |
| --- | --- |
| 首页 | `GET /api/classic-coffees`、`GET /api/rankings/top-recipes` |
| 登录注册页 | `POST /api/auth/register`、`POST /api/auth/login` |
| DIY 实验台 | `POST /api/recipes`、`PUT /api/recipes/{id}`、`GET /api/classic-coffees/{id}/recipe-template` |
| 经典咖啡页 | `GET /api/classic-coffees`、`GET /api/classic-coffees/{id}` |
| 我的配方页 | `GET /api/recipes/my`、`GET /api/recipes/{id}`、`DELETE /api/recipes/{id}` |
| 分享广场页 | `GET /api/public-recipes`、`POST /api/public-recipes/{id}/fork` |
| 公开配方详情页 | `GET /api/public-recipes/{id}`、评分、尝试、收藏、复刻接口 |
| Top20 榜单页 | `GET /api/rankings/top-recipes` |

## 10. 数据库实现提醒

- `recipes` 表可以用 JSON 字段保存 `syrups`、`toppings`、`flavorTags`、`flavorRadar`，同时保留 `recipe_ingredients` 表用于后续扩展原料明细。
- `recipe_ratings` 对 `public_recipe_id + user_id` 建唯一索引，保证同一用户只保留一条有效评分。
- `recipe_favorites` 对 `public_recipe_id + user_id` 建唯一索引，避免重复收藏。
- `recipe_try_records` 可允许多次尝试，也可第一版按 `public_recipe_id + user_id` 去重；默认建议按用户去重，避免刷尝试次数。
- `public_recipes.hot_score` 可以在评分、收藏、尝试、复刻变化后同步更新，也可以通过查询时计算。第一版建议同步更新，方便 Top20 排序。

## 11. 后续 OpenAPI 计划

后端集成 `springdoc-openapi` 后，需要保证 Swagger UI 中的接口路径、请求体、响应体与本文档一致。后续可从后端注解导出 OpenAPI JSON，再与本文档一起作为前后端联调依据。
