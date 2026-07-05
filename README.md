<div align="center">

# The Ashlar Protocol

**A private, disciplined companion for the lifelong work of becoming who you could be.**

*You are the rough stone. You are also the mason. Let's get to work.*

[**Read more at the project site →**](https://sampathmannam.github.io/The-Ashlar-Protocol-/)

</div>

---

The Ashlar Protocol is a mental-health practice built on the oldest structured self-improvement system humanity ever wrote down — the stonemason's craft of shaping the **rough ashlar** (the raw, unfinished stone) into the **perfect ashlar** (true, square, and fit to build with). Freemasonry made that a metaphor for a human life: *you are not broken; you are unfinished — and you already hold the tools.*

We keep what modern psychology validates, state honestly where the tradition is only a beautiful metaphor, and put the tools in the pocket of anyone willing to pick them up.

## Documentation

**Product & evidence**
- **[docs/VISION.md](docs/VISION.md)** — the north-star: what this is, who it's for, the principles, and the Freemasonry × mental-health synthesis.
- **[docs/RESEARCH_BASIS.md](docs/RESEARCH_BASIS.md)** — every psychological claim, its source, and an honest confidence level. *We never fabricate citations and never overclaim.*
- **[docs/AUDIT_AND_REDESIGN.md](docs/AUDIT_AND_REDESIGN.md)** — a full audit of the built app, a substance-first redesign plan, and research-grounded feature ideas.

**Planning & status**
- **[docs/MASTER_PLAN.md](docs/MASTER_PLAN.md)** — the phased roadmap from prototype to product (safety first).
- **[docs/CHANGELOG.md](docs/CHANGELOG.md)** — what's actually built, and its verification state.

**Engineering**
- **[docs/ARCHITECTURE.md](docs/ARCHITECTURE.md)** — how the code is organized and the load-bearing design decisions.
- **[docs/BUILD.md](docs/BUILD.md)** — how to build the APK and install to a device (the hard-won recipe + gotchas).
- **[docs/VERIFICATION.md](docs/VERIFICATION.md)** — the on-device QA checklist. *The §1 crisis pass is a blocking requirement.*

## What's inside (the Craft, as an app)

- **The Board** — your tracing board / dashboard: the rough→perfect ashlar, daily practice, reflection, and the three pillars *Wisdom · Strength · Beauty* (mind · body · spirit).
- **The Chamber of Reflection** — lay down the weight; a cathartic writing rite.
- **The Tools** — the working tools reframed as mental skills: the Plumb (values), the Level (paced breathing), the Gauge (daily structure), the Gavel, and Mouth-to-Ear (memory work).

## Principles

1. **Help is the only metric** — not engagement, not retention.
2. **Your inner work is yours** — reflection stays on-device by default; we never harvest the confessional.
3. **Every claim earns its place** — traceable to real evidence, hedged to its real confidence.
4. **A practice, not a clinician** — this app does not diagnose or treat, and it builds a real bridge to human help when someone needs more.

## Tech

Native Android — Kotlin, Jetpack Compose, Material 3, DataStore, WorkManager. **Fully on-device: no network, no accounts, no API keys.** The app declares no `INTERNET` permission — nothing you write ever leaves the phone. The daily word is bundled, the crisis detector runs locally, and all reflection lives in local storage.

## Build

**Prerequisites:** Android Studio.

1. Open the project in Android Studio and let it sync.
2. Run on an emulator or device. No keys or setup required.

> **Note:** this is an early prototype. Per [docs/MASTER_PLAN.md](docs/MASTER_PLAN.md), the crisis-safety layer (Phase 1) is a blocking requirement before any public release.

---

*The Ashlar Protocol borrows Freemasonry's symbolic architecture of self-improvement. It is not a religion, not a Masonic lodge, and it initiates no one into the Craft — it simply picks up a dropped thread and hands you the tools.*
