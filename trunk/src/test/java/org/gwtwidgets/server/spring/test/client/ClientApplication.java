/*
 * 
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
import java.util.Date;

import org.gwtwidgets.client.stream.Message;
import org.gwtwidgets.client.stream.StreamCallback;
import org.gwtwidgets.client.stream.HtmlStreamReader;
import org.gwtwidgets.server.spring.test.domain.CustomException;
import org.gwtwidgets.server.spring.test.domain.Discount;
import org.gwtwidgets.server.spring.test.domain.DomainException;
import org.gwtwidgets.server.spring.test.domain.Product;
import org.gwtwidgets.server.spring.test.domain.ProductOrder;
import org.gwtwidgets.server.spring.test.server.HibernateDomainService;
import org.gwtwidgets.server.spring.test.server.HibernateDomainServiceAsync;
import org.gwtwidgets.server.spring.test.server.ServiceTest;
import org.gwtwidgets.server.spring.test.server.ServiceTestAsync;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.ServiceDefTarget;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.RootPanel;


/**
 * Unit test client side application. Looks up three versions of the test
 * service (normal, transaction-proxied and POJO), invokes its methods and
 * prints test results.
 * 
 * @author George Georgovassilis, g.georgovassilis[at]gmail.com
 * 
 */
public class ClientApplication implements EntryPoint{

	private Date testStartTimestamp;
	
	private int lastRandomNumber = 0;
	private int randomInvocation = 0;
	
	private void startTiming() {
		testStartTimestamp = new Date();
	}

	private void logTiming(String entry) {
		Date now = new Date();
		log(entry + ": " + (now.getTime() - testStartTimestamp.getTime() + "ms"));
	}

	String testUrls[] = { 
			"../handler/service", 
			"../handler/tx", 
			"../handler/pojo", 
			"../handler/extended", 
			"../exporter/service", 
			"../exporter/tx",
			"../exporter/pojo",
			"../exporter/extended",
			"../annotation/service",
			"../exporter/pojo"
			};

	String testLabel[] = { 
			"GWTHandler with RPC service", 
			"GWTHandler with transaction RPC service", 
			"GWTHandler with POJO service", 
			"GWTHandler with extending service",
			"GWTRPCServiceExporter with RPC service", 
			"GWTRPCServiceExporter transaction RPC service",
			"GWTRPCServiceExporter POJO service", 
			"GWTRPCServiceExporter extending service", 
			"Annotated service",
			"GWTRPCServiceExporter POJO service"
			};

	int testIndex = 0;

	String newRandomString() {
		return "" + Math.random();
	}

	void log(String text) {
		RootPanel.get("testarea").add(new HTML("("+testIndex+") "+text));
	}

	void fail(String text, Throwable t) {
		RootPanel.get("testarea").add(new HTML("<span style='color:red'>" + text + "</span>"));
		if (t!=null)
			RootPanel.get("testarea").add(new HTML("<pre>" + t + "</pre>"));
		GWT.log(text, t);
		throw (t==null?new RuntimeException("Test failed"):new RuntimeException(t));
	}

	ServiceTestAsync getService() {
		ServiceTestAsync service = (ServiceTestAsync) GWT.create(ServiceTest.class);
		ServiceDefTarget endpoint = (ServiceDefTarget) service;
		String moduleRelativeURL = GWT.getModuleBaseURL();
		moduleRelativeURL += testUrls[testIndex];
		endpoint.setServiceEntryPoint(moduleRelativeURL);
		return service;
	}
	
	HibernateDomainServiceAsync getHibernateService(boolean annotated){
		HibernateDomainServiceAsync service = (HibernateDomainServiceAsync) GWT.create(HibernateDomainService.class);
		ServiceDefTarget endpoint = (ServiceDefTarget) service;
		String moduleRelativeURL = GWT.getModuleBaseURL();
		moduleRelativeURL += annotated?"../gileadannotated/domain":"../gilead/domain";
		endpoint.setServiceEntryPoint(moduleRelativeURL);
		return service;
		
	}

	void testsDone() {
		log("<h2>Tests finished successfully</h2>");
		logTiming("Tests finished in ");
	}

	void testAdd() {
		log("<h2>Testing " + testLabel[testIndex] + "</h2>");
		log("Test1: invoking add(3,4)");
		ServiceTestAsync service = getService();
		service.add(3, 4, new AsyncCallback<Integer>() {
			public void onFailure(Throwable exception) {
				fail("Test1 failed: " + exception, exception);
			}

			public void onSuccess(Integer result) {
				log("Test1 success: " + result);
				Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
					public void execute() {
						testSession();
					}
				});
			}
		});
	}

	void testSession() {

		final String newString = newRandomString();
		log("Test2: storing a value in the servlet session (" + newString + ")");
		ServiceTestAsync service = getService();
		service.replaceAttribute("gwt-sl", newString, new AsyncCallback<String>() {
			public void onFailure(Throwable exception) {
				fail("Test2 failed: " + exception, exception);
			}

			public void onSuccess(String result) {
				log("Test2 success: " + result);
				Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
					public void execute() {
						testSessionAlt();
					}
				});
			}
		});
	}

	void testSessionAlt() {

		final String newString = newRandomString();
		log("Test3: storing a value in the servlet session (" + newString + ") by using an alternative implementation");
		ServiceTestAsync service = getService();
		service.replaceAttribute("gwt-sl", newString, new AsyncCallback<String>() {
			public void onFailure(Throwable exception) {
				fail("Test3 failed: " + exception, exception);
			}

			public void onSuccess(String result) {
				log("Test3 success: (old value " + result + ")");
				Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
					public void execute() {
						testExpectedValue(newString);
					}
				});
			}
		});
	}

	void testExpectedValue(final String expectedSessionValue) {

		log("Test4: Retrieving a current session attribute and storing a new one.");

		ServiceTestAsync testRequest = getService();
		testRequest.replaceAttribute("gwt-sl", "", new AsyncCallback<String>() {
			public void onFailure(Throwable exception) {
				fail("Test4 failed: " + exception, exception);
			}

			public void onSuccess(String result) {
				if (expectedSessionValue.equals(result)) {
					log("Test4 success (" + result + ")");
					Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
						public void execute() {
							testException();
						}
					});
				} else
					fail("Test3 failed: " + result, null);
			}
		});
	}

	void testException() {

		log("Test5: Testing for exception translation.");
		ServiceTestAsync testRequest = getService();
		testRequest.throwUndeclaredException((new AsyncCallback<Object>() {
			public void onFailure(Throwable exception) {
				if (!(exception instanceof Serializable)) {
					fail("Test5 failed, expected a SerializableException.", exception);
					return;
				}
				log("Test5 success");
				Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
					public void execute() {
						testCustomException();
					}
				});
			}

			public void onSuccess(Object result) {
				fail("Test5 failed, expected a SerializableException.",null);
			}
		}));
	}
	
	void testCustomException() {

		log("Test5b: Testing for custom exception serialisability.");
		ServiceTestAsync testRequest = getService();
		testRequest.throwDeclaredException((new AsyncCallback<Object>() {
			public void onFailure(Throwable exception) {
				if (!(exception instanceof CustomException)) {
					fail("Test5b failed, expected a CustomException.", exception);
					return;
				}
				log("Test5b success");
				testIndex++;
				if (testIndex == 9) {
					testGWTSerialisable();
					return;
				}
				Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
					public void execute() {
						testAdd();
					}
				});

			}

			public void onSuccess(Object result) {
				fail("Test5b failed, expected a CustomException.", null);
			}
		}));
	}


	void testGWTSerialisable() {
		log("<h2>Testing IsSerializable compatibility</h2>");
		ServiceTestAsync test = getService();
		GWTSerialisableObject o = new GWTSerialisableObject();
		o.setString("STRING");
		o.setNumber(2);
		test.getGWTSerialisableObject(o, new AsyncCallback<GWTSerialisableObject>() {

			public void onFailure(Throwable exception) {
				fail("Test failed:" + exception, exception);
			}

			public void onSuccess(GWTSerialisableObject  o) {
				GWTSerialisableObject o1 = (GWTSerialisableObject) o;
				if (!"string".equals(o1.getString()) || o1.getNumber() != 3) {
					fail("Test failed with unexpected return values", null);
					return;
				}
				log("Test success");
				testJavaSerialisable();
			}

		});
	}

	void testJavaSerialisable() {
		log("<h2>Testing Serializable compatibility</h2>");
		ServiceTestAsync test = getService();
		JavaSerialisableObject o = new JavaSerialisableObject();
		o.setString("STRING");
		o.setNumber(2);
		test.getJavaSerialisableObject(o, new AsyncCallback<JavaSerialisableObject>() {

			public void onFailure(Throwable exception) {
				fail("Test failed:" + exception, exception);
			}

			public void onSuccess(JavaSerialisableObject o) {
				if (!"string".equals(o.getString()) || o.getNumber() != 3) {
					fail("Test failed with unexpected return values", null);
					return;
				}
				log("Test success");
				testGilead(false);
			}

		});
	}
	
	void testGilead(final boolean annotated){
		if (annotated)
			log("<h2>Testing Gilead with annotations</h2>");
		else
			log("<h2>Testing Gilead without annotations</h2>");
		HibernateDomainServiceAsync test = getHibernateService(annotated);
		final Discount stub = new Discount();
		stub.setId(1);
		
		test.getOrder(1, new AsyncCallback<ProductOrder>() {

			public void onFailure(Throwable exception) {
				fail("Test failed:" + exception, exception);
			}

			public void onSuccess(ProductOrder  order) {
				if (order == null){
					fail("Expected back a ProductOrder, return value was null",null);
					return;
				}
				if (order.getId()!=1){
					fail("Expected ProductOrder id 1, return value was "+order.getId(), null);
					return;
				}
				if (order.getProducts()==null){
					fail("Expected order list, return value was null", null);
					return;
				}
				if (order.getProducts().size()!=2){
					fail("Expected two products in order list, return value was "+ order.getProducts().size(), null);
					return;
				}
				log("Products in orderlist:");
				for (Product p:order.getProducts()){
					log(p+"");
					if (!p.getDiscounts().contains(stub))
						fail("Product "+p+" does not contain discount 10% sales", null);
				}
				
				log("Test success");
				if (annotated)
					testGileadExceptions();
				else testGilead(true);
			}
		});
	}
	
	void testGileadExceptions(){
		log("<h2>Testing Gilead exception serialization</h2>");
		HibernateDomainServiceAsync test = getHibernateService(false);
		test.throwProductException(new AsyncCallback<Object>() {

			public void onFailure(Throwable caught) {
				log("Exception received");
				DomainException exception = (DomainException)caught;
				Product p = exception.getProduct();
				if (p == null)
					fail("Payload is empty", null);
				else{
					log("Test succeeded");
					testResponseCaching();
				}
			}

			public void onSuccess(Object result) {
				fail("Expected exception", null);
			}
		});
	}
	
	
	void testResponseCaching(){
		log("<h2>Testing that RPC responses are not cached</h2>");
		final ServiceTestAsync test = getService();
		AsyncCallback<Integer> callback = new AsyncCallback<Integer>(){
			public void onFailure(Throwable exception) {
				fail("Test failed:" + exception, exception);
			}
			public void onSuccess(Integer value) {
				log("run "+randomInvocation+": last value = "+lastRandomNumber+" current value "+value);
				switch (randomInvocation){
				case 0:
					test.getRandomNumber(this);
					break;
				case 1:
				case 2:
					if (value.equals(lastRandomNumber)){
						fail("RPC returned same number twice", null);
						return;
					}
					test.getRandomNumber(this);
					break;
				default:
					log("Test success");
					testStream();
					return;
				}
				randomInvocation++;
				lastRandomNumber = value;
			}
			
		};
		test.getRandomNumber(callback);
	}
	
	void testStream(){
		log("<h2>Testing streaming</h2>");
		
		HtmlStreamReader reader = new HtmlStreamReader();
		reader.setPollingInterval(500);
		reader.setMaxAge(5000);
		reader.readAsync("../stream/timer", new StreamCallback(){

			int lastIteration = 0;
			
			public void onError(Exception e) {
				fail("test failed", e);
			}

			public void onMessage(Message message) {
				String expected = "Iteration "+lastIteration;
				if (!message.getContent().equals(expected))
					fail("Expected content '"+expected+"' instead of '"+message+"'",null);
				if (!(""+lastIteration).equals(message.getAttributes().get("iteration")))
						fail("Expected attribute iteration '"+lastIteration+"' instead of '"+message.getAttributes().get("iteration")+"'",null);
				log("Message: "+message);
				lastIteration++;
			}
			
			public void onDisconnect() {
				log("Test success");
				testsDone();
			}
		});
	}

	public void onModuleLoad() {
		startTiming();
		testAdd();
	}

}
