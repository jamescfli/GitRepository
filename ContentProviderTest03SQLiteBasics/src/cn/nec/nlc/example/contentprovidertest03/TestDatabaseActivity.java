package cn.nec.nlc.example.contentprovidertest03;

import java.util.List;
import java.util.Random;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class TestDatabaseActivity extends Activity {
	private CommentsDataSource datasource;	// DAO
	private ListView listView;				// view for display
	private ArrayAdapter<Comment> adapter;	// content for display

	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.activity_main);	// two buttons and listView

	    datasource = new CommentsDataSource(this);
	    datasource.open(); // getWritableDatabase()

	    // check if there are already some comments in SQLite database
	    List<Comment> values = datasource.getAllComments();	// read all columns

	    // use the SimpleCursorAdapter to show the
	    // elements in a ListView
	    adapter = new ArrayAdapter<Comment>(this,
	        android.R.layout.simple_list_item_1, values);
	    
	    // prepare listView
	    listView = (ListView) findViewById(android.R.id.list);
	    listView.setAdapter(adapter);
//	    setListAdapter(adapter);
	}

	// Will be called via the onClick attribute
	// of the buttons in main.xml
	public void onClick(View view) {
//		@SuppressWarnings("unchecked")
//		ArrayAdapter<Comment> adapter = (ArrayAdapter<Comment>) getListAdapter();
		Comment comment = null;
		switch (view.getId()) {
		case R.id.add:
			String[] comments = new String[] 
				{ "Love it", "Cool", "Very nice", "Good", "Fair", "Hate it" };
			int nextInt = new Random().nextInt(comments.length);
			// save the new comment to the database
			comment = datasource.createComment(comments[nextInt]);
			adapter.add(comment);
			break;
		case R.id.delete:
			if (adapter.getCount() > 0) {
				comment = (Comment) adapter.getItem(0);	// get the first item
				datasource.deleteComment(comment);
				adapter.remove(comment);
			}
			break;
		}
		// notify the change to the listView display
		adapter.notifyDataSetChanged();
	}

	@Override
	protected void onResume() {
		datasource.open();
		super.onResume();
	}

	@Override
	protected void onPause() {
		datasource.close();
		super.onPause();
	}
} 

