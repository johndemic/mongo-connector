package org.mule.module.mongo.automation.testcases;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mule.api.MuleEvent;
import org.mule.api.processor.MessageProcessor;
import org.mule.module.mongo.api.MongoCollection;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

public class UpdateObjectsByFunctionUsingMapTestCases extends MongoTestParent {
	
	@SuppressWarnings("unchecked")
	@Before
	public void setUp() {
		try {
			// Create the collection
			testObjects = (HashMap<String, Object>) context.getBean("updateObjectsByFunctionUsingMap");
			MessageProcessor flow = lookupFlowConstruct("create-collection");
			flow.process(getTestEvent(testObjects));
			
			String queryKey = testObjects.get("queryKey").toString();
			String queryValue = testObjects.get("queryValue").toString();
			int numberOfObjects = (Integer) testObjects.get("numberOfObjects");
			
			// Create the objects with the key-value pair
			List<DBObject> objects = new ArrayList<DBObject>();
			for (int i = 0; i < numberOfObjects; i++) {
				objects.add(new BasicDBObject(queryKey, queryValue));
			}
			
			// Insert the objects
			insertObjects(objects);
			
		} catch (Exception ex) {
			ex.printStackTrace();
			fail();
		}
	}

	@Category({SmokeTests.class, RegressionTests.class})
	@Test
	public void testUpdateObjectsByFunctionUsingMap() {
		try {
			String queryKey = (String) testObjects.get("queryKey");
			String elementValue = (String) testObjects.get("elementValue");
			int numberOfObjects = (Integer) testObjects.get("numberOfObjects");
			
			// Update objects
			MessageProcessor updateObjectsByFunctionUsingMap = lookupFlowConstruct("update-objects-by-function-using-map");
			MuleEvent response = updateObjectsByFunctionUsingMap.process(getTestEvent(testObjects));
			
			// Get all objects
			updateObjectsByFunctionUsingMap = lookupFlowConstruct("find-objects");
			response = updateObjectsByFunctionUsingMap.process(getTestEvent(testObjects));
			
			MongoCollection objects = (MongoCollection) response.getMessage().getPayload();
			for (DBObject obj : objects) {
				assertTrue(obj.containsField(queryKey));
				assertTrue(obj.get(queryKey).equals(elementValue));
			}
			assertTrue(objects.size() == numberOfObjects);
			
		}
		catch (Exception ex) {
			ex.printStackTrace();
			fail();
		}
	}
	
	@After
	public void tearDown() {
		try {
			MessageProcessor flow = lookupFlowConstruct("drop-collection");
			flow.process(getTestEvent(testObjects));
		}
		catch (Exception ex) {
			ex.printStackTrace();
			fail();
		}
	}

	
}
