/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *    http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.gwtwidgets.server.spring.test.client;

import java.io.Serializable;

/**
 * Simple class used to test RPC serialisation of java.io.Serializable
 * implementations.
 * 
 * @author George Georgovassilis, g.georgovassilis[at]gmail.com
 * 
 */

public class JavaSerialisableObject implements Serializable {
	private static final long serialVersionUID = -9112367310814219457L;
	private int number;
	private String string;

	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
	}

	public String getString() {
		return string;
	}

	public void setString(String string) {
		this.string = string;
	}
}
