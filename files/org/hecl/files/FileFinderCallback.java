//#condition midp >= 2.0
/*
 * Copyright 2009
 * DedaSys LLC - http://www.dedasys.com
 *
 * Author: David N. Welton <davidw@dedasys.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.hecl.files;

import javax.microedition.io.file.FileConnection;

/**
 * The <code>FileFinderCallback</code> interface should be implemented
 * by classes that wish to interact with a FileFinder.  The
 * implementation is passed to the FileFinder in its constructor.
 *
 * @author <a href="mailto:davidw@dedasys.com">David N. Welton</a>
 * @version 1.0
 */
public abstract interface FileFinderCallback {

    /**
     * The <code>error</code> method is called when something goes
     * wrong.
     *
     * @param ff a <code>FileFinder</code> value
     * @param errmsg a <code>String</code> value
     */
    public void error(FileFinder ff, String errmsg);

    /**
     * The <code>match</code> method is called to determine whether
     * the given file object matches the user's criteria.
     *
     * @param ff a <code>FileFinder</code> value
     * @param fconn a <code>FileConnection</code> value
     * @return a <code>boolean</code> value
     */
    public boolean match(FileFinder ff, FileConnection fconn);

    /**
     * The <code>selected</code> method is called when a file matches
     * the 'match' method, and is selected.
     *
     * @param ff a <code>FileFinder</code> value
     * @param currentFile a <code>String</code> value
     */
    public void selected(FileFinder ff, String currentFile);

    /**
     * The <code>cancel</code> method is a callback that is called
     * when the user hits 'cancel' to stop browsing files.
     *
     * @param ff a <code>FileFinder</code> value
     */
    public void cancel(FileFinder ff);
}