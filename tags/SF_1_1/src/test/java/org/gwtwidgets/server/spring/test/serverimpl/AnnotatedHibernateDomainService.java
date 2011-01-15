package org.gwtwidgets.server.spring.test.serverimpl;

import org.gwtwidgets.server.spring.GWTRequestMapping;
import org.gwtwidgets.server.spring.test.server.HibernateDomainService;

@GWTRequestMapping("/domain")
public interface AnnotatedHibernateDomainService extends HibernateDomainService{

}
