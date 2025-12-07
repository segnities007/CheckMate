# CheckMate-Android

学生生活サポートアプリ「CheckMate」

## 📚 ドキュメント (Documentation)

プロジェクトの詳細なドキュメントは `docs/` ディレクトリにあります。

| ドキュメント | 内容 |
| :--- | :--- |
| [Architecture Overview](docs/architecture_overview.md) | 全体アーキテクチャ (Layered Architecture + Multi-Module) |
| [Screen Guide](docs/screen_guide.md) | 画面実装ガイドライン (MVI, Compose) |
| [Navigation Guide](docs/navigation_guide.md) | 画面遷移の実装 (Navigation3) |
| [DI Guide](docs/di_guide.md) | 依存関係注入 (Koin) |
| [ViewModel Guide](docs/viewmodel_guide.md) | ViewModel & UiState 実装 |
| [Repository Guide](docs/repository_guide.md) | データ層の実装 |
| [UseCase Guide](docs/usecase_guide.md) | ドメイン層の実装 |
| [Design System](docs/design_system_guide.md) | デザインシステム・共通コンポーネント |
| [Testing Guide](docs/testing_guide.md) | テスト実装ガイド |
| [MVI Guide](docs/mvi_guide.md) | MVIアーキテクチャ詳細 |

## 🛠️ 技術スタック (Tech Stack)

*   **Language**: [Kotlin](https://kotlinlang.org/)
*   **UI**: [Jetpack Compose](https://developer.android.com/jetpack/compose) (Material3)
*   **Architecture**: Layered Architecture, Multi-Module, MVI (Model-View-Intent)
*   **Dependency Injection**: [Koin](https://insert-koin.io/)
*   **Navigation**: [Androidx Navigation3](https://developer.android.com/jetpack/compose/navigation)
*   **Database**: [Room](https://developer.android.com/training/data-storage/room)
*   **Asynchronous**: [Coroutines](https://kotlinlang.org/docs/coroutines-overview.html) & [Flow](https://kotlinlang.org/docs/flow.html)
*   **Network**: KtorClient

## 📦 モジュール構成 (Modules)

```mermaid
graph TD
    App --> Feature
    Feature --> Domain
    Feature --> Common
    Feature --> DesignSystem
    Data --> Domain
    App --> Data
```
詳細なモジュール構成やアーキテクチャについては [Architecture Overview](docs/architecture_overview.md) を参照してください。

*   `:app`: アプリケーションのエントリーポイント
*   `:presentation`: UI機能ごとの機能モジュール (`:feature:home`, `:navigation`, etc.)
*   `:domain`: ビジネスロジック (`:usecase`, `:model`, `:repository` interface)
*   `:data`: データ実装 (`:repository` impl, `:local`, `:remote`)
*   `:build-logic`: ビルドロジック (Gradle Convention Plugins)
