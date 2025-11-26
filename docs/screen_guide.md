# CheckMate Screen 実装ガイドライン

このドキュメントは、CheckMateプロジェクトにおける画面（Screen / Composable）の実装ルールを定義します。
MVIアーキテクチャに基づき、UIとロジックを適切に分離するための指針です。

---

## 1. Screen構成の基本パターン

各画面は、**StatefulなScreen** と **StatelessなContent** の2層構造で実装することを推奨します。

### ✅ 推奨構造

1. **XxxScreen (Stateful)**: ViewModelと結合し、Stateの監視、Effectの処理、Intentの送信を行う。
2. **XxxContent (Stateless)**: UI描画のみに専念し、データとコールバックを引数で受け取る。Preview可能にする。

```kotlin
// presentation/dashboard/DashboardScreen.kt

// 1. Stateful Screen (Entry Point)
@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel = koinViewModel(),
    onNavigate: (NavKey) -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

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

    // Contentへ委譲
    DashboardContent(
        state = state,
        onIntent = viewModel::sendIntent
    )
}

// 2. Stateless Content (UI Logic Only)
@Composable
fun DashboardContent(
    state: DashboardState,
    onIntent: (DashboardIntent) -> Unit
) {
    Scaffold(...) { padding ->
        if (state.isLoading) {
            LoadingIndicator()
        } else {
            LazyColumn(...) {
                items(state.items) { item ->
                    ItemRow(
                        item = item,
                        onClick = { onIntent(DashboardIntent.SelectItem(item.id)) }
                    )
                }
            }
        }
    }
}
```

---

## 2. MVI連携ルール

### Stateの監視

* `collectAsStateWithLifecycle()` を使用して、ライフサイクルを考慮したState監視を行ってください。
* Stateは不変（Immutable）なデータクラスとして定義されている前提です。

### Effectの処理

* `LaunchedEffect(Unit)` 内で `viewModel.effect.collect` を行います。
* ナビゲーションやトースト表示などの「一度きりのイベント」はここで処理します。

### Intentの送信

* ユーザーアクション（クリック、入力など）は全て `Intent` に変換して ViewModel へ送信します。
* UI側でロジックを持たず、`onIntent(DashboardIntent.Xxx)` のように委譲します。

---

## 3. Previewの実装

Statelessな `Content` Composable に対して Preview を作成します。
`PreviewParameterProvider` を使用して、様々な状態（ローディング中、エラー、データあり）をテストすることを推奨します。

```kotlin
@Preview
@Composable
private fun DashboardContentPreview() {
    CheckMateTheme {
        DashboardContent(
            state = DashboardState(
                isLoading = false,
                items = listOf(Item(name = "Test Item"))
            ),
            onIntent = {}
        )
    }
}
```

---

## 4. 禁止事項

### ❌ ViewModelをContentに渡さない

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
    state: DashboardState,
    onIntent: (DashboardIntent) -> Unit
) {
    ...
}
```

### ❌ UIでビジネスロジックを書かない

データのフィルタリングや計算は ViewModel (or Use Case) で行い、UI は加工済みの State を表示するだけにしてください。

### ❌ 巨大なComposableを作らない

画面が複雑になる場合は、部品ごとにファイルを分割するか、小さな Composable 関数に切り出してください。

---

## まとめ

* **Stateful (Screen)** と **Stateless (Content)** を分離する。
* **MVI** (State, Intent, Effect) のフローを厳守する。
* **Preview** を活用してUI開発効率を上げる。
