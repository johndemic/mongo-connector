package org.mule.module.mongo.automation.testcases;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.HashMap;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mule.api.MuleEvent;
import org.mule.api.processor.MessageProcessor;

import com.mongodb.DBObject;

public class FindOneObjectTestCases extends MongoTestParent {

	private DBObject myDbObject;
	
	@Before
	public void setUp() {
		try {
			// create the collection
			testObjects = (HashMap<String, Object>) context.getBean("createCollection");
			MessageProcessor flow = lookupFlowConstruct("create-collection");
			MuleEvent response = flow.process(getTestEvent(testObjects));
			
			// create the object
			testObjects = (HashMap<String, Object>) context.getBean("insertObject");
			flow = lookupFlowConstruct("insert-object");
			response = flow.process(getTestEvent(testObjects));
			
			// dbObject is modified in the insert-object flow, so just get a reference to it
			// so we can compare with it later
			myDbObject = (DBObject) testObjects.get("dbObject");
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}

	@Category({ SanityTests.class })
	@Test
	public void testFindOneObject() {
		try {
			testObjects = (HashMap<String, Object>) context.getBean("findOneObject");
			MessageProcessor flow = lookupFlowConstruct("find-one-object");
			MuleEvent response = flow.process(getTestEvent(testObjects));
			
			// Get the retrieved DBObject
			// No MongoException means that it found a match (we are matching using ID)
			DBObject payload = (DBObject) response.getMessage().getPayload();
			assertTrue(payload.equals(myDbObject));
			
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}

	@After
	public void tearDown() {
		try {
			// drop the collection
			testObjects = (HashMap<String, Object>) context.getBean("dropCollection");
			MessageProcessor flow = lookupFlowConstruct("drop-collection");
			MuleEvent response = flow.process(getTestEvent(testObjects));
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}

}
