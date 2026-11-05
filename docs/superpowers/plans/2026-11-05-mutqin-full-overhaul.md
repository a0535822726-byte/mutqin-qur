# مُتقن Full Overhaul Implementation Plan

> **For agentic workers:** Execute this plan task-by-task with test-first development and frequent commits.

**Goal:** Redesign the main experience and make the recitation workflow more reliable and transparent without claiming unsupported automatic tajweed assessment.

**Architecture:** Keep the current Kotlin/Compose app and existing repository/preferences. Separate pure Arabic text normalization/comparison from Compose UI; redesign the home screen and recitation screen in focused stages. Preserve current features and local data.

**Tech Stack:** Kotlin, Jetpack Compose Material 3, Android MediaRecorder/MediaPlayer/SpeechRecognizer, GitHub Actions.

**Spec:** `docs/superpowers/specs/2026-11-05-mutqin-full-overhaul-design.md`

## Global Constraints
- Arabic RTL throughout.
- Keep green/gold identity and light/dark themes.
- No new paid services, API keys, or remote audio upload.
- Speech-to-text comparison is approximate text matching, not tajweed grading.
- Preserve existing SharedPreferences keys and current routes unless deliberately migrated.
- Build with the existing GitHub Actions workflow.

## Review Focus
- Empty or noisy speech-recognition output must not show a misleading match score.
- Arabic diacritics, hamza forms, alif maqsurah, and punctuation must normalize consistently.
- Invalid or out-of-range surah/ayah values must be clamped safely.
- Recorder start/stop failures and permission denial must leave the UI in a recoverable state.
- Missing speech recognition service must not crash the app or disable local recording/playback.

---

### Task 1: Extract and test recitation text comparison

**Files:**
- Create: `src/RecitationText.kt`
- Create: `tests/RecitationTextTest.kt` (or a small JVM test harness wired into CI if no Gradle test source set exists)
- Modify: `src/RecitationCheckScreen.kt`

**Interfaces:**
- Produces `normalizeRecitationWords(value: String): List<String>`
- Produces `compareRecitation(reference: String, heard: String): RecitationComparison`
- Result includes matched count, reference word count, percentage nullable for empty/meaningless input, and per-word alignment.

- [ ] Write failing tests for exact match, diacritic-insensitive match, hamza normalization, missing words, reordered words, empty reference, and empty recognition.
- [ ] Run tests and confirm expected failures.
- [ ] Implement normalization and LCS alignment with explicit empty-input behavior.
- [ ] Run focused tests and full available test suite.
- [ ] Update UI to show matched/missing/extra word states and avoid a score when recognition is empty.
- [ ] Commit.

### Task 2: Make recitation session lifecycle robust

**Files:**
- Modify: `src/RecitationCheckScreen.kt`
- Optional create: `src/RecitationSessionStore.kt`

**Interfaces:**
- Recording state transitions are explicit: idle, recording, saved, failed.
- Recording file metadata is persisted locally and previous sessions can be selected.
- Playback always releases the previous player and handles completion/error.
- Recognition is disabled during recording and handles unavailable services.

- [ ] Add testable validation for session metadata and filename handling.
- [ ] Verify RED for new session validation tests.
- [ ] Implement safe start/stop/release and session listing.
- [ ] Add clear session selection, playback, and deletion confirmation.
- [ ] Run tests and inspect Android lifecycle cleanup.
- [ ] Commit.

### Task 3: Redesign home dashboard and navigation

**Files:**
- Modify: `src/ListScreen.kt`
- Modify: `src/MainActivity.kt`

**Interfaces:**
- Distinct sections: المصحف، التسميع، التجويد، الحفظ والمراجعة، البحث والتحليل، أدوات إضافية.
- Every displayed feature card maps to an existing route or a deliberately disabled explanatory state.
- Main action opens recitation; search filters actual available sections/tools.

- [ ] Inventory all existing home actions and verify destinations.
- [ ] Implement new dashboard hierarchy and card system.
- [ ] Keep existing route IDs and theme settings.
- [ ] Verify RTL, dark mode, narrow-screen scrolling, and back navigation.
- [ ] Commit.

### Task 4: Improve tajweed learning experience

**Files:**
- Modify: `src/TajweedScreen.kt`
- Optional create: `src/TajweedPracticeData.kt`

**Interfaces:**
- Each rule has definition, examples, common mistake, practice prompt, and quiz feedback.
- Practice is educational and never presented as automated audio diagnosis.

- [ ] Add tests for quiz scoring and answer feedback data.
- [ ] Verify RED.
- [ ] Implement separate rule cards and practice flow for ghunnah, madd, qalqalah, noon/tanween, meem, and stopping/starting.
- [ ] Run tests and review educational wording.
- [ ] Commit.

### Task 5: End-to-end build verification

**Files:**
- Modify workflow only if verification reveals a real issue.

- [ ] Trigger GitHub Actions on the feature branch.
- [ ] Inspect every job/step and fix failures.
- [ ] Confirm APK asset exists and is newly generated from this branch.
- [ ] Do not publish a latest release until the branch is accepted and build verified.
- [ ] Report what is complete, what remains limited, and the verified build link.

