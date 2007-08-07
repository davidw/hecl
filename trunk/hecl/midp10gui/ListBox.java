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

import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.ChoiceGroup;


/**
 * <code>ListBox</code>
 *
 * @author <a href="mailto:davidw@dedasys.com">David N. Welton</a>
 * @version 1.0
 */
public class ListBox extends Form {
    /* This is public for now, because it's faster that way if things
     * in GUICmds need to use it. */
    public ChoiceGroup cg = null;

    /**
     * Creates a new <code>ListBox</code> instance, that in turn,
     * creates a ChoiceGroup.
     *
     * @param title a <code>String</code> value
     * @param choicetype an <code>int</code> value
     * @param choices a <code>String[]</code> value
     */
    public ListBox(String title, int choicetype, String []choices) {
	super(title);
	cg = new ChoiceGroup("", choicetype, choices, null);
	this.append(cg);
    }

    /**
     * The <code>append</code> method appends to the ChoiceGroup
     * rather than the frame.
     *
     * @param item a <code>String</code> value
     * @return an <code>int</code> value
     */
    public int append(String item) {
	return cg.append(item, null);
    }
}
