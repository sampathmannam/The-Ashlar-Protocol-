# The Petition — Go-Live Checklist & Handoff

The petition page (`petition/index.html`) is the **validation-first** step of the society pivot
(`docs/superpowers/specs/2026-07-09-society-pivot-design.md` §5–6). Its one job is to test the
riskiest assumption: **will a stranger with no connection to the founder come forward to be initiated?**
Everything below is what "complete" means for *this* step — not the paid backend, which is deliberately
deferred (see the last section).

## Where the whole Phase 0 plan stands

- ✅ **The Craft / Daily Work / Your Stone** — already built and shipped in the app (Phases 1–3, on
  `origin/main`, `com.ashlarprotocol`): initiation rite → daily Working → tools → the tending stone →
  the Raising (degree ceremony) → the West Gate. Fully on-device, zero-cost.
- ✅ **The Threshold (petition → approval)** — this page, plus the reply template below.
- ⛔ **Payment (initiation fee + dues) and Backend + auth + sync** — **not built, on purpose.** The
  spec's decision is *validate first, build the backend only after the signal.* Both cost money and
  reverse the on-device/privacy model. Do not start them until the page proves demand.

## Launch steps (all yours — nothing here has been deployed)

1. **Deploy the page.** Easiest, no repo connection: drag the `petition/` folder onto
   <https://app.netlify.com/drop>. Or connect the repo — `netlify.toml` already sets `publish = "petition"`.
   Netlify auto-detects the `<form name="petition" data-netlify="true">` and captures submissions with
   **zero backend**. (Submissions appear under **Forms** in the Netlify dashboard.)
2. **Set the real domain — one place to change.** In `index.html` `<head>`, replace every
   `https://theashlarprotocol.org/` (the `canonical` link + the `og:*` / `twitter:*` tags + `og.png`
   URL) with your live domain, or link previews and the canonical URL will be wrong. There are ~5
   occurrences, all in the head block.
3. **Verify capture end-to-end** before driving traffic: submit one test petition on the live site and
   confirm it appears under Forms. The page now **fails loudly** if the POST fails (it no longer shows a
   false "received"), so a green confirmation means it really landed.
4. **Read the money signal.** Each submission carries the optional `pay_intent` field
   (`yes` / `maybe` / `free_only` / blank). That is your proxy for willingness-to-pay while the cohort
   is free. Watch the **share of `yes`** — that, not raw signups, is the number that de-risks charging.

## Measurement without spending or tracking

Netlify Analytics costs ~$9/mo and any JS tracker leaks data — both break your constraints, so the page
adds **neither**. Instead, get the **denominator (visitors) from wherever you drive traffic** — the
subreddit/forum/post's own view count — and compute conversion = petitions ÷ views. A petition count
with no denominator is uninterpretable; a *rate* is the signal.

## Distribution (the real gate)

The page validates nothing without **strangers** seeing it (the spec's exact bar: "no connection to the
founder"). Decide where the first ~200 come from *before* launch — that choice determines whether the
experiment even runs.

## The follow-up you now owe every petitioner

The page promises "you will hear from us about your initiation." Keep it simple and honest — a templated
reply that hands them the app:

> **Subject: Your petition is received — enter the Craft**
>
> You came forward, and that is the whole of the asking. You are enrolled among the founding cohort —
> initiated without an offering or dues, and your place as a founder does not lapse when pricing opens
> to those who follow.
>
> Begin the daily work here: [link to the app / release].
>
> The Craft asks the work of you, not our approval. We'll be in touch as the lodges form.

## The line held (do NOT cross without a decision)

**Payment gateway, accounts, backend, sync** are the heaviest lift and the point of no return from an
on-device, no-data product. Per the spec they wait for the signal, and per the budget they cost money.
When the page shows real demand (strangers petitioning, a healthy `yes` share), that is the moment to
*plan* the backend — and it should be its own spec with its cost, privacy, and safety model worked out
first. Not before.
