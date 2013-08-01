/**
 * Copyright (c) MuleSoft, Inc. All rights reserved. http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.md file.
 */

package org.mule.module.mongo.automation.testcases;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.HashMap;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mule.api.processor.MessageProcessor;
import org.mule.module.mongo.api.MongoCollection;

import com.mongodb.DBObject;

public class SaveObjectTestCases extends MongoTestParent {

	@SuppressWarnings("unchecked")
	@Before
	public void setUp() {
		try {
			testObjects = (HashMap<String, Object>) context.getBean("saveObject");
			MessageProcessor flow = lookupFlowConstruct("create-collection");
			flow.process(getTestEvent(testObjects));				
		}
		catch(Exception e) {
			e.printStackTrace();
			fail();
		}
	}
	
	@Category({SmokeTests.class, RegressionTests.class})
	@Test
	public void testSaveObject() {
		try {
			MessageProcessor flow = lookupFlowConstruct("save-object");
			flow.process(getTestEvent(testObjects));
			
			DBObject element = (DBObject) testObjects.get("elementRef");
			
			// Check that object was inserted
			MongoCollection dbObjects = getObjects(testObjects);
			assertTrue(dbObjects.contains(element));			
			
			// Get key and value from payload (defined in bean)
			String key = testObjects.get("key").toString();
			String value = testObjects.get("value").toString();
			
			// Modify object and save
			element.put(key, value);
			flow = lookupFlowConstruct("save-object");
			flow.process(getTestEvent(testObjects));
			
			// Check that object was changed in MongoDB
			dbObjects = getObjects(testObjects);
			assertTrue(dbObjects.contains(element));
			
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
