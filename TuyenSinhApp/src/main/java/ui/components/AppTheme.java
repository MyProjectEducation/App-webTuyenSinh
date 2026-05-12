package ui.components;

import java.awt.*;

public class AppTheme {
    // Primary colors
    public static final Color PRIMARY       = new Color(0x18, 0x5F, 0xA5);
    public static final Color PRIMARY_DARK  = new Color(0x0C, 0x44, 0x7C);
    public static final Color PRIMARY_LIGHT = new Color(0xE6, 0xF1, 0xFB);

    // Status colors
    public static final Color GREEN        = new Color(0x3B, 0x6D, 0x11);
    public static final Color GREEN_LIGHT  = new Color(0xEA, 0xF3, 0xDE);
    public static final Color AMBER        = new Color(0x85, 0x4F, 0x0B);
    public static final Color AMBER_LIGHT  = new Color(0xFA, 0xEE, 0xDA);
    public static final Color RED          = new Color(0xA3, 0x2D, 0x2D);
    public static final Color RED_LIGHT    = new Color(0xFC, 0xEB, 0xEB);
    public static final Color TEAL         = new Color(0x0F, 0x6E, 0x56);
    public static final Color TEAL_LIGHT   = new Color(0xE1, 0xF5, 0xEE);

    // Neutrals
    public static final Color BG_PRIMARY   = Color.WHITE;
    public static final Color BG_SECONDARY = new Color(0xF7, 0xF7, 0xF6);
    public static final Color BG_TERTIARY  = new Color(0xF0, 0xEF, 0xED);
    public static final Color BORDER       = new Color(0xE0, 0xDF, 0xDD);
    public static final Color TEXT_PRIMARY = new Color(0x1A, 0x1A, 0x18);
    public static final Color TEXT_SECOND  = new Color(0x6B, 0x6A, 0x67);
    public static final Color TEXT_THIRD   = new Color(0x9B, 0x9A, 0x97);

    // Sidebar
    public static final Color SIDEBAR_BG   = Color.WHITE;
    public static final Color SIDEBAR_ACTIVE_BG  = PRIMARY_LIGHT;
    public static final Color SIDEBAR_ACTIVE_TEXT = PRIMARY;

    // Fonts
    public static final Font FONT_TITLE    = new Font("Segoe UI", Font.BOLD, 14);
    public static final Font FONT_SUBTITLE = new Font("Segoe UI", Font.PLAIN, 11);
    public static final Font FONT_BODY     = new Font("Segoe UI", Font.PLAIN, 12);
    public static final Font FONT_SMALL    = new Font("Segoe UI", Font.PLAIN, 11);
    public static final Font FONT_BOLD     = new Font("Segoe UI", Font.BOLD, 12);
    public static final Font FONT_TABLE_HEADER = new Font("Segoe UI", Font.BOLD, 11);
    public static final Font FONT_MONO     = new Font("Consolas", Font.PLAIN, 11);

    // Dimensions
    public static final int SIDEBAR_WIDTH  = 210;
    public static final int TOPBAR_HEIGHT  = 52;
    public static final int ROW_HEIGHT     = 28;
}
