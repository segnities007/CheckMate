# CheckMate Navigation 実装ガイドライン

このドキュメントは、CheckMateプロジェクトにおける画面遷移（Navigation3）の実装ルールを定義します。
新規画面追加やリファクタリングの際は、必ずこのガイドラインに従ってください。

---

## 0. ナビゲーションキー (NavKeys)

全てのナビゲーション先は `NavKeys` sealed interface に集約されています。
階層構造を持っており、機能ごとにグループ化されています。
新しい画面を追加する場合は、必ず `presentation/common/src/main/java/com/segnities007/common/keys/NavKeys.kt` にキーを追加してください。

```kotlin
@Serializable
sealed interface NavKeys : NavKey {
    @Serializable
    data object SplashKey : NavKeys

    @Serializable
    sealed interface Auth : NavKeys {
        @Serializable
        data object LoginKey : Auth
    }

    @Serializable
    sealed interface Hub : NavKeys {
        @Serializable
        data object HomeKey : Hub
        // ...
    }
}
```

---

## 1. モジュール公開ルール (Entry Provider Pattern)

各機能モジュール（`presentation/dashboard`など）は、Composable Screenを直接公開せず、**`EntryProviderScope` への拡張関数** を公開してください。

これにより、`NavKeys` と `Screen` の紐付け（マッピング）をモジュール内に隠蔽し、利用側（`app`モジュールなど）の実装をシンプルに保ちます。

### ❌ 非推奨: Screenを直接公開

`Screen` を直接公開すると、利用側が「どのキーでどの画面を表示するか」を知る必要があり、結合度が上がってしまいます。

```kotlin
// presentation/dashboard/DashboardScreen.kt
@Composable
fun DashboardScreen(...) { ... }

// app/Navigation.kt (利用側)
// ❌ 利用側がキー(NavKeys.Hub.DashboardKey)との紐付けを書かなければならない
entry<NavKeys.Hub.DashboardKey> {
    DashboardScreen(...)
}
```

### ✅ 推奨: Entry拡張関数を公開

`EntryProviderScope<NavKey>` の拡張関数を作成し、その中で `entry` 定義を行います。

```kotlin
// presentation/dashboard/DashboardEntry.kt
fun EntryProviderScope<NavKey>.dashboardEntry(
    onNavigate: (NavKeys) -> Unit,
    onBack: () -> Unit
) {
    // ✅ キーと画面の紐付けはここで完結させる
    entry<NavKeys.Hub.DashboardKey> {
        DashboardScreen(onNavigate = onNavigate)
    }
}

// app/Navigation.kt (利用側)
val entryProvider = entryProvider {
    // ✅ 利用側は関数を呼び出すだけ
    dashboardEntry(onNavigate = onNavigate, onBack = onBack)
    homeEntry(onNavigate = onNavigate, onBack = onBack)
}
```

---

## 2. 状態管理ルール (BackStack Management)

ナビゲーションの状態（バックスタック）は、Navigation3の `rememberNavBackStack` を使用して管理します。

*   **BackStack**: `rememberNavBackStack` を使用して `MutableList<NavKeys>` のような振る舞いをするバックスタックを生成します。
*   **Navigation**: `backStack.add(key)` で遷移し、`backStack.removeLast()` で戻ります。

```kotlin
// MainNavigation.kt
@Composable
fun MainNavigation() {
    val mainViewModel: MainViewModel = koinViewModel()
    // 初期画面を設定してバックスタックを生成
    val backStack = remember { mutableStateListOf<NavKeys>(NavKeys.SplashKey) }
    
    // 遷移操作を定義
    val onNavigate: (NavKeys) -> Unit = { backStack.add(it) }
    val onBack: () -> Unit = { backStack.removeLastOrNull() }

    // ... Effect handling ...

    NavDisplay(
        backStack = backStack,
        onBack = onBack,
        entryProvider = entryProvider,
    )
}
```

ViewModelでナビゲーションイベント（Intent/Effect）を処理する場合でも、最終的な遷移アクションはUI層（Composable）で `onNavigate` コールバックを通じて実行されます。

---

## 3. 階層構造とネスト (Nested Navigation)

基本的には `MainNavigation` がトップレベルの `NavDisplay` として機能し、主要な画面遷移を管理します。
ただし、特定の機能内（例：`TemplatesScreen` 内でのリスト⇔詳細遷移など）で完結する遷移については、その画面内で `NavDisplay` をネストして使用することも可能です。

*   **Global Navigation**: `MainNavigation` (Splash, Login, Home, Dashboard, Settings, etc.)
*   **Local Navigation**: 機能内部での細かい遷移（必要な場合のみ）

---

## 4. ホワイトフラッシュ対策

`NavDisplay` を使用する際は、遷移時のチラつき（ホワイトフラッシュ）を防ぐため、必要に応じて背景色を設定してください。

```kotlin
NavDisplay(
    backStack = ...,
    modifier = Modifier.background(MaterialTheme.colorScheme.background), // ✅ 推奨
    entryProvider = ...
)
```
