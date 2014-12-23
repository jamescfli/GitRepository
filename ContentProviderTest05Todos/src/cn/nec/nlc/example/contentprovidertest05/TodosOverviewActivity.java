package cn.nec.nlc.example.contentprovidertest05;

import cn.nec.nlc.example.contentprovidertest05.contentprovider.MyTodoContentProvider;
import cn.nec.nlc.example.contentprovidertest05.database.TodoTable;
import android.app.ListActivity;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

/*
 * TodosOverviewActivity displays the existing todo items
 * in a list
 * 
 * You can create new ones via the ActionBar entry "Insert"
 * You can delete existing ones via a long press on the item
 * 
 * It is a good practice that an activity or the fragment which uses 
 * a Loader implements the LoaderManager.LoaderCallbacks interface.
 */

public class TodosOverviewActivity extends ListActivity implements
    	LoaderManager.LoaderCallbacks<Cursor> {
//	private static final int ACTIVITY_CREATE = 0;
//	private static final int ACTIVITY_EDIT = 1;
	private static final int DELETE_ID = Menu.FIRST + 1; 
	// .. First value for group and item identifier integers + 1
	// private Cursor cursor;
	private SimpleCursorAdapter adapter;

  
	/** Called when the activity is first created. */

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.todo_list);
		// getListView() for ListActivity
		// setDividerHeight() sets the height of the divider that will be drawn
		// between each item in the list. Calling this will override the 
		// intrinsic height as set by setDivider(Drawable)
		this.getListView().setDividerHeight(2);	// ListActivity
		fillData();
		// Registers a context menu to be shown for the given view
		registerForContextMenu(getListView());	// for content update
	}

	// create the menu based on the XML definition
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.listmenu, menu);
		return true;
	}

	// Reaction to the menu selection
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.insert:
			createTodo();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case DELETE_ID:
			AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
				.getMenuInfo();
			Uri uri = Uri.parse(MyTodoContentProvider.CONTENT_URI + "/"
					+ info.id);
			// .. info.id: The row id of the item for which the context menu is being displayed.
			getContentResolver().delete(uri, null, null);
			fillData();
			return true;
		}
		return super.onContextItemSelected(item);
	}

	private void createTodo() {
		Intent i = new Intent(this, TodoDetailActivity.class);
		startActivity(i);
	}

	// Opens the TodoDetail activity if an entry is clicked
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		Intent i = new Intent(this, TodoDetailActivity.class);
		Uri todoUri = Uri.parse(MyTodoContentProvider.CONTENT_URI + "/" + id);
		i.putExtra(MyTodoContentProvider.CONTENT_ITEM_TYPE, todoUri);

		startActivity(i);
	}

  

	private void fillData() {

		// Fields from the database (projection)
		// Must include the _id column for the adapter to work
		String[] from = new String[] { TodoTable.COLUMN_SUMMARY };
		// Fields on the UI to which we map
		int[] to = new int[] { R.id.label }; // textView

		// initLoader(int loader_id, Bundle args, LoaderCallbacks<D>)
		// start a new loader or re-connect to existing one
		// Ensures a loader is initialized and active. If the loader doesn't 
		// already exist, one is created and (if the activity/fragment is 
		// currently started) starts the loader. Otherwise the last created 
		// loader is re-used.
		getLoaderManager().initLoader(0, null, this);
		// (context, layout, cursor, fromString, toInt, flags)
		adapter = new SimpleCursorAdapter(this, R.layout.todo_row, null, from,
				to, 0);
		// ListActivity
		setListAdapter(adapter);
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		menu.add(0, DELETE_ID, 0, R.string.menu_delete);
	}

	// creates a new loader after the initLoader () call
	// allow you to load data asynchronously in an activity or fragment. 
	// They can monitor the source of the data and deliver new results when the 
	// content changes. They also persist data between configuration changes.
	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		String[] projection = { TodoTable.COLUMN_ID, TodoTable.COLUMN_SUMMARY };
		CursorLoader cursorLoader = new CursorLoader(this,
				MyTodoContentProvider.CONTENT_URI, projection, null, null, null);
		return cursorLoader;
	}

	// Once the Loader has finished reading data asynchronously, the 
	// onLoadFinished() method of the callback class is called.
	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
		adapter.swapCursor(data);
	}

	// If the Cursor becomes invalid, the onLoaderReset() method is called 
	// on the callback class.
	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		// data is not available anymore, delete reference
		adapter.swapCursor(null);
	}
} 
