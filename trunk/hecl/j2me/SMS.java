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

import javax.microedition.io.Connector;
import javax.wireless.messaging.MessageConnection;
import javax.wireless.messaging.TextMessage;

import org.hecl.HeclException;

/**
 * <code>SMS</code> is a small helper class that sends out an SMS.
 *
 * @author <a href="mailto:davidw@dedasys.com">David N. Welton</a>
 * @version 1.0
 */
public class SMS {

    public static void send (String num, String msgtxt) throws HeclException {
 //#ifdef sms
	try {

	    MessageConnection mc = (MessageConnection)
		Connector.open(num);
	    TextMessage msg = (TextMessage)
		mc.newMessage(MessageConnection.TEXT_MESSAGE);
	    msg.setPayloadText(msgtxt);
	    mc.send(msg);
	    mc.close();
	} catch (Exception e) {
	    throw new HeclException(e.toString());
	}
    }
//#else
    throw new HeclException("sms support not enabled!");
//#endif
}
