package org.mule.module.mongo.automation.testcases;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.HashMap;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mule.api.MuleEvent;
import org.mule.api.MuleMessage;
import org.mule.api.processor.MessageProcessor;

import com.mongodb.DBObject;

public class InsertObjectFromMapTestCases extends MongoTestParent {

	@Before
	public void setUp() {
		try {
			testObjects = (HashMap<String, Object>) context.getBean("insertObjectFromMap");
			MessageProcessor flow = lookupFlowConstruct("create-collection");
			flow.process(getTestEvent(testObjects));
		} catch (Exception ex) {
			ex.printStackTrace();
			fail();
		}
	}

	@Category({SmokeTests.class, SanityTests.class})
	@Test
	public void testInsertObjectFromMap_WithElements() {
		try {

			testObjects = (HashMap<String, Object>) context.getBean("insertObjectFromMap");
			String key = testObjects.get("key").toString();
			String value = testObjects.get("value").toString();
			
			MessageProcessor flow = lookupFlowConstruct("insert-object-from-map-with-elements");
			MuleEvent response = flow.process(getTestEvent(testObjects));
			MuleMessage message = response.getMessage();
			String objectID = message.getPayload().toString();
			
			assertTrue(objectID != null && !objectID.equals("") && !objectID.trim().equals(""));
			
			flow = lookupFlowConstruct("find-one-object-using-query-map");
			response = flow.process(getTestEvent(testObjects));
			
			DBObject object = (DBObject) response.getMessage().getPayload();
			assertTrue(object.containsField("_id"));
			assertTrue(object.containsField(key));
			
			assertTrue(object.get("_id").equals(objectID));
			assertTrue(object.get(key).equals(value));
		}
		catch (Exception ex) {
			ex.printStackTrace();
			fail();
		}
	}

	// This test fails since there are no elmeents defined
	// Instead of creating a basic DBObject, the connector simply does not create one, resulting in an error
	// Documentation states that the <mongo:element-attributes> segment of the message processor is optional
	// Evidently, it is not and must be supplied. Otherwise, a NPE is thrown.
	//
	// http://mulesoft.github.io/mongo-connector/mule/mongo-config.html#insert-object-from-map
	@Category({SmokeTests.class, SanityTests.class})
	@Test
	public void testInsertObjectFromMap_WithoutElements() {
		try {

			testObjects = (Map<String, Object>) context.getBean("insertObjectFromMap");
			MessageProcessor flow = lookupFlowConstruct("insert-object-from-map-without-elements");
			MuleEvent response = flow.process(getTestEvent(testObjects));
			MuleMessage message = response.getMessage();
			String objectID = message.getPayload().toString();
			
			assertTrue(objectID != null && !objectID.equals("") && !objectID.trim().equals(""));
			
			flow = lookupFlowConstruct("find-one-object-using-query-map");
			response = flow.process(getTestEvent(testObjects));
			
			DBObject object = (DBObject) response.getMessage().getPayload();
			assertTrue(object.containsField("_id"));			
			assertTrue(object.get("_id").equals(objectID));
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
