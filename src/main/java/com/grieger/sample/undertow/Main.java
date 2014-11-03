package com.grieger.sample.undertow;

import io.undertow.Undertow;
import io.undertow.jsp.HackInstanceManager;
import io.undertow.jsp.JspServletBuilder;
import io.undertow.server.handlers.PathHandler;
import io.undertow.server.handlers.resource.ClassPathResourceManager;
import io.undertow.servlet.api.DeploymentInfo;
import io.undertow.servlet.api.DeploymentManager;
import io.undertow.servlet.api.ServletContainer;
import org.apache.jasper.deploy.JspPropertyGroup;
import org.apache.jasper.deploy.TagLibraryInfo;

import javax.servlet.ServletException;
import java.util.HashMap;

public class Main {
  public static void main(String [] args) throws ServletException {
    final PathHandler servletPath = new PathHandler();
    final ServletContainer container = ServletContainer.Factory.newInstance();

    DeploymentInfo builder = new DeploymentInfo()
        .setClassLoader(Main.class.getClassLoader())
        .setContextPath("/servletContext")
        .setDeploymentName("servletContext.war")
        .setResourceManager(new DefaultResourceLoader(Main.class))
        .addServlet(JspServletBuilder.createServlet("Default Jsp Servlet", "*.jsp"));

    JspServletBuilder.setupDeployment(builder, new HashMap<String, JspPropertyGroup>(), new HashMap<String, TagLibraryInfo>(), new HackInstanceManager());
    DeploymentManager manager = container.addDeployment(builder);
    manager.deploy();

    servletPath.addPrefixPath(builder.getContextPath(), manager.start());

    // All JSPs will be exposed under http://localhost:8080/servletContext/
    //
    //
    Undertow.builder().addHttpListener(8080, "localhost")
        .setHandler(servletPath)
        .build()
        .start();

    System.out.println("http://localhost:8080/servletContext/test.jsp");
  }
  public static class DefaultResourceLoader extends ClassPathResourceManager {
    public DefaultResourceLoader(final Class<?> clazz) {
      super(clazz.getClassLoader(), clazz.getPackage().getName().replace(".", "/"));
    }
  }
}
