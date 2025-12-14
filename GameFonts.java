import java.awt.Font;

/**
 * Centralizes font selections for the game UI.
 */
public final class GameFonts {
	private static final String UI_FONT = "JetBrainsMono-VariableFont_wght.ttf";
	private static final String TITLE_FONT = "PressStart2P-Regular.ttf";

	private GameFonts() {
	}

	// Primary names
	public static Font jetts(float size) { return FontUtil.font(UI_FONT, Font.PLAIN, size); }
	public static Font jettsBold(float size) { return FontUtil.font(UI_FONT, Font.BOLD, size); }
	public static Font jettsItalic(float size) { return FontUtil.font(UI_FONT, Font.ITALIC, size); }

	public static Font press(float size) { return FontUtil.font(TITLE_FONT, Font.PLAIN, size); }
	public static Font pressBold(float size) { return FontUtil.font(TITLE_FONT, Font.BOLD, size); }

	// Backward-compat aliases
	public static Font ui(float size) { return jetts(size); }
	public static Font uiBold(float size) { return jettsBold(size); }
	public static Font uiItalic(float size) { return jettsItalic(size); }

	public static Font title(float size) { return press(size); }
	public static Font titleBold(float size) { return pressBold(size); }
}
