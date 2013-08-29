package de.onyxbits.listmyapps;

import java.util.ArrayList;
import java.util.Iterator;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.text.ClipboardManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

public class MainActivity extends Activity implements OnItemSelectedListener, OnItemClickListener {

	protected ArrayList<SortablePackageInfo> apps;
	private int formatIndex;
	private String[] formatTypes;

	public static final String PREFSFILE = "settings";
	private static final String ALWAYS_GOOGLE_PLAY = "always_link_to_google_play";
	private static final String FORMATTYPE = "formattype";
	public static final String SELECTED = "selected";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_PROGRESS);
		setContentView(R.layout.activity_main);
		setProgressBarIndeterminate(true);
		setProgressBarVisibility(true);
		CheckBox checkbox = (CheckBox) findViewById(R.id.always_link);
		Spinner spinner = (Spinner) findViewById(R.id.format_select);
		ListView listView = (ListView) findViewById(R.id.listView1);
		listView.setOnItemClickListener(this);
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
				R.array.formatnames, android.R.layout.simple_spinner_item);
		adapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(adapter);
		spinner.setOnItemSelectedListener(this);
		formatTypes = getResources().getStringArray(R.array.formatnames);
		SharedPreferences prefs = getSharedPreferences(PREFSFILE, 0);
		checkbox.setChecked(prefs.getBoolean((ALWAYS_GOOGLE_PLAY), false));
		formatIndex = prefs.getInt(FORMATTYPE, 0);
		spinner.setSelection(formatIndex);
		new ListTask(this, listView).execute("");
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		SharedPreferences.Editor editor = getSharedPreferences(PREFSFILE, 0).edit();
		editor.putBoolean(ALWAYS_GOOGLE_PLAY,
				((CheckBox) findViewById(R.id.always_link)).isChecked());
		editor.putInt(FORMATTYPE, formatIndex);
		if (apps != null) {
			Iterator<SortablePackageInfo> it = apps.iterator();
			while (it.hasNext()) {
				SortablePackageInfo spi = it.next();
				editor.putBoolean(SELECTED + "." + spi.packageName, spi.selected);
			}
		}
		editor.commit();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (apps == null) {
			return true;
		}

		switch (item.getItemId()) {
			case R.id.share: {
				if (isNothingSelected()) {
					return true;
				}
				Intent sendIntent = new Intent();
				sendIntent.setAction(Intent.ACTION_SEND);
				sendIntent.putExtra(Intent.EXTRA_TEXT, buildList().toString());
				sendIntent.setType("text/plain");
				startActivity(sendIntent);
				break;
			}
			case R.id.copy: {
				if (isNothingSelected()) {
					return true;
				}
				ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
				clipboard.setText(buildList().toString());
				Toast.makeText(this, R.string.list_copied_to_clipboard,
						Toast.LENGTH_SHORT).show();
				break;
			}
			case (R.id.deselect_all): {
				Iterator<SortablePackageInfo> it = apps.iterator();
				while (it.hasNext()) {
					SortablePackageInfo spi = it.next();
					spi.selected = false;
					((AppAdapter) ((ListView) findViewById(R.id.listView1)).getAdapter())
							.notifyDataSetChanged();
				}
				break;
			}
			case (R.id.select_all): {
				Iterator<SortablePackageInfo> it = apps.iterator();
				while (it.hasNext()) {
					SortablePackageInfo spi = it.next();
					spi.selected = true;
					((AppAdapter) ((ListView) findViewById(R.id.listView1)).getAdapter())
							.notifyDataSetChanged();
				}
				break;
			}
		}
		return true;
	}

	private CharSequence buildList() {
		StringBuilder ret = new StringBuilder();
		Iterator<SortablePackageInfo> it = apps.iterator();
		boolean toMarket = ((CheckBox) findViewById(R.id.always_link)).isChecked();
		while (it.hasNext()) {
			SortablePackageInfo spi = it.next();
			if (spi.selected) {
				String tmp = spi.installer;
				if (toMarket) {
					tmp = "com.google.vending";
				}
				String marketLink = getMarketLink(tmp, spi.packageName);

				switch (formatIndex) {
					case 0: { // Plain Text
						ret.append(spi.displayName);
						ret.append("\n\t");
						ret.append(spi.packageName);
						ret.append("\n");
						break;
					}
					case 1: { // HTML list
						ret.append("<li>");
						if (marketLink == null) {
							ret.append(spi.displayName);
						}
						else {
							ret.append("<a href=\"");
							ret.append(marketLink);
							ret.append("\">");
							ret.append(spi.displayName);
							ret.append("</a>");
						}
						ret.append("</li>\n");
						break;
					}
					case 2: { // BBCode
						ret.append("[*] ");
						if (marketLink == null) {
							ret.append(spi.displayName);
							ret.append("\n");
						}
						else {
							ret.append("[url=");
							ret.append(marketLink);
							ret.append("]");
							ret.append(spi.displayName);
							ret.append("[/url]\n");
						}
						break;
					}
					case 3: { // Markdown
						ret.append("* ");
						if (marketLink == null) {
							ret.append(spi.displayName);
							ret.append("\n");
						}
						else {
							ret.append("[");
							ret.append(spi.displayName);
							ret.append("](");
							ret.append(marketLink);
							ret.append(")\n");
						}
						break;
					}
				}
			}
		}
		return ret;
	}

	public boolean isNothingSelected() {
		if (apps != null) {
			Iterator<SortablePackageInfo> it = apps.iterator();
			while (it.hasNext()) {
				if (it.next().selected) {
					return false;
				}
			}
		}
		Toast
				.makeText(this, R.string.warn_nothing_selected, Toast.LENGTH_LONG)
				.show();
		return true;
	}

	public String getMarketLink(String installer, String packname) {
		if (installer == null) {
			return null;
		}
		if (installer.startsWith("com.google")) {
			return "https://play.google.com/store/apps/details?id=" + packname;
		}
		if (installer.startsWith("org.fdroid")) {
			return "https://f-droid.org/repository/browse/?fdid=" + packname;
		}

		return null;
	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position,
			long id) {
		String tmp = parent.getItemAtPosition(position).toString();
		for (int i = 0; i < formatTypes.length; i++) {
			if (tmp.equals(formatTypes[i])) {
				formatIndex = i;
				break;
			}
		}
	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {

	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		((CheckBox)view.findViewById(R.id.selected)).toggle();
	}

}
