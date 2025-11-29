# CheckMate Navigation 実装ガイドライン

このドキュメントは、CheckMateプロジェクトにおける画面遷移（Navigation3）の実装ルールを定義します。
新規画面追加やリファクタリングの際は、必ずこのガイドラインに従ってください。

---

## 0. ナビゲーションキー (NavKey)

全てのナビゲーション先は `NavKey` sealed interface に集約されています。
`Route` や `AuthRoute` などの個別のインターフェースは廃止されました。
新しい画面を追加する場合は、必ず `core/navigation/src/main/java/com/segnities007/navigation/NavKey.kt` にキーを追加してください。

```kotlin
@Serializable
sealed interface NavKey {
    @Serializable
    data object Home : NavKey
    // ...
}
```

---

## 1. モジュール公開ルール (Entry Provider Pattern)

各機能モジュール（`presentation/dashboard`など）は、Composable Screenを直接公開せず、**`EntryProviderScope` への拡張関数** を公開してください。

これにより、`NavKey` と `Screen` の紐付け（マッピング）をモジュール内に隠蔽し、利用側（`app`モジュールなど）の実装をシンプルに保ちます。

### ❌ 非推奨: Screenを直接公開

`Screen` を直接公開すると、利用側が「どのキーでどの画面を表示するか」を知る必要があり、結合度が上がってしまいます。

```kotlin
// presentation/dashboard/DashboardScreen.kt
@Composable
fun DashboardScreen(...) { ... }

// app/Navigation.kt (利用側)
// ❌ 利用側がキー(NavKey.Dashboard)との紐付けを書かなければならない
entry<NavKey.Dashboard> {
    DashboardScreen(...)
}
```

### ✅ 推奨: Entry拡張関数を公開

`EntryProviderScope<NavKey>` の拡張関数を作成し、その中で `entry` 定義を行います。

```kotlin
// presentation/dashboard/DashboardEntry.kt
fun EntryProviderScope<NavKey>.dashboardEntry(
    onNavigate: (NavKey) -> Unit
) {
    // ✅ キーと画面の紐付けはここで完結させる
    entry<NavKey.Dashboard> {
        DashboardScreen(onNavigate = onNavigate)
    }
}

// app/Navigation.kt (利用側)
val provider = entryProvider {
    // ✅ 利用側は関数を呼び出すだけ
    dashboardEntry(onNavigate = ...)
    homeEntry(onNavigate = ...)
}
```

---

## 2. 状態管理ルール (State-Driven)

ナビゲーションの状態（現在の画面、バックスタック）は、必ず **ViewModel** で管理してください。
`NavController` のようなオブジェクトを UI レイヤーで回してはいけません。

*   **ViewModel**: `currentRoute` (NavKey) を State として保持する。
*   **UI**: `NavDisplay` は State を監視して描画するだけ。

```kotlin
// ✅ 良い例
NavDisplay(
    currentEntry = state.currentRoute,
    entryProvider = ...
)
```

---

## 3. 階層構造ルール (Nested NavDisplay)

アプリ全体を1つの `NavDisplay` で管理せず、機能ごとに `NavDisplay` をネストさせてください。

*   **Root**: `MainActivity` (Splash, Auth, Hub)
*   **Hub**: `HubScreen` (Dashboard, Home, Items, Templates)
*   **Feature**: `ItemsScreen` (List -> Detail)

各レイヤーは、自分の責任範囲の遷移のみを管理します。

---

## 4. ホワイトフラッシュ対策

`NavDisplay` を使用する際は、遷移時のチラつき（ホワイトフラッシュ）を防ぐため、必ず背景色を設定してください。

```kotlin
NavDisplay(
    currentEntry = ...,
    modifier = Modifier.background(MaterialTheme.colorScheme.background), // ✅ 必須
    entryProvider = ...
)
```
