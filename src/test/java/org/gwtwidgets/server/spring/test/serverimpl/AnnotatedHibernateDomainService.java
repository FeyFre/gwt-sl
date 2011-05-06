package org.gwtwidgets.server.spring.test.serverimpl;

import org.gwtwidgets.server.spring.test.server.HibernateDomainService;

import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("/domain")
public interface AnnotatedHibernateDomainService extends HibernateDomainService{

}
