# CheckMate アーキテクチャガイド

> **AI Agent および開発者向けの設計原則・アーキテクチャガイドライン**

このドキュメントは、**CheckMate Android プロジェクト**のコード生成・修正時に**厳守すべき**設計原則とアーキテクチャガイドラインを定義します。

⚠️ **重要**: このガイドラインの違反は技術的負債を生み、長期的な保守性を損ないます。

---

## 📋 目次

1. [プロジェクト概要](#プロジェクト概要)
2. [アーキテクチャ原則](#アーキテクチャ原則)
3. [SOLID原則](#solid原則)
4. [レイヤー別実装ガイド](#レイヤー別実装ガイド)
5. [MVIパターン実装](#mviパターン実装)
6. [Dependency Injection](#dependency-injection)
7. [コード品質ルール](#コード品質ルール)
8. [禁止事項](#禁止事項)
9. [チェックリスト](#チェックリスト)

---

## プロジェクト概要

**CheckMate** は、持ち物管理とチェックリスト機能を提供するAndroidアプリケーションです。

### 技術スタック

| カテゴリ | 技術 |
|---------|------|
| 言語 | Kotlin 2.2.20 |
| UI | Jetpack Compose |
| アーキテクチャ | Clean Architecture + MVI |
| DI | Koin |
| 非同期処理 | Coroutines + Flow |
| データ永続化 | Room Database |
| ネットワーク | Ktor Client |
| 画像処理 | Coil 3 |
| 日時処理 | kotlinx-datetime |

### モジュール構造

```text
CheckMate/
├── app/                          # アプリケーションエントリーポイント
├── presentation/                 # UI層
│   ├── auth/                    # 認証画面
│   ├── dashboard/               # ダッシュボード
│   ├── home/                    # ホーム画面
│   ├── items/                   # アイテム管理
│   ├── templates/               # テンプレート管理
│   └── ...
├── domain/                      # ビジネスロジック層
│   ├── model/                   # Entity（ドメインモデル）
│   ├── repository/              # Repository Interface
│   └── usecase/                 # Use Case ⭐最重要
├── data/                        # データアクセス層
│   ├── local/                   # Room Database
│   ├── remote/                  # API通信
│   └── repository/              # Repository実装
├── core/                        # 共通機能
│   ├── common/                  # ユーティリティ + DIモジュール
│   ├── navigation/              # ナビゲーション
│   └── ui/                      # BaseViewModel等
└── widget/                      # Androidウィジェット
```

---

## アーキテクチャ原則

### Clean Architecture + MVI

本プロジェクトは**Clean Architecture**と**MVI (Model-View-Intent)**を組み合わせたアーキテクチャを採用しています。

```text
┌─────────────────────────────────────────────┐
│  Presentation Layer (UI層)                  │
│  • Composable (宣言的UI)                     │
│  • ViewModel (MVI状態管理)                   │
│  • Intent / State / Effect                  │
└──────────────┬──────────────────────────────┘
               ↓ Use Caseを使用
┌─────────────────────────────────────────────┐
│  Domain Layer (ビジネスロジック層)           │
│  • Entity (ドメインモデル)                   │
│  • Use Case (ビジネスロジック) ⭐           │
│  • Repository Interface                     │
└──────────────┬──────────────────────────────┘
               ↑ 実装
┌─────────────────────────────────────────────┐
│  Data Layer (データアクセス層)               │
│  • Repository実装                           │
│  • Room DAO / Entity                        │
│  • Ktor API Client                          │
└─────────────────────────────────────────────┘
```

### 依存関係の鉄則

#### ✅ 必ず守る

1. **依存は常に内側（Domain）に向かう**
2. **Presentation → Use Case → Repository の順序を厳守**
3. **Domain層は外側を知らない**（Presentation/Dataを参照しない）
4. **Domain層はAndroid SDKに依存しない**（kotlinx-datetimeは例外）
5. **1つのUse Case = 1つのビジネスアクション**（単一責任の原則）

#### ❌ 絶対禁止

1. **ViewModelから直接Repositoryを呼び出す** → 必ずUse Case経由
2. **内側のレイヤーが外側を参照する**
3. **Domain層でAndroid SDKを使用する**
4. **1つのUse Caseに複数の責任を持たせる**
5. **Presentation層にビジネスロジックを記述する**

---

## SOLID原則

### 1. 単一責任の原則 (SRP)

**各クラス/モジュールは1つの責任のみを持つ**

**特に重要: 1つのUse Case = 1つのビジネスアクション**

```kotlin
// ❌ 悪い例: 複数の責任
class UserManagerService {
    fun authenticate() { }
    fun getUserData() { }
    fun validateUser() { }
}

// ✅ 良い例: 単一責任
class AuthenticateUserUseCase(private val repository: UserRepository) {
    suspend operator fun invoke(credentials: Credentials): Result<User> {
        return repository.authenticate(credentials)
    }
}

class GetUserDataUseCase(private val repository: UserRepository) {
    suspend operator fun invoke(userId: Int): Result<User> {
        return repository.getUser(userId)
    }
}
```

### 2. 開放閉鎖の原則 (OCP)

**拡張に対して開き、修正に対して閉じる**

- 新機能追加時に既存コードを変更しない
- インターフェースと抽象クラスを活用

### 3. リスコフの置換原則 (LSP)

**派生クラスは基底クラスと置き換え可能**

- サブクラスが親クラスの契約を破らない

### 4. インターフェース分離の原則 (ISP)

**使用しないメソッドを含むインターフェースを強制しない**

- 大きなインターフェースを小さく分割

### 5. 依存性逆転の原則 (DIP)

**具象ではなく抽象に依存する**

- 上位レイヤーは下位レイヤーの実装に依存しない
- Repository InterfaceはDomain層に配置、実装はData層

### その他の重要原則

#### DRY (Don't Repeat Yourself)

- コードの重複を避ける
- 共通ロジックは関数/クラスに抽出

#### KISS (Keep It Simple, Stupid)

- シンプルに保つ
- 過度な抽象化を避ける

#### YAGNI (You Aren't Gonna Need It)

- 必要になるまで実装しない
- 現在の要件のみに集中

---

## レイヤー別実装ガイド

### Domain層（ビジネスロジック層）

#### 配置場所

- `domain/model/` : Entity
- `domain/usecase/` : Use Case
- `domain/repository/` : Repository Interface

#### 責務

- ビジネスルールの定義
- Entityの定義とドメインロジック
- **Use Caseによるビジネスロジックのカプセル化**
- Repository Interfaceの定義

#### ルール

- **Android SDKやライブラリに依存しない**（kotlinx-datetimeは例外）
- 純粋なビジネスロジックのみ
- DTOではなくDomain Modelを使用
- **Entityは`@Serializable`と`@Immutable`でマーク**
- **1つのUse Case = 1つのビジネスアクション**
- **Use Caseは`suspend fun`またはFlowを返す**

#### Entity実装例

```kotlin
// domain/model/Item.kt
package com.segnities007.model.item

import androidx.compose.runtime.Immutable
import kotlinx.serialization.Serializable
import kotlinx.datetime.Instant
import kotlinx.datetime.Clock

@Serializable
@Immutable
data class Item(
    val id: Int = 0,
    val name: String = "",
    val description: String = "",
    val category: ItemCategory = ItemCategory.OTHER_SUPPLIES,
    val imagePath: String = "",
    val createdAt: Instant = Clock.System.now(),
) {
    // ドメインロジック
    fun isValid(): Boolean = name.isNotBlank()
}
```

#### Repository Interface実装例

```kotlin
// domain/repository/ItemRepository.kt
package com.segnities007.repository

import com.segnities007.model.item.Item

interface ItemRepository {
    suspend fun getAllItems(): List<Item>
    suspend fun getItemById(id: Int): Item?
    suspend fun insertItem(item: Item)
    suspend fun deleteItem(id: Int)
}
```

#### Use Case実装例 ⭐

```kotlin
// domain/usecase/item/GetAllItemsUseCase.kt
package com.segnities007.usecase.item

import com.segnities007.model.item.Item
import com.segnities007.repository.ItemRepository

/**
 * 全てのアイテムを取得するUse Case
 * 単一責任: アイテム一覧の取得のみ
 */
class GetAllItemsUseCase(
    private val itemRepository: ItemRepository
) {
    suspend operator fun invoke(): Result<List<Item>> {
        return try {
            Result.success(itemRepository.getAllItems())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

/**
 * アイテムを追加するUse Case
 * ビジネスロジック: バリデーション + 保存
 */
class AddItemUseCase(
    private val itemRepository: ItemRepository
) {
    suspend operator fun invoke(item: Item): Result<Unit> {
        // バリデーション
        if (!item.isValid()) {
            return Result.failure(IllegalArgumentException("無効なアイテム"))
        }

        return try {
            itemRepository.insertItem(item)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
```

#### Use Case命名規則

| パターン | 用途 | 例 |
|---------|------|-----|
| `GetXxxUseCase` | データ取得 | `GetAllItemsUseCase` |
| `AddXxxUseCase` | データ追加 | `AddItemUseCase` |
| `UpdateXxxUseCase` | データ更新 | `UpdateItemUseCase` |
| `DeleteXxxUseCase` | データ削除 | `DeleteItemUseCase` |
| `ValidateXxxUseCase` | バリデーション | `ValidateItemUseCase` |
| `CalculateXxxUseCase` | 計算処理 | `CalculateCompletionRateUseCase` |

#### 重要な注意点

- **Use Caseは1つのビジネスアクションのみ実行**（単一責任の原則）
- **ViewModelはUse Caseを通じてビジネスロジックを実行**
- **RepositoryはUse Case内でのみ呼び出す**（ViewModelから直接呼ばない）
- **`operator fun invoke()`でUse Caseを関数のように呼び出し可能に**

---

### Data層（データアクセス層）

#### 配置場所

- `data/local/` : Room Database（DAO、Entity）
- `data/remote/` : Ktor API Client
- `data/repository/` : Repository Interface実装

#### 責務

- Repository Interfaceの実装
- 外部データソースとの通信
- Room EntityとDomain Modelの変換
- API DTOとDomain Modelの変換

#### ルール

- Domain層のInterfaceを実装
- Room Entity/API DTOは`data/local/entity`、`data/remote/dto`に配置
- Domain Modelへの変換は拡張関数（`toDomain()`）で実装
- Domain ModelからEntityへの変換は拡張関数（`toEntity()`）で実装

#### 実装例

```kotlin
// data/local/entity/ItemEntity.kt
package com.segnities007.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.segnities007.model.item.Item
import com.segnities007.model.item.ItemCategory

@Entity(tableName = "items")
data class ItemEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val description: String,
    val category: String,
    val imagePath: String,
    val createdAt: Long
)

// 変換拡張関数
fun ItemEntity.toDomain(): Item = Item(
    id = id,
    name = name,
    description = description,
    category = ItemCategory.valueOf(category),
    imagePath = imagePath,
    // ...
)

fun Item.toEntity(): ItemEntity = ItemEntity(
    id = id,
    name = name,
    description = description,
    category = category.name,
    imagePath = imagePath,
    // ...
)
```

```kotlin
// data/local/dao/ItemDao.kt
@Dao
interface ItemDao {
    @Query("SELECT * FROM items")
    suspend fun getAll(): List<ItemEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: ItemEntity)
}
```

```kotlin
// data/repository/ItemRepositoryImpl.kt
package com.segnities007.repository

import android.util.Log
import com.segnities007.local.dao.ItemDao
import com.segnities007.local.entity.toDomain
import com.segnities007.local.entity.toEntity
import com.segnities007.model.item.Item

class ItemRepositoryImpl(
    private val itemDao: ItemDao,
) : ItemRepository {
    override suspend fun getAllItems(): List<Item> {
        Log.d("ItemRepository", "getAllItems() called")
        return try {
            val entities = itemDao.getAll()
            Log.d("ItemRepository", "Fetched ${entities.size} items")
            entities.map { it.toDomain() }
        } catch (e: Exception) {
            Log.e("ItemRepository", "Failed to fetch items", e)
            throw e
        }
    }

    override suspend fun insertItem(item: Item) {
        Log.d("ItemRepository", "insertItem() called: ${item.name}")
        itemDao.insert(item.toEntity())
    }
}
```

---

### Presentation層（UI層）

#### 配置場所

- `presentation/<feature>/` : 各機能画面のモジュール
- `presentation/<feature>/mvi/` : ViewModel、Intent、State、Effect、Reducer
- `presentation/<feature>/components/` : 画面固有のComposable

#### 責務

- Jetpack Composeによる宣言的UI
- ユーザー入力の受付（Intent発行）
- MVIパターンによる状態管理（ViewModel）
- 画面ナビゲーション

#### ルール

- **ビジネスロジックを含まない**
- **ViewModelはUse Caseを通じてビジネスロジックを実行** ⭐
- **Repositoryを直接呼び出さない**（必ずUse Case経由）
- **BaseViewModelを継承**して実装
- **MVIパターンに厳密に従う**

---

## MVIパターン実装

### MVIアーキテクチャ構造

```text
┌─────────────────────────────────────────────┐
│  Composable (View)                          │
│  • state.collectAsStateWithLifecycle()     │
│  • viewModel.sendIntent(intent)             │
│  • effect.collect { ... }                   │
└──────────────┬──────────────────────────────┘
               ↓ sendIntent()
┌─────────────────────────────────────────────┐
│  BaseViewModel<Intent, State, Effect>       │
│  • handleIntent(): 画面固有のロジック        │
│  • setState(): State更新                     │
│  • sendEffect(): Effect発行                  │
└──────────────┬──────────────────────────────┘
               ↓ Use Case呼び出し ⭐
┌─────────────────────────────────────────────┐
│  Use Case (domain/usecase/)                 │
│  • ビジネスロジックのカプセル化               │
│  • 単一責任（1 Use Case = 1アクション）       │
└──────────────┬──────────────────────────────┘
               ↓ Repository呼び出し
┌─────────────────────────────────────────────┐
│  Repository Interface                       │
└─────────────────────────────────────────────┘
```

### BaseViewModel構造

**提供される機能:**

```kotlin
abstract class BaseViewModel<I : MviIntent, S : MviState, E : MviEffect>(
    initialState: S
) : ViewModel() {
    val state: StateFlow<S>          // UI監視用の状態
    val effect: Flow<E>               // 一度きりのイベント（Toast、ナビゲーション等）

    fun sendIntent(intent: I)        // Intentを送信
    protected fun setState(reducer: S.() -> S)  // State更新
    protected fun sendEffect(builder: () -> E)  // Effect発行
    protected abstract suspend fun handleIntent(intent: I)  // Intent処理（各画面で実装）
}
```

### Intent (ユーザーの意図)

**ルール:**

- `MviIntent`インターフェースを実装
- `sealed interface`で定義
- ユーザーアクションやライフサイクルイベントを表現
- データを持つ場合は`data class`、持たない場合は`data object`

**実装例:**

```kotlin
sealed interface DashboardIntent : MviIntent {
    data object LoadDashboardData : DashboardIntent
    data class SelectItem(val itemId: Int) : DashboardIntent
    data class UpdateFilter(val filter: String) : DashboardIntent
}
```

### State (アプリケーション状態)

**ルール:**

- `MviState`インターフェースを実装
- **イミュータブル（不変）** な`data class`として定義
- UI の描画に必要な全ての情報を含む
- `isLoading`, `error`などの共通フィールドを含める
- デフォルト値を設定

**実装例:**

```kotlin
data class DashboardState(
    val isLoading: Boolean = true,
    val itemCount: Int = 0,
    val templateCount: Int = 0,
    val uncheckedItemsToday: List<Item> = emptyList(),
    val completionRateToday: Int = 0,
    val error: String? = null
) : MviState
```

### Effect (一度きりのイベント)

**ルール:**

- `MviEffect`インターフェースを実装
- `sealed interface`で定義
- トースト表示、ナビゲーション、ダイアログ表示などの**一度きりの UI 操作**
- State には適さない一時的なイベント

**実装例:**

```kotlin
sealed interface DashboardEffect : MviEffect {
    data class ShowError(val message: String) : DashboardEffect
    data class NavigateToDetail(val itemId: Int) : DashboardEffect
    data object ShowSuccessToast : DashboardEffect
}
```

### Reducer (状態更新ロジック)

**ルール:**

- **純粋関数**として実装
- **副作用を含まない**（Repository 呼び出しなど不可）
- Intent を受け取り、新しい State を返す
- 同期的な状態変更のみ
- **ローディング状態の設定**などに使用

**実装例:**

```kotlin
class DashboardReducer {
    fun reduce(state: DashboardState, intent: DashboardIntent): DashboardState {
        return when (intent) {
            is DashboardIntent.LoadDashboardData ->
                state.copy(isLoading = true, error = null)
            is DashboardIntent.UpdateFilter ->
                state.copy(filter = intent.filter)
        }
    }
}
```

### ViewModel 実装パターン

**必須実装:**

```kotlin
class DashboardViewModel(
    private val getAllItemsUseCase: GetAllItemsUseCase,
    private val getAllTemplatesUseCase: GetAllTemplatesUseCase,
    private val getUncheckedItemsForTodayUseCase: GetUncheckedItemsForTodayUseCase,
) : BaseViewModel<DashboardIntent, DashboardState, DashboardEffect>(DashboardState()) {

    private val reducer = DashboardReducer()

    init {
        // 初期ロード
        sendIntent(DashboardIntent.LoadDashboardData)
    }

    override suspend fun handleIntent(intent: DashboardIntent) {
        when (intent) {
            is DashboardIntent.LoadDashboardData -> loadDashboardData()
            is DashboardIntent.SelectItem -> handleSelectItem(intent.itemId)
        }
    }

    private suspend fun loadDashboardData() {
        // 1. Reducerで同期的な状態更新（ローディング開始）
        setState { reducer.reduce(this, DashboardIntent.LoadDashboardData) }

        try {
            // 2. Use Caseを通じてビジネスロジック実行（副作用）⭐
            val items = getAllItemsUseCase()
            val templates = getAllTemplatesUseCase()
            val uncheckedItems = getUncheckedItemsForTodayUseCase()

            // 3. 成功時の状態更新
            setState {
                copy(
                    isLoading = false,
                    itemCount = items.size,
                    templateCount = templates.size,
                    uncheckedItemsToday = uncheckedItems
                )
            }
        } catch (e: Exception) {
            // 4. エラー時の状態更新とEffect発行
            setState { copy(isLoading = false, error = e.message) }
            sendEffect { DashboardEffect.ShowError(e.message ?: "Unknown error") }
        }
    }

    private suspend fun handleSelectItem(itemId: Int) {
        // Effect発行でナビゲーション
        sendEffect { DashboardEffect.NavigateToDetail(itemId) }
    }
}
```

### Composable（View）実装パターン

**実装ルール:**

- Koin で`koinViewModel()`を使用して ViewModel 取得
- `state.collectAsStateWithLifecycle()`で State 監視
- `LaunchedEffect`内で`effect.collect`して Effect 処理
- **State に基づいて宣言的に UI を構築**
- ユーザーアクションは`viewModel.sendIntent()`で送信

**実装例:**

```kotlin
@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel = koinViewModel(),
    onNavigateToDetail: (Int) -> Unit = {}
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    // Effect処理
    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is DashboardEffect.ShowError -> {
                    // Snackbar表示など
                }
                is DashboardEffect.NavigateToDetail -> {
                    onNavigateToDetail(effect.itemId)
                }
                is DashboardEffect.ShowSuccessToast -> {
                    // Toast表示
                }
            }
        }
    }

    // State-driven UI
    when {
        state.isLoading -> {
            LoadingIndicator()
        }
        state.error != null -> {
            ErrorView(message = state.error!!)
        }
        else -> {
            DashboardContent(
                itemCount = state.itemCount,
                onItemClick = { itemId ->
                    viewModel.sendIntent(DashboardIntent.SelectItem(itemId))
                }
            )
        }
    }
}
```

### MVI パターンのベストプラクティス

**DO（推奨）:**

- ✅ State は UI の完全な表現であること
- ✅ Intent は明確なユーザーアクションを表現
- ✅ Effect は一度きりのイベントに使用
- ✅ Reducer は純粋関数として実装
- ✅ ViewModel は Use Case を通じてビジネスロジックを実行 ⭐
- ✅ Use Case は単一責任（1 つのビジネスアクション）
- ✅ エラーハンドリングは必須

**DON'T（非推奨）:**

- ❌ Composable 内でビジネスロジックを記述
- ❌ State を Composable 内で直接変更
- ❌ Reducer 内で副作用（Repository 呼び出し等）
- ❌ Effect 使用時に State に同じ情報を保持
- ❌ ViewModel から View を直接操作
- ❌ **ViewModel から直接 Repository を呼び出す**（必ず Use Case を経由）⭐

## Dependency Injection (Koin)

このプロジェクトは**Koin**を DI フレームワークとして使用しています。

### Koin 設定ファイル構造

```text
app/src/main/java/
└── di/
    ├── AppModule.kt        # アプリケーション全体の依存関係
    ├── DatabaseModule.kt   # Room Database関連
    ├── RepositoryModule.kt # Repository実装
    ├── UseCaseModule.kt    # Use Case定義 ⭐NEW
    └── ViewModelModule.kt  # ViewModel定義
```

### モジュール定義パターン

**Use Case Module（新規）:**

```kotlin
val useCaseModule = module {
    // Item関連Use Case
    factory { GetAllItemsUseCase(get()) }
    factory { GetUncheckedItemsForTodayUseCase(get()) }
    factory { AddItemUseCase(get()) }
    factory { DeleteItemUseCase(get()) }

    // Template関連Use Case
    factory { GetAllTemplatesUseCase(get()) }
    factory { GetTemplateCountUseCase(get()) }
}
```

**ViewModel Module:**

```kotlin
val viewModelModule = module {
    viewModel { DashboardViewModel(get(), get(), get()) }  // Use Caseを注入
    viewModel { ItemsViewModel(get(), get()) }
}
```

**Repository Module:**

```kotlin
val repositoryModule = module {
    single<ItemRepository> { ItemRepositoryImpl(get(), get(), get()) }
    single<WeeklyTemplateRepository> { WeeklyTemplateRepositoryImpl(get()) }
}
```

**Database Module:**

```kotlin
val databaseModule = module {
    single {
        Room.databaseBuilder(
            androidContext(),
            AppDatabase::class.java,
            "checkmate_database"
        ).build()
    }
    single { get<AppDatabase>().itemDao() }
    single { get<AppDatabase>().weeklyTemplateDao() }
}
```

### Composable での使用方法

```kotlin
@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel = koinViewModel()
) {
    // ...
}
```

## デザインパターン適用ガイド

### 必ず使用すべきパターン

#### 1. Repository Pattern

- **使用場所:** Domain 層で Interface 定義、Data 層で実装
- **目的:** データソースの抽象化
- **実装:** 全てのデータアクセスは Repository を経由

#### 2. Use Case Pattern (Interactor) ⭐**最重要**

- **使用場所:** Domain 層（`domain/usecase/`）
- **目的:** ビジネスロジックのカプセル化、単一責任の実現
- **実装:** 1 つの Use Case = 1 つのビジネスアクション
- **命名:** `動詞 + 名詞 + UseCase` (例: `GetAllItemsUseCase`)

#### 3. Dependency Injection (Koin)

- **使用場所:** 全レイヤー
- **目的:** 疎結合の実現、テスト容易性向上
- **実装:** コンストラクタインジェクション、Koin のモジュール定義

#### 4. MVI Pattern

- **使用場所:** Presentation 層
- **目的:** 単方向データフロー、予測可能な状態管理
- **実装:** BaseViewModel を継承

### 推奨されるパターン

#### 5. Extension Functions

- **使用場所:** Entity 変換、共通ユーティリティ
- **例:** `ItemEntity.toDomain()`, `Item.toEntity()`
- **目的:** Kotlin idiomatic、コードの簡潔化

#### 6. Sealed Class/Interface

- **使用場所:** Intent、Effect、Result 型
- **目的:** 型安全な状態表現、網羅的な when 式

#### 7. Coroutines + Flow

- **使用場所:** 非同期処理、リアクティブストリーム
- **実装:** `suspend fun`、`StateFlow`、`Flow`

#### 8. Adapter Pattern

- **使用場所:** 外部ライブラリの統合
- **例:** Room Entity と Domain Model の変換
- **目的:** レイヤー間の分離

## コード品質ルール

### Kotlin 命名規則

- **クラス名:** PascalCase、名詞（例: `ItemRepository`, `DashboardViewModel`）
- **Use Case 名:** `動詞 + 名詞 + UseCase`（例: `GetAllItemsUseCase`, `AddItemUseCase`）
- **関数名:** camelCase、動詞（例: `getAllItems()`, `loadDashboardData()`）
- **プロパティ名:** camelCase（例: `isLoading`, `itemCount`）
- **定数:** UPPER_SNAKE_CASE（例: `MAX_RETRY_COUNT`）
- **パッケージ名:** 小文字、ドット区切り（例: `com.segnities007.model.item`）
- **インターフェース:** `I`プレフィックスなし（例: `ItemRepository`、`IItemRepository`ではない）
- **意味のある名前を使用** (`data`, `tmp`, `x` などは避ける)

### 関数/メソッドのルール

- **1 つの関数は 1 つのことをする**（単一責任）
- **引数は 3 つ以下が理想**（それ以上はデータクラスにまとめる）
- **純粋関数を優先** (副作用を避ける、特に Reducer)
- **深いネストを避ける** (早期リターン、ガード節推奨)
- **`suspend fun`は非同期処理にのみ使用**
- **拡張関数を活用**（変換ロジック、ユーティリティ）

#### 早期リターンパターンの活用

**深いネストを避けるため、早期リターン（ガード節）を積極的に使用してください。**

特に複数の`Result.fold()`や条件分岐がネストする場合、`getOrElse`と`return`を使用してフラットな構造にします。

**❌ 悪い例（3 階層のネスト）:**

```kotlin
private suspend fun selectDate(date: LocalDate) {
    getTemplatesForDayUseCase(date).fold(
        onSuccess = { templates ->
            getAllItemsUseCase().fold(
                onSuccess = { allItems ->
                    getCheckStatesUseCase(itemIds).fold(
                        onSuccess = { checkStates ->
                            // 処理
                        },
                        onFailure = { e ->
                            sendEffect { ShowError("チェック状態の取得に失敗") }
                        }
                    )
                },
                onFailure = { e ->
                    sendEffect { ShowError("アイテムの取得に失敗") }
                }
            )
        },
        onFailure = { e ->
            sendEffect { ShowError("テンプレートの取得に失敗") }
        }
    )
}
```

**✅ 良い例（早期リターンでフラット化）:**

```kotlin
private suspend fun selectDate(date: LocalDate) {
    // 早期リターンパターン
    val templates = getTemplatesForDayUseCase(date).getOrElse { e ->
        sendEffect { ShowError("テンプレートの取得に失敗") }
        return
    }

    val allItems = getAllItemsUseCase().getOrElse { e ->
        sendEffect { ShowError("アイテムの取得に失敗") }
        return
    }

    val checkStates = getCheckStatesUseCase(itemIds).getOrElse { e ->
        sendEffect { ShowError("チェック状態の取得に失敗") }
        return
    }

    // 成功時の処理（フラットな構造）
    setState { copy(templates = templates, items = allItems) }
}
```

**メリット:**

- 可読性の向上（ネストが減り、処理の流れが明確）
- 保守性の向上（各処理が独立して理解しやすい）
- バグの減少（エラーハンドリングの漏れを防ぐ）

**その他の良い例:**

```kotlin
// 単一責任、明確な命名
class GetAllItemsUseCase(private val repository: ItemRepository) {
    suspend operator fun invoke(): List<Item> {
        return repository.getAllItems()
    }
}

// 拡張関数で変換ロジック
fun ItemEntity.toDomain(): Item = Item(id, name, description)

// ガード節で条件チェック
fun validateItem(item: Item): Result<Unit> {
    if (item.name.isBlank()) {
        return Result.failure(IllegalArgumentException("名前は必須です"))
    }
    if (item.category == null) {
        return Result.failure(IllegalArgumentException("カテゴリは必須です"))
    }
    return Result.success(Unit)
}
```

**悪い例:**

```kotlin
// 複数の責任、副作用あり
fun doEverything(a: Int, b: String, c: Boolean, d: List<Item>, e: Repo) {
    // ローディング、データ取得、UI更新を全て実行
}

// 深いネスト
fun validateItem(item: Item): Result<Unit> {
    if (item.name.isNotBlank()) {
        if (item.category != null) {
            if (item.description.length <= 500) {
                return Result.success(Unit)
            } else {
                return Result.failure(IllegalArgumentException("説明が長すぎます"))
            }
        } else {
            return Result.failure(IllegalArgumentException("カテゴリは必須です"))
        }
    } else {
        return Result.failure(IllegalArgumentException("名前は必須です"))
    }
}
```

### クラス設計ルール

- **小さく保つ** (1 クラス 200 行以下を目安)
- **継承より合成を優先**
- **data class はイミュータブルに**（`val`のみ、`var`は避ける）
- **sealed class/interface で状態を型安全に表現**
- **companion object で定数や Factory 関数を定義**

**推奨:**

```kotlin
// Immutable data class
data class DashboardState(
    val isLoading: Boolean = true,
    val itemCount: Int = 0,
) : MviState

// Sealed interfaceで型安全な状態表現
sealed interface DashboardIntent : MviIntent {
    data object Load : DashboardIntent
    data class SelectItem(val id: Int) : DashboardIntent
}
```

### Kotlin イディオム

- **Null 安全性を活用** (`?.`, `?:`, `!!`の適切な使用)
- **スコープ関数を使用** (`let`, `apply`, `run`, `also`, `with`)
- **destructuring declarations を活用**
- **when 式で網羅性をチェック**（sealed class と組み合わせ）
- **標準ライブラリ関数を優先** (`map`, `filter`, `fold`等)

**例:**

```kotlin
// Null安全性
val name = item?.name ?: "Unknown"

// スコープ関数
val result = repository.getAllItems().map { it.toDomain() }

// when式の網羅性
when (intent) {
    is DashboardIntent.Load -> loadData()
    is DashboardIntent.SelectItem -> selectItem(intent.id)
} // sealed classなので全ケース網羅を強制
```

### ログ出力ルール（Log.d, Log.e など）

#### 基本方針

**ログは Data 層でのみ出力する**

ログ出力は問題の根本原因を特定するために、データアクセスレベルで行うべきです。

#### ✅ ログ出力が許可される場所

- **Data 層（`data/`）のみ:**
  - Repository 実装クラス
  - Room DAO の実装
  - Ktor API Client
  - データ変換処理（`toDomain()`, `toEntity()`）

**実装例:**

```kotlin
// ✅ Data層でのログ出力（推奨）
class ItemRepositoryImpl(
    private val itemDao: ItemDao,
) : ItemRepository {
    override suspend fun getAllItems(): List<Item> {
        Log.d("ItemRepository", "getAllItems() called")
        return try {
            val entities = itemDao.getAll()
            Log.d("ItemRepository", "Fetched ${entities.size} items from database")
            entities.map { it.toDomain() }
        } catch (e: Exception) {
            Log.e("ItemRepository", "Failed to fetch items", e)
            throw e
        }
    }
}
```

#### ❌ ログ出力が禁止される場所

- **Presentation 層（`presentation/`）:**

  - ViewModel
  - Composable（Screen、Component）
  - MVI（Intent、State、Effect、Reducer）

- **Domain 層（`domain/`）:**

  - Use Case
  - Entity（ドメインモデル）
  - Repository Interface

- **Core 層（`core/`）:**
  - 共通 UI コンポーネント
  - BaseViewModel

**理由:**

1. **関心の分離:** UI 層やビジネスロジック層はログ出力の責任を持たない
2. **デバッグ効率:** データアクセスレベルでのログのみで、問題の根本原因を特定できる
3. **パフォーマンス:** 不要なログ出力を削減
4. **Clean Architecture:** 各層の責務を明確に保つ

**悪い例:**

```kotlin
// ❌ Presentation層でのログ出力（禁止）
class DashboardViewModel(...) : BaseViewModel(...) {
    private suspend fun loadData() {
        Log.d("DashboardViewModel", "Loading data...")  // ❌ 削除すべき
        val items = getAllItemsUseCase()
        Log.d("DashboardViewModel", "Loaded ${items.size} items")  // ❌ 削除すべき
    }
}

// ❌ Domain層でのログ出力（禁止）
class GetAllItemsUseCase(private val repository: ItemRepository) {
    suspend operator fun invoke(): Result<List<Item>> {
        Log.d("GetAllItemsUseCase", "Executing...")  // ❌ 削除すべき
        return try {
            Result.success(repository.getAllItems())
        } catch (e: Exception) {
            Log.e("GetAllItemsUseCase", "Error", e)  // ❌ 削除すべき
            Result.failure(e)
        }
    }
}
```

### エラーハンドリング

- **try-catch は必要最小限に**（Repository 呼び出しなど）
- **具体的な例外メッセージ**
- **エラー状態を State で管理**
- **Effect でユーザーへの通知**
- **Kotlin の`Result`型を必要に応じて使用**

**実装例:**

```kotlin
private suspend fun loadDashboardData() {
    setState { copy(isLoading = true, error = null) }
    try {
        val items = getAllItemsUseCase()  // Use Case呼び出し
        setState { copy(isLoading = false, itemCount = items.size) }
    } catch (e: Exception) {
        val errorMessage = e.localizedMessage ?: "不明なエラーが発生しました"
        setState { copy(isLoading = false, error = errorMessage) }
        sendEffect { DashboardEffect.ShowError(errorMessage) }
    }
}
```

## テスタビリティ

### テスト可能なコード設計

- **依存性注入を使用**（Koin）
- **副作用を分離**（Use Case で副作用、Reducer は純粋関数）
- **純粋関数を優先**（Reducer は必須）
- **インターフェースで抽象化**（Repository Interface）
- **コンストラクタインジェクションでモック注入**

### テストの種類と対象

- **Unit Test:**

  - **Use Case（最も重要）** ⭐
  - Reducer（純粋関数）
  - Entity（ドメインロジック）
  - 拡張関数（変換ロジック）

- **Integration Test:**

  - Repository 実装
  - Room DAO
  - Ktor API Client

- **UI Test:**
  - Composable（State を注入してテスト）
  - ViewModel（モック Use Case を注入）

## 禁止事項

### 絶対にしてはいけないこと

❌ **Domain 層で Android SDK/フレームワークに依存**（kotlinx-datetime は例外）
❌ **内側のレイヤーが外側のレイヤーを参照**
❌ **ViewModel から直接 Repository を呼び出す**（必ず Use Case を経由）⭐
❌ **1 つの Use Case に複数の責任を持たせる**
❌ **Composable にビジネスロジックを記述**
❌ **State を mutable（`var`）で定義**
❌ **Reducer 内で副作用（Repository 呼び出し等）**
❌ **グローバル状態の乱用**
❌ **型の不適切なキャスト (`as`, `as?`の乱用)**
❌ **マジックナンバー/マジックストリング**
❌ **God Object (巨大な万能クラス)**
❌ **ViewModel で`Context`を直接保持**（AndroidViewModel 使用か、ApplicationContext のみ）

### 避けるべきアンチパターン

⚠️ **Anemic Domain Model** (ロジックのないエンティティ)
⚠️ **Fat ViewModel** (肥大化した ViewModel、200 行超えたら分割検討)
⚠️ **Fat Use Case** (複数の責任を持つ Use Case、分割検討)
⚠️ **Circular Dependency** (循環依存)
⚠️ **Deep Inheritance** (深い継承階層、3 階層まで)
⚠️ **Premature Optimization** (早すぎる最適化)
⚠️ **Composable 内で remember しない副作用**
⚠️ **State を LiveData や Observable で公開**（StateFlow 推奨）

## チェックリスト

コード作成・レビュー時に以下を確認してください:

### アーキテクチャ

- [ ] 各クラスは単一の責任のみを持っているか？
- [ ] 依存の方向は内側（Domain）に向いているか？
- [ ] Domain 層は Android SDK 非依存か（kotlinx-datetime 除く）？
- [ ] **Use Case は 1 つのビジネスアクションのみ実行しているか？** ⭐
- [ ] **ViewModel は Use Case を通じてビジネスロジックを実行しているか？** ⭐
- [ ] **ViewModel から直接 Repository を呼び出していないか？** ⭐
- [ ] Repository インターフェース（Domain）と実装（Data）が分離されているか？
- [ ] Use Case は`operator fun invoke()`を実装しているか？

### MVI 実装

- [ ] MVI パターンに厳密に従っているか？
- [ ] BaseViewModel を継承しているか？
- [ ] Intent、State、Effect が適切に定義されているか？
- [ ] State は`data class`でイミュータブル（`val`のみ）か？
- [ ] Reducer は純粋関数（副作用なし）か？
- [ ] Composable でビジネスロジックを実装していないか？
- [ ] `state.collectAsStateWithLifecycle()`を使用しているか？
- [ ] `effect.collect`を`LaunchedEffect`内で実行しているか？

### Kotlin/Compose

- [ ] DRY 原則に従っているか（重複がないか）？
- [ ] 命名は明確で理解しやすいか？
- [ ] Use Case の命名規則に従っているか（`動詞 + 名詞 + UseCase`）？
- [ ] Null 安全性を適切に活用しているか？
- [ ] 拡張関数で変換ロジックを実装しているか（`toDomain()`, `toEntity()`）？
- [ ] `sealed interface`で型安全な状態表現をしているか？
- [ ] `suspend fun`は非同期処理にのみ使用しているか？

### DI (Koin)

- [ ] Koin でモジュール定義しているか？
- [ ] Use Case Module を定義しているか？
- [ ] ViewModel は`koinViewModel()`で取得しているか？
- [ ] コンストラクタインジェクションを使用しているか？

### その他

- [ ] テスト可能な設計になっているか？
- [ ] Use Case の Unit Test を書いているか？
- [ ] エラーハンドリングは適切か（try-catch、Effect 発行）？
- [ ] 関数は小さく保たれているか（1 関数 50 行以下推奨）？
- [ ] クラスは小さく保たれているか（1 クラス 200 行以下推奨）？

## 参考資料

### アーキテクチャ

- Clean Architecture by Robert C. Martin
- SOLID Principles
- Domain-Driven Design by Eric Evans
- Use Case Pattern (Interactor Pattern)

### Android/Kotlin

- [Android Architecture Guide](https://developer.android.com/topic/architecture)
- [Jetpack Compose Documentation](https://developer.android.com/jetpack/compose)
- [Kotlin Coroutines Guide](https://kotlinlang.org/docs/coroutines-guide.html)
- [Koin Documentation](https://insert-koin.io/)

### MVI Pattern

- [MVI Architecture](https://www.youtube.com/watch?v=PXBXcHQeDLE)
- [Unidirectional Data Flow](https://developer.android.com/topic/architecture/ui-layer#udf)

---

## まとめ

このガイドラインは、CheckMateプロジェクトの**持続可能な開発**と**高品質なコード**を実現するために作成されました。

### 最重要事項 ⭐

1. **Use Caseパターンを必ず使用する**
2. **ViewModelから直接Repositoryを呼び出さない**
3. **1つのUse Case = 1つのビジネスアクション**
4. **依存は常に内側（Domain）に向かう**
5. **MVIパターンに厳密に従う**

これらの原則を守ることで、テスタブルで保守性の高いコードベースを維持できます。

---

**このドキュメントは必ず守ってください。設計原則の違反は技術的負債を生み、長期的な保守性を損ないます。**
