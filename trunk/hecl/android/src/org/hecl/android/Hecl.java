package org.hecl.android;

import android.app.Activity;
import android.os.Bundle;

import android.view.View;
import android.view.View.OnClickListener;

import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.hecl.HeclException;
import org.hecl.Interp;
import org.hecl.Thing;

public class Hecl extends Activity
{
    Interp interp;

    /** Called with the activity is first created. */
    @Override
    public void onCreate(Bundle icicle)
    {
        super.onCreate(icicle);
        setContentView(R.layout.main);

	try {
	    interp = new Interp();
	    ((Button) findViewById(R.id.execute)).setOnClickListener(mExecListener);
	} catch (Exception e) {
	    System.err.println("Hecl Error: " + e);
	}
    }

    OnClickListener mExecListener = new OnClickListener() {
        public void onClick(View v) {
	    EditText script = (EditText)findViewById(R.id.script);
	    TextView results = (TextView)findViewById(R.id.results);
	    String heclresult = null;
	    try {
		heclresult = interp.eval(new Thing(script.getText().toString())).toString();
	    } catch (HeclException he) {
		heclresult = he.toString();
	    }
	    results.setText(heclresult);
        }
    };


}
