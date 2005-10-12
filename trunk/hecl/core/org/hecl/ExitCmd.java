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

package org.hecl;

/**
 * <code>ExitCmd</code> implements the 'exit' command.
 * 
 * @author <a href="mailto:davidw@dedasys.com">David N. Welton </a>
 * @version 1.0
 */

class ExitCmd implements Command {

    public void cmdCode(Interp interp, Thing[] argv) throws HeclException {
	int returncode = 0;
	if (argv.length > 1) {
	    returncode = IntThing.get(argv[1]);
	}

	System.exit(returncode);
    }
}
