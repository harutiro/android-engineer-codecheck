# 株式会社ゆめみ Android エンジニアコードチェック課題

## 概要

このプロジェクトは、GitHubリポジトリの検索と表示を行うAndroidアプリケーションです。Jetpack Composeを使用してUIを構築し、Hiltを使用して依存性注入を行っています。

## アプリ仕様

本アプリは GitHub のリポジトリを検索するアプリです。

<img src="docs/app.gif" width="320">

## ディレクトリ構成
```bash
.
├── .editorconfig
├── .github/
│   ├── PULL_REQUEST_TEMPLATE.md
│   └── workflows/
├── .gitignore
├── .gradle/
├── .idea/
├── .kotlin/
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/
│   │   │   ├── kotlin/
│   │   │   └── res/
│   │   └── test/
│   └── build.gradle
├── build.gradle
├── docs/
├── gradle/
├── gradle.properties
├── gradlew
├── gradlew.bat
├── LICENSE
├── local.properties
├── README.md
└── settings.gradle
```

### 環境

- IDE：Android Studio Ladybug | 2024.2.1 Patch 3
- Kotlin： 2.0.21
- Java：17
- Gradle：8.9
- minSdk：23
- targetSdk：35

※ ライブラリの利用はオープンソースのものに限ります。
※ 環境は適宜更新してください。

### linterについて

このプロジェクトはktlintを用いて静的コード解析を行なっている。

ルールとして、パッケージ名に"_"を使うのは許可するものとしている。
理由としては、コーディングテストのパッケージ名が変わってしまうとアプリとして別物となってしまい、
リファクタリングの趣旨としてそぐわないと判断したため

コードは以下の二つがある適宜PRを出す前にチェックをすること。
```bash
# 自動でフォーマットをかける
 ./gradlew ktlintFormat
 
# コードのルール違反をチェックする
./gradlew ktlintCheck 
```

### Unitテストについて

- Hilt, JUnit, Mockitoを持ちいてUnitテストを作成しました。

```bash
# 全件実行をする方法
./gradlew test jacocoTestReport

# 単体で動かす方法
./gradlew :app:testDebugUnitTest --tests "jp.co.yumemi.android.code_check.features.github.GitHubServiceRepositoryImplTest"
```

レポートの保存場所
以下のパスにWeb表示ができるレポートが格納されます。
`app/build/reports/tests/testDebugUnitTest/`

### 動作

1. 何かしらのキーワードを入力
2. GitHub API（`search/repositories`）でリポジトリを検索し、結果一覧を概要（リポジトリ名）で表示
3. 特定の結果を選択したら、該当リポジトリの詳細（リポジトリ名、オーナーアイコン、プロジェクト言語、Star 数、Watcher 数、Fork 数、Issue 数）を表示


### 依存関係

主要な依存関係は以下の通りです。

- Jetpack Compose
- Hilt
- Retrofit
- Moshi
- Coil

依存関係の詳細は、`app/build.gradle` ファイルを参照してください。

### 重要なファイルとディレクトリ

app/src/main/java - Javaソースコード
kotlin - Kotlinソースコード
res - リソースファイル（レイアウト、文字列、画像など）
test - ユニットテスト
androidTest - インストルメンテーションテスト
build.gradle - モジュールのビルド設定
gradle.properties - プロジェクト全体のプロパティ設定

### トラブルシューティング

- ビルドエラーが発生する場合:
  - 依存関係が正しくインストールされているか確認してください。
  - キャッシュをクリアして再ビルドを試みてください。

```bash
./gradlew clean
./gradlew build
```

- テストが失敗する場合:
  - テストコードが最新の実装に対応しているか確認してください。
  - 必要に応じてモックデータを更新してください。

