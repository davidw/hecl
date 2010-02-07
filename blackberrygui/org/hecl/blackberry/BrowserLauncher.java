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

package org.hecl.blackberry;

import net.rim.blackberry.api.browser.Browser;
import net.rim.blackberry.api.browser.BrowserSession;

/**
 * The <code>BrowserLauncher</code> class exists solely to launch the
 * browser in a thread that won't block Hecl.
 *
 * @author <a href="mailto:davidw@dedasys.com">David N. Welton</a>
 * @version 1.0
 */
public class BrowserLauncher extends Thread {
    private String url = null;

    /**
     * Creates a new <code>BrowserLauncher</code> instance.
     *
     * @param newurl a <code>String</code> value
     */
    public BrowserLauncher(String newurl) {
	url = newurl;
    }

    public void run() {
	BrowserSession session = Browser.getDefaultSession();
	session.displayPage(url);
    }
}