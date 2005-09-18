/* Copyright 2004-2005 David N. Welton

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

package org.hecl;

/**
 * <code>HeclModuleCmd</code> implements the "module" command.
 * 
 * This command allows loading and unloading of modules on demand.
 * 
 * @author <a href="mailto:wojciech@kocjan.org">Wojciech Kocjan </a>
 * @version 1.0
 */

public class HeclModuleCmd implements Command {
    Thing defaultResult = new Thing(new IntThing(0));
    public void cmdCode(Interp interp, Thing[] argv) throws HeclException {
        String cmd, module;

        if (argv.length != 3) {
            throw HeclException.createWrongNumArgsException(argv, 1,
                    "command moduleName");
        }

        cmd = argv[1].getStringRep();
        module = argv[2].getStringRep();

        if (cmd.equals("load")) {
            interp.loadModule(module, true);
        } else if (cmd.equals("unload")) {
            interp.unloadModule(module, true);
        } else if (cmd.equals("tryload")) {
            interp.unloadModule(module, false);
        } else if (cmd.equals("tryunload")) {
            interp.unloadModule(module, false);
        } else {
            throw HeclException.createInvalidParameter(argv[1], "command",
                    "load, unload, tryload, tryunload");
        }
        interp.result = defaultResult;
        return;
    }
}