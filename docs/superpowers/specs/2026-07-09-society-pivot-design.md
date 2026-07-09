# The Ashlar Protocol — Society Pivot (Strategy & Phase 0 Design)

**Date:** 2026-07-09
**Status:** Approved direction (brainstorm complete) → validation-first
**Supersedes framing:** on-device, offline wellness *app* → premium, exclusive, **online initiatory society**

---

## 1. The pivot, in one line

**A premium, initiatory wellness society inspired by Freemasonry** — you are *initiated* into the Craft and advance through **degrees (Apprentice → Fellowcraft → Master)** by doing daily "ashlar work" on yourself. **Solo journey first; the lodge (small chapter, mentorship, brotherhood) switches on once there is density.**

This is a **mental-wellness** product (self-improvement, discipline, brotherhood, ritual), **not** a mental-health / crisis product. That distinction is load-bearing — it is what makes exclusivity, initiation, and paid membership *native* to the product rather than in tension with a duty to reach the isolated.

> **Duty-of-care floor (not the spine):** even a wellness product occasionally meets someone in distress. Keep one lightweight, unconditional "here is a real crisis line" backstop. It costs nothing and protects members and the brand. It is a floor, not a feature.

## 2. Key decisions (the forks we resolved)

| Fork | Decision | Why |
|---|---|---|
| Exclusive/premium at all? | **Yes** | Wellness-society framing makes it coherent; Freemasonry is itself a private, dues-paying, initiatory society. |
| Entry model | **Petition-based, not invite-based** | Masonry is "to be one, ask one" — the seeker knocks. Prestige by *initiation & commitment*, not by being hand-picked. Door opens to anyone willing to knock; the rite is the filter. |
| Core loop | **B + C together** | Degree progression (vertical: status/unlocks) *inside* daily practice + accountability (horizontal). This is literally how a lodge works. |
| Social architecture | **Lodge, not Broadcast** | Small chapters (≤~40) that go deep, scaling *sideways* (more lodges), not one giant feed. Exclusivity **feeds** this model instead of starving it. A broadcast feed would be an empty room. |
| Monetization | **Initiation fee (one-time) + Dues (recurring)** | Both. The initiation fee is the single most on-theme premium lever. Dues are the revenue engine. Framed as dues, not "subscription." |
| Cold start | **No founding circle exists** (confirmed) | Forces the sequencing below. |
| Launch shape | **Solo Craft first, lodge later** | See §3. |

## 3. The cold-start strategy (the crux)

**You cannot launch a lodge cold.** A paid, exclusive social layer with no members is a ghost town you're charging admission to. Chicken-and-egg kills it.

**Escape hatch:** the degree progression (B) is a *single-player game*. Your own stone doesn't need anyone else in the room. So:

- **Come for the tool, stay for the network.** Build the solo Craft so it's fully valuable at **N = 1**. The lodge is what members **graduate into** once enough of them exist — not a precondition for value.
- **The right thing to sequence is *when the social turns on*, not *whether*.** Every ambition is kept; the social layer is *earned*, not launched into a vacuum.

## 4. Phase 0 — the spine (works with one member)

1. **The Threshold** — petition → approval → **initiation fee + dues**. Payment gateway lands here as *earned entry*, not a paywall. The premium moment.
2. **The Craft (degree progression)** — three degrees, each a body of practices. Gated by *doing the work*, never by paying more. The single-player game.
3. **The Daily Work (tending the stone)** — daily ritual/reflection/practice; ashlar metaphor as the whole engine; streaks that **never zero-reset** (reuse existing KindStreak work).
4. **Your Stone (identity/profile)** — degree, progress, rough→perfect ashlar as the avatar of growth.
5. **Backend + auth + sync** — the real architectural pivot: Ashlar stops being on-device and becomes an online, account-based product. Heaviest lift → Phase 0 infrastructure.
6. **"The lodge is being built"** — deliberate *absence-with-presence*: show the Craft is populated (initiate count, anonymized progress of others) without a full social layer, so solo never feels dead.

**Deferred to Phase 1+ (do NOT build yet):** lodges, feed, DMs, mentorship, moderation — all social. Arrives with people.

## 5. Riskiest assumption & the validation-first plan

**Riskiest surviving assumption (singular, testable):**
> "Will a stranger with no connection to the founder pay real money to be *initiated* into an unproven solo practice?"

Everything else is plumbing. This is the bet.

**Cheapest test — do this BEFORE building the backend:**
- A **single petition landing page** with **real pricing shown** (initiation fee + dues) and a **real petition form** — collecting either a paid deposit or a paid/committed waitlist.
- **Signal = strangers petition (better: put money down).** If yes → de-risked the whole platform for the cost of one page. If no → saved building a backend nobody enters.

**Decision:** proceed **validation-first (option a).** Build the landing page + pricing + petition capture first; commit to the Phase 0 backend only after the signal.

## 6. Immediate next step

Design and build the **petition landing page** (its own small spec/plan): the pitch, the rite/positioning, the pricing presentation, and the petition-capture mechanism. Then measure.
