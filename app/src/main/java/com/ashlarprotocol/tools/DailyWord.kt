package com.ashlarprotocol.tools

/**
 * The daily word — a bundled, on-device rotation of short, grounded reflections in the app's
 * quiet-mentor voice. This replaces the old Gemini API call, which cost money per request and
 * shipped an extractable API key in the APK. Bundled words cost nothing, work fully offline, and
 * can't fail on a network error — strictly better on cost, privacy, and reliability.
 *
 * The words are craft-toned and evidence-flavoured but make no clinical promises (see
 * docs/RESEARCH_BASIS.md for what the app is careful never to overclaim). Pure + deterministic.
 */
object DailyWord {

    val WORDS: List<String> = listOf(
        "The rough ashlar is not a broken stone. It is a stone with work still in it — and so are you.",
        "Divide the day into three: something that matters, honest labour, and true rest. The gauge asks no more.",
        "A thought that leans is not a thought that stands. Test it against the plumb before you believe it.",
        "You meet everyone on the level today — no one above you, no one below. Least of all yourself.",
        "Small, true actions, laid one upon another, are how every wall that ever held was built.",
        "The work is daily, and it is never finished. That is not failure. That is the craft.",
        "Breathe as the level asks — slow out, and slower still. The body quiets when the breath leads.",
        "What you carry, you may set down for a while. The Chamber keeps no debts.",
        "Progress is quiet. It rarely announces itself. Trust the direction over the day.",
        "Square your actions to what you value, and the noise of the day loosens its grip.",
        "Strength is not the absence of weight. It is the choice to keep shaping the stone anyway.",
        "To reach for help is not to break. It is the oldest tool in the lodge.",
        "Keep your word — to others, and to yourself. A foundation is only as true as that.",
        "Some days you smooth the stone. Some days you only hold the chisel. Both are the work.",
        "What mattered today, even a little? Name it. Meaning is built, not found.",
        "You are the rough stone. You are also the mason. Begin where you stand."
    )

    /** Safe indexed access with wraparound; tolerates any Int (including negatives). */
    fun wordAt(index: Int): String {
        val n = WORDS.size
        val i = ((index % n) + n) % n
        return WORDS[i]
    }
}
