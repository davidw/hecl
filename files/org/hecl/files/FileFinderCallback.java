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

public abstract interface FileFinderCallback {

    public void error(FileFinder ff, String errmsg);

    public boolean match(FileFinder ff, FileConnection fconn);

    public void selected(FileFinder ff, String currentFile);

    public void cancel(FileFinder ff);
}