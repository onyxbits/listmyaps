package de.onyxbits.listmyapps;

import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class TemplateEditorActivity extends Activity implements OnClickListener {

	/**
	 * For passing in an format id via intents.
	 */
	public static final String EDITID = "editid";

	private static final int PENDING_DELETE = 1;
	private static final int PENDING_INSERT = 2;

	private TemplateData editing;
	private TemplateSource formatsDataSource;
	private int pendingAction;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_template_editor);
		formatsDataSource = new TemplateSource(this);
		formatsDataSource.open();
		try {
			editing = formatsDataSource.get(getIntent().getExtras().getLong(EDITID));
			((EditText) findViewById(R.id.tmpl_name)).setText(editing.formatName);
			((EditText) findViewById(R.id.tmpl_header)).setText(editing.header);
			((EditText) findViewById(R.id.tmpl_item)).setText(editing.item);
			((EditText) findViewById(R.id.tmpl_footer)).setText(editing.footer);
		}
		catch (Exception e) {
			editing = new TemplateData();
			editing.id = -1;
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.template_editor, menu);
		menu.findItem(R.id.remove).setEnabled(formatsDataSource.list().size() > 1);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.save: {
				if (toModel()) {
					formatsDataSource.insertOrUpdate(editing);
					finish();
				}
				else {
					Toast.makeText(this, R.string.msg_error_incomplete, Toast.LENGTH_SHORT).show();
				}
				return true;
			}
			case R.id.remove: {
				showDeleteConfirm();
				return true;
			}
			case R.id.variable: {
				showVariableSelector();
				return true;
			}
			case R.id.item_help: {
				Uri uri = Uri.parse(getString(R.string.url_help));
				MainActivity.openUri(this, uri);
			}
		}
		return super.onOptionsItemSelected(item);
	}

	private void showDeleteConfirm() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(R.string.title_really_delete);
		builder.setMessage(R.string.msg_this_cannot_be_undone);
		builder.setPositiveButton(android.R.string.ok,this);
		builder.setNegativeButton(android.R.string.cancel,this);
		builder.show();
		pendingAction = PENDING_DELETE;
	}

	private void showVariableSelector() {
		View focus = getWindow().getCurrentFocus();
		if (focus.getId() == R.id.tmpl_name) {
			Toast.makeText(this, R.string.msg_name_does_not_support_variables, Toast.LENGTH_SHORT).show();
			return;
		}

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(R.string.title_variable_to_insert);
		if (focus.getId() == R.id.tmpl_item) {
			builder.setItems(R.array.templatebodyvars, this);
		}
		if (focus.getId() == R.id.tmpl_footer || focus.getId() == R.id.tmpl_header) {
			builder.setItems(R.array.templateheaderfootervars, this);
		}
		builder.show();
		pendingAction = PENDING_INSERT;
	}

	/**
	 * Sync the displayed values to the model
	 * 
	 * @return true if the model can be saved to the database.
	 */
	private boolean toModel() {
		editing.formatName = MainActivity.noNull(((EditText) findViewById(R.id.tmpl_name)).getText()
				.toString());
		editing.header = MainActivity.noNull(((EditText) findViewById(R.id.tmpl_header)).getText()
				.toString());
		editing.item = MainActivity.noNull(((EditText) findViewById(R.id.tmpl_item)).getText()
				.toString());
		editing.footer = MainActivity.noNull(((EditText) findViewById(R.id.tmpl_footer)).getText()
				.toString());

		if (!editing.formatName.equals("") && !editing.item.equals("")) {
			return true;
		}
		return false;
	}

	@Override
	public void onClick(DialogInterface dialog, int which) {
		switch (pendingAction) {
			case PENDING_INSERT: {
				View focus = getWindow().getCurrentFocus();
				if (focus.getId() == R.id.tmpl_item) {
					EditText et = (EditText) focus;
					int start = Math.max(et.getSelectionStart(), 0);
					int end = Math.max(et.getSelectionEnd(), 0);
					String txt = getResources().getStringArray(R.array.templatebodyvars)[which];
					et.getText().replace(Math.min(start, end), Math.max(start, end), txt, 0, txt.length());
				}
				if (focus.getId() == R.id.tmpl_footer || focus.getId() == R.id.tmpl_header) {
					EditText et = (EditText) focus;
					int start = Math.max(et.getSelectionStart(), 0);
					int end = Math.max(et.getSelectionEnd(), 0);
					String txt = getResources().getStringArray(R.array.templateheaderfootervars)[which];
					et.getText().replace(Math.min(start, end), Math.max(start, end), txt, 0, txt.length());
				}
				break;
			}
			case PENDING_DELETE: {
				if (which == DialogInterface.BUTTON_POSITIVE) {
					formatsDataSource.delete(editing.id);
					finish();
				}
				break;
			}
		}
	}
}
