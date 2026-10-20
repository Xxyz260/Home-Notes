package site.chatgpt.vi52.homenotes;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.TypefaceSpan;
import android.util.TypedValue;
import android.widget.RemoteViews;

public class HomeNotesWidget extends AppWidgetProvider {
	@Override
	public void onUpdate(Context context, AppWidgetManager manager, int[] appWidgetIds) {
		for (int appWidgetId : appWidgetIds) {
			update(context, manager, appWidgetId);
		}
	}

	@Override
	public void onDeleted(Context context, int[] appWidgetIds) {
		for (int appWidgetId : appWidgetIds) {
			WidgetPrefs.delete(context, appWidgetId);
		}
	}

	static void update(Context context, AppWidgetManager manager, int appWidgetId) {
		SharedPreferences prefs = WidgetPrefs.get(context);
		String text = prefs.getString(
			WidgetPrefs.key(appWidgetId, "text"),
			context.getString(R.string.default_note)
		);
		int size = prefs.getInt(
			WidgetPrefs.key(appWidgetId, "size"),
			WidgetPrefs.DEFAULT_TEXT_SIZE
		);
		String fontKey = prefs.getString(
			WidgetPrefs.key(appWidgetId, "font"),
			FontCatalog.DEFAULT_KEY
		);
		String textColor = prefs.getString(
			WidgetPrefs.key(appWidgetId, "text_color"),
			WidgetPrefs.DEFAULT_TEXT_COLOR
		);
		int textOpacity = prefs.getInt(
			WidgetPrefs.key(appWidgetId, "text_opacity"),
			WidgetPrefs.DEFAULT_TEXT_OPACITY
		);
		String backgroundColor = prefs.getString(
			WidgetPrefs.key(appWidgetId, "background_color"),
			WidgetPrefs.DEFAULT_BACKGROUND_COLOR
		);
		int backgroundOpacity = prefs.getInt(
			WidgetPrefs.key(appWidgetId, "background_opacity"),
			WidgetPrefs.DEFAULT_BACKGROUND_OPACITY
		);

		RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_note);
		views.setTextViewText(R.id.note_text, styledText(text, FontCatalog.resolve(fontKey)));
		views.setTextViewTextSize(R.id.note_text, TypedValue.COMPLEX_UNIT_SP, size);
		views.setTextColor(R.id.note_text, WidgetPrefs.color(textColor, textOpacity));
		views.setInt(
			R.id.note_text,
			"setBackgroundColor",
			WidgetPrefs.color(backgroundColor, backgroundOpacity)
		);

		Intent editIntent = new Intent(context, HomeNotesConfigActivity.class)
			.setAction(AppWidgetManager.ACTION_APPWIDGET_CONFIGURE)
			.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
		PendingIntent editPendingIntent = PendingIntent.getActivity(
			context,
			appWidgetId,
			editIntent,
			PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
		);
		views.setOnClickPendingIntent(R.id.note_text, editPendingIntent);
		manager.updateAppWidget(appWidgetId, views);
	}

	private static CharSequence styledText(String text, Typeface typeface) {
		if (text.isEmpty() || typeface == Typeface.DEFAULT) {
			return text;
		}
		SpannableString styled = new SpannableString(text);
		styled.setSpan(
			new TypefaceSpan(typeface),
			0,
			styled.length(),
			Spannable.SPAN_INCLUSIVE_INCLUSIVE
		);
		return styled;
	}
}
