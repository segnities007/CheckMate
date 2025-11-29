# CheckMate デザインシステム & UI 実装ガイドライン

このドキュメントは、CheckMate プロジェクトにおける UI コンポーネントとデザインシステムの実装ルールを定義します。
一貫性のある美しい UI を効率的に構築することを目的としています。

## 1. 基本方針

*   **Material Design 3 (M3)**: 原則として M3 のコンポーネントとトークンを使用します。
*   **一貫性**: 色、タイポグラフィ、余白は定義された定数（Theme）を使用し、ハードコードしません。

---

## 2. テーマ (Theme) の使用

`MaterialTheme` オブジェクトを通じて、色や文字スタイルにアクセスします。

### 2.1 Color (色)
`Color.Red` などを直接使わず、`MaterialTheme.colorScheme` を使用します。

```kotlin
// ❌ 悪い例
Text(color = Color.Black)

// ✅ 良い例
Text(color = MaterialTheme.colorScheme.onBackground)
Text(color = MaterialTheme.colorScheme.primary)
```

### 2.2 Typography (文字)
`fontSize = 20.sp` などを直接指定せず、`MaterialTheme.typography` を使用します。

```kotlin
// ❌ 悪い例
Text(fontSize = 24.sp, fontWeight = FontWeight.Bold)

// ✅ 良い例
Text(style = MaterialTheme.typography.headlineMedium)
```

### 2.3 Spacing (余白)
余白の値も統一するために、定数クラス（例: `Dimens`）の使用を推奨します。

```kotlin
Spacer(modifier = Modifier.height(Dimens.Spacing.medium)) // 16.dp
```

---

## 3. コンポーネント設計

### 3.1 コンポーネントの粒度
再利用可能な部品は `core/ui/src/main/java/com/segnities007/ui/components/` に配置します。

*   **Atoms (最小単位)**: ボタン、アイコン、ラベル
*   **Molecules (複合)**: リストアイテム、入力フォーム群
*   **Organisms (機能)**: ヘッダー、カードリスト

### 3.2 引数の設計 (Slot API)
コンポーネントの柔軟性を高めるため、子要素を `composable lambda` (Slot) として受け取る設計を推奨します。

```kotlin
@Composable
fun CheckMateCard(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit // ✅ Slot API
) {
    Card(onClick = onClick, modifier = modifier) {
        Column(modifier = Modifier.padding(16.dp)) {
            content()
        }
    }
}
```

### 3.3 Modifier の扱い
すべてのカスタムコンポーネントは、**第一引数（または必須引数の直後）に `modifier: Modifier = Modifier` を受け取る**ようにしてください。
これにより、呼び出し元でレイアウト調整が可能になります。

```kotlin
@Composable
fun MyButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier // ✅ 必須
) {
    Button(
        onClick = onClick,
        modifier = modifier // ✅ 内部のルート要素に適用
    ) {
        Text(text)
    }
}
```

---

## 4. プレビュー (Preview)

コンポーネントを作成したら、必ず `@Preview` を作成して確認します。
`Light` / `Dark` モードの両方を確認することを推奨します。

```kotlin
@Preview(name = "Light Mode", showBackground = true)
@Preview(name = "Dark Mode", uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Composable
fun MyButtonPreview() {
    CheckMateTheme {
        MyButton(text = "Button", onClick = {})
    }
}
```
