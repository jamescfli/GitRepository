package cn.nec.nlc.example.activitytest9;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class MainActivity extends Activity {
	private static SensorManager sensorService;
	private MyCompassView compassView;
	private Sensor sensor;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		compassView = new MyCompassView(this);
		setContentView(compassView);
		
		sensorService = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		// new method can be found in OrientationActivity
		sensor = sensorService.getDefaultSensor(Sensor.TYPE_ORIENTATION);
		// This constant is deprecated. use SensorManager.getOrientation() instead.
		// public static float[] getOrientation (float[] R, float[] values)
		//	Since: API Level 3
		//	Computes the device's orientation based on the rotation matrix.
		//	When it returns, the array values is filled with the result:
		//		values[0]: azimuth, rotation around the Z axis.
		//		values[1]: pitch, rotation around the X axis.
		//		values[2]: roll, rotation around the Y axis.
		if (sensor != null) {
			sensorService.registerListener(mySensorEventListener, sensor, 
					SensorManager.SENSOR_DELAY_NORMAL);
			// SENSOR_DELAY_NORMAL (Value 3): rate (default) suitable for screen orientation changes
			// SENSOR_DELAY_UI (Value 2): rate suitable for the user interface
			// SENSOR_DELAY_GAME (Value 1): rate suitable for games
			// SENSOR_DELAY_FASTEST (Value 0): get sensor data as fast as possible
			// where FASTEST > GAME > UI > NORMAL
			Log.i("Compass MainActivity", "Registered for ORIENTATION Sensor");
		} else { // sometimes handset does not have Orientation sensor
			Log.e("Compass MainActivity", "Registered for ORIENTATION Sensor");
			Toast.makeText(this, "ORIENTATION Sensor not found", Toast.LENGTH_LONG).show();
			finish();
		}
	}

	// listener registered in onCreate() and unregistered in onDestroy()
	private SensorEventListener mySensorEventListener = new SensorEventListener () {
		@Override
		public void onAccuracyChanged(Sensor sensor, int accuracy) {
			// n.a.
		}
		
		@Override
		public void onSensorChanged(SensorEvent event) {
			// angle btw the magnetic north direction
			// 0=N, 90=E, 180=S, 270=W
			float azimuth = event.values[0]; // in degrees
			compassView.updateData(azimuth);
		}
	};
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (sensor != null) {
			sensorService.unregisterListener(mySensorEventListener);
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
