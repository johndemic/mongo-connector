package org.mule.module.mongo.automation.testcases;

import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.BeforeClass;
import org.mule.api.processor.MessageProcessor;
import org.mule.tck.junit4.FunctionalTestCase;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

public class MongoTestParent extends FunctionalTestCase {

	protected static final String[] SPRING_CONFIG_FILES = new String[] { "AutomationSpringBeans.xml" };
	protected static ApplicationContext context;
	protected Map<String, Object> testObjects;

	@Override
	protected String getConfigResources() {
		return "automation-test-flows.xml";
	}

	protected MessageProcessor lookupFlowConstruct(String name) {
		return (MessageProcessor) muleContext.getRegistry()
				.lookupFlowConstruct(name);
	}

	@BeforeClass
	public static void beforeClass() {
		context = new ClassPathXmlApplicationContext(SPRING_CONFIG_FILES);
	}
		
	protected List<DBObject> getEmptyDBObjects(int num) {
		List<DBObject> list = new ArrayList<DBObject>();
		for (int i = 0; i < num; i++) {
			list.add(new BasicDBObject());
		}
		return list;
	}

	protected void insertObjects(List<DBObject> objs) {
		try {
			MessageProcessor insertFlow = lookupFlowConstruct("insert-object");
			
			for (DBObject obj : objs) {
				testObjects.put("dbObject", obj);
				insertFlow.process(getTestEvent(testObjects));
			}
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}
}
