package cn.nec.nlc.example.activitytest9;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

public class OrientationActivity extends Activity {

	private SensorManager sensorManager;
	private Sensor accSensor;
	private Sensor magSensor;
	private Sensor gyroSensor;
	
	private TextView textViewSampleRateAcc;
	private TextView textViewSampleRateMag;
	private TextView textViewSampleRateGyro;
	
	float[] accelerometerValues = new float[3];
	float[] magneticFieldValues = new float[3];
	
	private static final String TAG = "sensor";
	
	private final int numTotalSamples = 200; 
	
	// measure sensor sampling rate
	private int counterAccSensor;
	private int counterMagSensor;
	private int counterGyroSensor;
	private long timeLastAccSensor;
	private long timeLastMagSensor;
	private long timeLastGyroSensor;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		accSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		magSensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
		gyroSensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
		
		// registration
		sensorManager.registerListener(myListener, accSensor, SensorManager.SENSOR_DELAY_FASTEST);
		sensorManager.registerListener(myListener, magSensor, SensorManager.SENSOR_DELAY_FASTEST);
		sensorManager.registerListener(myListener, gyroSensor, SensorManager.SENSOR_DELAY_FASTEST);
//		sensorManager.registerListener(myListener, accSensor, SensorManager.SENSOR_DELAY_UI);
//		sensorManager.registerListener(myListener, magSensor, SensorManager.SENSOR_DELAY_UI);
//		sensorManager.registerListener(myListener, gyroSensor, SensorManager.SENSOR_DELAY_UI);
		// method of update data
//		calculateOrientation();
		
		// prepare textView
		textViewSampleRateAcc = (TextView) findViewById(R.id.textView5);
		textViewSampleRateMag = (TextView) findViewById(R.id.textView6);
		textViewSampleRateGyro = (TextView) findViewById(R.id.textView7);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		// initiate
		counterAccSensor = 0;
		counterMagSensor = 0; 
		counterGyroSensor = 0;
		// Returns the current system time in milliseconds since Jan. 1st, 1970
		timeLastAccSensor = System.currentTimeMillis();
		timeLastMagSensor = System.currentTimeMillis();
		timeLastGyroSensor = System.currentTimeMillis();
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		sensorManager.unregisterListener(myListener);
	}
	
	final SensorEventListener myListener = new SensorEventListener() {
		public void onSensorChanged(SensorEvent sensorEvent) {
			if (sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
				accelerometerValues = sensorEvent.values;
				if (counterAccSensor < numTotalSamples) {
					counterAccSensor++;
				} else { // counterAccSensor = numTotalSamples
					double samplingRate = numTotalSamples 
							/ ((System.currentTimeMillis() - timeLastAccSensor) / 1000.0);
					// log the sampling rate after numTotalSamples samples were collected
					Log.i(TAG, "Acc Sampling Rate (FASTEST) = " + samplingRate);
					OrientationActivity.this.textViewSampleRateAcc.setText(String.valueOf(samplingRate));
					// recount
					counterAccSensor = 0;
					// update timelast
					timeLastAccSensor = System.currentTimeMillis();
				}
			}
			
			if (sensorEvent.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
				magneticFieldValues = sensorEvent.values;
				if (counterMagSensor < numTotalSamples) {
					counterMagSensor++;
				} else { // counterMagSensor = numTotalSamples
					double samplingRate = numTotalSamples 
							/ ((System.currentTimeMillis() - timeLastMagSensor) / 1000.0);
					// log the sampling rate after numTotalSamples samples were collected
					Log.i(TAG, "Mag Sampling Rate (FASTEST) = " + samplingRate);
					OrientationActivity.this.textViewSampleRateMag.setText(String.valueOf(samplingRate));
					// recount
					counterMagSensor = 0;
					// update timelast
					timeLastMagSensor = System.currentTimeMillis();
				}
			}
				
			if (sensorEvent.sensor.getType() == Sensor.TYPE_GYROSCOPE) {
				if (counterGyroSensor < numTotalSamples) {
					counterGyroSensor++;
				} else { // counterGyroSensor = numTotalSamples
					double samplingRate = numTotalSamples 
							/ ((System.currentTimeMillis() - timeLastGyroSensor) / 1000.0);
					// log the sampling rate after numTotalSamples samples were collected
					Log.i(TAG, "Gyro Sampling Rate (FASTEST) = " + samplingRate);
					OrientationActivity.this.textViewSampleRateGyro.setText(String.valueOf(samplingRate));
					// recount
					counterGyroSensor = 0;
					// update timelast
					timeLastGyroSensor = System.currentTimeMillis();
				}
			}
				
//			calculateOrientation();
		}
		public void onAccuracyChanged(Sensor sensor, int accuracy) {
			// n.a.
		}
	};
	
//	private void calculateOrientation() {
//		float[] values = new float[3];
//		float[] R = new float[9];
//		SensorManager.getRotationMatrix(R, null, accelerometerValues, magneticFieldValues);
//		SensorManager.getOrientation(R, values);
//		
//		// output is in radians, translate into degrees
//		values[0] = (float) Math.toDegrees(values[0]);
////		values[1] = (float) Math.toDegrees(values[1]);
////		values[2] = (float) Math.toDegrees(values[2]);
//		// difference from getDefaultSensor(Sensor.TYPE_ORIENTATION) 
//		// values[0] in (-PI, +PI)
//		
////		if (values[0] >= -5 && values[0] <= 5) {
////			Log.i(TAG, "North");
////			Toast.makeText(this, "North", Toast.LENGTH_SHORT).show();
////		} else if (values[0] > 5 && values[0] < 85) {
////			Log.i(TAG, "NorthEast");
////			Toast.makeText(this, "NorthEast", Toast.LENGTH_SHORT).show();
////		} else if (values[0] >= 85 && values[0] <= 95) {
////			Log.i(TAG, "East");
////			Toast.makeText(this, "East", Toast.LENGTH_SHORT).show();
////		} else if (values[0] > 95 && values[0] < 175) {
////			Log.i(TAG, "SouthEast");
////			Toast.makeText(this, "SouthEast", Toast.LENGTH_SHORT).show();
////		} else if ((values[0] >= 175 && values[0] <= 180) || (values[0] >= -180 && values[0] <= -175)) {
////			Log.i(TAG, "South");
////			Toast.makeText(this, "South", Toast.LENGTH_SHORT).show();
////		} else if (values[0] > -175 && values[0] < -95) {
////			Log.i(TAG, "SouthWest");
////			Toast.makeText(this, "SouthWest", Toast.LENGTH_SHORT).show();
////		} else if (values[0] >= -95 && values[0] <= -85) {
////			Log.i(TAG, "West");
////			Toast.makeText(this, "West", Toast.LENGTH_SHORT).show();
////		} else if (values[0] > -85 && values[0] < -5) {
////			Log.i(TAG, "NorthWest");
////			Toast.makeText(this, "NorthWest", Toast.LENGTH_SHORT).show();
////		}
//	}
}
