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

package org.hecl.files;

import org.hecl.*;

/**
 * <code>ChangeDirCmd</code> implements the "cd" command, which
 * changes the current working directory.  NOTE - this doesn't
 * *really* change the working directory, only the directory that Java
 * thinks its in, so this might well cause problems.
 * 
 * @author <a href="mailto:davidw@dedasys.com">David N. Welton </a>
 * @version 1.0
 */

class ChangeDirCmd implements Command {
    public void cmdCode(Interp interp, Thing[] argv) throws HeclException {
	HeclFile.changeDir(argv[1].toString());
    }
}
