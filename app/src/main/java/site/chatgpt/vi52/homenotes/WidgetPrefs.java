package site.chatgpt.vi52.homenotes;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;

final class WidgetPrefs {
	static final String DEFAULT_TEXT_COLOR = "000000";
	static final String DEFAULT_BACKGROUND_COLOR = "FFFFFF";
	static final int DEFAULT_TEXT_OPACITY = 100;
	static final int DEFAULT_BACKGROUND_OPACITY = 80;
	static final int DEFAULT_TEXT_SIZE = 18;

	private WidgetPrefs() {}

	static SharedPreferences get(Context context) {
		return context.getSharedPreferences("home_notes", Context.MODE_PRIVATE);
	}

	static String key(int widgetId, String name) {
		return widgetId + ":" + name;
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
