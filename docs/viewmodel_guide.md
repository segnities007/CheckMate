# UiState\<T\> 実装・活用ガイド

この `UiState` の最大の特徴は、**「LoadingやFailureになっても、以前のDataを捨てずに持ち運べる」**
点です。これにより、画面のリロード時に「一瞬真っ白になる（ホワイトフラッシュ）」を防ぐことができます。

## 1\. 状態遷移のイメージ

このクラスを使うと、以下のようなデータフローが実現できます。

1. **初期状態**: `Idle(data=null)`
2. **初回ロード**: `Loading(data=null)`
3. **成功**: `Success(data="A")`
4. **リフレッシュ**: `Loading(data="A")` ← **ここが重要！ "A"を表示したままローディングを出せる**
5. **失敗**: `Failure(msg="Err", data="A")` ← **"A"を表示したままエラーを出せる**

-----

## 2\. ViewModelでの扱い方 (BaseViewModel)

`toLoading()` や `toSuccess()` を活用して、現在のデータを自動的に引き継ぐヘルパーを作成します。

```kotlin
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

abstract class BaseViewModel<S>(initialData: S? = null) : ViewModel() {

    // データの変更を監視するStateFlow
    private val _uiState = MutableStateFlow<UiState<S>>(UiState.Idle(initialData))
    val uiState: StateFlow<UiState<S>> = _uiState.asStateFlow()

    /**
     * 非同期処理を実行し、UiStateを自動更新するヘルパー
     */
    protected fun execute(
        block: suspend () -> S // S型（データ）を返す関数を受け取る
    ) {
        viewModelScope.launch {
            // 1. Loadingへ遷移（現在のdataを引き継ぐ）
            _uiState.update { it.toLoading() }

            try {
                // 2. 処理実行
                val result = block()

                // 3. Successへ遷移（新しいdataで更新）
                _uiState.update { it.toSuccess(result) }

            } catch (e: Exception) {
                // 4. Failureへ遷移（現在のdataを引き継ぎ、エラーメッセージを付与）
                _uiState.update { it.toFailure(e.message ?: "Unknown Error") }
            }
        }
    }

    // 手動更新用（部分更新など）
    protected fun updateState(reducer: UiState<S>.() -> UiState<S>) {
        _uiState.update(reducer)
    }
}
```

### 使用例 (FeatureViewModel)

```kotlin
class UserViewModel(private val repository: UserRepository) : BaseViewModel<User>() {

    init {
        loadUser()
    }

    fun loadUser() {
        // これだけで「Loading(保持) -> 処理 -> Success/Failure(保持)」が動く
        execute {
            repository.fetchUser()
        }
    }
}
```

-----

## 3\. Jetpack Compose での描画戦略

`UiState` を使う場合、従来の `when(state)` で画面全体を切り替えるのではなく、*
*「データがあれば表示し、状態に応じてオーバーレイを重ねる」** アプローチが推奨されます。

### ❌ 良くない例（データが消えるパターン）

```kotlin
// これだとLoadingのたびに中身が消えてしまう
when (state) {
    is UiState.Loading -> CircularProgressIndicator() // 前のデータが見えなくなる
    is UiState.Success -> UserContent(state.data)
    is UiState.Failure -> ErrorText(state.message)
}
```

### ✅ 推奨パターン（データを維持するパターン）

```kotlin
@Composable
fun UserScreen(viewModel: UserViewModel = hiltViewModel()) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    Box(modifier = Modifier.fillMaxSize()) {
        // ------------------------------------------------
        // 1. ベースコンテンツレイヤー (データがあれば常に表示)
        // ------------------------------------------------
        state.data?.let { data ->
            UserContent(user = data)
        }

        // ------------------------------------------------
        // 2. ステータスレイヤー (Loading / Error)
        // ------------------------------------------------
        when (state) {
            is UiState.Loading -> {
                // データが空なら全画面ローディング、あるなら上部にバー表示など
                if (state.data == null) {
                    CircularProgressIndicator(Modifier.align(Alignment.Center))
                } else {
                    LinearProgressIndicator(Modifier.fillMaxWidth().align(Alignment.TopCenter))
                }
            }
            is UiState.Failure -> {
                // エラーメッセージの表示
                val message = (state as UiState.Failure).message
                if (state.data == null) {
                    // データがないので全画面エラー
                    RetryButton(message) { viewModel.loadUser() }
                } else {
                    // データはあるのでSnackbarでエラー通知
                    ErrorSnackbar(message)
                }
            }
            else -> {} // Idle, Successは何もしない
        }
    }
}
```
