package cn.nec.nlc.example.activitytest31dragdrop;

import android.app.Activity;
import android.content.ClipData;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.DragShadowBuilder;
import android.view.View.OnDragListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;

public class DragActivity extends Activity {
	  
	  @Override
	  public void onCreate(Bundle savedInstanceState) {
		  super.onCreate(savedInstanceState);
		  setContentView(R.layout.activity_main);
		  // all movable icons, ic_launcher, ImageView
		  // Register a callback for touch event
		  findViewById(R.id.myimage1).setOnTouchListener(new MyTouchListener());
		  findViewById(R.id.myimage2).setOnTouchListener(new MyTouchListener());
		  findViewById(R.id.myimage3).setOnTouchListener(new MyTouchListener());
		  findViewById(R.id.myimage4).setOnTouchListener(new MyTouchListener());
		  // containers for icons, LinearLayout
		  findViewById(R.id.topleft).setOnDragListener(new MyDragListener());
		  findViewById(R.id.topright).setOnDragListener(new MyDragListener());
		  findViewById(R.id.bottomleft).setOnDragListener(new MyDragListener());
		  findViewById(R.id.bottomright).setOnDragListener(new MyDragListener());
	  }

	  // View.OnTouchListener interface
	  // onTouch() callback to be invoked when a touch event is dispatched to this view
	  private final class MyTouchListener implements OnTouchListener {
		  // to respond before the target view
		  @Override
		  public boolean onTouch(View view, MotionEvent motionEvent) {
			  if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
				  // ACTION_DOWN: A pressed gesture has started, the motion 
				  // contains the initial starting location.
				  // ClipData: to save data of the object(view) and let drop zones receive the object
				  // i.e. ClipData is used for passing data from one container to another
				  ClipData data = ClipData.newPlainText("label", "text"); // user visible label and actual text
				  // DragShadowBuilder defines the visual effects when dragging the object
				  DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(view); // drag shadow image in the canvas
				  view.startDrag(data, shadowBuilder, view, 0);
				  view.setVisibility(View.INVISIBLE);	// disappear from the original spot
				  return true; 	// True if the listener has consumed the event, false otherwise.
			  } else {
				  return false; // Ignore other MotionEvent.*
			  }
		  }
	  }

	  // implement drag and drop features
	  class MyDragListener implements OnDragListener {
		  Drawable enterShape = getResources().getDrawable(R.drawable.shape_droptarget);
		  Drawable normalShape = getResources().getDrawable(R.drawable.shape);

		  // for OnDragListener
		  @Override
		  public boolean onDrag(View v, DragEvent event) {
			  int action = event.getAction();
			  switch (action) {
			  case DragEvent.ACTION_DRAG_STARTED:
				  // do nothing
				  break;
			  case DragEvent.ACTION_DRAG_ENTERED:
				  v.setBackgroundDrawable(enterShape);
				  break;
			  case DragEvent.ACTION_DRAG_EXITED:
				  v.setBackgroundDrawable(normalShape);
				  break;
			  case DragEvent.ACTION_DROP:
				  // Dropped, reassign View to ViewGroup
				  View view = (View) event.getLocalState(); // provide local info about drag and drop
				  ViewGroup owner = (ViewGroup) view.getParent();	// the starting LinearLayout, container
				  owner.removeView(view);	// remove the original view after the DROP event
				  LinearLayout container = (LinearLayout) v;	// the target LinearLayout
				  container.addView(view);
				  view.setVisibility(View.VISIBLE);
				  break;
			  case DragEvent.ACTION_DRAG_ENDED:
				  v.setBackgroundDrawable(normalShape);
			  default:
				  break;
			  }
			  return true;
		  }
	}
} 
