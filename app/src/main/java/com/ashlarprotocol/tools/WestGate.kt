package com.ashlarprotocol.tools

/**
 * The West Gate — the lodge's outer door, and the "turn outward" the Craft was built for (Phase 3).
 *
 * The app can't be your lodge: it hosts no relationships, keeps no accounts, sees no contacts. But it
 * can point you to the door. Social connection is the most robust protective factor there is — weak
 * social ties carry risk on the order of well-known lifestyle factors (Holt-Lunstad et al., 2010 &
 * 2015 meta-analyses), and belonging through *shared doing* (Men's Sheds and similar) counters
 * isolation. See docs/RESEARCH_BASIS.md §11.
 *
 * These are DOORWAYS, not a service and not endorsements: a few well-established, free, widely-available
 * starting points, honestly labelled. The right door is the one you'll actually walk through. This is
 * the *ordinary, non-crisis* turn toward other people — crisis help stays separate and always open
 * (safety/CrisisDetector + the NEED HELP header). Zero-infra by design: the app opens the phone's own
 * apps (a share for your people, a browser for a resource) and sends nothing itself.
 */
object WestGate {

    /** How a doorway opens: the user's OWN people (a share), a WEB resource, or a real-world PLACE. */
    enum class Kind { OWN_PEOPLE, WEB, PLACE }

    data class Doorway(
        val title: String,
        val body: String,
        val kind: Kind,
        /** For WEB doorways only — an https link to a well-established, free resource. */
        val url: String? = null,
        /** The tappable label, or null for a PLACE (nothing for the app to open — you walk there). */
        val action: String? = null
    )

    val DOORWAYS: List<Doorway> = listOf(
        Doorway(
            title = "Reach one of your own",
            body = "The people who already know you are the shortest way back to not being alone. " +
                "Send one honest line to someone you trust.",
            kind = Kind.OWN_PEOPLE,
            action = "Open a message"
        ),
        Doorway(
            title = "Find a free listening ear",
            body = "Trained volunteers who will simply listen — free, confidential, no account needed. " +
                "Find a line for your country.",
            kind = Kind.WEB,
            url = "https://findahelpline.com",
            action = "Find a line near you"
        ),
        Doorway(
            title = "Sit with others, online",
            body = "7 Cups connects you with trained volunteer listeners and peer-support communities — " +
                "free and anonymous.",
            kind = Kind.WEB,
            url = "https://www.7cups.com",
            action = "Open 7 Cups"
        ),
        Doorway(
            title = "Build something shoulder to shoulder",
            body = "A group that meets in person — a shed or workshop, a class, a place of worship, a " +
                "morning of volunteering — puts you beside people without having to perform. Look for " +
                "one near you.",
            kind = Kind.PLACE
        )
    )

    /** Every user-facing string, for the mortality-symbolism safety sweep (SafetyAuditTest). */
    fun allText(): List<String> =
        DOORWAYS.flatMap { listOfNotNull(it.title, it.body, it.action) }
}
