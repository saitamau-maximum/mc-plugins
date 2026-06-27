# mc-plugins ドキュメント

| 種別 | 説明 |
| --- | --- |
| [how-to/](./how-to/) | 開発・リリースの手順 |
| [adr/](./adr/) | アーキテクチャ上の決定記録（ADR） |

## How-to

| ドキュメント | 内容 |
| --- | --- |
| [local-dev.md](./how-to/local-dev.md) | 初回セットアップ、テスト、ビルド、git hooks |
| [release.md](./how-to/release.md) | タグ付け、GitHub Release |

## ADR

| # | タイトル | 状態 |
| --- | --- | --- |
| [0001](./adr/0001-monorepo-and-release-via-github.md) | モノレポ化と GitHub Release による成果物配布 | Accepted |
| [0002](./adr/0002-login-notifier-adapter-pattern.md) | 外部通知の LoginNotifier アダプタパターン | Accepted |
| [0003](./adr/0003-no-untrusted-external-plugins.md) | 本リポジトリは自前開発プラグインのみを扱う | Accepted |
| [0004](./adr/0004-core-platform-adapter-split.md) | core と platform adapter のモジュール分離 | Accepted |
| [0005](./adr/0005-metrics-exporter-plugin.md) | Minecraft メトリクス exporter プラグイン | Accepted |
| [0006](./adr/0006-release-artifact-declaration.md) | Release 配布物の宣言（mcRelease） | Accepted |
