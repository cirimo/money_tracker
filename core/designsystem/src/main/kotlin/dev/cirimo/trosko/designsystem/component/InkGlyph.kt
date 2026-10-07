package dev.cirimo.trosko.designsystem.component

/**
 * The icons of the app, each one stroke of the marker on a grid of 24 units: round ends, round
 * joins, no fills. [pathData] is the stroke in SVG path syntax. See docs/DESIGN.md, icons.
 */
enum class InkGlyph(
    internal val pathData: String,
) {
    Basket("M4 9h16l-1.5 10h-13zM8 9l3-5M16 9l-3-5"),
    Cup("M5 9h11v6a4 4 0 0 1-4 4H9a4 4 0 0 1-4-4zM16 11h1.5a2.5 2.5 0 0 1 0 5H16M8 3v2M12 3v2"),
    Bus("M5 5h14v11H5zM5 11h14M8 16v3M16 16v3M8.5 13.5h.01M15.5 13.5h.01"),
    House("M4 11l8-7 8 7M6 10v9h12v-9M10 19v-5h4v5"),
    Bolt("M13 3L6 13h5l-1 8 7-10h-5z"),
    Heart("M12 20s-7-4.5-7-10a4 4 0 0 1 7-2.5A4 4 0 0 1 19 10c0 5.5-7 10-7 10z"),
    Ticket("M4 8h16v3a2 2 0 0 0 0 4v3H4v-3a2 2 0 0 0 0-4zM14 8v10"),
    Bag("M6 8h12l1 12H5zM9 8V6a3 3 0 0 1 6 0v2"),
    Dots("M6 12h.01M12 12h.01M18 12h.01"),
    Backspace("M9 6h11v12H9l-5-6zM12.5 10l4 4M16.5 10l-4 4"),
    ChevronLeft("M14 6l-6 6 6 6"),
    ChevronRight("M10 6l6 6-6 6"),
    Trash("M5 7h14M9 7V4h6v3M7 7l1 13h8l1-13M10 11v5M14 11v5"),
}
