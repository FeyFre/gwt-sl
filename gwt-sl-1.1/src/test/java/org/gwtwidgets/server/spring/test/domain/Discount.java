/* Licensed under the Apache License, Version 2.0 (the "License");
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
package org.gwtwidgets.server.spring.test.domain;

/**
 * Discount applied  
 * @author George Georgovassilis, g.georgovassilis[at]gmail.com
 *
 */
public class Discount extends BaseEntity{

	private double amount;
	private String name;
	
	public Discount(){
	}
	
	public Discount(double amount, String name){
		setAmount(amount);
		setName(name);
	}

	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Discount)) return false;
		return ((Discount)obj).getId() == getId();
	}
	
	@Override
	public int hashCode() {
		return getId();
	}
}
