# WhiteBoard 项目 Java 转 Kotlin 改造计划

本名为 WhiteBoard 的项目即将进行从 Java 到 Kotlin 的迁移。为了确保迁移过程平稳且不影响现有功能，制定以下改造计划。

## 1. 环境准备

### 1.1 开发环境
- 确保 Android Studio 已更新至最新稳定版。
- 确认 Gradle 版本支持 Kotlin (当前项目使用 building gradle 8.3.0，支持良好)。

### 1.2 依赖配置
需要在项目级和模块级 `build.gradle` 中添加 Kotlin 相关配置。

**根目录 `build.gradle`**:
```groovy
plugins {
    id 'org.jetbrains.kotlin.android' version '1.9.0' apply false
}
```

**模块目录 (`app/build.gradle`, `wblib/build.gradle`)**:
```groovy
plugins {
    ...
    id 'org.jetbrains.kotlin.android'
}

dependencies {
    ...
    implementation "org.jetbrains.kotlin:kotlin-stdlib:1.9.0"
}
```

## 2. 迁移策略

建议采用 **"渐进式迁移"** (Incremental Migration) 策略，而不是一次性重写所有代码。

### 2.1 优先级
1.  **新功能开发**: 所有新功能必须使用 Kotlin 开发。
2.  **独立工具类 (Utils)**: 优先迁移依赖较少的工具类，风险低且能快速验证环境。
3.  **数据模型 (Models/POJOs)**: 利用 Kotlin 的 `data class` 大幅简化代码。
4.  **核心业务逻辑**: 在最后阶段迁移，确保有充分的测试覆盖。

### 2.2 迁移步骤
对于每个文件：
1.  使用 Android Studio 的 "Convert Java File to Kotlin File" 功能 (快捷键 `Cmd + Option + Shift + K` 或 `Ctrl + Alt + Shift + K`)。
2.  **人工校对**: 自动转换后的代码可能不是最优的（例如 `!!` 强制非空断言），需要人工优化为更符合 Kotlin 风格的写法（使用 `?` 和 `?:`）。
3.  **编译检查**: 确保转换后项目能成功编译。
4.  **运行测试**: 运行单元测试和集成测试，确保功能一致。

## 3. 常见问题与解决方案 (Pitfalls)

### 3.1 Null Safety (空安全)
- **问题**: Java 中大量对象默认为可空，自动转换后可能会出现大量的 `?` 或 `!!`。
- **解决**: 仔细分析业务逻辑，明确哪些字段确实可空，哪些一定不为空。尽量避免使用 `!!`。

### 3.2 静态成员 (Static)
- **问题**: Kotlin 没有 `static` 关键字。
- **解决**: 使用 `companion object` 或顶层函数 (Top-level functions)。工具类方法推荐转为顶层函数。

### 3.3 Lombok
- **问题**: 如果 Java 代码使用了 Lombok，Kotlin 无法直接识别。
- **解决**: 移除 Lombok 注解，手动还原 Getter/Setter 后再转换为 Kotlin (或者转换为 Kotlin 的属性和 Data Class)。

### 3.4 ButterKnife / View Binding
- **问题**: 旧项目如果使用了 ButterKnife，在 Kotlin 中配置较麻烦（需要 KAPT）。
- **解决**: 借此机会迁移到 ViewBinding，或者使用 Kotlin Android Extensions (已废弃，不推荐) / `findViewById`。推荐 ViewBinding。

## 4. 验证计划

- **构建验证**: 每次提交必须通过 `./gradlew assembleDebug`。
- **功能验证**: 手动测试核心画板功能。

## 5. 进度追踪

将在项目的 `doc` 目录下维护迁移日志，记录已迁移的模块和遇到的问题。
