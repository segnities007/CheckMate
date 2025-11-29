# CheckMate UseCase 実装ガイドライン

このドキュメントは、CheckMate プロジェクトにおける **UseCase (ドメイン層)** の実装ルールを定義します。
ビジネスロジックを再利用可能な単位でカプセル化し、ViewModel の肥大化を防ぐことを目的としています。

## 1. UseCase の役割

*   **単一責任**: 1つの UseCase は「1つのアクション」のみを担当します。
*   **再利用性**: 複数の ViewModel から同じロジックを呼び出せるようにします。
*   **純粋な Kotlin**: Android フレームワーク（Context, LiveDataなど）に依存してはいけません。

---

## 2. 命名規則

`動詞` + `対象(名詞)` + `UseCase` の形式とします。

*   `GetItemsUseCase`
*   `LoginUserUseCase`
*   `ValidateInputUseCase`

---

## 3. 実装ルール

### 3.1 `invoke` オペレーター
実行メソッドの名前は `invoke` に統一します。
これにより、クラスインスタンスを関数のように呼び出すことができます。

```kotlin
class GetItemsUseCase(
    private val repository: ItemRepository
) {
    // invoke を定義
    operator fun invoke(): Flow<List<Item>> {
        return repository.getItems()
            .map { items -> 
                // 必要ならここでフィルタリングや加工を行う
                items.filter { !it.isDeleted }
            }
    }
}
```

### 3.2 呼び出し方 (ViewModel)

```kotlin
class DashboardViewModel(
    private val getItemsUseCase: GetItemsUseCase // コンストラクタ注入
) : BaseViewModel(...) {

    fun load() {
        // 関数のように呼び出せる
        val itemsFlow = getItemsUseCase()
    }
}
```

---

## 4. UseCase を作る基準

すべての処理に UseCase を作る必要はありません（Over-Engineering になるため）。
以下の基準で判断してください。

### ✅ UseCase を作るべき場合
*   **ロジックが複雑**: 複数の Repository を組み合わせる、複雑な計算やフィルタリングがある。
*   **再利用される**: 複数の画面で同じロジックが必要。
*   **テストしたい**: ロジック単体でテストを書きたい。

### ❌ UseCase が不要な場合
*   **単純なプロキシ**: Repository のメソッドを右から左に流すだけの場合。
    *   この場合は ViewModel が直接 Repository を呼んで構いません。

---

## 5. ディレクトリ構成

`core/domain/src/main/java/com/segnities007/domain/usecase/` 配下に配置します。
機能ごとにパッケージを分けても構いません。
