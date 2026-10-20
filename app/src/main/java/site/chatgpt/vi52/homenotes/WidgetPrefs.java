package site.chatgpt.vi52.homenotes;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;

import java.util.Locale;

final class WidgetPrefs {
	static final int DEFAULT_TEXT_OPACITY = 100;
	static final int DEFAULT_BACKGROUND_OPACITY = 100;
	static final int DEFAULT_TEXT_SIZE = 24;
	// Snapshot independent of widget lifetime; Android widget IDs are nonnegative.
	static final int LATEST_SETTINGS_ID = -1;

	private WidgetPrefs() {}

	static SharedPreferences get(Context context) {
		return context.getSharedPreferences("home_notes", Context.MODE_PRIVATE);
	}

	static String key(int widgetId, String name) {
		return widgetId + ":" + name;
	}

	static int settingsSource(SharedPreferences prefs, int widgetId) {
		return prefs.contains(key(widgetId, "text")) ? widgetId : LATEST_SETTINGS_ID;
	}

	static String defaultTextColor(Context context) {
		return rgb(context.getColor(R.color.widget_text));
	}

	static String defaultBackgroundColor(Context context) {
		return rgb(context.getColor(R.color.widget_background));
	}

	private static String rgb(int color) {
		return String.format(Locale.ROOT, "%06X", color & 0xFFFFFF);
	}

	static int color(String rgb, int opacityPercent) {
		int opaque = Color.parseColor("#" + rgb);
		int alpha = Math.round(255f * opacityPercent / 100f);
		return Color.argb(alpha, Color.red(opaque), Color.green(opaque), Color.blue(opaque));
	}

	static void delete(Context context, int widgetId) {
		SharedPreferences prefs = get(context);
		SharedPreferences.Editor editor = prefs.edit();
		for (String name : new String[] {
			"text", "size", "font", "text_color", "text_opacity",
			"background_color", "background_opacity"
		}) {
			editor.remove(key(widgetId, name));
		}
		editor.apply();
	}
}
