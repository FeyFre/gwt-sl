package org.gwtwidgets.server.spring.test.serverimpl.stream;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.gwtwidgets.server.spring.stream.HtmlSLStreamWriterImpl;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;

public class TimerStream extends AbstractController {

	private Log log = LogFactory.getLog(getClass());

	private int getLastReceivedMessageId(HttpServletRequest request) {
		String lastMessageId = request.getParameter("lastmessageid");
		try {
			return Integer.parseInt(lastMessageId);
		} catch (Exception e) {
			return 0;
		}
	}

	@Override
	protected ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) throws Exception {
		log.debug(request+ "Requesting timer");
		HtmlSLStreamWriterImpl stream = new HtmlSLStreamWriterImpl();
		stream.setResponse(response);
		int nextMessageSN = getLastReceivedMessageId(request) + 1;
		stream.setSerialStartpoint(nextMessageSN);
		log.debug(request+ "Starting from message ID "+nextMessageSN);
		for (int i = nextMessageSN; i < 10; i++) {
			Thread.sleep(1000);
			Map<String, String> attributes = new HashMap<String, String>();
			attributes.put("iteration", "" + i);
			attributes.put("time", (new Date()).toString());
			stream.writeMessage("Iteration " + i, attributes);
			log.debug(request+" Sent timer " + i);
		}
		log.debug("Closing connection");
		stream.close();
		return null;
	}

}
