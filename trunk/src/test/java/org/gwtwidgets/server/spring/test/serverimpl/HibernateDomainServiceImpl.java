/*
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
package org.gwtwidgets.server.spring.test.serverimpl;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.gwtwidgets.server.spring.test.domain.Discount;
import org.gwtwidgets.server.spring.test.domain.DomainException;
import org.gwtwidgets.server.spring.test.domain.Product;
import org.gwtwidgets.server.spring.test.domain.ProductOrder;
import org.gwtwidgets.server.spring.test.server.HibernateDomainService;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.beans.factory.InitializingBean;

import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * Simple service that returns objects managed by hibernate.
 * @author George Georgovassilis, g.georgovassilis[at]gmail.com
 *
 */
@RemoteServiceRelativePath("/domain")
public class HibernateDomainServiceImpl implements HibernateDomainService, InitializingBean{

	private SessionFactory sessionFactory;
	private Log log = LogFactory.getLog(getClass());
	
	public ProductOrder getOrder(int id) {
		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();
		
		ProductOrder order = (ProductOrder)session.get(ProductOrder.class, id);
		tx.commit();
		session.close();
		return order;
	}

	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	public void afterPropertiesSet() throws Exception {
		log.debug("Setting up database with test entries");
		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();
		
		Product p1 = new Product("product 1", 10);
		Product p2 = new Product("product 2", 20);
		Product p3 = new Product("product 3", 40);
		
		Discount d1 = new Discount(10,"Sales 10%");
		Discount d2 = new Discount(30,"Special offer 30%");
		
		session.save(d1);
		session.save(d2);
		
		p1.getDiscounts().add(d1);
		p1.getDiscounts().add(d2);
		p2.getDiscounts().add(d1);
		p3.getDiscounts().add(d1);
		
		session.save(p1);
		session.save(p2);
		session.save(p3);
		
		ProductOrder o1 = new ProductOrder();
		o1.getProducts().add(p1);
		o1.getProducts().add(p2);
		
		session.save(o1);
		
		tx.commit();
		session.close();
		
	}

	public Discount getDiscount(int id) {
		// TODO Auto-generated method stub
		return null;
	}

	public Product getProduct(int id) {
		// TODO Auto-generated method stub
		return null;
	}

	@SuppressWarnings("unchecked")
	public void throwProductException() throws DomainException{
		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();
		
		List<Product> products = session.createQuery("from org.gwtwidgets.server.spring.test.domain.Product").list();
		Product product = products.get(0);
		tx.commit();
		session.close();
		DomainException e = new DomainException();
		e.setProduct(product);
		throw e;
	}

}
