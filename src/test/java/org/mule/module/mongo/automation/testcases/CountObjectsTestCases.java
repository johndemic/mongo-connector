package org.mule.module.mongo.automation.testcases;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.HashMap;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mule.api.MuleEvent;
import org.mule.api.processor.MessageProcessor;

public class CountObjectsTestCases extends MongoTestParent {

	@SuppressWarnings("unchecked")
	@Before
	public void setUp() {
		try {
			// Create collection
			testObjects = (HashMap<String, Object>) context.getBean("insertObject");
			lookupFlowConstruct("create-collection").process(getTestEvent(testObjects));
		} catch (Exception ex) {
			ex.printStackTrace();
			fail();
		}
	}

	@SuppressWarnings("unchecked")
	@After
	public void tearDown() {
		try {
			// Delete collection
			testObjects = (HashMap<String, Object>) context.getBean("createCollection");
			lookupFlowConstruct("drop-collection").process(getTestEvent(testObjects));
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}

	@Category({ SmokeTests.class, SanityTests.class })
	@Test
	public void testCountObjects() {
		insertObjects(getEmptyDBObjects(2));
		
		MuleEvent response = null;
		try {
			MessageProcessor countFlow = lookupFlowConstruct("count-objects");
			testObjects.put("queryRef", null);
			response = countFlow.process(getTestEvent(testObjects));
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
		assertEquals(new Long(2), response.getMessage().getPayload());
	}
	
}
