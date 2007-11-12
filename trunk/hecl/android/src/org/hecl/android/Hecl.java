package org.hecl.android;

import android.app.Activity;
import android.os.Bundle;

public class Hecl extends Activity
{
    /** Called with the activity is first created. */
    @Override
    public void onCreate(Bundle icicle)
    {
        super.onCreate(icicle);
        setContentView(R.layout.main);
    }
}
