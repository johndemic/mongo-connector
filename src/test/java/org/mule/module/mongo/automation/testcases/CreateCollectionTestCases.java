package org.mule.module.mongo.automation.testcases;

import static org.junit.Assert.fail;

import java.util.HashMap;

import org.junit.After;
import org.junit.Test;
import org.junit.experimental.categories.Category;

public class CreateCollectionTestCases extends MongoTestParent {

	@After
	public void tearDown() {
		try {
			flow = lookupFlowConstruct("drop-collection");
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
			
			flow = lookupFlowConstruct("create-collection");
			response = flow.process(getTestEvent(testObjects));
		}
		catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}
	
}
