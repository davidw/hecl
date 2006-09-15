/* Copyright 2005-2006 by data2c.com

Authors:
Wolfgang S. Kechel - wolfgang.kechel@data2c.com
Jörn Marcks - joern.marcks@data2c.com

Wolfgang S. Kechel, Jörn Marcks

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
package org.hecl.net;

public class StringQParam extends QParam {
    public StringQParam(String paramName,String data) {
	super(paramName,
	      HttpRequest.urlencode(HttpRequest.IRIencode(data)).getBytes());
    }
}
