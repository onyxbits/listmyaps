package de.onyxbits.listmyapps;


import android.os.Bundle;
import android.app.ListActivity;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class TemplatesActivity extends ListActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_templates);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		TemplateSource formatsDataSource = new TemplateSource(this);
		formatsDataSource.open();
		ArrayAdapter<TemplateData> adapter = new ArrayAdapter<TemplateData>(this,
				android.R.layout.simple_list_item_1, formatsDataSource.list());
		setListAdapter(adapter);
	}
	
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		long edit = ((TemplateData)getListAdapter().getItem(position)).id;
		Intent intent = new Intent(this,TemplateEditorActivity.class);
		intent.putExtra(TemplateEditorActivity.EDITID,edit);
		startActivity(intent);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.templates, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.item_add: {
				startActivity(new Intent(this,TemplateEditorActivity.class));
				return true;
			}
		}
		return super.onOptionsItemSelected(item);
	}

}
