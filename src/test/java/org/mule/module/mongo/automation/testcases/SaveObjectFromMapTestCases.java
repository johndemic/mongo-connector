/**
 * Copyright (c) MuleSoft, Inc. All rights reserved. http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.md file.
 */

package org.mule.module.mongo.automation.testcases;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mule.api.MuleEvent;
import org.mule.api.processor.MessageProcessor;

import com.mongodb.DBObject;

public class SaveObjectFromMapTestCases extends MongoTestParent {

	@Before
	public void setUp() {
		try {
			testObjects = (Map<String, Object>) context.getBean("saveObjectFromMap");
			MessageProcessor flow = lookupFlowConstruct("create-collection");
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
	
	private DBObject getSingleObjectFromMap(Map<String, Object> testObjs) throws Exception {
		MessageProcessor flow = lookupFlowConstruct("find-one-object-using-query-map");
		MuleEvent response = flow.process(getTestEvent(testObjects));
		DBObject object = (DBObject) response.getMessage().getPayload();
		return object;
	}
	
	@Category({SmokeTests.class, SanityTests.class})
	@Test
	public void testSaveObjectFromMap() {
		try {
		
			String key = testObjects.get("key").toString();
			String value = testObjects.get("value").toString();
		
			// Save object to MongoDB
			MessageProcessor flow = lookupFlowConstruct("save-object-from-map");
			MuleEvent response = flow.process(getTestEvent(testObjects));
			
			// Check whether it was saved
			DBObject object = getSingleObjectFromMap(testObjects);
			assertTrue(object.containsField(key));
			assertTrue(object.get(key).equals(value));
			
			// Modify object and save to MongoDB
			testObjects.put("value", "differentValue");
			String differentValue = testObjects.get("value").toString();
			flow = lookupFlowConstruct("save-object-from-map");
			response = flow.process(getTestEvent(testObjects));
			
			// Check that modifications were saved
			object = getSingleObjectFromMap(testObjects);
			assertTrue(object.containsField(key));
			assertFalse(object.get(key).equals(value));
			assertTrue(object.get(key).equals(differentValue));
		}
		catch (Exception ex) {
			ex.printStackTrace();
			fail();
		}
	}
}
