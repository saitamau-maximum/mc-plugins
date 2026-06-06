# mc-plugins

Paper / Bukkit 向けプラグインの Gradle モノレポ。

## クイックスタート

```bash
mise run setup   # 初回: ツール + git hooks
mise run check   # spotlessCheck + test
mise run fix     # spotlessApply
mise run build   # JAR ビルド
```

## プラグイン

| ディレクトリ | Paper 名 | 説明 |
| --- | --- | --- |
| `login-notify/` | MaximumLoginNotify | 第 1 プラグイン（`core/` + `bukkit/`） |
| `login-notify/core/` | — | pure Java コア（Bukkit 非依存） |
| `login-notify/bukkit/` | MaximumLoginNotify | Bukkit adapter + 配布 JAR（core 同梱） |

## ドキュメント

| 種別 | リンク |
| --- | --- |
| 開発手順 | [docs/how-to/local-dev.md](./docs/how-to/local-dev.md) |
| リリース | [docs/how-to/release.md](./docs/how-to/release.md) |
| 設計判断 (ADR) | [docs/adr/](./docs/adr/) |
| 索引 | [docs/README.md](./docs/README.md) |
