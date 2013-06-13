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

public class FindObjectsTestCases extends MongoTestParent {

	private List<String> objectIDs = new ArrayList<String>();
	
	@SuppressWarnings("unchecked")
	@Before
	public void setUp() {
		try {
			// create collection
			testObjects = (HashMap<String, Object>) context.getBean("insertMultipleObjects");
			MessageProcessor flow = lookupFlowConstruct("create-collection");
			MuleEvent response = flow.process(getTestEvent(testObjects));
			
			// create sample objects			
			flow = lookupFlowConstruct("insert-object");

			int numberOfObjects = Integer.parseInt(testObjects.get("numberOfObjects").toString());
			
			for (int i = 0; i < numberOfObjects; i++) {
				BasicDBObject dbObject = new BasicDBObject();
				testObjects.put("dbObject", dbObject);
				response = flow.process(getTestEvent(testObjects));
				
				String payload = response.getMessage().getPayload().toString();
				objectIDs.add(payload);
			}
			
		}
		catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}

	@Category({SanityTests.class})
	@Test
	public void testFindObjects() {
		try {
			MessageProcessor flow = lookupFlowConstruct("find-objects");
			MuleEvent response = flow.process(getTestEvent(testObjects));
			
			MongoCollection payload = (MongoCollection) response.getMessage().getPayload();
			
			assertTrue(objectIDs.size() == payload.size());
			for (DBObject obj : payload) { 
				String dbObjectID = obj.get("_id").toString();
				assertTrue(objectIDs.contains(dbObjectID));
			}
		}
		catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}
	
	@After
	public void tearDown() {
		try {
			MessageProcessor flow = lookupFlowConstruct("drop-collection");
			flow.process(getTestEvent(testObjects));
		}
		catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}
	
	
}
