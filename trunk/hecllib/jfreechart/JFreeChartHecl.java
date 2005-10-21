/* Copyright 2005 David N. Welton

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

 http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
 */

import org.hecl.Command;
import org.hecl.Eval;
import org.hecl.HeclException;
import org.hecl.Interp;
import org.hecl.DoubleThing;
import org.hecl.IntThing;
import org.hecl.ListThing;
import org.hecl.ObjectThing;
import org.hecl.Properties;
import org.hecl.StringThing;
import org.hecl.Thing;

import java.io.File;

import java.util.Enumeration;
import java.util.Vector;

import org.jfree.chart.*;
import org.jfree.data.category.*;
import org.jfree.data.general.*;
import org.jfree.chart.plot.*;

import org.hecl.modules.HeclModule;


/**
 * <code>JFreeChartHecl</code> provides both a hecl module and
 * implements the barchart and savechart commands.
 *
 * @author <a href="mailto:davidw@dedasys.com">David N. Welton</a>
 * @version 1.0
 */
public class JFreeChartHecl implements Command, HeclModule {

    public void cmdCode(Interp interp, Thing[] argv) throws HeclException {
        String cmdname = argv[0].getStringRep();

        if (cmdname.equals("barchart")) {
	    Properties p = new Properties(
		new Object[] {"legend", IntThing.create(1)});
	    p.setProps(argv, 5);

	    DefaultCategoryDataset dataset = new DefaultCategoryDataset();
	    Vector points = ListThing.get(argv[4]);

 	    int i = 0;
	    for (Enumeration e = points.elements(); e.hasMoreElements();) {
		Vector point = ListThing.get((Thing)e.nextElement());

		String colkey = ((Thing)point.elementAt(0)).toString();
		String rowkey = ((Thing)point.elementAt(1)).toString();
		Number num = (Number)DoubleThing.get((Thing)point.elementAt(2));
		dataset.addValue(num, (Comparable)rowkey, (Comparable)colkey);
		i ++;
	    }

	    boolean legend = (IntThing.get(p.getProp("legend")) == 1);

 	    JFreeChart chart = ChartFactory.createBarChart(
		argv[1].toString(),         // chart title
		argv[2].toString(),               // domain axis label
		argv[3].toString(),                  // range axis label
		dataset,
		PlotOrientation.HORIZONTAL, // orientation
		legend,                     // include legend
		true,                     // tooltips?
		false                     // URLs?
		);
	    interp.setResult(ObjectThing.create(chart));

	} else if (cmdname.equals("savechart")) {
	    JFreeChart chart = (JFreeChart)ObjectThing.get(argv[1]);
	    String filename = argv[2].toString();
	    String type = argv[3].toString();
	    int x = IntThing.get(argv[4]);
	    int y = IntThing.get(argv[5]);

	    try {
		if (type.equals("png")) {
		    ChartUtilities.saveChartAsPNG(new File(filename), chart, x, y);
		} else if (type.equals("jpeg")) {
		    ChartUtilities.saveChartAsJPEG(new File(filename), chart, x, y);
		} else {
		    throw new HeclException("bad chart type: " + type + " should be one of (png, jpeg)");
		}
	    } catch (Exception e) {
		throw new HeclException(e.toString());
	    }
	}
    }

    public void loadModule(Interp interp) throws HeclException {
        interp.commands.put("barchart", this);
	interp.commands.put("savechart", this);
    }

    public void unloadModule(Interp interp) throws HeclException {
	interp.commands.remove("barchart");
	interp.commands.remove("savechart");
    }
}
