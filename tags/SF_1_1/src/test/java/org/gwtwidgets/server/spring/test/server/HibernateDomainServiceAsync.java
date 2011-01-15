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

package org.gwtwidgets.server.spring.test.server;

import org.gwtwidgets.server.spring.test.domain.Discount;
import org.gwtwidgets.server.spring.test.domain.Product;
import org.gwtwidgets.server.spring.test.domain.ProductOrder;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * Asynchronous interface for the domain object service 
 * @author George Georgovassilis, g.georgovassilis[at]gmail.com
 *
 */
public interface HibernateDomainServiceAsync {

	void getOrder(int id, AsyncCallback<ProductOrder> callback);
	void getProduct(int id, AsyncCallback<Product> callback);
	void getDiscount(int id, AsyncCallback<Discount> callback);
	void throwProductException(AsyncCallback<Object> callback);

}
