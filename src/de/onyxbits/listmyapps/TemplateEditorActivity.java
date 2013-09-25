package de.onyxbits.listmyapps;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;
import android.support.v4.app.NavUtils;
import android.annotation.TargetApi;
import android.os.Build;

public class TemplateEditorActivity extends Activity {

	/**
	 * For passing in an format id via intents.
	 */
	public static final String EDITID = "editid";

	private TemplateData editing;
	private TemplateSource formatsDataSource;

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
					Toast.makeText(this, R.string.msg_error_incomplete, Toast.LENGTH_SHORT)
							.show();
				}
				return true;
			}
			case R.id.remove: {
				formatsDataSource.delete(editing.id);
				finish();
				return true;
			}
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * Sync the displayed values to the model
	 * 
	 * @return true if the model can be saved to the database.
	 */
	private boolean toModel() {
		editing.formatName = MainActivity
				.noNull(((EditText) findViewById(R.id.tmpl_name)).getText().toString());
		editing.header = MainActivity
				.noNull(((EditText) findViewById(R.id.tmpl_header)).getText()
						.toString());
		editing.item = MainActivity
				.noNull(((EditText) findViewById(R.id.tmpl_item)).getText().toString());
		editing.footer = MainActivity
				.noNull(((EditText) findViewById(R.id.tmpl_footer)).getText()
						.toString());

		if (!editing.formatName.equals("") && !editing.item.equals("")) {
			return true;
		}
		return false;
	}

}
