/* Copyright 2008 David N. Welton - DedaSys LLC - http://www.dedasys.com

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

package org.hecl.android;

import android.os.IBinder;
import android.util.Log;
import org.hecl.HeclException;
import org.hecl.Interp;
import org.hecl.ListThing;
import org.hecl.LongThing;
import org.hecl.ObjectThing;
import org.hecl.Thing;
import java.util.Vector;


class HeclChatListener extends com.google.android.gtalkservice.IChatListener.Stub {

    private Interp interp = null;
    public Thing convertedToGroupChat = null;
    public Thing newMessageReceived = null;
    public Thing participantJoined = null;
    public Thing participantLeft = null;
    /* From com.google.android.gtalkservice.IChatListener  */

    public HeclChatListener(Interp i) {
	interp = i;
    }

    public void convertedToGroupChat(String oldJid, String groupChatRoom, long groupId) {
	if (convertedToGroupChat == null) {
	    return;
	}

	try {
	    Vector vec = ListThing.get(convertedToGroupChat.deepcopy());
	    vec.add(new Thing(oldJid));
	    vec.add(new Thing(groupChatRoom));
	    vec.add(LongThing.create(groupId));
	    interp.eval(ListThing.create(vec));
	} catch (HeclException he) {
	    Hecl.logStacktrace(he);
	    Log.v("hecl convertedToGroupChat callback", he.toString());
	}
    }

    public void newMessageReceived(String from, String body) {
	if (newMessageReceived == null) {
	    return;
	}

	try {
	    Vector vec = ListThing.get(newMessageReceived.deepcopy());
	    vec.add(new Thing(from));
	    vec.add(new Thing(body));
	    interp.eval(ListThing.create(vec));
	} catch (HeclException he) {
	    Hecl.logStacktrace(he);
	    Log.v("hecl newMessageReceived callback", he.toString());
	}
    }

    public void participantJoined(String groupChatRoom, String nickname) {
	if (participantJoined == null) {
	    return;
	}

	try {
	    Vector vec = ListThing.get(participantJoined.deepcopy());
	    vec.add(new Thing(groupChatRoom));
	    vec.add(new Thing(nickname));
	    interp.eval(ListThing.create(vec));
	} catch (HeclException he) {
	    Hecl.logStacktrace(he);
	    Log.v("hecl participantJoined callback", he.toString());
	}
    }
    public void participantLeft(String groupChatRoom, String nickname) {
	if (participantLeft == null) {
	    return;
	}

	try {
	    Vector vec = ListThing.get(participantLeft.deepcopy());
	    vec.add(new Thing(groupChatRoom));
	    vec.add(new Thing(nickname));
	    interp.eval(ListThing.create(vec));
	} catch (HeclException he) {
	    Hecl.logStacktrace(he);
	    Log.v("hecl participantLeft callback", he.toString());
	}
    }

}