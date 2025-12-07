# CheckMate 依存関係注入 (DI) ガイドライン

このドキュメントは、CheckMate プロジェクトにおける **Koin** を用いた依存関係注入の実装ルールを定義します。
疎結合なアーキテクチャを維持し、テスト容易性を高めることを目的としています。

## 1. 基本方針

*   **Service Locator ではなく DI として使う**: クラス内で `by inject()` を使うのではなく、**コンストラクタ注入**を基本とします。
*   **モジュールの分割**: レイヤーや機能ごとにモジュールを分割し、管理しやすくします。

---

## 2. モジュール構成

`app/src/main/java/com/segnities007/checkmate/di/` 配下に以下のモジュールを定義します。

| モジュール名 | 役割 | 定義内容の例 |
| :--- | :--- | :--- |
| `remoteModule` | ネットワーク通信 | Retrofit, OkHttp, API Service |
| `databaseModule` | ローカルDB | Room Database, DAO |
| `repositoryModule` | データ層 | RepositoryImpl (Interfaceへの紐付け) |
| `useCaseModule` | ドメイン層 | UseCase クラス |
| `viewModelModule` | プレゼンテーション層 | ViewModel クラス |

### 実装例 (`repositoryModule.kt`)

インターフェースと実装クラスを紐付けます。

```kotlin
val repositoryModule = module {
    // single: シングルトン（アプリ内で1つ）
    single<UserRepository> { UserRepositoryImpl(get()) }
    
    // get() で依存関係を自動解決
    single<ItemRepository> { ItemRepositoryImpl(get(), get()) }
}
```

---

## 3. スコープ定義

Koin のスコープ関数を適切に使い分けます。

### 3.1 `single` (Singleton)
*   アプリの生存期間中、常に同じインスタンスを返します。
*   **用途**: Repository, Database, API Client, UseCase (ステートレスな場合)

### 3.2 `factory`
*   要求されるたびに新しいインスタンスを生成します。
*   **用途**: ステートフルな UseCase や、使い捨てのヘルパークラス（あまり使いません）。

### 3.3 `viewModel` / `factoryOf`
*   Android の ViewModel ライフサイクルに従います。
*   **用途**: ViewModel クラス。
*   **注意**: Compose Multiplatform / Koin Compose では `koinViewModel()` が使用されるため、Koin モジュール側の定義は `factory` (または `factoryOf`) で十分なケースが増えています。Android固有の `viewModel { }` DSL の代わりに、**`factoryOf`** の使用を推奨します。

```kotlin
val viewModelModule = module {
    // 推奨: Constructor DSL (factoryOf) を使うと記述が簡潔になります
    // ViewModelも純粋なクラスとしてファクトリー定義します
    factoryOf(::DashboardViewModel)
    
    // 従来の方法 (viewModelOf)
    // viewModelOf(::DashboardViewModel)
}
```

---

## 4. 注入方法 (Injection)

### 4.1 コンストラクタ注入 (推奨)
クラスのコンストラクタで依存関係を受け取ります。
テスト時にモックを差し込みやすいため、最も推奨されます。

```kotlin
class DashboardViewModel(
    private val getUserUseCase: GetUserUseCase, // ✅ コンストラクタで受け取る
    private val itemRepository: ItemRepository
) : BaseViewModel(...)
```

### 4.2 フィールド注入 (非推奨)
`by inject()` は、Activity/Fragment や Application クラスなど、コンストラクタ注入ができない Android コンポーネントでのみ使用します。

```kotlin
class MainActivity : ComponentActivity() {
    // ⚠️ Android Component以外では避ける
    private val viewModel: MainViewModel by viewModel()
}
```

---

## 5. テスト時の DI

テストコード（ユニットテスト）では、Koin を起動する必要はありません。
コンストラクタ注入を採用しているため、直接モックを渡してインスタンス化できます。

```kotlin
@Test
fun test() {
    val mockRepo = mockk<UserRepository>()
    // Koinを使わずに直接生成
    val viewModel = DashboardViewModel(mockRepo)
}
```
