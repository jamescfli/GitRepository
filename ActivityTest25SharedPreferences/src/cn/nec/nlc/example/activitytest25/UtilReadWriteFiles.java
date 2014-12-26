package cn.nec.nlc.example.activitytest25;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;

import android.content.Context;
import android.os.Environment;
import android.util.Log;


// This class provides some examples to read and write file to both
// internal and external storage. Note: the class has not been tested.
public class UtilReadWriteFiles {
	// write configuration to file
	public static  void writeConfiguration(Context context) {
		// Wraps an existing java.io.Writer and buffers the output. 
		BufferedWriter writer = null;
		try {
			// A specialized OutputStream that writes to a file in the file 
			// system. All write requests made by calling methods in this 
			// class are directly forwarded to the equivalent function of 
			// the underlying operating system. Since this may induce some 
			// performance penalty, in particular if many small write requests 
			// are made, a FileOutputStream is often wrapped by a BufferedOutputStream.
			FileOutputStream openFileOutput = context.openFileOutput
					("config.txt", Context.MODE_PRIVATE);
			// .. Context.MODE_PRIVATE: file creation mode: the default mode, 
			// where the created file can only be accessed by the calling 
			// application
			
			// Converts this string to a byte array, ignoring the high order 
			// bits of each character.
			openFileOutput.write("This is a test1.".getBytes());
			openFileOutput.write("This is a test2.".getBytes());
		} catch (Exception e) {
			throw new RuntimeException(e);
		}  finally {
			if (writer != null) {
				try {
					writer.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	// read configuration file from internal storage
	public static void readFileFromInternalStorage(Context context, String fileName) {
		String eol = System.getProperty("line.separator");
		BufferedReader input = null;
		try {
			FileInputStream openFileInput = context.openFileInput(fileName);
			input = new BufferedReader(new InputStreamReader(openFileInput));
			String line;
			StringBuffer buffer = new StringBuffer();
			while ((line = input.readLine()) != null) {
				buffer.append(line + eol);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	
	// If the Android device is connected via USB to a computer, a SD card 
	// which might be used for the external storage system is not available.
	public static void readFileFromSDCard() {
		File directory = Environment.getExternalStorageDirectory();
//		// Call the following method to check the state of the external 
//		// storage system, + if
//		Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
		// assumes that a file article.rss is available on the SD card
		File file = new File(directory + "/article.rss");
		if (!file.exists()) {
			throw new RuntimeException("File not found - article.rss - on SD card");
		}
		Log.e("Testing", "Starting to read");
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(file));
			// A modifiable sequence of characters for use in creating strings. 
			// This class is intended as a direct replacement of StringBuffer 
			// for non-concurrent use; unlike StringBuffer this class is not synchronized.
			StringBuilder builder = new StringBuilder();
			String line;
			while ((line = reader.readLine()) != null) {
				builder.append(line);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
