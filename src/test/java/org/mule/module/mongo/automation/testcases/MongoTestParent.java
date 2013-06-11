package org.mule.module.mongo.automation.testcases;

import java.util.Map;

import org.junit.BeforeClass;
import org.mule.api.MuleEvent;
import org.mule.api.processor.MessageProcessor;
import org.mule.tck.junit4.FunctionalTestCase;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class MongoTestParent extends FunctionalTestCase {

	protected static final String[] SPRING_CONFIG_FILES  = new String[] {"AutomationSpringBeans.xml"};
	protected static ApplicationContext context;
	protected MessageProcessor flow;
	protected MuleEvent response;
	protected Map<String,Object> testObjects;

	
	@Override
	protected String getConfigResources() {
		return "automation-test-flows.xml";
	}
	
    protected MessageProcessor lookupFlowConstruct(String name) {
        return (MessageProcessor) muleContext.getRegistry().lookupFlowConstruct(name);
    }
	
    @BeforeClass
    public static void beforeClass(){    	
    	context = new ClassPathXmlApplicationContext(SPRING_CONFIG_FILES);    	
    }

}
