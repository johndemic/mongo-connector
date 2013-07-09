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

import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mule.api.MuleEvent;
import org.mule.api.processor.MessageProcessor;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

public class UpdateObjectsTestCases extends MongoTestParent {

	@SuppressWarnings("unchecked")
	@Before
	public void setUp() {
		try {
			// Create the collection
			testObjects = (Map<String, Object>) context.getBean("updateObjects");
			MessageProcessor flow = lookupFlowConstruct("create-collection");
			flow.process(getTestEvent(testObjects));
			
			// Insert object
			flow = lookupFlowConstruct("insert-object");
			flow.process(getTestEvent(testObjects));
			
		} catch (Exception ex) {
			ex.printStackTrace();
			fail();
		}
	}
	
	@After
	public void tearDown() {
		try {
			// Drop the collection
			MessageProcessor flow = lookupFlowConstruct("drop-collection");
			flow.process(getTestEvent(testObjects));
		}
		catch (Exception ex) {
			ex.printStackTrace();
			fail();
		}
	}
	
	@Category({RegressionTests.class})
	@Test
	public void testUpdateObjects_OneObject() {
		try {
		
			// Grab the key-value pair
			String key = testObjects.get("key").toString();
			String value = testObjects.get("value").toString();
			
			// Create new DBObject based on key-value pair to replace existing DBObject
			DBObject newDBObject = new BasicDBObject(key, value);
			
			// Place it in testObjects so that the flow can access it
			testObjects.put("elementRef", newDBObject);
			
			// Update the object
			MessageProcessor flow = lookupFlowConstruct("update-objects-single-object");
			MuleEvent response = flow.process(getTestEvent(testObjects));
			
			// Attempt to find the object
			flow = lookupFlowConstruct("find-one-object");
			response = flow.process(getTestEvent(testObjects));
			
			DBObject obj = (DBObject) response.getMessage().getPayload();
			
			// Assert that the object retrieved from MongoDB contains the key-value pairs
			// Not the most ideal way to test, but since update returns void in connector, 
			// we cannot retrieve the ID granted to newDBObject
			assertTrue(obj.containsField(key));
			assertTrue(obj.get(key).equals(value));
			
		}
		catch (Exception ex) {
			ex.printStackTrace();
			fail();
		}
	}
	
}
