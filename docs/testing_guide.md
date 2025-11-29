# テスト実装ガイドライン (Gherkin スタイル)

このドキュメントは、CheckMate プロジェクトにおけるテストコードの記述スタイル、特に **Gherkin (Given-When-Then)** 記法を用いた実装ルールを定義します。
可読性が高く、仕様書としても機能するテストを書くことを目的としています。

## 1. Gherkin (Given-When-Then) とは

テストシナリオを「前提」「操作」「結果」の3段階に分けて記述するスタイルです。

*   **Given (前提)**: テストを実行するための初期状態や環境設定。**具体的なコンテキスト**を記述する。
*   **When (もし)**: テスト対象のアクション（メソッド呼び出し、イベント送信）。
*   **Then (ならば)**: 期待される結果（戻り値、状態変化、副作用）。

## 2. 命名規則

Kotlin ではバッククォート ``` を使うことで、メソッド名にスペースを含めることができます。
テストコードの可読性を最大化するため、**バッククォートを使用した英語での記述**を推奨します。

### ✅ 良い命名（具体的なコンテキスト）

形式: `Given [specific context] When [action] Then [expected result]`

**重要**: `Given initial state` のような汎用的な表現は避け、**テストの具体的な前提条件**を記述します。

#### 良い例
*   `Given BottomSheet is hidden When user clicks FAB Then BottomSheet is displayed`
*   `Given 3 templates registered When searching for "test" Then only 1 item is shown`
*   `Given item is unchecked When user taps checkbox Then item becomes checked`
*   `Given network error When loading data Then error message is displayed`

#### ❌ 避けるべき例
*   `Given initial state When UpdateIsShowBottomSheet Then isShowBottomSheet becomes true` 
    - 「initial state」is too generic
    - Contains implementation details (UpdateIsShowBottomSheet)

*   `Given initial state When loading data Then State becomes Success`
    - Unclear what data
    - Unclear what situation

### ✅ リファクタリング例

#### Before (❌)
```kotlin
@Test
fun `Given initial state When UpdateIsShowBottomSheet(true) Then isShowBottomSheet becomes true`()
```

#### After (✅)
```kotlin
@Test
fun `Given BottomSheet is hidden When user clicks FAB Then BottomSheet is displayed`()
```

---

## 3. 実装フォーマット

テストメソッド内部でも、コメントを使って3つのセクションを明確に区切ります。

```kotlin
@Test
fun `Given 未認証ユーザー When ログインボタンをタップ Then ログイン画面に遷移する`() = runTest {
    // Given (前提: ユーザーが未認証)
    val mockAuth = mockk<AuthRepository>()
    coEvery { mockAuth.isAuthenticated() } returns false
    val viewModel = LoginViewModel(mockAuth)

    // When (操作: ログインボタンをタップ)
    viewModel.sendIntent(LoginIntent.OnLoginButtonClick)

    //Then (結果: ログイン画面への遷移Effect)
    val effect = viewModel.effect.first()
    assertTrue(effect is LoginEffect.NavigateToLogin)
}
```

---

## 4. ViewModel (MVI) のテストパターン

MVI アーキテクチャにおける ViewModel テストの典型的なパターンです。

### 4.1 状態遷移のテスト (State)

```kotlin
@Test
fun `Given データ未取得 When データ読み込み Then Loadingになりデータが表示される`() = runTest {
    // Given (前提: リポジトリがデータを返す準備)
    val mockData = listOf(Item(id = "1", name = "Test Item"))
    coEvery { repository.getItems() } returns flowOf(mockData)
    val viewModel = ItemsViewModel(repository)

    // When (操作: データ読み込みIntentを送信)
    viewModel.sendIntent(ItemsIntent.LoadData)
    advanceUntilIdle()

    // Then (結果: データが表示される)
    val state = viewModel.uiState.value
    assertTrue(state is UiState.Success)
    assertEquals(mockData, state.data.items)
}
```

### 4.2 副作用のテスト (Effect)

```kotlin
@Test
fun `Given 空の入力 When 送信ボタンをタップ Then バリデーションエラーが表示される`() = runTest {
    // Given (前提: ViewModelが作成済み)
    val viewModel = InputViewModel()
    
    viewModel.effect.test { // Turbineを使用
        // When (操作: 空文字で送信)
        viewModel.sendIntent(InputIntent.Submit(""))

        // Then (結果: エラーSnackbarが表示される)
        val effect = awaitItem()
        assertTrue(effect is InputEffect.ShowSnackbar)
        assertEquals("入力が必要です", (effect as InputEffect.ShowSnackbar).message)
    }
}
```

---

## 5. Screen (Compose UI) のテストパターン

Compose UIのテストでは、ユーザーの視点でテストを書きます。

```kotlin
@Test
fun `Given アイテムリスト画面 When FABをタップ Then アイテム追加ダイアログが表示される`() {
    // Given (前提: アイテムリスト画面を表示)
    composeTestRule.setContent {
        ItemsListScreen(
            items = sampleItems,
            onFabClick = { /* mock */ }
        )
    }

    // When (操作: FABをタップ)
    composeTestRule.onNodeWithContentDescription("アイテムを追加").performClick()

    // Then (結果: ダイアログが表示される)
    composeTestRule.onNodeWithText("新しいアイテム").assertIsDisplayed()
}
```

---

## 6. UseCase のテストパターン

```kotlin
@Test
fun `Given 5件のアイテム When フィルター条件を適用 Then 2件のみ返される`() = runTest {
    // Given (前提: リポジトリに5件のアイテム)
    val allItems = createSampleItems(5)
    coEvery { repository.getAllItems() } returns flowOf(allItems)
    val useCase = FilterItemsUseCase(repository)

    // When (操作: カテゴリフィルターを適用)
    val result = useCase(category = ItemCategory.STUDY).first()

    // Then (結果: 該当する2件のみ)
    assertEquals(2, result.size)
    assertTrue(result.all { it.category == ItemCategory.STUDY })
}
```

---

## 7. Repository のテストパターン

```kotlin
@Test
fun `Given ローカルDBにデータあり When getAllItems Then DBからデータが返される`() = runTest {
    // Given (前提: DAOにデータを準備)
    val mockDao = mockk<ItemDao>()
    val testItems = createSampleItems(3)
    coEvery { mockDao.getAllItems() } returns flowOf(testItems)
    val repository = ItemRepositoryImpl(mockDao)

    // When (操作: アイテム一覧を取得)
    val result = repository.getAllItems().first()

    // Then (結果: DAOから取得したデータが返される)
    assertEquals(testItems, result)
    coVerify { mockDao.getAllItems() }
}
```

---

## 8. 推奨ライブラリ

*   **Mockk**: Kotlin 向けの強力なモックライブラリ。`coEvery` (Coroutines対応) が便利。
*   **kotlinx-coroutines-test**: `runTest`, `UnconfinedTestDispatcher` など、Coroutines のテストに必須。
*   **Turbine**: Flow のテストを直感的に書けるライブラリ。Effect の検証に最適。
---

## 6. チェックリスト

テストを書く際は以下を確認してください。

- [ ] メソッド名は `given_when_then` の形式になっているか？
- [ ] コード内に `// Given`, `// When`, `// Then` のコメントがあるか？
- [ ] 1つのテストで検証するシナリオは1つに絞られているか？
- [ ] 非同期処理を含む場合、`runTest` を使用しているか？
- [ ] `UiState` が `Loading` や `Failure` になっても、`data` が保持されていることを確認しているか？
