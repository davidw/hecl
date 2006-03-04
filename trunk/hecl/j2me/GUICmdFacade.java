/* Copyright 2006 David N. Welton

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
import org.hecl.HeclException;
import org.hecl.Interp;
import org.hecl.Thing;

/**
 * <code>GUICmdFacade</code> provides the front end to the GUI
 * commands.  To easier integrate it with the existing code, though,
 * it's a little bit different from the other Facade systems in that
 * it holds a reference to the GUI.  This was to avoid making GUI's
 * dispatch method static, as it is elsewhere.
 *
 * @author <a href="mailto:davidw@dedasys.com">David N. Welton</a>
 * @version 1.0
 */
class GUICmdFacade implements Command {
    private int cmdtype;
    private GUI gui;

    public GUICmdFacade(int cmd, GUI g) {
	cmdtype = cmd;
	gui = g;
    }

    public void cmdCode(Interp interp, Thing[] argv) throws HeclException {
	gui.dispatch(cmdtype, argv);
    }
}
