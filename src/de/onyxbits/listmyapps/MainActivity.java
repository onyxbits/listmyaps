package de.onyxbits.listmyapps;

import java.sql.Date;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.text.ClipboardManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity implements OnItemSelectedListener,
		OnItemClickListener, OnItemLongClickListener {

	protected ArrayList<SortablePackageInfo> apps;
	private TemplateSource templateSource;
	private TemplateData template;

	public static final String PREFSFILE = "settings";
	private static final String ALWAYS_GOOGLE_PLAY = "always_link_to_google_play";
	private static final String TEMPLATEID = "templateid";
	public static final String SELECTED = "selected";

	@Override
	protected void onCreate(Bundle b) {
		super.onCreate(b);
		requestWindowFeature(Window.FEATURE_PROGRESS);
		setContentView(R.layout.activity_main);
		setProgressBarIndeterminate(true);
		setProgressBarVisibility(true);
		ListView listView = (ListView) findViewById(R.id.applist);
		listView.setOnItemClickListener(this);
		listView.setOnItemLongClickListener(this);
		new ListTask(this).execute("");
		AppRater.appLaunched(this);
	}

	@Override
	protected void onResume() {
		super.onResume();
		CheckBox checkbox = (CheckBox) findViewById(R.id.always_gplay);
		Spinner spinner = (Spinner) findViewById(R.id.format_select);
		templateSource = new TemplateSource(this);
		templateSource.open();

		List<TemplateData> formats = templateSource.list();
		ArrayAdapter<TemplateData> adapter = new ArrayAdapter<TemplateData>(this,
				android.R.layout.simple_spinner_item, formats);
		adapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(adapter);
		spinner.setOnItemSelectedListener(this);
		SharedPreferences prefs = getSharedPreferences(PREFSFILE, 0);
		checkbox.setChecked(prefs.getBoolean((ALWAYS_GOOGLE_PLAY), false));
		int selection = 0;
		Iterator<TemplateData> it = formats.iterator();
		int count = 0;
		while (it.hasNext()) {
			template = it.next();
			if (template.id == prefs.getLong(TEMPLATEID, 0)) {
				selection = count;
				break;
			}
			template = null;
			count++;
		}
		spinner.setSelection(selection);
	}

	@Override
	public void onPause() {
		super.onPause();
		SharedPreferences.Editor editor = getSharedPreferences(PREFSFILE, 0).edit();
		editor.putBoolean(ALWAYS_GOOGLE_PLAY,
				((CheckBox) findViewById(R.id.always_gplay)).isChecked());
		if (template != null) {
			editor.putLong(TEMPLATEID, template.id);
		}
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
				if (!isNothingSelected()) {
					Intent sendIntent = new Intent();
					sendIntent.setAction(Intent.ACTION_SEND);
					sendIntent.putExtra(Intent.EXTRA_TEXT, buildOutput().toString());
					sendIntent.setType("text/plain");
					startActivity(Intent.createChooser(sendIntent, getResources()
							.getText(R.string.send_to)));
				}
				break;
			}
			case R.id.copy: {
				if (!isNothingSelected()) {
					ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
					clipboard.setText(buildOutput().toString());
					Toast.makeText(this, R.string.list_copied_to_clipboard,
							Toast.LENGTH_SHORT).show();
				}
				break;
			}
			case (R.id.deselect_all): {
				Iterator<SortablePackageInfo> it = apps.iterator();
				while (it.hasNext()) {
					SortablePackageInfo spi = it.next();
					spi.selected = false;
					((AppAdapter) ((ListView) findViewById(R.id.applist)).getAdapter())
							.notifyDataSetChanged();
				}
				break;
			}
			case (R.id.select_all): {
				Iterator<SortablePackageInfo> it = apps.iterator();
				while (it.hasNext()) {
					SortablePackageInfo spi = it.next();
					spi.selected = true;
					((AppAdapter) ((ListView) findViewById(R.id.applist)).getAdapter())
							.notifyDataSetChanged();
				}
				break;
			}
			case (R.id.edit_templates): {
				startActivity(new Intent(this, TemplatesActivity.class));
				break;
			}
		}
		return true;
	}

	/**
	 * Construct what is to be shared/copied to the clipboard
	 * 
	 * @return the output for sharing.
	 */
	private CharSequence buildOutput() {
		StringBuilder ret = new StringBuilder();
		DateFormat df = DateFormat.getDateTimeInstance();
		Iterator<SortablePackageInfo> it = apps.iterator();
		boolean alwaysGP = ((CheckBox) findViewById(R.id.always_gplay)).isChecked();

		if (template == null) {
			return getString(R.string.error_no_templates);
		}

		ret.append(template.header);
		while (it.hasNext()) {
			SortablePackageInfo spi = it.next();
			if (spi.selected) {
				String tmp = spi.installer;
				if (alwaysGP) {
					tmp = "com.google.vending";
				}
				String firstInstalled = df.format(new Date(spi.firstInstalled));
				String lastUpdated = df.format(new Date(spi.lastUpdated));
				String sourceLink = createSourceLink(tmp, spi.packageName);
				String tmpl = template.item
						.replace("${packagename}", noNull(spi.packageName))
						.replace("${displayname}", noNull(spi.displayName))
						.replace("${source}", noNull(sourceLink))
						.replace("${comment}", noNull(spi.comment))
						.replace("${versioncode}", "" + spi.versionCode)
						.replace("${version}", noNull(spi.version))
						.replace("${uid}", "" + spi.uid)
						.replace("${firstinstalled}", firstInstalled)
						.replace("${lastupdated}", lastUpdated)
						.replace("${datadir}", noNull(spi.dataDir))
						.replace("${marketid}", noNull(spi.installer));
				ret.append(tmpl);
			}
		}
		ret.append(template.footer);
		return ret;
	}

	/**
	 * Make sure a string is not null
	 * 
	 * @param input
	 *          the string to check
	 * @return the input string or an empty string if the input was null.
	 */
	public static String noNull(String input) {
		if (input == null) {
			return "";
		}
		return input;
	}

	/**
	 * Check if at least one app is selected. Pop up a toast if none is selected.
	 * 
	 * @return true if no app is selected.
	 */
	public boolean isNothingSelected() {
		if (apps != null) {
			Iterator<SortablePackageInfo> it = apps.iterator();
			while (it.hasNext()) {
				if (it.next().selected) {
					return false;
				}
			}
		}
		Toast.makeText(this, R.string.warn_nothing_selected, Toast.LENGTH_LONG)
				.show();
		return true;
	}

	/**
	 * Figure out from where an app can be downloaded
	 * 
	 * @param installer
	 *          id of the installing app or null if unknown.
	 * @param packname
	 *          pacakgename of the app
	 * @return a url containing a market link. If no market can be determined, a
	 *         search engine link is returned.
	 */
	public static String createSourceLink(String installer, String packname) {
		if (installer == null) {
			return "https://www.google.com/search?q=" + packname;
		}
		if (installer.startsWith("com.google")) {
			return "https://play.google.com/store/apps/details?id=" + packname;
		}
		if (installer.startsWith("com.android")) {
			return "https://play.google.com/store/apps/details?id=" + packname;
		}
		if (installer.startsWith("org.fdroid")) {
			return "https://f-droid.org/repository/browse/?fdid=" + packname;
		}
		if (installer.startsWith("com.amazon")) {
			return "http://www.amazon.com/gp/mas/dl/android?p=" + packname;
		}
		return "https://www.google.com/search?q=" + packname;
	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View v, int pos, long id) {
		template = (TemplateData) parent.getAdapter().getItem(pos);
	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {

	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		ListView listView = (ListView) findViewById(R.id.applist);
		AppAdapter aa = (AppAdapter) listView.getAdapter();
		SortablePackageInfo spi = aa.getItem(position);
		spi.selected = !spi.selected;
		aa.notifyDataSetChanged();
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view,
			int position, long id) {
		ListView listView = (ListView) findViewById(R.id.applist);
		AppAdapter aa = (AppAdapter) listView.getAdapter();
		SortablePackageInfo spi = aa.getItem(position);
		View content = getLayoutInflater().inflate(R.layout.details, null);
		ScrollView scrollView = new ScrollView(this);
		scrollView.addView(content);

		DateFormat df = DateFormat.getDateTimeInstance();
		((TextView) content.findViewById(R.id.lbl_val_version))
				.setText(spi.version);
		((TextView) content.findViewById(R.id.lbl_val_versioncode)).setText(""
				+ spi.versionCode);
		((TextView) content.findViewById(R.id.lbl_val_installed)).setText(df
				.format(new Date(spi.firstInstalled)));
		((TextView) content.findViewById(R.id.lbl_val_updated)).setText(df
				.format(new Date(spi.lastUpdated)));
		((TextView) content.findViewById(R.id.lbl_val_uid)).setText("" + spi.uid);
		((TextView) content.findViewById(R.id.lbl_val_datadir))
				.setText(spi.dataDir);

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(spi.displayName).setIcon(spi.icon).setView(scrollView)
				.setNegativeButton(null, null).setPositiveButton(null, null)
				.setNeutralButton(null, null).show();
		return true;
	}

}
