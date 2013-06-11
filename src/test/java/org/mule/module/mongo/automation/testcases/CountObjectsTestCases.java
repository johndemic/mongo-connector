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

import com.mongodb.BasicDBObject;

public class CountObjectsTestCases extends MongoTestParent {

	@Before
	public void setUp() {
		try {
			// Create collection
			testObjects = (HashMap<String, Object>) context.getBean("createCollection");
			lookupFlowConstruct("create-collection").process(getTestEvent(testObjects));
		} catch (Exception ex) {
			ex.printStackTrace();
			fail();
		}
	}

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

	@SuppressWarnings("unchecked")
	@Category({ SmokeTests.class, SanityTests.class })
	@Test
	public void testCountObjects() {

		try {
			MessageProcessor insertFlow = lookupFlowConstruct("insert-object");
			
			testObjects.put("dbObject", new BasicDBObject());
			insertFlow.process(getTestEvent(testObjects));
			
			testObjects.put("dbObject", new BasicDBObject());
			insertFlow.process(getTestEvent(testObjects));
			
			MessageProcessor countFlow = lookupFlowConstruct("count-objects");
			
			testObjects.put("queryRef", null);
			
			MuleEvent response = countFlow.process(getTestEvent(testObjects));
			assertEquals(new Long(2), response.getMessage().getPayload());

		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}

}
