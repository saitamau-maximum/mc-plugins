# 0004. core と platform adapter のモジュール分離

- Status: Accepted
- Date: 2026-06-06

## Context

login-notify の責務は **Bukkit イベント → ドメイン → 外部通知**（[ADR 0002](./0002-login-notifier-adapter-pattern.md) の外向き adapter）。

モジュール構成について、次を検討した。

| 案 | 概要 |
| --- | --- |
| **A. 単一 subproject** | `login-notify/` 1 本。実装は簡単 |
| **B. core + bukkit** | `{plugin}/core/`（pure Java）+ `{plugin}/bukkit/`（platform adapter） |

案 A だと、`paper-api` compile 依存と pure Java ロジックが同居しやすく、Bukkit 型（`FileConfiguration` 等）がコアに漏れやすい。テストも Bukkit 前提になりがち。

mc-plugins を **お手本リポジトリ** とするなら、プラットフォーム境界を Gradle モジュールで enforce した方がよい。2 個目プラグイン以降も同型テンプレートにしたい。

## Decision

**案 B** を採用。**1 プラグイン = 1 ディレクトリ**（`login-notify/`）。その下に nest する。

```
login-notify/
├── core/                # :login-notify:core — pure Java（Bukkit/Paper 非依存）
└── bukkit/              # :login-notify:bukkit — platform adapter + 配布 JAR
```

将来 2 個目以降も同型: `another-plugin/core/` + `another-plugin/bukkit/`。

`settings.gradle.kts`:

```kotlin
include("login-notify:core", "login-notify:bukkit")
```

### レイヤ

| Gradle project | パッケージ | 責務 |
| --- | --- | --- |
| `:login-notify:core` | `vc.maximum.mc.loginnotify.core` | `ConnectionEvent` ドメイン、`LoginNotificationService`、外向き `LoginNotifier`（Discord 等） |
| `:login-notify:bukkit` | `vc.maximum.mc.loginnotify.bukkit` | `PlayerConnectionListener`、`BukkitConnectionEventFactory`、`LoginNotifyConfigMapper`、`MaximumLoginNotifyPlugin` |

### 境界ルール

1. **core に `org.bukkit.*` / `io.papermc.*` を import しない**
2. 設定は core 側で **`LoginNotifySettings` 等の record** に正規化。YAML 読み取りは bukkit の `LoginNotifyConfigMapper` のみ
3. **`paper-api` は `:login-notify:bukkit` のみ** `compileOnly`
4. 配布 JAR（`MaximumLoginNotify.jar`）は `:login-notify:bukkit:jar` が core の classes を同梱
5. **テストは core に集約**（Bukkit なしで実行可能）
6. git tag / Release workflow の plugin 名は **ディレクトリ名**（`login-notify-v1.0.0` → `./gradlew :login-notify:bukkit:jar`）

### プラグイン API について

対象サーバーは Paper だが、**bukkit 層のコードは Bukkit API のみ**（Paper 固有 API は必要になるまで入れない）。`paper-api` は Maven 座標の都合。

## Consequences

### Positive

- コアロジックとテストが Bukkit なしで完結
- モジュール境界が Gradle で enforce される
- プラグイン単位でディレクトリがまとまり、モノレポの見通しが良い
- 2 個目プラグイン追加時に `login-notify/core` を `libs/notify-core` へ昇格しやすい

### Negative

- 1 プラグインあたり Gradle subproject が 2 つになり、初見の複雑さが増える
- JAR 同梱設定が必要（Shadow 未使用の明示的 `from(core.output)`）
- Release workflow は `:plugin:bukkit:jar` / `plugin/bukkit/build/libs/` を参照

### Follow-up

- 2 個目のプラグイン追加時に [how-to/add-new-plugin.md](../how-to/add-new-plugin.md) で同型テンプレートを文書化
- Paper 固有 API が必要になったら、**その機能だけ** bukkit 層に閉じるか、例外 ADR を書く
