# 本番反映（server-ansible との境界）

mc-plugins の Release 完了後、**本番サーバーへの配置は server-ansible 側**で行う。ここではプラグイン開発者が知っておく境界と handoff のみ記す（Ansible 実装の詳細は server-ansible リポジトリを参照）。

## 責務分担

| リポジトリ | やること | やらないこと |
| --- | --- | --- |
| **mc-plugins** | ソース、CI、tag、GitHub Release（JAR asset） | 本番 secret、compose、vault |
| **server-ansible** | JAR 取得・配置、config テンプレ + vault 注入、MC 再起動 | Java ソース、Gradle ビルド |

## Release 後の流れ

1. GitHub Release に JAR が載っていることを確認（[release.md](./release.md) §3）
2. **server-ansible** の prod group vars で `release_tag` を新 tag に更新
3. PR → merge → CD（`ansible-playbook`）が JAR を本番に配置
4. 必要に応じて Paper コンテナを再起動

mc-plugins だけ push しても **本番は更新されない**。

## server-ansible 側の pin（想定）

prod vars にプラグインごとのエントリを置き、**許可リスト**として運用する（[ADR 0003](../adr/0003-no-untrusted-external-plugins.md)）。

```yaml
# inventories/prod/group_vars/all/vars.yml（例）

mc_plugins_repo: "https://github.com/saitamau-maximum/mc-plugins"

mc_plugins:
  MaximumLoginNotify:
    release_tag: "login-notify-v1.0.0"
    jar_name: "MaximumLoginNotify.jar"
    config_dest: "MaximumLoginNotify"   # plugin-config/{name}/config.yml
```

webhook 等の secret は repo に含めず vault 経由で config に注入する。

## Release asset URL

JAR は次の形式で取得される（`get_url` 等）:

```
{mc_plugins_repo}/releases/download/{release_tag}/{jar_name}
```

例:

```
https://github.com/saitamau-maximum/mc-plugins/releases/download/login-notify-v1.0.0/MaximumLoginNotify.jar
```

private repo の場合は server-ansible 側で GitHub 認証（PAT 等）が必要。

## トラブルシュート

| 症状 | 確認 |
| --- | --- |
| Release は成功したが本番が古い | server-ansible の `release_tag` pin が更新されているか、CD が走ったか |
| JAR 取得 404 | tag 名・`jar_name` が Release asset と一致しているか |
| 通知が飛ばない | server-ansible 側 config / vault（webhook URL）を確認 |

## 参照

- [release.md](./release.md) — tag 付けと GitHub Release
- [ADR 0001](../adr/0001-monorepo-and-release-via-github.md) — モノレポ + Release 方針
- [server-ansible](https://github.com/saitamau-maximum/server-ansible) — Ansible playbook / roles / vars の実装
