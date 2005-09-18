/* Copyright 2005 Wojciech Kocjan

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

package org.hecl.http;

public interface HttpRequestClass {
    /**
     * Sets query type for POST requests
     * 
     * @param type new MIME type
     */
    public abstract void setQueryType(String type);
    /**
     * Sets new URL of the request
     * 
     * @param url new URL
     * @return whether setting the url has succeeded
     */
    public abstract boolean setUrl(String url);
    /**
     * Executes a GET query
     * 
     * @return HTTP body as byte array
     */
    public abstract byte[] executeQuery();
    /**
     * Executes a POST query
     * 
     * @param data POST data
     * @return HTTP body as byte array
     */
    public abstract byte[] executeQuery(byte[] data);
    public abstract String getErrorMessage();
}
