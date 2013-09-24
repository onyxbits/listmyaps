package de.onyxbits.listmyapps;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class TemplateSource {

	private Schema schema;
	protected SQLiteDatabase database;
	private static final String[] all = { Schema.COLUMN_ID, Schema.COLUMN_TNAME,
			Schema.COLUMN_HEADER, Schema.COLUMN_ITEM, Schema.COLUMN_FOOTER };

	public TemplateSource(Context ctx) {
		schema = new Schema(ctx);
	}

	/**
	 * Open database in r/w mode
	 * @throws SQLException
	 */
	public void open() throws SQLException {
		database = schema.getWritableDatabase();
	}
	
	/**
	 * Delete an entry
	 * @param id row id of the format to kill. Fails silently if unknown.
	 */
	public void delete(long id) {
		database.delete(Schema.TABLE_TEMPLATES,"_id="+id,null);
	}
	
	/**
	 * Insert or update a template
	 * @param data the data. If its id is unknown it gets inserted, otherwise
	 * updated.
	 */
	public void insertOrUpdate(TemplateData data) {
		ContentValues values = new ContentValues();
		values.put(Schema.COLUMN_TNAME,data.formatName);
		values.put(Schema.COLUMN_HEADER,data.header);
		values.put(Schema.COLUMN_ITEM,data.item);
		values.put(Schema.COLUMN_FOOTER,data.footer);
		if (database.update(Schema.TABLE_TEMPLATES,values,"_id="+data.id,null)!=1) {
			database.insert(Schema.TABLE_TEMPLATES,null,values);
		}
	}

	public TemplateData get(long id) {
		Cursor cursor = database.query(Schema.TABLE_TEMPLATES, all, "_id=" + id,
				null, null, null, null);
		cursor.moveToFirst();
		TemplateData ret = new TemplateData();
		ret = new TemplateData();
		ret.id = cursor.getLong(0);
		ret.formatName = cursor.getString(1);
		ret.header = cursor.getString(2);
		ret.item = cursor.getString(3);
		ret.footer = cursor.getString(4);
		return ret;
	}

	public void add(TemplateData data) {
	}

	public void remove(long id) {
	}

	public List<TemplateData> list() {
		
		List<TemplateData> ret = new ArrayList<TemplateData>();
		String[] all = { Schema.COLUMN_ID, Schema.COLUMN_TNAME,
				Schema.COLUMN_HEADER, Schema.COLUMN_ITEM, Schema.COLUMN_FOOTER };
		Cursor cursor = database.query(Schema.TABLE_TEMPLATES, all, null, null, null,
				null, null);


		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
      TemplateData fd =new TemplateData();
      fd.id = cursor.getLong(0);
			fd.formatName = cursor.getString(1);
			fd.header = cursor.getString(2);
			fd.item = cursor.getString(3);
			fd.footer = cursor.getString(4);
      ret.add(fd);
      cursor.moveToNext();
    }
		cursor.close();
		return ret;
	}

}
