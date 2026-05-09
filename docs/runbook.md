# CoffeeLab 运行手册

## 环境要求

- Node.js：可运行 Vite/Vue3 项目。
- Java：17。
- Maven：可构建 Spring Boot 项目。
- MySQL：8.x，仅在导入数据库脚本或接真实数据库时需要。

## 启动前端

```powershell
cd E:\OnlyTest\CoffeeLab\frontend
npm.cmd install
npm.cmd run dev
```

默认开发服务由 Vite 提供。若 PowerShell 拦截 `npm.ps1`，使用 `npm.cmd`。

## 构建前端

```powershell
cd E:\OnlyTest\CoffeeLab\frontend
npm.cmd run build
```

构建产物在 `frontend/dist/`，已被 `.gitignore` 排除。

## 启动后端

```powershell
cd E:\OnlyTest\CoffeeLab\backend
mvn spring-boot:run
```

默认端口：`8080`

Swagger UI：

```text
http://localhost:8080/swagger-ui.html
```

## 构建后端

```powershell
cd E:\OnlyTest\CoffeeLab\backend
mvn -DskipTests package
```

构建产物在 `backend/target/`，已被 `.gitignore` 排除。

## 初始化数据库

后端默认连接名为 `coffeelab` 的数据库。新机器第一次联调时，先在 MySQL CLI 中创建数据库：

```sql
CREATE DATABASE IF NOT EXISTS coffeelab CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;
USE coffeelab;
```

然后从项目根目录导入脚本。Windows PowerShell 不支持 Bash 风格的 `<` 输入重定向，建议通过 `cmd /c` 执行：

```powershell
cmd /c "mysql -u <user> -p coffeelab < database\schema.sql"
cmd /c "mysql -u <user> -p coffeelab < database\seed.sql"
```

或在 MySQL CLI 中执行：

```sql
USE coffeelab;
SOURCE E:/OnlyTest/CoffeeLab/database/schema.sql;
SOURCE E:/OnlyTest/CoffeeLab/database/seed.sql;
```

## 常见问题

### PowerShell 无法直接执行 npm

使用：

```powershell
npm.cmd run dev
```

不要依赖 `npm.ps1`。

### 数据库连接与兜底

后端当前默认优先连接 MySQL，连接信息来自 `application.yml` 和环境变量：

```powershell
$env:COFFEELAB_DB_HOST = "localhost"
$env:COFFEELAB_DB_PORT = "3306"
$env:COFFEELAB_DB_NAME = "coffeelab"
$env:COFFEELAB_DB_USER = "root"
$env:COFFEELAB_DB_PASSWORD = "root"
```

如果 MySQL 未启动、账号密码不对或数据库尚未初始化，后端会自动短时间熔断并回落到 `InMemoryStore` 演示数据，避免前端页面直接不可用。真实联调前建议先执行 `database/schema.sql` 和 `database/seed.sql`。

演示账号：

```text
latte@example.com / 123456
```

### README 或终端出现中文乱码

文件使用 UTF-8。若 PowerShell 输出乱码，先设置：

```powershell
$OutputEncoding = [System.Text.UTF8Encoding]::new()
[Console]::OutputEncoding = [System.Text.UTF8Encoding]::new()
```

### 前端 API 回退逻辑

经典咖啡、我的配方、分享广场、Top20、登录和 DIY 保存已优先请求真实 API。接口不可用时页面会回落到 `coffeeLabMock.js`，因此开发时前端不会白屏。
