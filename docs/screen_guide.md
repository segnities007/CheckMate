# CheckMate Screen 実装ガイドライン

このドキュメントは、CheckMate プロジェクトにおける画面（Screen / Composable）の実装ルールを定義します。
MVI アーキテクチャに基づき、UI とロジックを適切に分離するための指針です。

---

## 1. Screen 構成の基本パターン

各画面は、**Stateful な Screen** と **Stateless な Content** の 2 層構造で実装することを推奨します。

### ✅ 推奨構造

1. **XxxScreen (Stateful)**: ViewModel と結合し、State の監視、Effect の処理、Intent の送信を行う。
2. **XxxContent (Stateless)**: UI 描画のみに専念し、データとコールバックを引数で受け取る。Preview
   可能にする。

```kotlin
// presentation/dashboard/DashboardScreen.kt

// 1. Stateful Screen (Entry Point)
@Composable
fun DashboardScreen(
    onNavigate: (NavKey) -> Unit
) {
    val viewModel: DashboardViewModel = koinViewModel()
    // BaseViewModelのuiStateを監視
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // Effect処理
    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is DashboardEffect.NavigateToDetail -> onNavigate(NavKey.ItemDetail(effect.itemId))
                is DashboardEffect.ShowError -> { /* Snackbar表示など */
                }
            }
        }
    }

    // uiStateからデータを取得してContentへ委譲
    // dataがnullの場合のハンドリングもここで行うのが一般的（あるいはContent内でLoading表示）
    DashboardContent(
        uiState = uiState,
        onIntent = viewModel::sendIntent
    )
}

// 2. Stateless Content (UI Logic Only)
@Composable
fun DashboardContent(
    uiState: UiState<DashboardState>,
    onIntent: (DashboardIntent) -> Unit
) {
    Scaffold(...) { padding ->
        // UiStateに応じて表示を切り替える
        when (uiState) {
            is UiState.Loading -> {
                // データがあればコンテンツを表示しつつ、上部にローディングバーなどを出す
                if (uiState.data != null) {
                    DashboardList(items = uiState.data.items, onIntent = onIntent)
                    LinearProgressIndicator(...)
                } else {
                    LoadingIndicator()
                }
            }
            is UiState.Success -> {
                DashboardList(items = uiState.data.items, onIntent = onIntent)
            }
            is UiState.Failure -> {
                // エラー表示
            }
            else -> {}
        }
    }
}
```

---

## 2. MVI 連携ルール

### State の監視

- `collectAsStateWithLifecycle()` を使用して、ライフサイクルを考慮した State 監視を行ってください。
- State は不変（Immutable）なデータクラスとして定義されている前提です。

### Effect の処理

- `LaunchedEffect(Unit)` 内で `viewModel.effect.collect` を行います。
- ナビゲーションやトースト表示などの「一度きりのイベント」はここで処理します。

### Intent の送信

- ユーザーアクション（クリック、入力など）は全て `Intent` に変換して ViewModel へ送信します。
- UI 側でロジックを持たず、`onIntent(DashboardIntent.Xxx)` のように委譲します。

---

## 3. Preview の実装

Stateless な `Content` Composable に対して Preview を作成します。
`PreviewParameterProvider` を使用して、様々な状態（ローディング中、エラー、データあり）をテストすることを推奨します。

```kotlin
@Preview
@Composable
private fun DashboardContentPreview() {
    CheckMateTheme {
        DashboardContent(
            uiState = UiState.Success(
                DashboardState(
                    isLoading = false,
                    items = listOf(Item(name = "Test Item"))
                )
            ),
            onIntent = {}
        )
    }
}
```

---

## 4. 禁止事項

### ❌ ViewModel を Content に渡さない

`Content` Composable に ViewModel を渡すと、プレビューが困難になり、再利用性が下がります。
必ず State と コールバック関数（または Intent 送信関数）を渡してください。

```kotlin
// ❌ 悪い例
@Composable
fun DashboardContent(viewModel: DashboardViewModel) {
    ...
}

// ✅ 良い例
@Composable
fun DashboardContent(
    uiState: UiState<DashboardState>,
    onIntent: (DashboardIntent) -> Unit
) {
    ...
}
```

### ❌ UI でビジネスロジックを書かない

データのフィルタリングや計算は ViewModel (or Use Case) で行い、UI は加工済みの State を表示するだけにしてください。

### ❌ 巨大な Composable を作らない

画面が複雑になる場合は、部品ごとにファイルを分割するか、小さな Composable 関数に切り出してください。