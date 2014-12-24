package cn.nec.nlc.example.xmlpullparsertest01;

import java.io.IOException;
import java.io.StringReader;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		TextView tv = (TextView) findViewById(R.id.textview1);
		
		XmlPullParserFactory factory;
		try {
			factory = XmlPullParserFactory.newInstance();
			// set the features to be set (true) when XML Pull Parser is created by
			// this factory
			factory.setNamespaceAware(true);
			XmlPullParser xpp = factory.newPullParser();
			
			// setInput(Reader in): Set the input source for parser to the 
			// given reader and resets the parser.
			// StringReader: specialized Reader that reads characters from a 
			// String in a sequential manner
			xpp.setInput(new StringReader("<foo>Hello World ~~</foo>"));
			StringBuffer tvOutput = new StringBuffer();
			// Returns the type of the current event (START_TAG, END_TAG, TEXT, etc.)
			int eventType = xpp.getEventType();
	        while (eventType != XmlPullParser.END_DOCUMENT) {
	        	if(eventType == XmlPullParser.START_DOCUMENT) {  	// = 0
	        		// display in LogCat, Level Info (I)
	        		System.out.println("Start document");
	        		tvOutput.append("Start document\n");
	        	} else if(eventType == XmlPullParser.END_DOCUMENT) {
	        		System.out.println("End document");
	        		tvOutput.append("End document\n");
	        	} else if(eventType == XmlPullParser.START_TAG) { 	// = 2
	        		System.out.println("Start tag "+xpp.getName());
	        		tvOutput.append("Start tag "+xpp.getName() + "\n");
	        	} else if(eventType == XmlPullParser.END_TAG) {
	        		System.out.println("End tag "+xpp.getName()); 	// = 3
	        		tvOutput.append("End tag "+xpp.getName() + "\n");
	        	} else if(eventType == XmlPullParser.TEXT) { 		// = 4
	        		System.out.println("Text "+xpp.getText());
	        		tvOutput.append("Text "+xpp.getText() + "\n");
	        	}
	        	// get next parsing event
	        	eventType = xpp.next();
	        }
	        tv.setText(tvOutput);
		} catch (XmlPullParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
