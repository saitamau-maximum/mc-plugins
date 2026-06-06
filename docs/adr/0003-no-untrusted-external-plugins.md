# 0003. 外部 Paper プラグインは原則導入しない

- Status: Accepted
- Date: 2026-06-06

## Context

MAXIMUM Minecraft サーバー（`mc.maximum.vc`）向け Paper プラグインの **セキュリティ方針** を、初回デプロイ前に定める。

Paper / Bukkit プラグインは **JAR を `plugins/` に置くだけで JVM 上で実行される**。サンドボックス、権限分離、署名検証、再現可能ビルドといったサプライチェーンは、一般的なアプリ配布と比べてほぼ存在しない。

コミュニティでは次のようなリスクが常態化している。

- SpigotMC / 配布サイトから **ビルド済み JAR を直接 drop** する運用
- 作者・ビルド環境・再配布経路が不透明
- プラグイン更新時に **差分レビューが困難**（難読化、shade された依存関係）
- マルウェア入り JAR や乗っ取り incident の報告（「便利なプラグイン」名目の RAT 等）

MAXIMUM の Minecraft サーバー（`mc.maximum.vc`）も、プラグインを増やすほど **攻撃面とサプライチェーンリスク** が広がる。インフラ repo と分離した mc-plugins モノレポ（[ADR 0001](./0001-monorepo-and-release-via-github.md)）は、セキュリティ方針をコードとデプロイの両方で enforce するための土台でもある。

### スコープ

| 対象 | 本 ADR の扱い |
| --- | --- |
| **第三者製 Paper/Bukkit プラグイン** | 原則禁止（本 ADR の主題） |
| **自社開発（mc-plugins）** | 許可。ソース・CI・Release pin で管理 |
| **Paper 本体・itzg/minecraft-server イメージ** | インフラ選定。別途 pin / 更新判断（プラグイン方針とは別レイヤ） |

## Decision

1. **本番に載せる Paper プラグインは mc-plugins で自前開発したものに限る**（first-party only）。
2. **第三者製プラグインの JAR を本番 `plugins/` に置かない。** Modrinth / Hangar / SpigotMC 等からの **ビルド済み JAR 直リンク導入はデフォルト禁止**。
3. 本番配置は [ADR 0001](./0001-monorepo-and-release-via-github.md) のとおり、**GitHub Release の tag pin + server-ansible `get_url`** のみ。手動 scp や「とりあえず落として試す」は本番禁止。
4. **例外**（どうしても第三者製が必要な場合）は次をすべて満たすまで導入しない。
   - 新規 ADR で理由・代替案不可の理由・リスク受容を記録
   - ソース入手可能なら **自前ビルド**（fork + mc-plugins 取り込み、または vendored submodule + CI）を優先
   - ソース不可の場合は **バイナリのハッシュ pin**、入手経路の記録、定期見直し
   - server-ansible の `mc_plugins` に明示エントリ（暗黙の volume マウント禁止）
5. mc-plugins 側の **最低限の自社ゲート**: CI テスト、pre-commit gitleaks、Conventional Commits、Release tag の semver 管理。

## Consequences

### Positive

- 本番 `plugins/` の中身が **自社 repo と Release tag で説明可能**になる
- マルウェア混入経路（配布サイト・再パッケージ JAR）を構造的に断てる
- 機能追加は「ダウンロード」ではなく **設計・レビュー・CI** のフローに乗せられる
- server-ansible の `mc_plugins` 辞書が **許可リスト** として機能する

### Negative

- 既存の便利プラグインをそのまま使えない。必要な機能は **自前実装 or fork 保守** コストがかかる
- Paper エコシステムの「JAR を落として試す」文化と逆行する
- 例外 ADR の運用負荷

### Follow-up

- server-ansible 側の `mc_plugins` vars / task 実装時に、許可リスト運用を反映
- 2 個目以降のプラグイン追加時も [how-to/add-new-plugin.md](../how-to/add-new-plugin.md)（未作成）で first-party フローを明文化
