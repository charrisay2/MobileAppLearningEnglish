# Implementation Plan: Premium, Streak, and Vocabulary Management

This plan outlines the integration of Google Play Billing, a Daily Streak system, and advanced Vocabulary folder management.

## User Review Required
- **Verification Method**: Uses Firebase Cloud Functions for secure server-side verification (Requires **Blaze Plan**).
- **Drag & Drop**: Implementing a full drag-and-drop UI in Compose for folders requires a custom implementation for touch handling.
- **Streak Logic**: A day is counted upon the first login of that UTC day. If a day is missed, the streak resets to 1.

## Proposed Changes

### 1. User & Stats Logic (Daily Streak & Word Count)
#### [NEW] [UserStats.kt](file:///C:/Users/Admin/Downloads/lingua-master/app/src/main/java/com/example/domain/user/UserStats.kt)
- Data class for `streakCount`, `lastLoginDate`, and `totalWordsLearned`.

#### [FirestoreManager.kt](file:///C:/Users/Admin/Downloads/lingua-master/app/src/main/java/com/example/data/premium/FirestoreManager.kt)
- Add `updateStreak()`: Checks `lastLoginDate` and increments or resets `streakCount`.
- Add `incrementWordCount()`: Called when a new word is added.

---

### 2. Vocabulary Enhancements (Folders & Drag-and-Drop)
#### [VocabularyScreen.kt](file:///C:/Users/Admin/Downloads/lingua-master/app/src/main/java/com/example/presentation/vocabulary/VocabularyScreen.kt)
- **Folder Selection**: When adding a word, show a dropdown to select a target folder (default: Current Folder or All).
- **Drag & Drop**: Implement a custom `Modifier` to detect long-press and drag on vocabulary items, highlighting folders as potential targets.

---

### 3. Profile & Authentication
#### [ProfileScreen.kt](file:///C:/Users/Admin/Downloads/lingua-master/app/src/main/java/com/example/presentation/profile/ProfileScreen.kt)
- Remove "Admin Settings" button.
- Implement `onSignOut` to call `FirebaseAuth.getInstance().signOut()` and navigate to `LoginRoute`.
- Fetch and display real `streakCount` and `totalWordsLearned` from Firestore.

---

### 4. Premium Subscription (Google Play Billing)
[... Existing BillingManager and Paywall logic from previous plan ...]

## Verification Plan
1. **Streak Test**: Change system clock to tomorrow, log in, and verify streak increments.
2. **Word Count Test**: Add a word and verify the count on the Profile screen updates immediately.
3. **Drag & Drop Test**: Drag a word from "All" into a specific folder and verify it moves correctly in the database.
4. **Sign Out**: Verify that clicking Sign Out returns the user to the Login screen and clears the session.
