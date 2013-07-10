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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mule.api.MuleEvent;
import org.mule.api.processor.MessageProcessor;
import org.mule.module.mongo.api.MongoCollection;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

public class RemoveObjectsUsingQueryMapTestCases extends MongoTestParent{

	@SuppressWarnings("unchecked")
	@Before
	public void setUp() {
		try {
			// Create the collection
			testObjects = (HashMap<String, Object>) context.getBean("removeObjectsUsingQueryMap");
			MessageProcessor flow = lookupFlowConstruct("create-collection");
			flow.process(getTestEvent(testObjects));

			// Load variables from testObjects
			String key = testObjects.get("key").toString();
			String value = testObjects.get("value").toString();
			int numberOfObjects = (Integer) testObjects.get("numberOfObjects");
			int extraObjects = (Integer) testObjects.get("extraObjects");
			
			// Create list of objects, some with key-value pair, some without
			List<DBObject> objects = new ArrayList<DBObject>();
			for (int i = 0; i < numberOfObjects; i++) {
				DBObject dbObj = new BasicDBObject(key, value);
				objects.add(dbObj);
			}
			
			objects.addAll(getEmptyDBObjects(extraObjects));
			
			// Insert objects into collection
			insertObjects(objects);
			
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
	public void testRemoveUsingQueryMap_WithQueryMap() {
		try {
			int extraObjects = (Integer) testObjects.get("extraObjects");
			String key = testObjects.get("key").toString();
			
			// Remove all records matching key-value pair
			MessageProcessor flow = lookupFlowConstruct("remove-objects-using-query-map-with-query-map");
			MuleEvent response = flow.process(getTestEvent(testObjects));
			
			// Get all objects
			flow = lookupFlowConstruct("find-objects");
			response = flow.process(getTestEvent(testObjects));
			
			// Only objects which should be returned are those without the key value pairs
			MongoCollection objects = (MongoCollection) response.getMessage().getPayload();
			assertTrue(objects.size() == extraObjects);
			
			// Check that each returned object does not contain the defined key-value pair
			for (DBObject obj : objects) {
				assertTrue(!obj.containsField(key));
			}
			
		}
		catch (Exception ex) {
			ex.printStackTrace();
			fail();
		}
	}

	@Category({ RegressionTests.class})
	@Test
	public void testRemoveUsingQueryMap_WithoutQueryMap() {
		try {
			
			// Remove all records
			MessageProcessor flow = lookupFlowConstruct("remove-objects-using-query-map-without-query-map");
			MuleEvent response = flow.process(getTestEvent(testObjects));
			
			// Get all objects
			flow = lookupFlowConstruct("find-objects");
			response = flow.process(getTestEvent(testObjects));
			
			MongoCollection objects = (MongoCollection) response.getMessage().getPayload();
			assertTrue(objects.isEmpty());
		}
		catch (Exception ex) {
			ex.printStackTrace();
			fail();
		}
	}
}
