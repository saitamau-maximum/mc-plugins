# mc-plugins

MAXIMUM Minecraft サーバー（Paper, `mc.maximum.vc`）向けプラグインのモノレポ。

## クイックスタート

```bash
mise run setup   # 初回: ツール + git hooks
mise run test    # テスト
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
| 本番反映 | [docs/how-to/production-deploy.md](./docs/how-to/production-deploy.md) |
| 設計判断 (ADR) | [docs/adr/](./docs/adr/)（[モジュール分離](./docs/adr/0004-core-platform-adapter-split.md) 等） |
| 索引 | [docs/README.md](./docs/README.md) |

本番デプロイの Ansible 実装は [server-ansible](https://github.com/saitamau-maximum/server-ansible)。handoff は [production-deploy.md](./docs/how-to/production-deploy.md)。
