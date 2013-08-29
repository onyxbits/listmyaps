package de.onyxbits.listmyapps;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;


import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ListView;

public class ListTask extends
		AsyncTask<Object, Object, ArrayList<SortablePackageInfo>> {

	private ListView listView;
	private MainActivity mainActivity;

	public ListTask(MainActivity mainActivity, ListView listView) {
		this.listView = listView;
		this.mainActivity = mainActivity;
	}

	@Override
	protected ArrayList<SortablePackageInfo> doInBackground(Object... params) {
		SharedPreferences prefs = mainActivity.getSharedPreferences(MainActivity.PREFSFILE, 0);
		ArrayList<SortablePackageInfo> ret = new ArrayList<SortablePackageInfo>();
		PackageManager pm = mainActivity.getPackageManager();
		List<PackageInfo> list = pm.getInstalledPackages(0);
		SortablePackageInfo spitmp[] = new SortablePackageInfo[list.size()];
		Iterator<PackageInfo> it = list.iterator();
		int idx = 0;
		while (it.hasNext()) {
			PackageInfo info = it.next();
			CharSequence tmp = pm.getApplicationLabel(info.applicationInfo);
			if (pm.getLaunchIntentForPackage(info.packageName) != null) {
				String inst = pm.getInstallerPackageName(info.packageName);
				spitmp[idx] = new SortablePackageInfo(info.packageName, tmp,true,inst);
				idx++;
			}
		}
		SortablePackageInfo spi[] = new SortablePackageInfo[idx];
		System.arraycopy(spitmp, 0, spi, 0, idx);
		Arrays.sort(spi);
		for (int i = 0; i < spi.length; i++) {
			spi[i].selected=prefs.getBoolean(MainActivity.SELECTED+"."+spi[i].packageName,false);
			ret.add(spi[i]);
		}
		return ret;
	}

	@Override
	protected void onPostExecute(ArrayList<SortablePackageInfo> result) {
		super.onPostExecute(result);
		listView
				.setAdapter(new AppAdapter(mainActivity, R.layout.app_item, result));
		mainActivity.apps=result;
		mainActivity.setProgressBarIndeterminate(false);
		mainActivity.setProgressBarVisibility(false);
	}

}
