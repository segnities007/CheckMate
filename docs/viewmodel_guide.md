# ViewModel 実装ガイドライン

このドキュメントは、CheckMate プロジェクトにおける **ViewModel** の実装ルールとベストプラクティスを定義します。
ViewModel は UI ロジックの中心であり、MVI アーキテクチャにおける **Model (State)** の管理と **Intent** の処理を担当します。

## 1. ViewModel の役割

ViewModel の主な責務は以下の通りです。

1.  **状態 (State) の保持と公開**: 画面の Source of Truth として、UI に最新の状態を提供します。
2.  **ナビゲーション状態の管理**: Navigation3 を採用しているため、**現在の画面 (`currentRoute`) や遷移ロジックは ViewModel が管理します**。
3.  **ビジネスロジックの実行**: Repository や UseCase を呼び出し、データの取得や加工を行います。
4.  **副作用 (Effect) の送信**: トースト表示などの一時的なイベントを UI に通知します。

---

## 2. 基本実装 (BaseViewModel)

本プロジェクトでは、共通の振る舞いを提供する `BaseViewModel<I, S, E>` を継承して実装します。
内部では `StateMachine` クラスを使用して、状態遷移と副作用の管理を行っています。

### 2.1 クラス定義

Intent, State, Effect の型パラメータを指定します。

```kotlin
class MyViewModel(
    private val repository: MyRepository
) : BaseViewModel<MyIntent, MyState, MyEffect>(MyState()) {

    override suspend fun handleIntent(intent: MyIntent) {
        when (intent) {
            is MyIntent.LoadData -> loadData()
        }
    }
}
```

### 2.2 データの読み込みと更新 (`execute`)

非同期処理（API 通信など）を行い、その結果に基づいて State を更新する場合は `execute` メソッドを使用します。
これは **LCE (Loading / Content / Error)** パターンを自動的に適用します。

```kotlin
fun loadData() {
    // 1. Loading 状態へ遷移 (以前のデータは保持)
    // 2. repository.getData() を実行
    // 3. 成功なら Success、失敗なら Failure へ遷移
    execute(
        action = { repository.getData() },
        reducer = { data -> copy(items = data) }
    )
}
```

### 2.3 同期的な更新 (`setState`)

ユーザー入力や**ナビゲーション**など、非同期処理を伴わない更新には `setState` を使用します。
Loading 状態を経由せず、即座に State を書き換えます。

```kotlin
// ナビゲーションの例
fun onDetailRequested(id: String) {
    setState {
        copy(currentRoute = NavKey.Detail(id))
    }
}
```

---

## 3. 状態管理 (UiState)

ViewModel は `UiState<S>` 型で State を公開します。
`UiState` は以下の4つの状態を持つステートマシンです。
**すべての状態で `data: S` (Non-nullable) を保持します。**

*   **Idle**: 初期状態。
*   **Loading**: 読み込み中。**以前のデータを保持します**。
*   **Success**: 成功。
*   **Failure**: 失敗。エラーメッセージを持ち、**以前のデータを保持します**。

この仕組みにより、リフレッシュ時などに画面が真っ白になることを防ぎます。

---

## 4. 副作用 (Effect) の処理

`BaseViewModel` には Effect 管理機能が組み込まれています。
`sendEffect` メソッドを使用して Effect を送信します。

```kotlin
fun onErrorOccurred() {
    sendEffect { MyEffect.ShowSnackbar("エラーが発生しました") }
}
```

---

## 5. ベストプラクティス

### ✅ 推奨事項
*   **execute の活用**: 非同期処理は可能な限り `execute` を使って、Loading/Error 状態の管理を統一してください。
*   **ナビゲーション**: ナビゲーションは `setState` で `currentRoute` を更新することで行います。

### ❌ 禁止事項
*   **UiState の手動生成**: 基本的に `execute` や `setState` を通じて `UiState` を更新してください。手動で `UiState.Loading` などを生成する必要はありません。

---

## 6. テスト

ViewModel のテストは、入力（メソッド呼び出し）に対する出力（State の変化）を検証します。

```kotlin
@Test
fun `loadData updates state correctly`() = runTest {
    // Arrange
    val viewModel = MyViewModel(mockRepository)

    // Act
    viewModel.sendIntent(MyIntent.LoadData)

    // Assert
    val state = viewModel.uiState.value
    assert(state is UiState.Success)
    assertEquals(expectedData, state.data.items)
}
```
