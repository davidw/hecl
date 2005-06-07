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
 * <code>StringCmd</code> implements the "slen" and "sindex" commands.
 * 
 * @author <a href="mailto:davidw@dedasys.com">David N. Welton </a>
 * @version 1.0
 */

class StringCmd implements Command {

    public void cmdCode(Interp interp, Thing[] argv) throws HeclException {
        String cmd = argv[0].getStringRep();
        String str = argv[1].getStringRep();

        if (cmd.equals("slen")) {
            interp.setResult(IntThing.create(str.length()));
            return;
        } else if (cmd.equals("sindex")) {
            int idx = IntThing.get(argv[2]);
            try {
                char chars[] = new char[1];
                chars[0] = str.charAt(idx);
                interp.setResult(new Thing(new String(chars)));
            } catch (StringIndexOutOfBoundsException e) {
                interp.setResult(new Thing(""));
            }
            return;
        }
    }

}
