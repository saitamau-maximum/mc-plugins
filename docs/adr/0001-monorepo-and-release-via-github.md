# 0001. モノレポ化と GitHub Release による成果物配布

- Status: Accepted
- Date: 2026-06-06

## Context

MAXIMUM Minecraft サーバー（Paper, `mc.maximum.vc`）向けに **MaximumLoginNotify** から独自 Paper プラグインを開発する。初回 push 前の設計段階。

プラグインの置き場所と成果物の流れについて、次の選択肢を検討した。

| 案 | 概要 |
| --- | --- |
| **A. server-ansible 内** | `server-ansible/plugins/` にソースを置き、Ansible 側でビルド or JAR 配置 |
| **B. mc-plugins モノレポ** | プラグイン専用 repo。JAR は GitHub Release。server-ansible は取得・配置のみ |

案 A だと、server-ansible 上に Ansible task / CI の draft はあるが **プラグインソースは remote 未 push・本番未デプロイ**。このまま進める場合の懸念:

- インフラ repo とプラグイン開発の責務混在
- リポジトリ内 `plugins/` とサーバー上 `minecraft/plugins/` の同名
- JAR を git に入れるか、controller で `./gradlew` するか方針がぶれる
- プラグイン増加で server-ansible が肥大化

一方、プラグイン数は当面少数（1〜数個）で、Paper API・Java toolchain・CI は共通化したい。

## Decision

**案 B（mc-plugins）** を採用する。

1. **mc-plugins** を Gradle マルチプロジェクトのモノレポとし、プラグインソースはここに置く。
2. **ビルド成果物（JAR）は git 管理しない。** タグ push で GitHub Actions が Release に JAR を upload する。
3. **本番反映は server-ansible が担当。** Release URL から JAR を取得し、設定（webhook 等）は vault 経由で注入。server-ansible 側では Java / Gradle を不要にする。
4. 本番バージョンは server-ansible の prod vars で **明示的に pin** する（k3s の `k3s_install_version` と同型）。

タグ形式: `{plugin-dir}-v{semver}`（例: `login-notify-v1.0.0`）。

## Consequences

### Positive

- 責務分離が明確（開発 vs デプロイ）
- リリース履歴が GitHub Release に集約される
- モノレポで Gradle・CI・Paper API バージョンを一元管理できる
- server-ansible の CD に Java ビルド環境が不要

### Negative

- リポジトリが 2 つになり、本番反映は mc-plugins の tag と server-ansible の vars 更新の両方が必要
- private repo の場合、Ansible から Release を取る際に認証（PAT 等）が必要

### Follow-up

- server-ansible の `plugins/` 参照（task・CI path filter 等）を整理し、`get_url` で Release JAR を取得する（Phase 2）
- 2 個目のプラグイン追加時に [how-to/add-new-plugin.md](../how-to/add-new-plugin.md) を追加（未作成）
