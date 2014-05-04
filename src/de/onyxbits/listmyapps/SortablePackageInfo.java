package de.onyxbits.listmyapps;

import android.content.pm.ApplicationInfo;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.CheckBox;

/**
 * Data container for holding all the relevant information on a package.
 * @author patrick
 * 
 */
class SortablePackageInfo implements Comparable<SortablePackageInfo>,
		View.OnClickListener {

	public String packageName;
	public String displayName;
	public String installer;
	public String tags;
	public boolean selected;
	public int versionCode;
	public String version;
	public long firstInstalled;
	public long lastUpdated;
	public int uid;
	public int rating;
	public String dataDir;
	public String comment;
	public int category;
	public int targetsdk;
	public ApplicationInfo appInfo;

	public SortablePackageInfo(){}

	@Override
	public int compareTo(SortablePackageInfo another) {
		return displayName.toLowerCase().compareTo(another.displayName.toLowerCase());
	}

	@Override
	public void onClick(View v) {
		selected = ((CheckBox) v).isChecked();
	}

}
