<h1>UNMAINTAINED</h1>

This document has moved to <a href='https://github.com/ggeorgovassilis/gwt-sl/blob/master/README.md'><a href='https://github.com/ggeorgovassilis/gwt-sl/blob/master/README.md'>https://github.com/ggeorgovassilis/gwt-sl/blob/master/README.md</a></a>

<h1>Server Library for GWT- Reference Documentation v1.2</h1>

<h2><a></a>1. Table of Contents</h2>
<ul>
<li><a href='#TOC'>1. Table of contents</a></li>
<li><a href='#Overview'>2. General overview</a>
<ul>
<li><a href='#Quickstart'>2.1. Quickstart</a></li>
</ul>
</li>
<li><a href='#RPC'>3. Publishing beans as RPC services</a>
<ul>
<li><a href='#GWTRPCServiceExporter'>3.1 Publishing POJOs as services - <code>GWTRPCServiceExporter</code></a></li>
<li><a href='#GWTHandler'>3.2 Publishing multiple beans - <code>GWTHandler</code></a></li>
<li><a href='#GWTSpringController'>3.3 Extending a base class - <code>GWTSpringController</code></a></li>
</ul>
</li>
<li><a href='#HowTo'>4. How to</a>
<ul>
<li><a href='#ExceptionTranslation'>4.1 Exception translation</a></li>
<li><a href='#ServletAPI'>4.2 Accessing the Servlet API from inside a service</a></li>
<li><a href='#Hibernate4GWT'>4.3 Using Gilead (Hibernate4GWT)</a></li>
<li><a href='#LightEntityMode'>4.3.1 Light Entity Mode</a></li>
<li><a href='#DynamicProxyMode'>4.3.2 Dynamic Proxy Mode</a></li>
<li><a href='#HTTPHeaders'>4.4 Changing HTTP Headers</a></li>
<li><a href='#StrongNamePermutationCheck'>4.5 Enhance RPC security (strong name permutation check)</a></li>
<li><a href='#ObfuscatedTypeNames'>4.6 Decrease RPC payload size</a></li>
<li><a href='#SerializationPolicyProvider'>4.7 Do more things with Serialization Policies</a></li>
<li><a href='#MultipleSerializationPolicies'>4.8 Use multiple serialization policies</a></li>
</ul>
</li>
<li><a href='#FAQ'>5. FAQ</a>
<ul>
<li><a href='#Classloading'>I am seeing a <code>java.lang.NoClassDefFoundError</code> although the class/jar is there!</a></li>
<li><a href='#java14'>My application runs on a 1.4 JRE and I'm getting a <code>ClassFormatError</code></a></li>
<li><a href='#InvocationTargetException'>I am getting a <code>java.lang.reflect.InvocationTargetException</code></a></li>
<li><a href='#404'>RPC to my services don't work, I'm getting a <code>404</code> but no exception in the server log</a></li>
<li><a href='#Tests'>How do I run the unit tests?</a></li>
<li><a href='#Hosted'>Why does my project not run with the hosted mode browser?</a></li>
<li><a href='#CachedRPC'>RPCs are always returning the same objects to the client</a></li>
<li><a href='#NoRPCInAppContext'>I am getting a <code>NullPointerException</code> in <code>GenericServlet</code></a></li>
<li><a href='#GileadNoPolicy'>I am getting something about <code>SomeClass_gilead_15</code> and serialization policy</a></li>
</ul>
</li>
<li><a href='#Resources'>6. Links and Resources</a></li>
</ul>

<h2><a></a>2. General Overview</h2>
<p>
The Server Library for GWT (in short SL) is a collection of Java server side components for the Google Web Toolkit AJAX framework<br>
with the current focus on the Spring framework by facilitating publishing of Spring beans as RPC services. The main<br>
binary dependencies are GWT 2.0 and Spring 2.5.<br>
</p>
<p>The main features of this library are:</p>
<ul>
<li>Exporting POJOs as services to GWT's PRC</li>
<li>Integration of Gilead for passing Hibernate-managed objects through RPC</li>
</ul>
<p>
The Spring Framework is an established component framework for web applications that span<br>
authentication, database access and complex page flow. Through its aspect oriented approach<br>
it is unobtrusive and cleanly separates the presentation layer from the business logic and<br>
the data model, allowing for back end services which are agnostic towards the way the<br>
presentation is rendered. This is the ideal base for a GWT application, which also separates the<br>
presentation (widgets that run in the browser) from the business logic (RPC services<br>
running on the web server) from the data model (the objects serialised over RPC).<br>
</p>
<p>
GWT binds java methods to RPC calls by using the Servlet API so that<br>
each service you write is a servlet.The Servlet API however is rather crude and the servlet<br>
container (like Tomcat) is a gross environment providing little assistance to elaborate tasks<br>
like transaction management, AOP tasks (authentication, logging, per-task caching) etc. Also<br>
the notoriously scarce configuration abilities are by far inferior to Spring's XML<br>
configuration and bean injection which allows even the most complex configurations by<br>
plugging objects together in XML. With the SL you can easily write Spring managed beans<br>
which act as GWT services, taking full advantage of both frameworks.<br>
</p>
<h3><a></a>2.1. Quickstart</h3>
<p>
We'll briefly cover the steps necessary to publish a simple Spring managed bean so that a GWT client can communicate with it over RPC. There are several ways of<br>
exporting a bean to RPC, here we shall follow the simplest one, namely the <code>GWTHandler</code>. Details follow in <a href='#RPC'>Chapter 3</a>.<br>
</p>
<p>
We start off with the definition of the service interface. Our service will perform a simple arithmetic addition. Since<br>
the client application running in a browser will access this interface also (its Async version, to be precise), the service interface must extend GWT's<br>
<code>RemoteService</code> interface.<br>
</p>
<p>
<pre><code>ServiceTest.java :<br>
<br>
package org.gwtwidgets.server.spring.test.server;<br>
<br>
import com.google.gwt.user.client.rpc.RemoteService;<br>
<br>
public interface ServiceTest extends RemoteService{<br>
  public int add(int a, int b);<br>
}<br>
</code></pre>
</p>
<p>
The implementation is free of any dependencies on Spring, GWT or the servlet API.<br>
</p>
<p>

<pre><code>ServiceTestImpl.java :<br>
<br>
package org.gwtwidgets.server.spring.test.serverimpl;<br>
<br>
import org.gwtwidgets.server.spring.test.server.ServiceTest;<br>
<br>
public class ServiceTestImpl implements ServiceTest{<br>
<br>
  public int add(int a, int b) {<br>
    return a + b;<br>
  }<br>
}<br>
</code></pre>
</p>
<p>
This is a Spring application, so we load the Spring servlet which will export our service bean under the url <code>http://localhost:8080/gwtWebApp/handler/rpctest</code>
</p>
<p>
<pre><code>web.xml :<br>
<br>
&lt;web-app&gt;<br>
<br>
&lt;!-- <br>
Mapping an RPC service defined in handler-servlet.xml<br>
--&gt;<br>
<br>
&lt;servlet&gt;<br>
  &lt;servlet-name&gt;handler&lt;/servlet-name&gt;`<br>
  &lt;servlet-class&gt;org.springframework.web.servlet.DispatcherServlet&lt;/servlet-class&gt;<br>
  &lt;load-on-startup&gt;1&lt;/load-on-startup&gt;<br>
&lt;/servlet&gt;<br>
&lt;servlet-mapping&gt;<br>
  &lt;servlet-name&gt;handler&lt;/servlet-name&gt;<br>
  &lt;url-pattern&gt;/handler/*&lt;/url-pattern&gt;<br>
&lt;/servlet-mapping&gt;<br>
<br>
&lt;/web-app&gt;<br>
</code></pre>

</p>
<p>
Last, we define a URL handler mapping like we'd do with any regular Spring MVC application, only that we won't use a <code>SimpleUrlHandlerMapping</code>
but a <code>GWTHandler</code> which knows how to translate RPC calls to method invocations on POJOs.<br>
</p>
<p>
<pre><code>handler-servlet.xml :<br>
&lt;beans&gt;<br>
  <br>
  &lt;bean class="org.gwtwidgets.server.spring.GWTHandler"&gt;<br>
    &lt;property name="mappings"&gt;<br>
      &lt;map&gt;<br>
        &lt;entry key="/rpctest" value-ref="RPCTest" /&gt;<br>
    &lt;/map&gt;<br>
    &lt;/property&gt;<br>
  &lt;/bean&gt;<br>
<br>
  &lt;bean id="RPCTest" class="org.gwtwidgets.server.spring.test.serverimpl.ServiceTestImpl" /&gt;<br>
<br>
&lt;/beans&gt;<br>
</code></pre>
</p>
<h2><a></a>3. Publishing beans as RPC services</h2>
<p>
The SL creates RPC services following different implementation strategies, from extending a base<br>
class similar to the default way services are created in GWT to publishing POJOs as services without<br>
dependencies on any class, interface or even annotation. It is common to all approaches that eventually<br>
a <code>RemoteServiceServlet</code> is generated (usually at runtime) automatically which delegates<br>
RPCs to method invocations to a specified service bean. The following sections describe the different<br>
approaches towards creating services which can be used alone or together.<br>
</p>
<p />
There are always three common steps and you always need a service interface, an asynchronous callback version
of that interface and a service implementation.
<ul>
<li>Create the service interface A, the <code>RemoteService</code> and <code>AsyncCallback</code> interfaces from the service interface</li>
<li>Create a service implementation of A</li>
<li>Use one of the RPC publishing methods to expose your service implementation</li>
</ul>
Note that nothing changes on the client, regardless of which way you use to expose a service to RPC.
The client does not notice whether you use plain GWT, the SL or even a PHP backend that simulates the RPC
protocol.<p />
Without Spring and the SL, a regular RPC request is processed by GWT similar to this figure:<p />
<table>
<tr><td>1.</td><td>Browser encodes objects to RPC string</td></tr>
<tr><td>2.</td><td>Browser makes HTTP request to the designated RPC URL on the server</td></tr>
<tr><td /><td>3.</td><td>Web server forwards the HTTP request to the web application which is bound to that URL</td></tr>
<tr><td /><td>4.</td><td>Based on the servlet mapping in <code>web.xml</code> the request is routed to the corresponding servlet</td></tr>
<tr><td /><td>5.</td><td>The <code>RemoteServiceServlet</code> decodes the RPC payload and invokes the service method</td></tr>
<tr><td>6.</td><td>The service response is encoded as an RPC response payload and send as an HTTP response back to the browser</td></tr>
<tr><td>7.</td><td>Browser decodes the RPC payload and invokes the service callback</td></tr>
</table>
<p />
Now, with Spring and the SL, there are a few more steps which are performed by the framework:<p />
<table>
<tr><td>1.</td><td>Browser encodes objects to RPC string</td></tr>
<tr><td>2.</td><td>Browser makes HTTP request to the designated RPC URL on the server</td></tr>
<tr><td /><td>3.</td><td>Web server forwards the HTTP request to the web application which is bound to that URL</td></tr>
<tr><td /><td>4.</td><td>Based on the servlet mapping in <code>web.xml</code> the request is routed to the corresponding servlet</td></tr>
<tr><td /><td>5.</td><td>Spring routes the request based on its mapping (i.e. the <code>SimpleUrlHandlerMapping</code>) to the corresponding controller</td></tr>
<tr><td /><td /><td>5.1</td><td>SL's <code>GWTRPCServiceExporter</code> decodes the RPC request into Java objects</td></tr>
<tr><td /><td /><td>5.2</td><td>The <code>GWTRPCServiceExporter</code> forwards the request to a specified service bean</td></tr>
<tr><td /><td /><td>5.3</td><td>The <code>GWTRPCServiceExporter</code> encodes the service bean's response into an RPC payload</td></tr>
<tr><td /><td>6.</td><td>Spring sends the response back to the servlet container and back to the Browser</td></tr>
<tr><td>7.</td><td>Browser decodes the RPC payload and invokes the service callback</td></tr>
</table>

<img src='images/rpc-request-flow.png' />

<h2><a></a>3.1 Publishing POJOs as services - <code>GWTRPCServiceExporter</code></h2>
<p>
The <code>GWTRPCServiceExporter</code> is the purist's take at developing services since it does not introduce any API and compile<br>
time dependency to your service. Essentially it is a wrapper which exports any POJO as an RPC service. In order for the<br>
<code>GWTRPCServiceExporter</code> to know which methods to bind to RPC, the service bean should either implement a <code>RemoteService</code>
interface or you should provide it in the configuration. If no interface is specified, all methods are published to RPC<br>
which should be avoided - even fundamental methods like <code>wait()</code> can provide backdoors for various denial of service<br>
attacks.<br>
</p>
<p>
Let's create a simple bean which we will later publish as a service. This example is taken from the unit<br>
test web application and it shows a trivial bean with a method that adds two integers. Note that<br>
the bean is really a POJO, it does not extend or implement any class or interface.<br>
</p>
```
package org.gwtwidgets.server.spring.test.serverimpl;

public class ServiceTestPOJO {

public int add(int a, int b) {
  return a + b;
}
...
}
```
<p>
Next we need an interface which extends <code>RemoteService</code> and declares the methods which should be exposed<br>
to RPC. Since in our example we have only one, this is an easy task:<br>
</p>
```
package org.gwtwidgets.server.spring.test.server;

public interface ServiceTest extends RemoteService{

  public int add(int a, int b);

}
```

<p>
That leaves only the correct bean creation and URL mapping in the Spring servlet XML:<br>
</p>

```
<!-- 
Create POJO version of the Test service: implements only methods but does not expose any RPC interfaces 
-->

<bean id="ServiceTestPOJO" class="org.gwtwidgets.server.spring.test.serverimpl.ServiceTestPOJO" />


<!-- 
Declaration of RPC service with the RPCServiceExporter which behaves very similar to 
a Spring controller and needs to be mapped to URLs the same way any other Spring 
controller is mapped...
-->

<bean id="RPCTestPOJO" class="org.gwtwidgets.server.spring.GWTRPCServiceExporter">

<!--
Reference to the service bean which should be exported via RPC to the web.
-->

  <property name="service" ref="ServiceTestPOJO" />

<!--
If our Test service was not a 100% pure POJO but also implemented the ServiceTest interface then
we wouldn't have to specify it here. Note that you can provide multiple interface names, as long as
your service has the corresponding methods with a matching signature.
-->

  <property name="serviceInterfaces">
    <value>
      org.gwtwidgets.server.spring.test.server.ServiceTest
    </value>
  </property>
</bean>


<!--
... with a SimpleUrlHandlerMapping for instance:
-->

  <bean class="org.springframework.web.servlet.handler.SimpleUrlHandlerMapping">
    <property name="mappings">
      <map>
        <entry key="/service" value-ref="RPCTestPOJO" />
      </map>
    </property>
  </bean>
```


<h2><a></a>3.2 Publishing multiple beans - <code>GWTHandler</code></h2>
<p>
The <code>GWTHandler</code> allows you to quickly map multiple RPC service beans to different URLs<br>
very similar to the way Spring's <code>SimpleUrlHandlerMapping</code> maps URLs to controllers. The<br>
mapped beans are internally wrapped into <code>&lt;a href="#GWTRPCServiceExporter"&gt;GWTRPCServiceExporter&lt;/a&gt;</code>
instances, with the notable difference that you cannot specify a service interface in the configuration<br>
and the service beans must implement the <code>RemoteService</code> interface (as a matter of fact there is<br>
a workaround even for that by providing your own implementation of a <code>RPCServiceExporter</code>).<br>
</p>

<p>
First we need the service interface from our previous example:<br>
</p>
```
package org.gwtwidgets.server.spring.test.server;

public interface ServiceAdd extends RemoteService{

  public int add(int a, int b);

}
```

<p>
And the corresponding implementation:<br>
</p>
```
package org.gwtwidgets.server.spring.test.serverimpl;

public class ServiceAddImpl implements ServiceAdd{

public int add(int a, int b) {
  return a + b;
}

}

```
<p>Finally the mapping:</p>

```
<bean id="urlMapping" class="org.gwtwidgets.server.spring.GWTHandler">

<!-- Supply here mappings between URLs and services. Services must implement the RemoteService interface but
are not otherwise restricted.-->
  <property name="mappings">
    <map>

<!-- Other mappings could follow -->
      <entry key="/add.rpc" value-ref="ServiceAdd" />
    </map>
</property>
</bean>

<!-- Declare service -->
<bean id="ServiceAdd" class="org.gwtwidgets.server.spring.test.serverimpl.ServiceAddImpl" />

```
<p>
Annotations can also be used to publish services to URLs. Beans in the current application context<br>
featuring the <code>GWTRequestMapping</code> annotation on their service interface will be picked<br>
up by the <code>GWTHandler</code> and mapped to the specified URL. Please note that the URL is relative<br>
to URL which the <code>GWTHandler</code> is already servicing:<br>
</p>
```
<beans>
<bean class="org.gwtwidgets.server.spring.GWTHandler"/>

<bean id="annotatedService" class="org.gwtwidgets.server.spring.test.serverimpl.AnnotatedServiceTestImpl"/>

</beans>


@GWTRequestMapping("/service")
public interface AnnotatedService extends ServiceTest{

}
```
<p>
You can optionally export annotated beans from a parent application context by enabling the<br>
<code>scanParentApplicationContext</code> property.<br>
</p>
<p>
A sidenote on method interceptors and AOP advice: advice applied on the <code>GWTHandler</code> is<br>
applied on the servlet request/response level and not the method invocation level of a particular service.<br>
In this case it is applied before/after/around the RPC request handling and not around the service method<br>
invocation. You should carefully distinguish between advice applied on the <code>GWTHandler</code> (i.e. access<br>
authorisation based on session attributes) and advice applied on services (i.e. logging or transaction management).<br>
</p>
<p>
If you require a different functionality (for example with exception translation) to be<br>
wrapped around your services than the one provided by the default <code>GWTRPCServiceExporter</code> which is used<br>
by the <code>GWTHandler</code> you can inject your own implementation of a <code>RPCServiceExporter</code> by providing<br>
a <code>RPCServiceExporterFactory</code> which may be of interest to Gilead users as it allows services<br>
to be wrapped with a <code>GileadRPCServiceExporter</code> instead of the plain <code>RPCServiceExporter</code>. For further<br>
details please consult <a href='#Hibernate4GWT'>4.3 Using Gilead</a>.<br>
</p>
<h2><a></a>3.3 Extending a base class - GWTSpringController</h2>
<p>
The <code>GWTSpringController</code> is the SL's oldest yet best-performing approach towards integrating GWT with Spring<br>
and it requires an RPC service to extend the <code>org.gwtwidgets.server.spring.GWTSpringController</code> class.<br>
While you are usually better off with any of the previous strategies, the <code>GWTSpringController</code> is lighter<br>
than the other implementations, relies less on reflection and consumes less processing time.<br>
</p>
<p>The service extends <code>GWTSpringController</code>:</p>

```
package org.gwtwidgets.server.spring.test.serverimpl;

public class ControllerAdd extends GWTSpringController implements ServiceAdd{

public int add(int a, int b) {
  return a+b;
}

}

```

<p>And the mapping:</p>
```
<bean class="org.springframework.web.servlet.handler.SimpleUrlHandlerMapping">
<property name="mappings">
<map>
<entry key="/add.rpc" value-ref="ControllerAdd" />
</map>
</property>
</bean>

<bean id="ControllerAdd" class="org.gwtwidgets.server.spring.test.serverimpl.ControllerAdd"/>
```

<h2><a></a>4. How to</h2>
<p>
In this chapter we will discuss common cases in a Spring web application and how the SL<br>
deals with them - yet, since it uses the basic Spring building blocks and conventions we hope<br>
that you will find little surprises and hardly any unexpected behaviour.<br>
</p>

<h2><a></a>4.1 Exception translation</h2>
<p>
The term Exception translation in general refers to the task of converting one exception<br>
instance to an other of a different class as the program execution passes through different application<br>
layers which nest the original exception in an exception relevant to the current layer. Exceptions<br>
are usually nested so that the root cause lies deep in the stack trace while the exception at the<br>
bottom of the stack trace is an entirely different one, generated by the various program layers the<br>
method invocation went through.<br>
</p>
<p>
By default the <code>GWTHandler</code> and the <code>GWTRPCServiceExporter</code> unwrap the immediate root<br>
cause of any exception and propagate it back to the client.<br>
In this regard they behave slightly different from previous versions which would propagate the first<br>
<code>SerializableException</code> found in the stack trace to the client, regardless how deep in the<br>
stack trace it was.</p>
<p>Exception translation is important for RPC services because only <code>SerializableException</code>s are<br>
propagated through the RPC protocol back to the client and all other exceptions will usually result in<br>
terminating the current request with the client not getting any meaningful message. In previous versions<br>
you could switch off exception translation, now this it possible by overriding methods in the<br>
<code>GWTRPCServiceExporter</code>.<br>
</p>
<p>
<code>SerializableException</code> is a checked exception and as such it does not cause<br>
transactions in the exception-throwing service to be rolled back. This is best tackled either from<br>
inside the service implementation (since you may want the transaction to be committed) or by declaring<br>
the conditions under which a transaction should be rolled back by the transaction proxy - for details on<br>
how to attain both please consult the<br>
<a href='http://static.springframework.org/spring/docs/2.0.x/reference/transaction.html#transaction-declarative'>Spring Reference Manual</a>.<br>
</p>
<p>In v0.1.4c the <code>GWTRPCServiceExporter</code> was refactored to move exception handling into<br>
separate methods. By extending <code>GWTRPCServiceExporter</code> and overriding these methods you can<br>
influence substantially the way target services are invoked and the way exceptions are handled.<br>
</p>
<p>
For instance, imagine that you are trying to handle exceptions in your Spring applications in a common way.<br>
You want to catch all exceptions before they reach the console (or the user), possibly filter out the long traces that<br>
are often introduced by runtime class weaving (such as with CGLIB), filter out <code>InvocationTargetException</code>
traces which are appended to exceptions during reflective invocation and want to repackage these exceptions<br>
into other, more friendly and application specific exceptions. You would do that with the Spring AOP or a<br>
<code>MethodInterceptor</code>, catch there any exceptions, inspect them and throw new exceptions.<br>
</p>
<p>
The exceptions you throw from within the advice would be <code>RuntimeException</code>s, because they cause<br>
Springs transaction management to roll-back by default any transactions and because they do not violate the<br>
signature of the invoked method. If the last statement looks puzzling, think of this scenario:<br>
</p>
```
class MathService {

public double divide(double a, double b){
  return a / b;
}
}
```
<p>
And an obvious advice:<br>
</p>

```
class DivisionByZero extends Exception{
}

class MathAdvice {

public Object doAround(Object target, Method method, Object[] args){
  double b = (double)args[1];
  if (b == null) throw new DivisionByZero();
    return method.invoke(target, args);
  }
}
```
<p>
This means trouble: although you have a <code>MathService</code> instance at hand (more precicely, an advised<br>
proxy - but that is not the invoker's concern), calling <code>divide(3,0)</code> will throw a checked<br>
exception, namely <code>DivisionByZero</code>. If <code>DivisionByZero</code> was a <code>RuntimeException</code>
the call would be fine, but now <code>divide</code>'s method signature contract is violated. Because the JVM<br>
will not allow that, it will wrap the exception into an<br>
<a href='http://java.sun.com/j2se/1.4.2/docs/api/java/lang/reflect/UndeclaredThrowableException.html'>UndeclaredThrowableException</a>
which is a <code>RuntimeException</code> and does not violate the method signature when thrown. But you may have<br>
your reasons for throwing checked exceptions (i.e. because you don't want a transaction to be rolled back) the<br>
<code>GWTRPCServiceExporter</code> can be overridden to handle exceptions. The particular case discussed can be<br>
tackled by catching an <code>UndeclaredThrowableException</code> and propagating the enclosed exception to the<br>
client :<br>
</p>

```
public class MyGWTRPCServiceExporter extends GWTRPCServiceExporter{

@Override
protected String handleUndeclaredThrowableException(Exception e, Method targetMethod, RPCRequest rpcRequest) throws Exception {
  Throwable cause = e.getCause();
  String failurePayload = RPC.encodeResponseForFailure(rpcRequest.getMethod(), cause);
  return failurePayload;
}

}
```
<h2><a></a>4.2 Accessing the Servlet API from inside a service</h2>
<p>
While not 100% in tune with the MVC pattern, it is often convenient to access the servlet<br>
container, the HTTP session or the current HTTP request from the business layer. The SL<br>
provides several strategies to achieve this which pose a compromise in the amount of configuration<br>
required to set up and the class dependencies introduced to the business code.<br>
</p>
<p>
The easiest way to obtain the current HTTP request is by using the <code>ServletUtils</code> class<br>
which provides convenience methods for accessing the <code>HttpServletRequest</code> and<br>
<code>HttpServletResponse</code> instances. Please note that it makes use of thread local variables<br>
and will obviously not return correct values if used in any other than the invoking thread.<br>
</p>
<p>
A simple example:<br>
</p>
```
package org.gwtwidgets.server.spring.test.serverimpl;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.gwtwidgets.server.spring.ServletUtils;

public class ServiceTestPOJO {

...
public String replaceAttribute(String name, String value) {
  HttpSession session = ServletUtils.getRequest().getSession(true)
  String oldValue = (String) session.getAttribute(name);
  session.setAttribute(name, value);
  return oldValue;
}
}
```
<p>
It is also possible to inject the request/response pair in an IoC manner, when compile-time<br>
dependency on the SL API is not desired. This approach sets the request and response to the service<br>
as a thread local variable. Thus, the service implementation could look similar to:<br>
</p>
```
package org.gwtwidgets.server.spring.test.serverimpl;

public class ServiceHttpRequestTestPOJOImpl{

private static ThreadLocal`<HttpServletRequest>` servletRequest = new ThreadLocal`<HttpServletRequest>`();

private static ThreadLocal`<HttpServletResponse>` servletResponse = new ThreadLocal`<HttpServletResponse>`();

...

// These two setters...
public void setRequest(HttpServletRequest request) {
  servletRequest.set(request);
}

// ... must be public
public void setResponse(HttpServletResponse request) {
  servletResponse.set(request);
}

protected HttpServletRequest getRequest() {
  return servletRequest.get();
}

// That's a service method exposed to RPC
public String test2(String newValue) {
  HttpSession session = getRequest().getSession();
  return (String) session.getAttribute(attrName);
}

}
```
<p>
Now we have to create the bean that will perform the actual injection of the servlet request and response<br>
and glue it to the service:<br>
</p>
```
<!-- Target service bean -->
<bean id="ServiceTestHTTPTarget" class="org.gwtwidgets.server.spring.test.serverimpl.ServiceHttpRequestTestImpl" />

<!-- Create request setter -->

<bean id="requestSetter" class="org.gwtwidgets.server.spring.RequestInjection">
<property name="requestSetterName" value="setRequest"/>
<property name="responseSetterName" value="setResponse"/>
</bean>

<!-- Create Servlet API aware service -->
<bean id="ServiceTestHTTP" class="org.springframework.aop.framework.ProxyFactoryBean">
<property name="target" ref="ServiceTestHTTPTarget"/>
<property name="autodetectInterfaces" value="true"/>
<property name="interceptorNames">
<list>
<value>requestSetter</value>
</list>
</property>
</bean>
```

<h2><a></a>4.3 Using Gilead (Hibernate4GWT)</h2>
<p>
<a href='http://noon.gilead.free.fr/gilead/'>Gilead</a> is a library that moves<br>
Hibernate managed objects via RPC between a GWT client and a server.<br>
The SL incorporates Gilead via the <code>GileadRPCServiceExporter</code> which can<br>
be used similar to the <code>GWTRPCServiceExporter</code>. With regard to domain objects,<br>
Gilead supports two modes of operation: the light entity mode where domain objects need to extend the<br>
<code>LightEntity</code> class and the dynamic proxy mode. For serveral reasons, you are strongly<br>
advised to stick with the light entity mode, though dynamic proxy mode is possible (more on that later).<br>
</p>
<h3><a></a>4.3.1 Light Entity Mode</h3>
<p>
A simple setup which uses the <code>GileadRPCServiceExporter</code> looks like this:<br>
</p>
```
<!--
That's the business service which returns and or accepts Hibernate managed objects.
 -->
<bean id="HibernateDomainService" class="org.gwtwidgets.server.spring.test.serverimpl.HibernateDomainServiceImpl">
<property name="sessionFactory" ref="SessionFactory"/>
</bean>

<!--
The SL's GileadRPCServiceExporter combines Gilead's HibernateBeanManager with the
business service and exports it's facade via RPC.
-->

<bean id="GileadRPCService" class="org.gwtwidgets.server.spring.gilead.GileadRPCServiceExporter">
<property name="sessionFactory" ref="SessionFactory" />
<property name="service" ref="HibernateDomainService" />
</bean>

<!--
This is a simple Spring managed Hibernate session factory.
 -->
 
<bean id="SessionFactory" class="org.springframework.orm.hibernate3.LocalSessionFactoryBean">
...
</bean>
```

<p>
The same example, this time with annotated services and the <code>GWTHandler</code>:<br>
</p>
```
<!-- 
Example on how to use the GileadRPCServiceExporter which combines Gilead's (formerly hibernate4gwt)
HibernateBeanManager with a GWTRPCServiceExporter. This servlet loads an in-memory
HSQLDB.
-->
<bean class="org.gwtwidgets.server.spring.GWTHandler" depends-on="HibernateDomainService">
  <property name="serviceExporterFactory">
    <bean class="org.gwtwidgets.server.spring.gilead.GileadRPCServiceExporterFactory">
      <lookup-method name="create" bean="GileadRPCServiceExporterPrototype"/>
    </bean>
  </property>
</bean>

<!-- Factory for creating RPC wrappers that can serialise Hibernate managed objects -->
<bean id="GileadRPCServiceExporterPrototype" class="org.gwtwidgets.server.spring.gilead.GileadRPCServiceExporter" scope="prototype">
  <property name="beanManager" ref="HibernateBeanManager"/>
<!-- Use with dynamic proxy mode 
  <property name="usingProxyClassLoader" value="true"/>
 -->
</bean>

<!-- This is Gilead boilerplate -->
<bean id="PojoStore" class="net.sf.gilead.core.store.stateless.StatelessProxyStore"/>

<bean id="HibernateUtil" class="net.sf.gilead.core.hibernate.HibernateUtil">
  <property name="sessionFactory" ref="SessionFactory"/>
</bean>

<bean id="HibernateBeanManager" class="net.sf.gilead.core.PersistentBeanManager">
  <property name="proxyStore" ref="PojoStore" />
  <property name="persistenceUtil" ref="HibernateUtil"/>
<!-- Use with dynamic proxy mode 
  <property name="classMapper" ref="ClassMapper"/>#
 -->
</bean>

<!-- Use with dynamic proxy mode 
<bean id="ClassMapper" class="net.sf.gilead.core.beanlib.mapper.ProxyClassMapper">
  <property name="persistenceUtil" ref="HibernateUtil"/>
  <property name="java5" value="true"/>
</bean>
 -->

<!--
Setup transaction handling, Sessionfactories, Datasources etc 
 -->
...

<!--
Uppon initialisation, this service adds a few entries to the mapped Hibernate database used later on
by the client application.
 -->
<bean id="HibernateDomainService" class="org.gwtwidgets.server.spring.test.serverimpl.HibernateDomainServiceAnnotated">
  <property name="sessionFactory" ref="SessionFactory"/>
</bean>
```
<p>
Note that for a fully functional setup transaction support should be added to the service beans.<br>
For complete examples consult <code>gilead-servlet.xml</code> and <code>gileadannotated-servlet.xml</code> from the test package.<br>
</p>
<h3><a></a>4.3.2 Dynamic Proxy Mode</h3>
<p>
Dynamic proxy mode for domain objects has some prerequisites:<br>
</p>
<p>
<strong>IsSerializable is mandatory:</strong>
Domain objects must implement <code>IsSerializable</code>. Implementing <code>Serializable</code> won't do.<br>
</p>
<p>
<strong>No serialization policy file:</strong>
You cannot have any serialization policy. Delete the generated serialization policy prior to deploying.<br>
A consequence of the missing serialization policy is that any DTOs or Exceptions must implement <code>IsSerializable</code>.<br>
</p>
<p>
<strong>Generator:</strong>
The <code>*.gwt.xml</code> module must reference Gilead's generator:<br>
<br>
<pre><code>&lt;generate-with class="net.sf.gilead.proxy.gwt.Gwt15ProxyGenerator"&gt;<br>
  &lt;when-type-assignable class="com.google.gwt.user.client.rpc.IsSerializable" /&gt;<br>
&lt;/generate-with&gt;<br>
</code></pre>
</p>
<p>
<strong>GWT.create:</strong>
You have to use <code>GWT.create(DomainClass.class)</code> instead of <code>new DomainClass()</code> when<br>
creating instances of Hibernate managed objects on the client side.<br>
</p>
<p>
<strong>ProxyClassMapper:</strong>
You need to use the <code>ProxyClassMapper</code> and tell the <code>GileadRPCServiceExporter</code> to use it.<br>
<pre><code>&lt;bean id="ClassMapper" class="net.sf.gilead.core.beanlib.mapper.ProxyClassMapper"&gt;<br>
  &lt;property name="persistenceUtil" ref="HibernateUtil"/&gt;<br>
  &lt;property name="java5" value="true"/&gt;<br>
&lt;/bean&gt;<br>
<br>
&lt;bean id="HibernateBeanManager"<br>
class="net.sf.gilead.core.PersistentBeanManager"&gt;<br>
  &lt;property name="proxyStore" ref="PojoStore" /&gt;<br>
  &lt;property name="persistenceUtil" ref="HibernateUtil"/&gt;<br>
  &lt;property name="classMapper" ref="ClassMapper"/&gt;<br>
&lt;/bean&gt;<br>
<br>
&lt;bean id="GileadDomainService" class="org.gwtwidgets.server.spring.gilead.GileadRPCServiceExporter"&gt;<br>
  &lt;property name="beanManager" ref="HibernateBeanManager" /&gt;<br>
  &lt;property name="service" ref="HibernateDomainService" /&gt;<br>
  &lt;property name="usingProxyClassLoader" value="true"/&gt;<br>
&lt;/bean&gt;<br>
</code></pre>
</p>

<h2><a></a>4.4 HTTP Headers</h2>
<p>
Tweaking HTTP headers is a useful technique when it comes to performance tuning your GWT application. The SL<br>
contains the <code>ResponseHeaderFilter</code> which is a servlet filter used to alter response headers.<br>
It was inspired by <a href='http://www.onjava.com/pub/a/onjava/2004/03/03/filters.html'>an article</a>
written by Jason Falkner which discusses many interesting applications of HTTP headers.<br>
</p>
<p>
<code>init-param</code> entries for the <code>ResponseHeaderFilter</code> are HTTP header names and values and are not further interpreted,<br>
except for the special parameter <code>ResponseHeaderFilter.UrlPattern</code> which can be used to specify a regular expression which,<br>
when specified, applies headers only to the subset of request URLs which match the provided pattern.<br>
</p>
<p>
Some HTTP headers describe the validity period of a web resource and can be used to enable caching of resources in a browser.<br>
In an GWT application this will usually be the <code>.cache.html</code>, the module entry HTML page, CSS and images.<br>
<br>
To enable caching for a resource, something along the lines of<br>
</p>
```
<filter>
  <filter-name>CachingFilter</filter-name>
  <filter-class>org.gwtwidgets.server.filters.ResponseHeaderFilter</filter-class>
  <init-param>
    <param-name>Expires</param-name>
    <param-value>Sun, 17 Jan 2038 19:14:07 GMT</param-value>
  </init-param>
</filter>
```
<p>
could be used in <code>web.xml</code>
</p>
<p>
A further interesting application is compressing of static resources such as scripts and static HTML pages. These do not have to be<br>
compressed again for every request but can reside in an already compressed format on the file system. It then suffices to add the appropriate<br>
headers to the HTTP response in order to instruct the browser to inflate the transmitted resource:<br>
</p>
```
<filter>
  <filter-name>GzipFilter</filter-name>
  <filter-class>org.gwtwidgets.server.filters.ResponseHeaderFilter</filter-class>
  <init-param>
    <param-name>Content-Type</param-name>
    <param-value>text/html; charset=utf-8</param-value>
  </init-param>
  <init-param>
    <param-name>Content-Encoding</param-name>
    <param-value>gzip</param-value>
  </init-param>
  <init-param>
    <param-name>ResponseHeaderFilter.UrlPattern</param-name>
    <param-value>.*?\.html</param-value> <!-- Match only *.html URLs -->
  </init-param>
</filter>
```
<h2><a></a>4.5 Enhance RPC security (strong name permutation check)</h2>
<p>
A few versions ago GWT's <code>RemoteServiceServlet</code> started checking certain <code>X-GWT-</code> headers<br>
generated by the client (<a href='https://groups.google.com/group/google-web-toolkit/web/security-for-gwt-applications'>more</a>). As this is<br>
a breaking change with ealier SL versions, this check can be enabled optionally in the <code>GWTRPCServiceExporter</code> (and extending classes) or <code>GWTHandler</code>
with the <code>shouldCheckStrongPermutationName</code> flag.<br>
</p>
<h2><a></a>4.6 Decrease RPC payload size</h2>
<p>
If you've ever inspected an RPC payload you would notice that it contains the complete class names of the DTOs used. GWT also has a not-so-well<br>
known generator which can produce payloads without these class names, significantly reducing the payload. In order to use it you must:<br>
</p>
<p>
<ul>
<li>Inherit <code>com.google.gwt.user.RemoteServiceObfuscateTypeNames</code> in your module xml</li>
<li>Use a serialization policy (many SL-users conviniently don't, so please double check)</li>
<li>Set the correct serialization flag (<code>GWTRPCServiceExporter.setSerializationFlags()</code>) on your exporter, which currently (GWT 2.1.1) is <code>AbstractSerializationStream.FLAG_ELIDE_TYPE_NAMES</code></li>
</ul>
</p>

<h2><a></a>4.7 Do more things with Serialization Policies</h2>
<p>
As of 1.2 you can implement the SL <code>SerializationPolicyProvider</code> interface (not the GWT one!) and inject it<br>
into your favorite flavour of RPCServiceExporters. Your implementation will then be consulted for each and every<br>
request about the serialization policy to use.<br>
</p>

<h2><a></a>4.8 Use multiple serialization policies</h2>
<p>
In case you'd like to use multiple serialization policies at the same time, I strongly recommend Alex Moffat's excelent<br>
<a href='http://jectbd.com/?p=1174'>article</a> on that matter which also provides ready to be used code for your use case.<br>
</p>

<h2><a></a>5. FAQ</h2>

<h2><a></a>I am seeing a <code>java.lang.NoClassDefFoundError</code> although the class/jar is there!</h2>
<p>
The most common cause of this error is that not all related classes are loaded by the same class loader. If, for example, the spring library,<br>
GWT, CGLib and your project classes are partially deployed in WEB-INF/lib and common/lib a <code>NoClassDefFoundError</code> may be thrown.<br>
This is a configuration detail of the concrete servlet container you are using and is often solved by placing all libraries into the same location,<br>
though that may not always be easy because of your web server's dependencies. A very good overview of how class loading is dealt with can be read<br>
at the <a href='http://jakarta.apache.org/commons/logging/tech.html'>Commons Logging</a> project site.<br>
</p>

<h2><a></a>I am getting a <code>java.lang.reflect.InvocationTargetException</code></h2>
<p>
<code>InvocationTargetException</code>s are envelopes that contain other exceptions - it occurs when an exception is thrown in a method<br>
which is invoked via reflection and it is the most common exception you will see when your GWT services throw unintentionally an exception.<br>
This is because RPC joins object methods with HTTP requests by relying on method invocation through reflection. The true cause of the exception<br>
then lies deeper in the stack trace and is nested inside the <code>InvocationTargetException</code>. You may also want to read up on<br>
<a href='#ExceptionTranslation'>Exception Translation</a> which discusses the special case of not treating <code>SerializableException</code>s<br>
as exceptions but propagating them properly back to the client.<br>
</p>

<h2><a></a>My application runs on a 1.4 JRE and I'm getting a <code>ClassFormatError</code></h2>
<p>
Since 0.1.5 the SL requires Java 1.5. If you have to use Java 1.4, consider using a 0.1.4 release of the SL which<br>
includes Java 1.4 support. For this, please consult the corresponding documentation in the 0.1.4 SL.<br>
</p>

<h2><a></a>RPCs to my services don't work, I'm getting a <code>404</code> but no exception in the server log</h2>
<p>
If you can rule out any other errors and you are not seeing any exceptions in the server log (let's supposed that logs are working<br>
OK) your URL mappings may be incorrect. The SL hosts multiple RPC services under one or more Spring managed servlets, thus a good<br>
start is to check the URL mappings in <code>web.xml</code>:<br>
</p>
```
<servlet>
  <servlet-name>myRPCServices</servlet-name>
  <servlet-class>
  org.springframework.web.servlet.DispatcherServlet
  </servlet-class>
  <load-on-startup>1</load-on-startup>
</servlet>

<servlet-mapping>
  <servlet-name>myRPCServices</servlet-name>
  <url-pattern>/myRPCServicesURL/*</url-pattern>
</servlet-mapping>
```

<p>
This maps <code>/myRPCServicesURL/...</code> to a spring servlet which is defined in <code>myRPCServices-servlet.xml</code>.<br>
Note that the <code>/myRPCServicesURL</code> part of the URL is already 'consumed' by the servlet container and you should not duplicate it in<br>
the subsequent Spring mappings:<br>
</p>
```
<bean id="urlMapping" class="org.gwtwidgets.server.spring.GWTHandler">
<property name="mappings">
<map>
<-- Attention: We don't repeat the servlet URL, so it's not /myRPCServicesURL/service1 but just /service1 -->
<entry key="/service1" value-ref="FirstServiceBean" />
<entry key="/service2" value-ref="SecondServiceBean" />
<entry key="/service3" value-ref="ThirdServiceBean" />
...
</map>
</property>
</bean>
```


<h2><a></a>How do I run the unit tests?</h2>
<p>
Very basic stand-alone unit test can be run with <code>ant test</code>. In order to run the demo unit test web application,<br>
first compile it with <code>ant war</code> and deploy it to a servlet container, it is then accessible under <code>http://myServer:port/gwt-sl</code>.<br>
It runs a series of automated tests and should conclude with 'Finished tests' and no error messages.<br>
</p>

<h2><a></a>Why does my project not run with the hosted mode browser?</h2>

<p>The short answer: understand and use the hosted browser's <code>-noserver</code> option.</p>
<p>Some background: The embedded tomcat server that comes with the hosted mode browser is intended as a simple aid<br>
when making your first steps with GWT, so that you can focus on client development and so that you do not<br>
have to worry about server configuration. An applications's ability to run with the hosted mode browser is not<br>
affected by the SL, simply because the SL is a server side framework, while the browser runs the client<br>
side portion of the application code.<br>
Where problems do occur is when you run the browser together with the embedded tomcat server: your application<br>
requires Spring and a mapping of URLs to the RPC services to be set up, but the embedded tomcat starts with<br>
its own configuration instead of yours. Hence it is simpler to setup your own development server, as<br>
you would do with every traditional web application and treat the GWT compiler output as a bunch of static<br>
resources. For an example of such a setup run the <code>package</code> task of <code>build.xml</code> and<br>
inspect the contents of the <code>target/webapp</code> directory or check the equivalent demo WAR in the<br>
project's download area (sources are in <code>src/test</code>).<br>
</p>

<h2><a></a>RPCs are always returning the same objects to the client</h2>
<p>
An RPC is nothing but an HTTP request to the server - depending on the HTTP headers returned by the server's response<br>
it can happen that the browser decides that the response can be cached. This means that future similar requests (with the same URL)<br>
may be served from the browser's cache. The best way to avoid caching RPC responses is by modifying the HTTP headers of the response<br>
in such a way that browser caching is disabled - for an example of how to do this consult the <a href='#HTTPHeaders'>HTTP Headers</a> chapter.<br>
An alternative solution is to make sure that each request is dispatched to an unique URL, i.e. by appending to each RPC request a parameter<br>
with a random, unique value.<br>
</p>
<p>
Also note that the SL's RPC implementations (see chapter 3) attempt to disable response caching by default.<br>
</p>

<h2><a></a>I am getting a NullPointerException in GenericServlet</h2>
<p>
Don't export services in the application context, always export them in a <code>-servlet.xml</code> file. GWT's RPC mechanism expects<br>
a servlet environment into which it logs information. The application context does not provide this<br>
environment (namely the serlvet configuration), which causes <code>GWTRPCServiceExporter</code> instances<br>
declared in the application context to lack the servlet configuration and thus procude NPEs.<br>
</p>
<h2><a></a>I am getting something about <code>SomeClass_gilead_15</code> and serialization policy</h2>
<p>
In short: make sure you have read <a href='#Hibernate4GWT'>Chapter 4.3</a>.<br>
</p>
<p>
Some background: The GWT compiler needs to generate wrapper classes for every DTO class managed by Gilead. For the client, these<br>
classes are generated by the GWT compiler as part of the Javascript output and for the server<br>
they are generated as Java bytecode when the application starts. Currently there is a mismatch between Gilead<br>
and GWT when dynamic proxy mode is used, as a result of which the GWT compiler does not generate all the<br>
necessary code to handle these classes. The only known workaround is to switch to light entity mode. If you are<br>
interested in resolving this issue, please consider voting for <a href='http://code.google.com/p/google-web-toolkit/issues/detail?id=3849&can=5'><a href='https://code.google.com/p/gwt-sl/issues/detail?id=3849'>issue 3849</a></a>.<br>
</p>

<h2><a></a>6. Links and Resources</h2>
<p>
1. Project homepage <a href='http://gwt-widget.sourceforge.net/'><a href='http://gwt-widget.sourceforge.net/'>http://gwt-widget.sourceforge.net/</a></a><br />
2. User group <a href='http://groups.google.com/group/gwt-sl/topics'><a href='http://groups.google.com/group/gwt-sl/topics'>http://groups.google.com/group/gwt-sl/topics</a></a><br />
3. Spring project homepage <a href='http://springframework.org/'><a href='http://springframework.org/'>http://springframework.org/</a></a><br />
4. Hibernate project homepage <a href='http://hibernate.org/'><a href='http://hibernate.org/'>http://hibernate.org/</a></a><br />
5. GWT homepage <a href='http://code.google.com/webtoolkit/'><a href='http://code.google.com/webtoolkit/'>http://code.google.com/webtoolkit/</a></a><br />
6. Gilead homepage <a href='http://noon.gilead.free.fr/gilead/'><a href='http://noon.gilead.free.fr/gilead/'>http://noon.gilead.free.fr/gilead/</a></a><br />
7. Hibernate4GWT project homepage <a href='http://hibernate4gwt.sourceforge.net/'><a href='http://hibernate4gwt.sourceforge.net/'>http://hibernate4gwt.sourceforge.net/</a></a><br />
8. Security with GWT <a href='https://groups.google.com/group/google-web-toolkit/web/security-for-gwt-applications'><a href='https://groups.google.com/group/google-web-toolkit/web/security-for-gwt-applications'>https://groups.google.com/group/google-web-toolkit/web/security-for-gwt-applications</a></a><br />
9. Inline GWT serialized data for reduced page load time <a href='http://jectbd.com/?p=1174'><a href='http://jectbd.com/?p=1174'>http://jectbd.com/?p=1174</a></a>
</p>