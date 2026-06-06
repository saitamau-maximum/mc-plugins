# Architecture Decision Records (ADR)

このディレクトリには、mc-plugins に関する重要な設計判断を記録する。

## ファイル命名

```
NNNN-kebab-case-title.md
```

- `NNNN`: 4 桁の連番（`0001` から）
- タイトル: 英語 kebab-case

## テンプレート

各 ADR は次の見出し構成に従う。

```markdown
# NNNN. タイトル

- Status: Proposed | Accepted | Deprecated | Superseded by ADR-XXXX
- Date: YYYY-MM-DD

## Context
（背景・検討した選択肢・採用理由の材料）

## Decision
（採用した方針）

## Consequences
（メリット・デメリット・フォローアップ）
```

### 書き方

- **初回 push 前・未デプロイの決定**は、「以前 xxx だったので問題だった」のように**過去形の問題設定にしない**
- 代わりに **検討した案（A / B）**、**懸念**、**なぜこの方針にしたか** を書く
- すでに本番で起きた incident や merge 済みの構成変更を記録するときだけ、事後の Context として「以前〜」を使う

## いつ ADR を書くか

- リポジトリ構成・リリース方式・依存関係方針など、**後から理由を忘れやすい決定**
- 複数の妥当な選択肢があり、**トレードオフを明示したい**とき

手順だけの変更（コマンド追加など）は [how-to/](../how-to/) に書く。
