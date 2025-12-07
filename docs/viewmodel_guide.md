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

## 2. ViewModelでの扱い方 (BaseViewModel)

`BaseViewModel<I, S, E>` は、Intent (I), State (S), Effect (E) の 3 つの要素を持つ MVI アーキテクチャの基底クラスです。
内部で `StateMachine` を使用して状態遷移を管理します。

```kotlin
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
// ... imports

/**
 * MVIアーキテクチャの基底ViewModel
 * I: Intent (イベント)
 * S: State (状態データ)
 * E: Effect (副作用・単発イベント)
 */
abstract class BaseViewModel<I : MviIntent, S : MviState, E : MviEffect>(
    initialState: S
) : ViewModel() {

    private val stateMachine = StateMachine<S, E>(initialState, viewModelScope)

    // UIで監視するStateFlow (UiState<S> でラップされている)
    val uiState: StateFlow<UiState<S>> = stateMachine.uiState
    
    // 副作用（ナビゲーション、トーストなど）のFlow
    val effect = stateMachine.effect

    // 現在の状態データ（S）へのショートカット
    protected val currentState: S
        get() = uiState.value.data

    // Intentハンドラ（サブクラスで実装）
    abstract suspend fun handleIntent(intent: I)

    // Intent送信（UIから呼ばれる）
    fun sendIntent(intent: I) {
        viewModelScope.launch {
            handleIntent(intent)
        }
    }

    // Effect送信（ViewModel内部から呼ぶ）
    protected fun sendEffect(builder: () -> E) {
        stateMachine.sendEffect(builder)
    }

    // 非同期処理の実行と状態更新
    protected fun <T> execute(
        action: suspend () -> T,    // 実行する処理
        reducer: S.(T) -> S         // 結果を受け取ってStateを更新する処理
    ) {
        stateMachine.execute(action, reducer)
    }
    
    // 手動での状態更新
    protected fun setState(reducer: S.() -> S) {
        stateMachine.setState(reducer)
    }
}
```

### 使用例 (FeatureViewModel)

`handleIntent` を実装し、Intent に応じて処理を振り分けます。

```kotlin
class UserViewModel(
    private val getUserUseCase: GetUserUseCase
) : BaseViewModel<UserIntent, UserState, UserEffect>(UserState()) {

    // 初期化時にIntentを送信
    init {
        sendIntent(UserIntent.LoadUser)
    }

    // Intentのハンドリング
    override suspend fun handleIntent(intent: UserIntent) {
        when (intent) {
            is UserIntent.LoadUser -> loadUser()
            is UserIntent.Refresh -> loadUser()
        }
    }

    private fun loadUser() {
        // execute: Loading -> Action -> Success/Failure の自動遷移
        execute(
            action = { getUserUseCase().getOrThrow() },
            reducer = { user -> copy(user = user) }
        )
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
