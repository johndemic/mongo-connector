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

public class FindOneObjectUsingQueryMap extends MongoTestParent {

	@Before
	public void setUp() {
		try {
			testObjects = (HashMap<String, Object>) context.getBean("findOneObjectUsingQueryMap");
			MessageProcessor flow = lookupFlowConstruct("create-collection");
			flow.process(getTestEvent(testObjects));
			
			flow = lookupFlowConstruct("save-object-from-map");
			flow.process(getTestEvent(testObjects));
		} catch (Exception ex) {
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
	
	@Category({SmokeTests.class, SanityTests.class})
	@Test
	public void testFindOneObjectUsingQueryMap() {
		try {
			String key = testObjects.get("key").toString();
			String value = testObjects.get("value").toString();
			
			MessageProcessor flow = lookupFlowConstruct("find-one-object-using-query-map");
			MuleEvent response = flow.process(getTestEvent(testObjects));
			
			DBObject dbObject = (DBObject) response.getMessage().getPayload();
			assertTrue(dbObject.get(key).equals(value));
		}
		catch (Exception ex) {
			ex.printStackTrace();
			fail();
		}
	}
	
}
