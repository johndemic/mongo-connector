package org.mule.module.mongo.automation.testcases;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.HashMap;

import org.junit.After;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mule.api.MuleEvent;
import org.mule.api.processor.MessageProcessor;
import org.mule.module.mongo.api.MongoCollection;

import com.mongodb.DBCollection;

public class CreateCollectionTestCases extends MongoTestParent {

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
	
	@SuppressWarnings("unchecked")
	@Category({SmokeTests.class, SanityTests.class})
	@Test
	public void testCreateCollection() {
		
		try {
			testObjects = (HashMap<String, Object>) context.getBean("createCollection");
			
			MessageProcessor flow = lookupFlowConstruct("create-collection");
			MuleEvent response = flow.process(getTestEvent(testObjects));
			
			flow = lookupFlowConstruct("exists-collection");
			Boolean collection = (Boolean) flow.process(getTestEvent(testObjects)).getMessage().getPayload();
			assertTrue(collection);
		}
		catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}
		
}
