package de.onyxbits.listmyapps;

import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.CheckBox;

/**
 * Wrapper class for sorting packages alphabetically by label.
 * 
 * @author patrick
 * 
 */
class SortablePackageInfo implements Comparable<SortablePackageInfo>, View.OnClickListener{

	public String packageName;
	public String displayName;
	public String installer;
	public Drawable icon;
	public boolean selected;

	public SortablePackageInfo(CharSequence pn, CharSequence dn, boolean sel, String inst, Drawable ico) {
		packageName = pn.toString();
		displayName = dn.toString();
		selected=sel;
		installer=inst;
		icon=ico;
	}

	@Override
	public int compareTo(SortablePackageInfo another) {
		return displayName.compareTo(another.displayName);
	}

	@Override
	public void onClick(View v) {

		selected = ((CheckBox)v).isChecked();
	}

}
