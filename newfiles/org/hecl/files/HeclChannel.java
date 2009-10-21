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

import java.io.DataInputStream;
import java.io.DataOutputStream;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * The <code>HeclChannel</code> class acts as a container for a data
 * input stream and/or data output stream.  This makes it possible to
 * both read and write from one "channel".
 *
 * @author <a href="mailto:davidw@dedasys.com">David N. Welton</a>
 * @version 1.0
 */

public class HeclChannel {
    /**
     * <code>datainputstream</code> is the stream that this channel
     * reads from.
     *
     */
    public DataInputStream datainputstream = null;
    /**
     * <code>dataoutputstream</code> is the stream this channel writes
     * to.
     *
     */
    public DataOutputStream dataoutputstream = null;

    /**
     * Creates a new read-only <code>HeclChannel</code> instance.
     *
     * @param dis a <code>DataInputStream</code> value
     */
    public HeclChannel(DataInputStream dis) {
	datainputstream = dis;
    }

    /**
     * Creates a new write-only <code>HeclChannel</code> instance.
     *
     * @param dos a <code>DataOutputStream</code> value
     */
    public HeclChannel(DataOutputStream dos) {
	dataoutputstream = dos;
    }

    /**
     * Creates a new <code>HeclChannel</code> instance for reading and
     * writing.
     *
     * @param dis a <code>DataInputStream</code> value
     * @param dos a <code>DataOutputStream</code> value
     */
    public HeclChannel(DataInputStream dis, DataOutputStream dos) {
	datainputstream = dis;
	dataoutputstream = dos;
    }

    /**
     * <code>readable</code> returns true if the channel is open for
     * reading.
     *
     * @return a <code>boolean</code> value
     */
    public boolean readable() {
	if (datainputstream != null) {
	    return true;
	}
	return false;
    }

    /**
     * <code>writable</code> returns true if the channel is open for
     * writing.
     *
     * @return a <code>boolean</code> value
     */
    public boolean writable() {
	if (dataoutputstream != null) {
	    return true;
	}
	return false;
    }

}