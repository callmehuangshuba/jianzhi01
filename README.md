# jianzhi01

Java 21 + Maven 基线工程，用于 Coding Agent 标注任务。

## 环境要求

| 项 | 版本 | 说明 |
|---|---|---|
| JDK | Oracle JDK **21.0.9** | 本机路径 `G:\tools\jdk-21.0.9` |
| Maven | Apache Maven **3.9.16** | 本机路径 `G:\tools\apache-maven-3.9.16` |

每次开新终端，先激活环境（只需一次）：

```bash
# Git Bash
source /g/tools/env.sh
```

```cmd
:: cmd.exe
call G:\tools\env.cmd
```

激活后验证：

```bash
java -version    # 应输出 21.0.9
mvn -version     # 应输出 Apache Maven 3.9.16
```

## 统一命令

本工程约定以下命令为**唯一**的测试、构建、启动入口：

```bash
mvn test                              # 运行全部自动化测试
mvn package                           # 构建，产出 target/jianzhi01.jar
mvn spring-boot:run                   # 启动 Web 系统（仅当任务要求 Web 页面时使用）
```

产物名称固定为 **`jianzhi01.jar`**（由 `pom.xml` 的 `<finalName>` 锁定，不随版本号变化）。

## 启动命令

```bash
java -jar target/jianzhi01.jar
```

## 最小验证命令

交付后人工验收，按顺序执行这三条即可确认核心功能可用：

```bash
mvn package                                        # 1. 能构建通过
java -jar target/jianzhi01.jar version             # 2. 输出 jianzhi01 1.0.0
java -jar target/jianzhi01.jar ping                # 3. 输出 pong
```

预期输出：

```
jianzhi01 1.0.0
pong
```

## 项目结构

```
jianzhi01-base/
├── pom.xml                                   # Maven 配置，锁定 Java 21 与产物名
├── README.md                                 # 本文件
├── .gitignore                                # 已覆盖凭据、构建产物、作业私有文件
└── src/
    ├── main/
    │   ├── java/com/anno/app/
    │   │   └── Main.java                     # 统一命令行入口
    │   └── resources/                        # 配置与数据模板放这里
    └── test/
        └── java/com/anno/app/
            └── MainTest.java                 # 入口测试（基线为绿色）
```

## 设计约束

本工程遵循以下约束，后续实现的所有功能都必须遵守：

- **零外部运行时依赖**：不需要启动数据库、Redis、消息队列或任何外部服务。所有状态保存在本地（内存或 `data/` 下的本地文件）。
- **不联网**：实现与测试过程不得访问远程服务或真实 API。
- **确定性**：相同输入必须产生相同结果，不得依赖 HashMap 遍历顺序、系统时区、随机数或当前时间的隐式默认值。
- **可测试**：核心逻辑必须能被 `mvn test` 覆盖，不得只能通过启动整个应用来验证。
- **入口统一**：新增功能挂接到 `Main` 的子命令下，不新建第二个入口。

## 关于本地状态存储

任务如需持久化，统一使用项目根目录下的 `data/`（已被 `.gitignore` 排除）。
不要在 `src/main/resources` 下写运行时状态——那会让构建产物不可重复。
