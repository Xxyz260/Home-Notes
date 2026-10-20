package site.chatgpt.vi52.homenotes;

import android.graphics.Typeface;
import android.graphics.fonts.Font;
import android.graphics.fonts.FontFamily;
import android.graphics.fonts.FontStyle;
import android.graphics.fonts.SystemFonts;

import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

final class FontCatalog {
	static final String DEFAULT_KEY = "default";

	static final class Entry {
		final String key;
		final String label;
		final Typeface typeface;

		Entry(String key, String label, Typeface typeface) {
			this.key = key;
			this.label = label;
			this.typeface = typeface;
		}
	}

	private FontCatalog() {}

	static List<Entry> load() {
		List<Entry> entries = new ArrayList<>();
		entries.add(new Entry(DEFAULT_KEY, "System default", Typeface.DEFAULT));

		for (Font font : SystemFonts.getAvailableFonts()) {
			File file = font.getFile();
			if (file == null) {
				continue;
			}

			FontStyle style = font.getStyle();
			String base = file.getName().replaceFirst("\\.[^.]+$", "");
			String slant = style.getSlant() == FontStyle.FONT_SLANT_ITALIC ? ", italic" : "";
			String label = base + " (" + style.getWeight() + slant + ")";
			String key = file.getAbsolutePath() + "#" + font.getTtcIndex();
			Typeface typeface = new Typeface.CustomFallbackBuilder(
				new FontFamily.Builder(font).build()
			).build();
			entries.add(new Entry(key, label, typeface));
		}

		entries.subList(1, entries.size()).sort(
			Comparator.comparing((Entry entry) -> entry.label, String.CASE_INSENSITIVE_ORDER)
				.thenComparing(entry -> entry.key)
		);
		return entries;
	}

	static Typeface resolve(String key) {
		if (key == null || DEFAULT_KEY.equals(key)) {
			return Typeface.DEFAULT;
		}
		for (Entry entry : load()) {
			if (entry.key.equals(key)) {
				return entry.typeface;
			}
		}
		return Typeface.DEFAULT;
	}
}
