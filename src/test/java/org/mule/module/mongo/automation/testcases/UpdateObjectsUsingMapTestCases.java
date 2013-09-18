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
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mule.api.MuleEvent;
import org.mule.api.processor.MessageProcessor;
import org.mule.module.mongo.api.MongoCollection;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

public class UpdateObjectsUsingMapTestCases extends MongoTestParent {

	@SuppressWarnings("unchecked")
	@Before
	public void setUp() {
		try {
			// Create the collection
			testObjects = (HashMap<String, Object>) context.getBean("updateObjectsUsingMap");
			MessageProcessor flow = lookupMessageProcessorConstruct("create-collection");
			flow.process(getTestEvent(testObjects));

			String queryKey = testObjects.get("queryKey").toString();
			String queryValue = testObjects.get("queryValue").toString();
			int numberOfObjects = (Integer) testObjects.get("numberOfObjects");
			
			// Create the objects with the key-value pair
			List<DBObject> objects = new ArrayList<DBObject>();
			for (int i = 0; i < numberOfObjects; i++) {
				DBObject object = new BasicDBObject(queryKey, queryValue);
				objects.add(object);
			}
			
			// Insert the objects
			insertObjects(objects);
			
		} catch (Exception ex) {
			ex.printStackTrace();
			fail();
		}
	}

	@Category({RegressionTests.class})
	@Test
	public void testUpdateObjectsUsingMap() {
		try {
			String elementKey = testObjects.get("elementKey").toString();
			String elementValue = testObjects.get("elementValue").toString();
			int numberOfObjects = (Integer) testObjects.get("numberOfObjects");
			
			// Update objects
			MessageProcessor flow = lookupMessageProcessorConstruct("update-objects-using-map");
			MuleEvent response = flow.process(getTestEvent(testObjects));
			
			// Get all objects
			flow = lookupMessageProcessorConstruct("find-objects");
			response = flow.process(getTestEvent(testObjects));
			
			MongoCollection objects = (MongoCollection) response.getMessage().getPayload();
			for (DBObject obj : objects) {
				assertTrue(obj.containsField(elementKey));
				assertTrue(obj.get(elementKey).equals(elementValue));
			}
			assertTrue(objects.size() == numberOfObjects);
			
		}
		catch (Exception ex) {
			ex.printStackTrace();
			fail();
		}
	}
	
	@After
	public void tearDown() {
		try {
			MessageProcessor flow = lookupMessageProcessorConstruct("drop-collection");
			flow.process(getTestEvent(testObjects));
		}
		catch (Exception ex) {
			ex.printStackTrace();
			fail();
		}
	}

	
}
