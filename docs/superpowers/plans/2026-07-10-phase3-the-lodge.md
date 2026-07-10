# Phase 3 — The Lodge (connection) Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Reduce isolation — the single highest-leverage protective factor — by pointing the solo user *outward* to their own people and real, free community, **with zero infrastructure**: no server, no social graph, no accounts, no moderation.

**Architecture:** The app hosts no relationships and sees no contacts. The connection layer is bundled, on-device data + hand-off to the phone's own apps (a share sheet for the user's people, a browser for a resource). Reach-out (openers → `ACTION_SEND`) and the Well (`Relief`) already exist in the Chamber. Phase 3 adds **The West Gate** — a small set of honest "doorways" to real connection — and a gratitude-outward micro-practice. Crisis (§9) stays entirely separate and always on top.

**Tech Stack:** Kotlin, Jetpack Compose, Material 3, Android intents (`ACTION_SEND`, `ACTION_VIEW`). Native, on-device, **no new dependencies, no network calls by the app itself.**

## Global Constraints

- **Zero-infra (verbatim, ACTION_PLAN Phase 3):** "without a server, a social graph, or moderation liability." No backend, no `INTERNET` permission, no telemetry, no accounts. The app *opens* the phone's apps; it never sends or hosts anything.
- **Budget:** "i cannot afford to spend money on it" — nothing here costs anything to run.
- **Privacy:** "never gather data at any cost" — the app sees no contacts, stores no relationships.
- **Connection guardrails (BINDING, ACTION_PLAN/MASTER_PLAN Phase 3):** never a dopamine feed; never public vulnerability by default; the default is **your own people and real-world rooms, not a platform**. NOT building any peer layer / feed / DMs (the society pivot defers all in-app social until there's density; this is the *solo-outward*, N=1 layer that does not need people to exist).
- **Safety precedence (⚠️):** The West Gate is **ordinary, non-crisis** connection. It is NOT the §9 crisis surface — `CrisisSupportDialog` stays separate, unconditional, and rendered on top (SPEC P0.8). Crisis-grade lines are not duplicated as everyday doorways.
- **Copy/safety audit (⚠️):** every new user-facing string added to `SafetyAuditTest` → mortality-clean; invitations, never obligation; non-appropriative (Men's Sheds referenced as a *model*, not claimed/branded).
- **Research basis:** social connection is the most robust protective factor (Holt-Lunstad et al. 2010/2015); Men's Sheds counter isolation (RESEARCH_BASIS §11). Cite in KDoc; do not overclaim.
- **Package:** `com.ashlarprotocol`. Build: `gradle :app:testDebugUnitTest :app:assembleDebug --no-daemon --no-configuration-cache` (JBR JAVA_HOME, sdk ANDROID_HOME).

**Existing, do NOT rebuild:** `tools/ReachOut.kt` (editable openers) + `reachOut()` share-sheet in `ChamberScreen`; `tools/Relief.kt` (the Well) in the Chamber; `Trowel` (self-compassion / common-humanity). Reference design available at git `336e7ac` (old-line West Gate, `com.example`).

---

### Task P3.1: The West Gate — doorways model + curation (pure logic)

**Files:**
- Create: `app/src/main/java/com/ashlarprotocol/tools/WestGate.kt`
- Test: `app/src/test/java/com/ashlarprotocol/WestGateTest.kt`

**Interfaces:**
- Produces: `WestGate.Kind { OWN_PEOPLE, WEB, PLACE }`; `WestGate.Doorway(title, body, kind, url: String? = null, action: String? = null)`; `WestGate.DOORWAYS: List<Doorway>`; `WestGate.allText(): List<String>` (title+body+action for the safety sweep).

- [ ] **Step 1 — failing tests** (`WestGateTest.kt`):
```kotlin
package com.ashlarprotocol
import com.ashlarprotocol.tools.WestGate
import org.junit.Assert.assertTrue
import org.junit.Assert.assertNull
import org.junit.Assert.assertNotNull
import org.junit.Test

class WestGateTest {
    @Test fun offersSeveralDoorways() { assertTrue(WestGate.DOORWAYS.size >= 4) }

    @Test fun everyWebDoorwayHasHttpsUrlAndAction() {
        WestGate.DOORWAYS.filter { it.kind == WestGate.Kind.WEB }.forEach {
            assertNotNull("WEB doorway '${it.title}' needs a url", it.url)
            assertTrue("WEB url must be https: ${it.url}", it.url!!.startsWith("https://"))
            assertNotNull("WEB doorway needs a tappable action", it.action)
        }
    }

    @Test fun ownPeopleDoorwayHasActionNoUrl() {
        val own = WestGate.DOORWAYS.filter { it.kind == WestGate.Kind.OWN_PEOPLE }
        assertTrue(own.isNotEmpty())
        own.forEach { assertNotNull(it.action); assertNull(it.url) }
    }

    @Test fun placeDoorwayIsAnInvitationNotAnAppAction() {
        // A real-world room is something you walk to; the app has nothing to open for it.
        WestGate.DOORWAYS.filter { it.kind == WestGate.Kind.PLACE }.forEach {
            assertNull(it.url); assertNull(it.action)
        }
    }

    @Test fun noDoorwayIsEmpty() {
        WestGate.DOORWAYS.forEach { assertTrue(it.title.isNotBlank() && it.body.isNotBlank()) }
    }
}
```
- [ ] **Step 2 — run, expect FAIL** (`WestGate` unresolved).
- [ ] **Step 3 — implement** `WestGate.kt` (adapt the `336e7ac` design to `com.ashlarprotocol`; refresh the four doorways: reach your own people (OWN_PEOPLE), a free listening ear via findahelpline.com (WEB), peer support online via 7cups.com (WEB), build shoulder-to-shoulder / real-world room (PLACE)). Add `allText()`. KDoc cites Holt-Lunstad + Men's Sheds (RESEARCH_BASIS §11); states plainly these are doorways not endorsements, and that crisis stays separate.
- [ ] **Step 4 — run, expect PASS.**
- [ ] **Step 5 — commit** `feat(lodge): the West Gate — doorways to real connection (zero-infra)`.

---

### Task P3.2: The West Gate in the Chamber (UI + hand-off)

**Files:**
- Modify: `app/src/main/java/com/ashlarprotocol/ui/screens/ChamberScreen.kt` (add `WestGateSection`; it supersedes the single reach-out line — its OWN_PEOPLE doorway reuses `reachOut()`)
- Test: `app/src/test/java/com/ashlarprotocol/SafetyAuditTest.kt` (add `WestGate.allText()` to the sweep)

**Interfaces:**
- Consumes: `WestGate.DOORWAYS`, existing `reachOut(context, message)`, `ReachOut.openerAt`.
- Produces: `@Composable ChamberScreen`-local `WestGateSection(...)` rendering doorways as calm cards; a `openWeb(context, url)` intent helper (ACTION_VIEW), mirroring `reachOut`.

- [ ] **Step 1 — a browser hand-off helper** in `ChamberScreen.kt` (beside `reachOut`):
```kotlin
private fun openWeb(context: Context, url: String) {
    val intent = Intent(Intent.ACTION_VIEW, url.toUri()).apply { addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) }
    try { context.startActivity(intent) } catch (e: Exception) { /* no browser — fail quietly */ }
}
```
(add `import androidx.core.net.toUri`.)
- [ ] **Step 2 — the section:** a "THE WEST GATE" titled block (gold overline like the other Chamber headings) with a short honest line ("The app can't be your lodge — but it can point you to the door.") and one calm card per `WestGate.DOORWAYS`. Each card shows title + body + (for OWN_PEOPLE/WEB) a tappable action; a PLACE card is a non-tappable invitation. OWN_PEOPLE → `reachOut(context, ReachOut.openerAt(reachIndex)); reachIndex++`; WEB → `openWeb(context, doorway.url!!)`. Style: reuse the Chamber's card idiom (Surface/Slate bg, Gold action label). NOT a feed; calm, static list.
- [ ] **Step 3 — wire it in:** replace the standalone bottom reach-out line (the "reach someone you trust" `Text`) with `WestGateSection(...)`, so the gate is the one coherent turn-outward surface (its first doorway *is* reach-your-own). Keep `reachOut()` — the section calls it. Gate on `if (!purging)` as the old line was.
- [ ] **Step 4 — build + all tests green.**
- [ ] **Step 5 — commit** `feat(lodge): surface the West Gate in the Chamber (share/browser hand-off, no server)`.

---

### Task P3.3: Safety sweep + distinct-from-crisis + device verify

**Files:**
- Modify: `app/src/test/java/com/ashlarprotocol/SafetyAuditTest.kt`

- [ ] **Step 1 — add the West Gate copy to the mortality sweep** (in the corpus map): `"WestGate" to WestGate.allText()` (+ import). Build; if it fails, fix the *copy*, never the gate.
- [ ] **Step 2 — assert non-crisis distinctness** (a small test): the West Gate must not masquerade as the crisis surface — assert no West Gate doorway `action` equals a crisis call-to-action like "CALL 988" (it points to everyday connection; §9 owns crisis). Keep it light: assert none of `WestGate.DOORWAYS.mapNotNull { it.action }` contains "988" or "911".
- [ ] **Step 3 — build + full suite green.**
- [ ] **Step 4 — device-verify** on ZD2232FCR5: install; open CHAMBER; confirm "THE WEST GATE" + the doorways render (UI dump); tap a WEB doorway → a browser/chooser opens (or the intent fires without crash); confirm `NEED HELP?`/crisis still independent. Screenshot.
- [ ] **Step 5 — commit** `test(lodge): West Gate copy is mortality-clean and distinct from the §9 crisis surface`.

---

### Task P3.4: A gratitude-outward micro-practice (small; the "turn outward" that heals the turner)

**Files:**
- Modify: `app/src/main/java/com/ashlarprotocol/tools/PowerUps.kt` (add one grounded lifter: "Thank someone")
- Test: existing `PowerUpsTest` / `SafetyAuditTest` already sweep PowerUps — extend if a dedicated assertion helps.

**Interfaces:** reuses the existing `PowerUps.POWER_UPS` list + its Board/steady surface (never gated, ≤2 taps).

- [ ] **Step 1 — add a Power-Up** whose steps guide a 30-second act of gratitude *to a specific person* (evidence-based: gratitude expression raises wellbeing — Seligman et al. 2005), ending with an invitation to send it (the person, not the app, receives it). No new infra; it's steps + an optional hand-off to the share sheet if trivially wired, else just the prompt.
- [ ] **Step 2 — build + tests green** (PowerUps is already swept by SafetyAuditTest; confirm still clean).
- [ ] **Step 3 — device-verify** the lifter appears in the STEADY/Power-Ups sheet and expands. Screenshot.
- [ ] **Step 4 — commit** `feat(lodge): a gratitude-outward lifter — turning toward a real person`.

*(P3.4 is intentionally small. If wiring the send hand-off from a Power-Up is not clean, ship it as the prompt-only version and note it — the value is the outward turn, not the plumbing.)*

---

## Self-Review

- **Spec coverage:** ACTION_PLAN Phase 3 "reach out" → already exists (reused by P3.2); "the West Gate (bridge to real community)" → P3.1–P3.3; MASTER_PLAN "acts-of-kindness / gratitude that turn outward" → P3.4; "the Well" → already exists. "Done when: measurably points people toward real connection without compromising privacy/safety" → the West Gate is that surface, zero-infra.
- **NOT built (by design):** any peer layer / feed / DMs / accounts — deferred by the pivot until density; guardrail honored.
- **Safety:** every new string swept by `SafetyAuditTest`; West Gate is non-crisis and distinct from §9 (P3.3 step 2); crisis dialog untouched, still top layer.
- **Zero-infra:** only `ACTION_SEND` / `ACTION_VIEW` hand-offs; no network by the app; no new deps.
- **Verification honesty:** P3.2/P3.3/P3.4 device-verified (render + a doorway opens); note anything not frame-captured.

## Execution

Inline via **executing-plans** in worktree `feat/phase3-lodge` off `origin/main`, one commit per task, tests+assemble green after each, device-verify on ZD2232FCR5, then **finishing-a-development-branch** → one PR for the whole Lodge.
