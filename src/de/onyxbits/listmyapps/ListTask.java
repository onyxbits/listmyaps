package de.onyxbits.listmyapps;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.AsyncTask;
import android.widget.ListView;

/**
 * Query the packagemanager for a list of all installed apps that are not system
 * apps. Populate a listview with the result.
 * 
 * @author patrick
 * 
 */
public class ListTask extends
		AsyncTask<Object, Object, ArrayList<SortablePackageInfo>> {

	private MainActivity mainActivity;

	/**
	 * New task
	 * 
	 * @param mainActivity
	 *          context reference
	 */
	public ListTask(MainActivity mainActivity) {
		this.mainActivity = mainActivity;
	}

	@Override
	protected ArrayList<SortablePackageInfo> doInBackground(Object... params) {
		SharedPreferences prefs = mainActivity.getSharedPreferences(
				MainActivity.PREFSFILE, 0);
		ArrayList<SortablePackageInfo> ret = new ArrayList<SortablePackageInfo>();
		PackageManager pm = mainActivity.getPackageManager();
		List<PackageInfo> list = pm.getInstalledPackages(0);
		SortablePackageInfo spitmp[] = new SortablePackageInfo[list.size()];
		Iterator<PackageInfo> it = list.iterator();
		int idx = 0;
		while (it.hasNext()) {
			PackageInfo info = it.next();
			try {
				ApplicationInfo ai = pm.getApplicationInfo(info.packageName, 0);
				if ((ai.flags & ApplicationInfo.FLAG_SYSTEM) != ApplicationInfo.FLAG_SYSTEM
						&& (ai.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) {
					spitmp[idx] = new SortablePackageInfo();
					spitmp[idx].packageName = info.packageName;
					spitmp[idx].displayName = pm
							.getApplicationLabel(info.applicationInfo).toString();
					spitmp[idx].installer = pm.getInstallerPackageName(info.packageName);
					spitmp[idx].icon = ai.loadIcon(pm);
					spitmp[idx].versionCode = info.versionCode;
					spitmp[idx].version = info.versionName;
					spitmp[idx].firstInstalled = info.firstInstallTime;
					spitmp[idx].lastUpdated = info.lastUpdateTime;
					spitmp[idx].uid = info.applicationInfo.uid;
					spitmp[idx].dataDir = info.applicationInfo.dataDir;
					idx++;
				}
			}
			catch (NameNotFoundException exp) {
			}
		}
		// Reminder: the copying is necessary because we are filtering away the
		// system apps.
		SortablePackageInfo spi[] = new SortablePackageInfo[idx];
		System.arraycopy(spitmp, 0, spi, 0, idx);
		Arrays.sort(spi);
		for (int i = 0; i < spi.length; i++) {
			spi[i].selected = prefs.getBoolean(MainActivity.SELECTED + "."
					+ spi[i].packageName, false);
			ret.add(spi[i]);
		}
		return ret;
	}

	@Override
	protected void onPostExecute(ArrayList<SortablePackageInfo> result) {
		super.onPostExecute(result);
		((ListView) mainActivity.findViewById(R.id.applist))
				.setAdapter(new AppAdapter(mainActivity, R.layout.app_item, result));
		mainActivity.apps = result;
		mainActivity.setProgressBarIndeterminate(false);
		mainActivity.setProgressBarVisibility(false);
	}

}
