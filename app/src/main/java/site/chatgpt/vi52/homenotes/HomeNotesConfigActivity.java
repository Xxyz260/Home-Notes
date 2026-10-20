package site.chatgpt.vi52.homenotes;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputType;
import android.view.Gravity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class HomeNotesConfigActivity extends Activity {
	private int appWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
	private EditText noteField;
	private TextView sizeLabel;
	private SeekBar sizeSeek;
	private Spinner fontSpinner;
	private EditText textColorField;
	private SeekBar textOpacitySeek;
	private TextView textOpacityLabel;
	private EditText backgroundColorField;
	private SeekBar backgroundOpacitySeek;
	private TextView backgroundOpacityLabel;
	private List<FontCatalog.Entry> fonts;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setResult(RESULT_CANCELED);

		Intent intent = getIntent();
		if (intent != null && intent.getExtras() != null) {
			appWidgetId = intent.getExtras().getInt(
				AppWidgetManager.EXTRA_APPWIDGET_ID,
				AppWidgetManager.INVALID_APPWIDGET_ID
			);
		}
		if (appWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
			finish();
			return;
		}

		setTitle(getString(R.string.app_name));
		setContentView(buildUi());
		loadValues();
	}

	private View buildUi() {
		int pad = dp(16);
		LinearLayout content = new LinearLayout(this);
		content.setOrientation(LinearLayout.VERTICAL);
		content.setPadding(pad, pad, pad, pad);

		content.addView(label("Note"));
		noteField = new EditText(this);
		noteField.setGravity(Gravity.TOP | Gravity.START);
		noteField.setMinLines(6);
		noteField.setInputType(
			InputType.TYPE_CLASS_TEXT
				| InputType.TYPE_TEXT_FLAG_MULTI_LINE
				| InputType.TYPE_TEXT_FLAG_CAP_SENTENCES
		);
		content.addView(noteField, matchWrap());

		sizeLabel = label("");
		content.addView(sizeLabel);
		sizeSeek = new SeekBar(this);
		sizeSeek.setMax(88);
		sizeSeek.setOnSeekBarChangeListener(simpleSeek(progress -> updateSizeLabel(progress + 8)));
		content.addView(sizeSeek, matchWrap());

		content.addView(label("Font"));
		fontSpinner = new Spinner(this);
		fonts = FontCatalog.load();
		List<String> fontLabels = new ArrayList<>();
		for (FontCatalog.Entry entry : fonts) {
			fontLabels.add(entry.label);
		}
		ArrayAdapter<String> fontAdapter = new ArrayAdapter<>(
			this,
			android.R.layout.simple_spinner_item,
			fontLabels
		);
		fontAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		fontSpinner.setAdapter(fontAdapter);
		content.addView(fontSpinner, matchWrap());

		content.addView(label("Text color (#RRGGBB)"));
		textColorField = colorField();
		content.addView(textColorField, matchWrap());
		textOpacityLabel = label("");
		content.addView(textOpacityLabel);
		textOpacitySeek = opacitySeek(textOpacityLabel, "Text opacity");
		content.addView(textOpacitySeek, matchWrap());

		content.addView(label("Background color (#RRGGBB)"));
		backgroundColorField = colorField();
		content.addView(backgroundColorField, matchWrap());
		backgroundOpacityLabel = label("");
		content.addView(backgroundOpacityLabel);
		backgroundOpacitySeek = opacitySeek(backgroundOpacityLabel, "Background opacity");
		content.addView(backgroundOpacitySeek, matchWrap());

		LinearLayout buttons = new LinearLayout(this);
		buttons.setOrientation(LinearLayout.HORIZONTAL);
		buttons.setGravity(Gravity.END);
		buttons.setPadding(0, dp(16), 0, 0);

		Button cancel = new Button(this);
		cancel.setText(android.R.string.cancel);
		cancel.setOnClickListener(view -> finish());
		buttons.addView(cancel);

		Button save = new Button(this);
		save.setText(android.R.string.ok);
		save.setOnClickListener(view -> save());
		buttons.addView(save);
		content.addView(buttons, matchWrap());

		ScrollView scroll = new ScrollView(this);
		scroll.addView(content);
		return scroll;
	}

	private void loadValues() {
		SharedPreferences prefs = WidgetPrefs.get(this);
		int settingsId = WidgetPrefs.settingsSource(prefs, appWidgetId);
		noteField.setText(prefs.getString(
			WidgetPrefs.key(appWidgetId, "text"),
			getString(R.string.default_note)
		));

		int size = prefs.getInt(
			WidgetPrefs.key(settingsId, "size"),
			WidgetPrefs.DEFAULT_TEXT_SIZE
		);
		sizeSeek.setProgress(Math.max(0, Math.min(88, size - 8)));
		updateSizeLabel(size);

		String fontKey = prefs.getString(
			WidgetPrefs.key(settingsId, "font"),
			FontCatalog.DEFAULT_KEY
		);
		int fontIndex = 0;
		for (int i = 0; i < fonts.size(); i++) {
			if (fonts.get(i).key.equals(fontKey)) {
				fontIndex = i;
				break;
			}
		}
		fontSpinner.setSelection(fontIndex);

		textColorField.setText(prefs.getString(
			WidgetPrefs.key(settingsId, "text_color"),
			WidgetPrefs.defaultTextColor(this)
		));
		setOpacity(
			textOpacitySeek,
			textOpacityLabel,
			"Text opacity",
			prefs.getInt(
				WidgetPrefs.key(settingsId, "text_opacity"),
				WidgetPrefs.DEFAULT_TEXT_OPACITY
			)
		);

		backgroundColorField.setText(prefs.getString(
			WidgetPrefs.key(settingsId, "background_color"),
			WidgetPrefs.defaultBackgroundColor(this)
		));
		setOpacity(
			backgroundOpacitySeek,
			backgroundOpacityLabel,
			"Background opacity",
			prefs.getInt(
				WidgetPrefs.key(settingsId, "background_opacity"),
				WidgetPrefs.DEFAULT_BACKGROUND_OPACITY
			)
		);
	}

	private void save() {
		String textColor = normalizedColor(textColorField);
		String backgroundColor = normalizedColor(backgroundColorField);
		if (textColor == null || backgroundColor == null) {
			Toast.makeText(this, "Use six hexadecimal digits for colors.", Toast.LENGTH_SHORT).show();
			return;
		}

		FontCatalog.Entry font = fonts.get(fontSpinner.getSelectedItemPosition());
		SharedPreferences.Editor editor = WidgetPrefs.get(this).edit()
			.putString(WidgetPrefs.key(appWidgetId, "text"), noteField.getText().toString());
		for (int settingsId : new int[] { appWidgetId, WidgetPrefs.LATEST_SETTINGS_ID }) {
			editor
				.putInt(WidgetPrefs.key(settingsId, "size"), sizeSeek.getProgress() + 8)
				.putString(WidgetPrefs.key(settingsId, "font"), font.key)
				.putString(WidgetPrefs.key(settingsId, "text_color"), textColor)
				.putInt(WidgetPrefs.key(settingsId, "text_opacity"), textOpacitySeek.getProgress())
				.putString(WidgetPrefs.key(settingsId, "background_color"), backgroundColor)
				.putInt(WidgetPrefs.key(settingsId, "background_opacity"), backgroundOpacitySeek.getProgress());
		}
		editor.apply();

		HomeNotesWidget.update(
			this,
			AppWidgetManager.getInstance(this),
			appWidgetId
		);

		Intent result = new Intent().putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
		setResult(RESULT_OK, result);
		finish();
	}

	private EditText colorField() {
		EditText field = new EditText(this);
		field.setSingleLine(true);
		field.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS);
		return field;
	}

	private SeekBar opacitySeek(TextView label, String prefix) {
		SeekBar seek = new SeekBar(this);
		seek.setMax(100);
		seek.setOnSeekBarChangeListener(simpleSeek(progress -> label.setText(
			String.format(Locale.ROOT, "%s: %d%%", prefix, progress)
		)));
		return seek;
	}

	private void setOpacity(SeekBar seek, TextView label, String prefix, int value) {
		int bounded = Math.max(0, Math.min(100, value));
		seek.setProgress(bounded);
		label.setText(String.format(Locale.ROOT, "%s: %d%%", prefix, bounded));
	}

	private void updateSizeLabel(int size) {
		sizeLabel.setText(String.format(Locale.ROOT, "Text size: %d sp", size));
	}

	private String normalizedColor(EditText field) {
		String value = field.getText().toString().trim().replace("#", "").toUpperCase(Locale.ROOT);
		if (!value.matches("[0-9A-F]{6}")) {
			return null;
		}
		try {
			Color.parseColor("#" + value);
			return value;
		} catch (IllegalArgumentException ignored) {
			return null;
		}
	}

	private TextView label(String text) {
		TextView label = new TextView(this);
		label.setText(text);
		label.setPadding(0, dp(12), 0, dp(4));
		return label;
	}

	private LinearLayout.LayoutParams matchWrap() {
		return new LinearLayout.LayoutParams(
			LinearLayout.LayoutParams.MATCH_PARENT,
			LinearLayout.LayoutParams.WRAP_CONTENT
		);
	}

	private int dp(int value) {
		return Math.round(value * getResources().getDisplayMetrics().density);
	}

	private SeekBar.OnSeekBarChangeListener simpleSeek(IntConsumer consumer) {
		return new SeekBar.OnSeekBarChangeListener() {
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				consumer.accept(progress);
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {}

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {}
		};
	}

	private interface IntConsumer {
		void accept(int value);
	}
}
