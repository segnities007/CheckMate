# CheckMate Repository 実装ガイドライン

このドキュメントは、CheckMate プロジェクトにおける **Repository (データ層)** の実装ルールを定義します。
データの取得・保存・同期を一元管理し、ドメイン層やプレゼンテーション層からデータの詳細（通信、DBなど）を隠蔽することを目的としています。

## 1. Repository の役割

*   **Single Source of Truth (SSOT)**: アプリ内のデータへの唯一のアクセスポイントとなります。
*   **データの隠蔽**: データがネットワークから来るのか、ローカルDBから来るのかを隠蔽します。
*   **データの同期**: リモートデータとローカルキャッシュの同期を管理します。

---

## 2. インターフェース定義

Repository は必ずインターフェースを定義し、実装クラス (`Impl`) と分離します。
これにより、テスト時のモック化や実装の差し替えが容易になります。

```kotlin
interface ItemRepository {
    // データの監視には Flow を使用
    fun getItems(): Flow<List<Item>>
    
    // 単発の操作は suspend fun
    suspend fun refreshItems()
    suspend fun addItem(item: Item)
}
```

---

## 3. 実装パターン

### 3.1 Flow によるデータ公開
Room などのローカルDBを SSOT とし、`Flow` を使って常に最新のデータを流し続けます。

```kotlin
class ItemRepositoryImpl(
    private val itemDao: ItemDao,
    private val apiService: ApiService
) : ItemRepository {

    // ローカルDBを監視してそのまま流す
    override fun getItems(): Flow<List<Item>> {
        return itemDao.getAllItems().map { entities ->
            entities.map { it.toDomainModel() }
        }
    }
}
```

### 3.2 リモートデータの同期
`refresh` メソッドなどで API からデータを取得し、ローカルDBに保存します。
UI は `getItems()` の Flow を監視しているため、DB が更新されると自動的に UI も更新されます。

```kotlin
override suspend fun refreshItems() {
    // IOスレッドで実行
    withContext(Dispatchers.IO) {
        val remoteItems = apiService.fetchItems()
        itemDao.insertAll(remoteItems.map { it.toEntity() })
    }
}
```

---

## 4. エラーハンドリング

*   **Flow**: データ取得の Flow 自体は、原則としてエラーで停止させません（空リストを返すか、前回のキャッシュを維持）。
*   **Suspend Functions**: ネットワークエラーなどの例外は、そのままスローして構いません。
    *   呼び出し元の ViewModel (`StateMachine`) が `try-catch` でハンドリングし、`UiState.Failure` に変換します。

---

## 5. DTO とドメインモデル

*   **Entity / DTO**: API レスポンスや DB テーブルに対応するデータクラス。
*   **Domain Model**: アプリ内で使用する純粋なデータクラス。

Repository 層でこれらを相互変換 (`Mapper`) し、外側には常に **Domain Model** だけを公開してください。

```kotlin
// ❌ Entityをそのまま返さない
fun getItems(): Flow<List<ItemEntity>>

// ✅ Domain Modelを返す
fun getItems(): Flow<List<Item>>
```
