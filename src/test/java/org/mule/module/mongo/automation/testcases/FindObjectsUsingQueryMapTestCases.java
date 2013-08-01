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
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mule.api.MuleEvent;
import org.mule.api.processor.MessageProcessor;
import org.mule.module.mongo.api.MongoCollection;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

public class FindObjectsUsingQueryMapTestCases extends MongoTestParent {
			
	@SuppressWarnings("unchecked")
	@Before
	public void setUp() {
		try {
			// create collection
			testObjects = (Map<String, Object>) context.getBean("findObjectsUsingQueryMap");
			MessageProcessor flow = lookupFlowConstruct("create-collection");
			flow.process(getTestEvent(testObjects));
			
			// Create a number of objects
			int extraObjects = (Integer) testObjects.get("extraObjects");
			int numberOfObjects = (Integer) testObjects.get("numObjects");
			String queryKey = testObjects.get("queryKey").toString();
			String queryValue = testObjects.get("queryValue").toString();
			
			List<DBObject> objects = new ArrayList<DBObject>();
			for (int i = 0; i < numberOfObjects; i++) {
				BasicDBObject obj = new BasicDBObject(queryKey, queryValue);
				objects.add(obj);
			}
			
			// Add extra objects which do not have the key-value pair defined in testObjects
			// These should not be retrieved
			objects.addAll(getEmptyDBObjects(extraObjects));
			
			insertObjects(objects);			
			
		}
		catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}

	@Category({SmokeTests.class, RegressionTests.class})
	@Test
	public void testFindObjectsUsingQueryMap_WithQuery() {
		try {
			int numberOfObjects = (Integer) testObjects.get("numObjects");
			String queryKey = testObjects.get("queryKey").toString();
			String queryValue = testObjects.get("queryValue").toString();
			
			MessageProcessor flow = lookupFlowConstruct("find-objects-using-query-map-with-query");
			MuleEvent response = flow.process(getTestEvent(testObjects));
			
			MongoCollection collection = (MongoCollection) response.getMessage().getPayload();
			
			assertTrue(numberOfObjects == collection.size());
			for (DBObject obj : collection) {
				assertTrue(obj.containsField(queryKey));
				assertTrue(obj.get(queryKey).equals(queryValue));
			}
		}
		catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}
	
	@Category({RegressionTests.class})
	@Test
	public void testFindObjectsUsingQueryMap_WithoutQuery() {
		try {
			int extraObjects = (Integer) testObjects.get("extraObjects");
			int numberOfObjects = (Integer) testObjects.get("numObjects");
			
			MessageProcessor flow = lookupFlowConstruct("find-objects-using-query-map-without-query");
			MuleEvent response = flow.process(getTestEvent(testObjects));
			
			MongoCollection collection = (MongoCollection) response.getMessage().getPayload();
			
			// Assert that everything was retrieved (empty objects + key-value pair objects)
			assertTrue(numberOfObjects + extraObjects == collection.size());
		}
		catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}

	@Category({RegressionTests.class})
	@Test
	public void testFindObjectsUsingQueryMap_WithLimit() {
		try {
		
			int limit = (Integer) testObjects.get("limit");
			
			MessageProcessor flow = lookupFlowConstruct("find-objects-using-query-map-with-limit");
			MuleEvent response = flow.process(getTestEvent(testObjects));
			
			MongoCollection collection = (MongoCollection) response.getMessage().getPayload();
			
			// Assert that only "limit" objects were retrieved
			assertTrue(limit == collection.size());
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
