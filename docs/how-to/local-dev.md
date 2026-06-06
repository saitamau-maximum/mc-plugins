# ローカル開発

## 前提

- [mise](https://mise.jdx.dev/)（推奨）または Java 21 + 手動で lefthook / gitleaks
- Git

mise を使わない場合も `./gradlew` は Wrapper 同梱のため Gradle の事前インストールは不要。

## 初回セットアップ

```bash
git clone <mc-plugins-url>
cd mc-plugins
mise run setup
```

`mise run setup` は次を実行する。

1. `mise install` — Java 21.0.2、lefthook、gitleaks
2. `lefthook install` — pre-commit / commit-msg hooks

初回のみ `mise trust` が必要な環境では、プロンプトに従って trust する。

## 動作確認

```bash
mise run check     # spotlessCheck + test（CI と同じゲート）
mise run fix       # spotlessApply（コミット前）
mise run test      # テストのみ
mise run build     # JAR ビルド
```

単体プラグインだけ触る場合:

```bash
./gradlew :login-notify:core:test     # コア（Bukkit なし）
./gradlew :login-notify:bukkit:jar    # 配布 JAR
# または
mise run build:login-notify
```

## モジュール構成（login-notify）

[ADR 0004](../adr/0004-core-platform-adapter-split.md) に従い、**1 プラグイン = 1 ディレクトリ** の下に core / bukkit を nest する。

```
login-notify/
├── core/      # :login-notify:core
└── bukkit/    # :login-notify:bukkit → MaximumLoginNotify.jar
```

| Gradle project | 依存 | 役割 |
| --- | --- | --- |
| `:login-notify:core` | なし（pure Java） | ドメイン + 外向き adapter（Discord 等） |
| `:login-notify:bukkit` | core + `paper-api` compileOnly | Bukkit listener / config マッピング / plugin エントリ |

```
PlayerConnectionListener          LoginNotificationService
  → BukkitConnectionEventFactory      → LoginNotifier (Discord, …)
       ↓ ConnectionEvent
```

**core に `org.bukkit.*` を import しない** — 境界違反は compile 前に気づけるよう、Gradle モジュールで分離している。

ビルド成果物: `login-notify/bukkit/build/libs/MaximumLoginNotify.jar`（core classes 同梱）

## Git hooks

| hook | 内容 |
| --- | --- |
| pre-commit | gitleaks、spotlessCheck（format 検証）、変更された Java/kts の compile + testClasses |
| commit-msg | [Conventional Commits](https://www.conventionalcommits.org/) 形式 |

コミットメッセージ例:

```
feat(login-notify): add disconnect notification
fix(login-notify): handle empty webhook URL
```

## IDE

`.vscode/extensions.json` に Java 拡張の推奨設定あり。Java 21 を IDE が認識しない場合は `.java-version`（mise と連携）を参照する。

Homebrew 等で Java 25 が PATH 優先されていると Gradle が失敗することがある。その場合は mise の Java 21 を有効にするか、競合する JDK を PATH から外す。

## Format / 静的解析

**Spotless のみ**（google-java-format + ktlint for `.gradle.kts`）。Checkstyle / Error Prone 等の別 linter は未導入。

| コマンド | 内容 |
| --- | --- |
| `mise run check` | format 検証 + テスト |
| `mise run fix` | format 自動修正 |
| `mise run test` | テストのみ |

## CI との対応

| ローカル | CI |
| --- | --- |
| `mise run check` | `spotlessCheck` + `test` |
| `mise run build` | `jar` |
| `mise run fix` | （ローカルのみ）`spotlessApply` |

GitHub Actions では gitleaks も実行する。

## Git / `.gitignore`

| パス | git | 理由 |
| --- | --- | --- |
| `gradlew`, `gradlew.bat` | コミット | Wrapper 実行用 |
| `gradle/wrapper/` | コミット | Gradle バージョン固定（`gradle-wrapper.jar` 含む） |
| `.gradle/` | ignore | ローカルキャッシュ |
| `build/` | ignore | ビルド成果物 |
| `mise.lock` | コミット | ツール pin（`.gitignore` で ignore しない） |

**注意:** ignore するのは **`.gradle/`**（先頭ドット）。`gradle/wrapper/` とは別物。Wrapper を ignore すると clone 直後に `./gradlew` が動かず、CI も失敗する。

CI では `gradle/actions/wrapper-validation` で `gradle-wrapper.jar` の checksum を検証している（`.github/workflows/ci.yml`）。
