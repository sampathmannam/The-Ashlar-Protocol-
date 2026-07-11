package com.ashlarprotocol.tools

/**
 * Adornment — the third thing wages buy: the finish of your Temple. Each finish is a deterministic
 * palette (no random), bought once with wages and then yours for good; selecting between the ones you
 * own is free. Spending is the same "spent but never lost" model as raising courses — the finish is
 * permanent. Purely cosmetic: it never gates a tool or the §9 path, and the default finish is free, so
 * the Temple always has a look even at zero wages. Pure — colours are ARGB longs; the UI renders them.
 */
data class Finish(
    val id: String,
    val name: String,
    val cost: Int,
    /** The colour the raised courses take, as 0xAARRGGBB. */
    val argb: Long
)

object Adornment {
    const val DEFAULT_ID = "sandstone"

    val FINISHES: List<Finish> = listOf(
        Finish("sandstone", "Sandstone", 0, 0xFFC9A24A),   // the warm gold default — free
        Finish("marble", "Marble", 8, 0xFFEDE3D1),         // candlelit off-white
        Finish("verdigris", "Verdigris", 10, 0xFF7FA98C),  // aged copper green
        Finish("rose", "Rose Quartz", 10, 0xFFCB8E9A),     // warm rose
        Finish("lapis", "Lapis", 12, 0xFF6E8CC4)           // deep blue
    )

    fun finishOf(id: String?): Finish? = FINISHES.firstOrNull { it.id == id }

    fun costOf(id: String): Int = finishOf(id)?.cost ?: 0

    /** A finish is available if it's free (the default) or has been bought. */
    fun isAvailable(id: String, ownedIds: List<String>): Boolean =
        costOf(id) == 0 || id in ownedIds

    /** Wages laid into finishes (only bought ones cost anything; the default is free). */
    fun totalSpend(ownedIds: List<String>): Int =
        ownedIds.mapNotNull { finishOf(it) }.sumOf { it.cost }

    /** The chosen finish, falling back to the free default if none is selected or it's unknown. */
    fun selectedOrDefault(selectedId: String?): Finish =
        finishOf(selectedId) ?: finishOf(DEFAULT_ID)!!

    fun allText(): List<String> = FINISHES.map { it.name }
}
