package de.onyxbits.listmyapps;

import java.sql.Date;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;


import android.net.Uri;
import android.os.Bundle;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.text.ClipboardManager;
import android.util.Log;
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
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

public class MainActivity extends ListActivity implements
		OnItemSelectedListener, OnItemClickListener, OnItemLongClickListener {

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
		ListView listView = getListView();
		listView.setOnItemClickListener(this);
		listView.setOnItemLongClickListener(this);
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
		setListAdapter(new AppAdapter(this, R.layout.app_item,
				new ArrayList<SortablePackageInfo>(), R.layout.app_item));
		new ListTask(this, R.layout.app_item).execute("");
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
		ListAdapter adapter = getListAdapter();
		int count = adapter.getCount();
		for (int i = 0; i < count; i++) {
			SortablePackageInfo spi = (SortablePackageInfo) adapter.getItem(i);
			editor.putBoolean(SELECTED + "." + spi.packageName, spi.selected);
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

		switch (item.getItemId()) {
			case R.id.share: {
				if (!isNothingSelected()) {
					CharSequence buf = buildOutput();
					if (buf.length() < 90 * 1024) {
						// Intents get sent through the Binder and according to the
						// TransactionLogTooLargeException docs that thing has a fixed
						// size, shared buffer (1mb max). There is no telling how much
						// free space there really is, but exceeding the limit will crash
						// the device. Restricting ourselves to 90kb (found by trial and
						// error) is anything but pretty, but still better than forcing
						// users to reboot. Curiously, the copy&paste buffer can handle
						// more.
						Intent sendIntent = new Intent();
						sendIntent.setAction(Intent.ACTION_SEND);
						sendIntent.putExtra(Intent.EXTRA_TEXT, buf.toString());
						sendIntent.setType("text/plain");
						startActivity(Intent.createChooser(sendIntent, getResources()
								.getText(R.string.title_send_to)));
						break;
					}
					else {
						Toast.makeText(this, R.string.msg_too_large, Toast.LENGTH_SHORT)
								.show();
						// Fallthrough to copy!
					}
				}
			}
			case R.id.copy: {
				if (!isNothingSelected()) {
					ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
					clipboard.setText(buildOutput().toString());
					ListAdapter adapter = getListAdapter();
					int count = adapter.getCount();
					int selected = 0;

					for (int i = 0; i < count; i++) {
						SortablePackageInfo spi = (SortablePackageInfo) adapter.getItem(i);
						if (spi.selected) {
							selected++;
						}
					}
					Toast
							.makeText(
									this,
									getString(R.string.msg_list_copied_to_clipboard, selected,
											count), Toast.LENGTH_SHORT).show();
				}
				break;
			}
			case (R.id.deselect_all): {
				ListAdapter adapter = getListAdapter();
				int count = adapter.getCount();
				for (int i = 0; i < count; i++) {
					SortablePackageInfo spi = (SortablePackageInfo) adapter.getItem(i);
					spi.selected = false;
				}
				((AppAdapter) adapter).notifyDataSetChanged();
				break;
			}
			case (R.id.select_all): {
				ListAdapter adapter = getListAdapter();
				int count = adapter.getCount();
				for (int i = 0; i < count; i++) {
					SortablePackageInfo spi = (SortablePackageInfo) adapter.getItem(i);
					spi.selected = true;
				}
				((AppAdapter) adapter).notifyDataSetChanged();
				break;
			}

			case (R.id.annotations): {
				startActivity(new Intent(this, AnnotationsActivity.class));
				break;
			}
			case (R.id.edit_templates): {
				startActivity(new Intent(this, TemplatesActivity.class));
				break;
			}
			case (R.id.bytag): {
				TagSelectionListener tsl = new TagSelectionListener(getListAdapter());
				AlertDialog.Builder builder = new AlertDialog.Builder(this);
				if (tsl.listTags().length == 0) {
					builder.setTitle(R.string.title_select_apps_by_tag).setMessage(
							R.string.msg_no_apps_tagged);
				}
				else {
					builder.setTitle(R.string.title_select_apps_by_tag)
							.setMultiChoiceItems(tsl.listTags(), null, tsl)
							.setPositiveButton(android.R.string.ok, tsl);
				}
				builder.show();
				break;
			}
			case (R.id.item_help): {
				Uri uri = Uri.parse(getString(R.string.url_help));
				MainActivity.openUri(this,uri);
				return true;
			} 
			case (R.id.browse): {
				if (!isNothingSelected()) {
					doStumble();
				}
				break;
			}
		}
		return true;
	}
	
	/**
	 * Share with the world.
	 */
	private void doStumble() {
		ListAdapter adapter = getListAdapter();
		int count = adapter.getCount();
		ArrayList<String> collect = new ArrayList<String>(); 
		for (int i = 0; i < count; i++) {
			SortablePackageInfo spi = (SortablePackageInfo) adapter.getItem(i);
			if (spi.selected) {
				collect.add(spi.packageName);
			}
		}
		
		Collections.shuffle(collect);
		StringBuilder sb = new StringBuilder();
		for (int i=0;i<collect.size();i++) {
			if (sb.length()>0) {
				sb.append(",");
			}
			sb.append(collect.get(i));
			if (sb.length()>200) {
				break; // prevent the url from growing overly large. 
			}
		}
		openUri(this,Uri.parse(getString(R.string.url_browse,sb.toString())));
	}

	/**
	 * Construct what is to be shared/copied to the clipboard
	 * 
	 * @return the output for sharing.
	 */
	private CharSequence buildOutput() {
		if (template == null) {
			return getString(R.string.msg_error_no_templates);
		}

		StringBuilder ret = new StringBuilder();
		DateFormat df = DateFormat.getDateTimeInstance();
		boolean alwaysGP = ((CheckBox) findViewById(R.id.always_gplay)).isChecked();
		ListAdapter adapter = getListAdapter();
		int count = adapter.getCount();

		String now = java.text.DateFormat.getDateTimeInstance().format(
				Calendar.getInstance().getTime());
		int selected = 0;

		for (int i = 0; i < count; i++) {
			SortablePackageInfo spi = (SortablePackageInfo) adapter.getItem(i);
			if (spi.selected) {
				selected++;
				String tmp = spi.installer;
				if (alwaysGP) {
					tmp = "com.google.vending";
				}
				String firstInstalled = df.format(new Date(spi.firstInstalled));
				String lastUpdated = df.format(new Date(spi.lastUpdated));
				String sourceLink = createSourceLink(tmp, spi.packageName);
				String tmpl = template.item.replace("${comment}", noNull(spi.comment))
						.replace("${tags}",noNull(spi.tags))
						.replace("${packagename}", noNull(spi.packageName))
						.replace("${displayname}", noNull(spi.displayName))
						.replace("${source}", noNull(sourceLink))
						.replace("${versioncode}", "" + spi.versionCode)
						.replace("${targetsdk}", "" + spi.targetsdk)
						.replace("${version}", noNull(spi.version))
						.replace("${rating}", "" + spi.rating)
						.replace("${uid}", "" + spi.uid)
						.replace("${firstinstalled}", firstInstalled)
						.replace("${lastupdated}", lastUpdated)
						.replace("${datadir}", noNull(spi.dataDir))
						.replace("${marketid}", noNull(spi.installer));
				ret.append(tmpl);
			}
		}
		ret.insert(
				0,
				template.header.replace("${now}", now).replace("${count}",
						"" + selected));
		ret.append(template.footer.replace("${now}", now).replace("${count}",
				"" + selected));
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
		ListAdapter adapter = getListAdapter();
		if (adapter != null) {
			int count = adapter.getCount();
			for (int i = 0; i < count; i++) {
				SortablePackageInfo spi = (SortablePackageInfo) adapter.getItem(i);
				if (spi.selected) {
					return false;
				}
			}
		}
		Toast.makeText(this, R.string.msg_warn_nothing_selected, Toast.LENGTH_LONG)
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
		AppAdapter aa = (AppAdapter) getListAdapter();
		SortablePackageInfo spi = aa.getItem(position);
		spi.selected = !spi.selected;
		aa.notifyDataSetChanged();
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view,
			int position, long id) {
		AppAdapter aa = (AppAdapter) getListAdapter();
		SortablePackageInfo spi = aa.getItem(position);

		try {
			// FIXME: This intent is only available in Gingerbread and up. I don't
			// want to ditch Froyo, yet. I don't want to implement a giant
			// compatibility cludge either, so dirty hack compromise: use the value
			// android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS directly
			// that way it works on newer droids and silently fails without crashing
			// on Froyo.
			Intent i = new Intent("android.settings.APPLICATION_DETAILS_SETTINGS");
			i.addCategory(Intent.CATEGORY_DEFAULT);
			i.setData(Uri.parse("package:" + spi.packageName));
			startActivity(i);
		}
		catch (Exception e) {
			Log.w(getClass().getName(), e);
		}

		return true;
	}
	
	/**
	 * Open an url in a webbrowser
	 * 
	 * @param ctx
	 *          a context
	 * @param uri
	 *          target
	 */
	public static void openUri(Context ctx, Uri uri) {
		try {
			Intent browserIntent = new Intent(Intent.ACTION_VIEW, uri);
			ctx.startActivity(browserIntent);
		}
		catch (ActivityNotFoundException e) {
			// There are actually people who don't have a webbrowser installed
			Toast.makeText(ctx, de.onyxbits.listmyapps.R.string.msg_no_webbrowser, Toast.LENGTH_SHORT)
					.show();
		}
	}

}
