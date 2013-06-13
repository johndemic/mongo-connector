package org.mule.module.mongo.automation.testcases;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mule.api.MuleEvent;
import org.mule.api.processor.MessageProcessor;
import org.mule.module.mongo.api.MongoCollection;

import com.mongodb.DBObject;

public class SaveObjectTestCases extends MongoTestParent {

	@Before
	public void setUp() {
		try {
			testObjects = (HashMap<String, Object>) context.getBean("saveObject");
			MessageProcessor flow = lookupFlowConstruct("create-collection");
			MuleEvent response = flow.process(getTestEvent(testObjects));				
		}
		catch(Exception e) {
			e.printStackTrace();
			fail();
		}
	}
	
	@Category({SmokeTests.class, SanityTests.class})
	@Test
	public void testSaveObject() {
		try {
			MessageProcessor flow = lookupFlowConstruct("save-object");
			MuleEvent response = flow.process(getTestEvent(testObjects));
			
			DBObject element = (DBObject) testObjects.get("element");
			
			// Check that object was inserted
			MongoCollection dbObjects = getObjects(testObjects);
			assertTrue(dbObjects.contains(element));			
			
			// Get key and value from payload (defined in bean)
			String key = testObjects.get("key").toString();
			String value = testObjects.get("value").toString();
			
			// Modify object and save
			element.put(key, value);
			flow = lookupFlowConstruct("save-object");
			response = flow.process(getTestEvent(testObjects));
			
			// Check that object was changed in MongoDB
			dbObjects = getObjects(testObjects);
			assertTrue(dbObjects.contains(element));
			
		}
		catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}
	
	private MongoCollection getObjects(Map<String, Object> testObjects) throws Exception {
		MessageProcessor flow = lookupFlowConstruct("find-objects");
		MuleEvent response = flow.process(getTestEvent(testObjects));
		return (MongoCollection) response.getMessage().getPayload();
	}
	
	@After
	public void tearDown() {
		try {
			MessageProcessor flow = lookupFlowConstruct("drop-collection");
			MuleEvent response = flow.process(getTestEvent(testObjects));
		}
		catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}
	
}
