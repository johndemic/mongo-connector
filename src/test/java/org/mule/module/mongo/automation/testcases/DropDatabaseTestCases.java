package org.mule.module.mongo.automation.testcases;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;

import java.util.HashMap;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mule.api.MuleEvent;
import org.mule.api.processor.MessageProcessor;

public class DropDatabaseTestCases extends MongoTestParent {

	@SuppressWarnings("unchecked")
	@Category({ SmokeTests.class, SanityTests.class })
//	@Test
	public void testDropDatabase() {
		MuleEvent response = null;
		try {
			testObjects = (HashMap<String, Object>) context.getBean("createCollection");
			lookupFlowConstruct("create-collection").process(getTestEvent(testObjects));
			
			MessageProcessor dropDBFlow = lookupFlowConstruct("drop-database");
			dropDBFlow.process(getTestEvent(testObjects));
			
			MessageProcessor flow = lookupFlowConstruct("exists-collection");
			response = flow.process(getTestEvent(testObjects));
			
			Object payload = response.getMessage().getPayload();
			assertFalse((Boolean)payload);
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}
	
}
