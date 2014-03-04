package de.onyxbits.listmyapps;

import java.util.Arrays;
import java.util.Vector;

import android.content.DialogInterface;
import android.widget.ListAdapter;

/**
 * Utility class for selecting apps by tag.
 * 
 * @author patrick
 * 
 */
public class TagSelectionListener implements
		DialogInterface.OnMultiChoiceClickListener, DialogInterface.OnClickListener {

	private ListAdapter adapter;
	private String[] items;
	private boolean[] states;

	/**
	 * 
	 * @param adapter
	 *          The listadapter (containing the list of apps) to control.
	 */
	public TagSelectionListener(ListAdapter adapter) {
		this.adapter = adapter;
		int count = adapter.getCount();
		Vector<String> collect = new Vector<String>();
		for (int i = 0; i < count; i++) {
			SortablePackageInfo spi = (SortablePackageInfo) adapter.getItem(i);
			String[] tags = MainActivity.noNull(spi.tags).split(",");
			for (String tag : tags) {
				String tmp = tag.trim();
				if (tmp.length() > 0 && !collect.contains(tmp)) {
					collect.add(tmp);
				}
			}
		}
		items = collect.toArray(new String[0]);
		states = new boolean[items.length];
		Arrays.sort(items);
	}

	/**
	 * Extract the tags from the apps in the provided adapter.
	 * 
	 * @return
	 */
	protected String[] listTags() {
		return items;
	}

	@Override
	public void onClick(DialogInterface dialog, int which) {
		if (which == DialogInterface.BUTTON_POSITIVE) {
			int count = adapter.getCount();
			for (int i = 0; i < count; i++) {
				SortablePackageInfo spi = (SortablePackageInfo) adapter.getItem(i);
				spi.selected = false;
				for (int a = 0; a < items.length; a++) {
					if (states[a] && MainActivity.noNull(spi.tags).contains(items[a])) {
						spi.selected=true;
						break;
					}
				}
			}
			((AppAdapter) adapter).notifyDataSetChanged();
		}
	}

	@Override
	public void onClick(DialogInterface dialog, int which, boolean isChecked) {
		states[which] = isChecked;
	}

}
