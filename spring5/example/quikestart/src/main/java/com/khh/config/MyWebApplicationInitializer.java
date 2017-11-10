package com.khh.config;

public class MyWebApplicationInitializer {
	public static void main(String[] args) {
//		HttpHandler handler = ...
		Servlet servlet = new TomcatHttpHandlerAdapter(handler);

		Tomcat server = new Tomcat();
		File base = new File(System.getProperty("java.io.tmpdir"));
		Context rootContext = server.addContext("", base.getAbsolutePath());
		Tomcat.addServlet(rootContext, "main", servlet);
		rootContext.addServletMappingDecoded("/", "main");
		server.setHost(host);
		server.setPort(port);
		server.start();
	}
}
