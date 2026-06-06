# 0002. 外部通知の LoginNotifier アダプタパターン

- Status: Accepted
- Date: 2026-06-06

## Context

**MaximumLoginNotify**（`login-notify/`）は、プレイヤーの参加・退出を外部（初版: Discord Webhook）へ通知する。

通知先の選び方:

| 案 | 概要 |
| --- | --- |
| **A. Discord 直書き** | join/quit ハンドラから Discord クライアントを直接呼ぶ |
| **B. LoginNotifier adapter** | 通知先を interface で差し替え可能にする |

初版は Discord のみだが、Slack・generic HTTP 等への拡張可能性がある。Paper イベント処理・非同期実行はプラグイン本体の責務として固定したい。

## Decision

**案 B** を採用。通知先ごとの実装を **LoginNotifier** adapter として分離する。

```
PlayerConnectionListener (bukkit)
  → LoginNotificationService (core)
    → LoginNotifier (interface)
      → DiscordWebhookLoginNotifier
      → NoOpLoginNotifier
```

- `config.yml` の `notifier.type` で実装を選択（`LoginNotifierFactory`）
- 通知処理は `CompletableFuture<Void>` で非同期。サーバーのメインスレッドをブロックしない
- Discord 固有の embed 組み立ては `DiscordEmbedPayloadBuilder` に閉じ込める
- webhook URL 等の secret は repo に含めず、サーバー上の `config.yml` で設定する

## Consequences

### Positive

- 新しい通知先を adapter 追加 + factory 分岐で拡張できる
- テストでは `NoOpLoginNotifier` や HTTP モックで本体ロジックを検証しやすい
- Discord API 詳細が 1 クラスに閉じ、イベント層が薄い

### Negative

- プラグイン 1 個の段階では interface + factory はやや過剰（YAGNI とのトレードオフ）

### Follow-up

- 2 個目のプラグインでも通知が必要になったら `login-notify/core` の昇格 or `libs/notify-core` を ADR で再検討
