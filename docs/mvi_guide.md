# CheckMate MVI 実装ガイドライン

このドキュメントは、CheckMate プロジェクトにおける **MVI (Model-View-Intent)** アーキテクチャの実装ルールを定義します。
本プロジェクトでは、用語の定義を以下のように定めます。

## 1. 用語定義

| 用語          | 旧名称          | 説明                                                                                                    |
|:------------|:-------------|:------------------------------------------------------------------------------------------------------|
| **Model**   | Screen Model | 画面で使用される情報を表すイミュータブルなデータクラス（例: `DashboardModel` / `DashboardState`）。読み込み状態は `UiState<T>` でラップして表現します。 |
| **UiState** | UiState      | 非同期データの読み込み状態（LCE）を管理するラッパー。`UiState<T>`。                                                             |
| **LCE 更新**  | -            | `UiState` の手動更新（MutableStateFlow を使った Loading/Success/Failure の更新パターン）。                               |

---

## 2. MVI アーキテクチャ概要

CheckMate では、以下の単方向データフローを採用しています。

- **Model**: 画面の唯一の状態。イミュータブル。
- **View (Composable)**: Model をレンダリングし、Intent を送信する。
- **Intent**: ユーザーのアクションやイベント。
- **Effect**: 一度きりのイベント（ナビゲーション、トーストなど）。

---

## 3. 実装ガイドライン (推奨・非推奨)

### 3.1 Model (画面で使用される情報)

# CheckMate — MVI 実装ガイドライン（簡潔版）

このガイドは CheckMate の MVI（Model-View-Intent）実装ルールを短くまとめたものです。

目的

- UI 側の状態管理を統一し、ViewModel ⇆ Use Case 間の責務を明確にすること。

用語（簡潔）

- Model: 画面表示に必要な純粋なデータ（`data class`）。例: `DashboardState`。
- `UiState<T>`: LCE（Idle/Loading/Success/Failure）を表すラッパーで、`val data: T?` を保持する。
- Intent: ユーザーの操作（`sealed interface`）。
- Effect: 一度きりの UI イベント（ナビゲーション、Toast等）。

基本方針

- Model は純粋データのみ（`val`）。読み込み状態は `UiState<Model>` で扱う。
- ViewModel は Use Case を呼び、`UiState` を更新する。ViewModel から直接 Repository を呼ばない。

短いコード例（ViewModel 側）

```kotlin
 // BaseViewModel 版の例（推奨）
class DashboardViewModel(
    private val repository: DashboardRepository
) : BaseViewModel<DashboardState>(DashboardState()) {

    private val _screenState =
        MutableStateFlow<UiState<DashboardState>>(UiState.Idle(DashboardState()))
    val screenState: StateFlow<UiState<DashboardState>> = _screenState


    // 非同期でデータ取得して UiState を自動で Loading/Success/Failure に遷移させる
    fun loadDashboard() {
        execute(
            action = { repository.loadDashboardModel() },
            reducer = { model ->
                // 'this' は現在の DashboardState。model を元に新しい State を返す
                copy(
                    isLoading = false,
                    itemCount = model.itemCount,
                    // 他のフィールドも model に合わせて更新
                )
            }
        )
    }

    // 同期的な UI 入力（トグルやフォーム）は Loading を出さず即時更新
    fun toggleFlag(enabled: Boolean) {
        updateState {
            copy(/* フィールド更新 */)
        }
    }
}
 ```

説明:

- `execute` は内部で `UiState.toLoading()` → `action()` 実行 → `toSuccess(newData)` /
  `toFailure(msg)` を行います。
- `updateState` は同期的に現在のデータを `copy` して `toSuccess(newData)` を書き込みます。

このパターンに揃えることで、ドキュメントに載せている "手動で MutableStateFlow を更新する"
サンプルが推奨しない古いやり方であることが明確になります。
val prev = _screenState.value
_screenState.value = prev.toLoading()
try {
val model = repository.loadDashboardModel()
_screenState.value = prev.toSuccess(model)
} catch (t: Throwable) {
_screenState.value = prev.toFailure(t.message ?: "エラー")
}
}

```

Composable 側の受け取り方（簡単）

```kotlin
@Composable
fun DashboardScreen(screenState: UiState<DashboardState>, onRetry: () -> Unit = {}) {
  when (screenState) {
    is UiState.Loading -> { /* プレースホルダ or 部分描画 */ }
    is UiState.Success -> { /* 完全表示: screenState.data!! */ }
    is UiState.Failure -> { /* エラー表示 + 以前データがあれば表示 */ }
    is UiState.Idle -> { /* 空状態 */ }
  }
}
```

実践メモ（短く）

- 既存の `setState { copy(...) }` を使う箇所は、`UiState` に移す場合 `updateData` などの拡張で置換すると簡単。
- エラー情報は `UiState.Failure(message: String)` を使う（Throwable を文字列化して渡す）。
- `StateMachine` の自動化はプロジェクトに存在しないため、手動で `toLoading()/toSuccess()/toFailure()`
  を使うパターンを推奨します。

チェックリスト

- [ ] Model は純粋データか（`val` のみ）
- [ ] ViewModel は Use Case 経由でデータを取得しているか
- [ ] UI には `UiState<T>` を渡しているか
- [ ] Reducer は副作用を持たない（副作用は `handleIntent` 等で扱う）

必要なら `presentation/dashboard` をサンプルとして移行するパッチを作ります。どちらが良いですか？