package de.onyxbits.listmyapps;

import java.util.ArrayList;

import android.os.Bundle;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ListView;

public class AnnotationsActivity extends ListActivity implements
		DialogInterface.OnClickListener {

	private EditText comment;
	private SortablePackageInfo spi;
	private AnnotationsSource annotationsSource;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_PROGRESS);
		setContentView(R.layout.activity_annotations);
		setProgressBarIndeterminate(true);
		setProgressBarVisibility(true);
		annotationsSource = new AnnotationsSource(this);
		annotationsSource.open();
		setListAdapter(new AppAdapter(this, R.layout.app_item_annotation,
				new ArrayList<SortablePackageInfo>(), R.layout.app_item_annotation));
		new ListTask(this, R.layout.app_item_annotation).execute("");
	}

	@Override
	public void onListItemClick(ListView l, View v, int pos, long id) {
		AppAdapter aa = (AppAdapter) getListAdapter();
		spi = aa.getItem(pos);
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		comment = new EditText(this);
		comment.setHint(R.string.hint_my_notes);
		comment.setText(MainActivity.noNull(spi.comment));
		builder.setTitle(spi.displayName).setView(comment).setIcon(spi.icon)
				.setPositiveButton(R.string.btn_save, this)
				.setNegativeButton(R.string.btn_cancel, this).show();
	}

	@Override
	public void onClick(DialogInterface dialog, int which) {
		if (which == DialogInterface.BUTTON_POSITIVE) {
			spi.comment = comment.getText().toString();
			annotationsSource.putComment(spi.packageName, spi.comment);
			((AppAdapter) getListAdapter()).notifyDataSetChanged();
		}
	}
}
