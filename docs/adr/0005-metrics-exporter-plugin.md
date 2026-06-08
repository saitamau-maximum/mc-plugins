# 0005. Minecraft メトリクス exporter プラグイン

- Status: Accepted
- Date: 2026-06-08

## Context

**MaximumMetricsExporter-Paper**（`metrics-exporter/`）は、Paper / Bukkit サーバーの運用指標（プレイヤー数、TPS、ワールド状態など）を [Prometheus](https://prometheus.io/) 形式で公開する。

収集方式の候補:

| 案 | 概要 |
| --- | --- |
| **A. 外部 exporter** | RCON / log パースで MC 外から収集 |
| **B. プラグイン内 HTTP pull** | Bukkit/Paper API で収集し `/metrics` を公開 |
| **C. Push Gateway** | プラグインから Prometheus Push Gateway へ push |

案 A は API で取れる指標が限られ、RCON 依存が増える。案 C は常時 push と lifecycle 管理が煩雑。案 B なら Bukkit イベントと Paper TPS を直接参照でき、Prometheus の標準的な **pull** モデルに合わせられる。

ライブラリ候補:

| 案 | 概要 |
| --- | --- |
| **Micrometer** | 抽象化が厚い。MC プラグイン 1 個には過剰 |
| **Prometheus simpleclient** | 実績が多く、HTTPServer / hotspot が同梱 |

TPS / tick time は Paper の `Server` 拡張 API が必要。Bukkit-only サーバー向け JAR は **出さない**。対応しないなら対応しない、と割り切り、Paper 固有部分は adapter として分離する。

cardinality 上、プレイヤー UUID や kick reason 原文をラベルにすると series が爆発する。kick reason は固定 bucket（`banned` / `kicked` / `timeout` / `other`）に正規化する。

## Decision

**案 B + simpleclient** を採用。`metrics-exporter/` を [ADR 0004](./0004-core-platform-adapter-split.md) を拡張した **core + bukkit + paper** で実装する。

```
metrics-exporter/core/    — MetricsRegistry, Settings, KickReasonNormalizer, ExporterService
metrics-exporter/bukkit/  — ServerMetricsCollector, BukkitServerMetricsCollector, listener, HTTP 起動
metrics-exporter/paper/   — PaperServerMetricsCollector, Plugin, 配布 JAR
```

| Gradle project | パッケージ | 責務 |
| --- | --- | --- |
| `:metrics-exporter:core` | `...core` | Prometheus registry、設定 record、kick reason 正規化 |
| `:metrics-exporter:bukkit` | `...bukkit` | Bukkit API のみで取れる gauge / event counter。`ServerMetricsCollector` interface |
| `:metrics-exporter:paper` | `...paper` | TPS / tick time を追加する Paper adapter。配布 JAR のエントリポイント |

- Phase 1: gauge（players, worlds, entities, chunks, TPS, tick duration, uptime, plugins）+ counter（join/quit/kick/death）+ `simpleclient_hotspot` の JVM メトリクス
- HTTP `/metrics` を config で指定した host:port（デフォルト `0.0.0.0:9225`）で公開
- Bukkit API 参照はメインスレッドの sync タスク内のみ。scrape スレッドは Prometheus registry の state のみ読む
- 配布 JAR 名は **`MaximumMetricsExporter-Paper.jar`**。plugin.yml の `name` も `MaximumMetricsExporter-Paper` とし、Paper 必須であることを命名で明示する
- Release 配布 subproject は `:metrics-exporter:paper`（[ADR 0006](./0006-release-artifact-declaration.md) の `mcRelease` 宣言）。`:bukkit` は library のため Release 対象外
- scrape 設定・ダッシュボード・ネットワーク到達性は **本リポジトリの外**（運用側の Prometheus / Grafana 設定）で行う

## Consequences

### Positive

- Bukkit adapter と Paper adapter の境界が Gradle モジュールで enforce される
- 配布物の命名から Paper 必須が読み取れる
- 任意の Prometheus インスタンスから pull できる
- core のユニットテストで registry / normalizer を Bukkit なしで検証できる

### Negative

- 1 プラグインあたり Gradle subproject が 3 つになる（login-notify より複雑）
- Prometheus ライブラリ同梱のため JAR が login-notify より大きい
- Spigot 等への対応 JAR は意図的に提供しない（必要になれば別 adapter + 別命名で追加）

### Follow-up

- Phase 2: チャット数、セッション時間 histogram、UU 集計
