import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

/**
 * Helper to load Press Start 2P / JetBrains Mono with graceful fallbacks.
 */
public final class FontUtil {

	private static final String FONT_DIR = "fonts";
	private static final String PRESS_START_FILE = "PressStart2P-Regular.ttf";
	private static final String JB_MONO_FILE = "JetBrainsMono-VariableFont_wght.ttf";

	private FontUtil() { }

	public static Font pressStart(float size) {
		return pressStart(size, Font.PLAIN);
	}

	public static Font pressStart(float size, int style) {
		return loadFont(PRESS_START_FILE, "Press Start 2P", style, size);
	}

	public static Font jetBrainsMono(float size) {
		return jetBrainsMono(size, Font.PLAIN);
	}

	public static Font jetBrainsMono(float size, int style) {
		return loadFont(JB_MONO_FILE, "JetBrains Mono", style, size);
	}

	/**
	 * Compatibility wrapper for existing callers that pass file names.
	 */
	public static Font font(String resourceName, int style, float size) {
		String lower = resourceName == null ? "" : resourceName.toLowerCase();
		if (lower.contains("pressstart2p")) return pressStart(size, style);
		if (lower.contains("jetbrainsmono")) return jetBrainsMono(size, style);
		// Fallback: try to load whatever name was provided, then default.
		Font loaded = loadFont(resourceName, null, style, size);
		if (loaded != null) return loaded;
		return new Font(Font.MONOSPACED, style, Math.round(size));
	}

	private static Font loadFont(String fileName, String familyName, int style, float size) {
		Font loaded = tryLoadFromResources(fileName);
		if (loaded == null) loaded = tryLoadFromFileSystem(fileName);

		if (loaded != null) {
			Font derived = loaded.deriveFont(style, size);
			register(derived);
			return derived;
		}

		if (familyName != null && isInstalled(familyName)) {
			return new Font(familyName, style, Math.round(size));
		}

		return new Font(Font.MONOSPACED, style, Math.round(size));
	}

	private static Font tryLoadFromResources(String fileName) {
		try (InputStream in = FontUtil.class.getResourceAsStream("/" + FONT_DIR + "/" + fileName)) {
			if (in == null) return null;
			return Font.createFont(Font.TRUETYPE_FONT, in);
		} catch (Exception e) {
			return null;
		}
	}

	private static Font tryLoadFromFileSystem(String fileName) {
		Path userDir = Path.of(System.getProperty("user.dir"));
		Path[] candidates = new Path[] {
			userDir.resolve(FONT_DIR).resolve(fileName),
			userDir.resolve("oop-dsa-final-project-main").resolve(FONT_DIR).resolve(fileName)
		};

		for (Path candidate : candidates) {
			if (Files.exists(candidate)) {
				try (InputStream in = Files.newInputStream(candidate)) {
					return Font.createFont(Font.TRUETYPE_FONT, in);
				} catch (Exception ignored) {
				}
			}
		}
		return null;
	}

	private static void register(Font font) {
		try {
			GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
			ge.registerFont(font);
		} catch (Exception ignored) {
		}
	}

	private static boolean isInstalled(String familyName) {
		return Arrays.stream(GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames())
				.anyMatch(name -> name.equalsIgnoreCase(familyName));
	}
}