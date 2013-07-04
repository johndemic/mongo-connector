/**
 * Copyright (c) MuleSoft, Inc. All rights reserved. http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.md file.
 */

package org.mule.module.mongo.automation.testcases;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.HashMap;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mule.api.MuleEvent;
import org.mule.api.processor.MessageProcessor;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

public class CountObjectsUsingQueryMapTestCases extends MongoTestParent {

	@SuppressWarnings("unchecked")
	@Before
	public void setUp() {
		try {
			// Create collection
			testObjects = (HashMap<String, Object>) context.getBean("countObjectsUsingQueryMap");
			lookupFlowConstruct("create-collection").process(getTestEvent(testObjects));
		} catch (Exception ex) {
			ex.printStackTrace();
			fail();
		}
	}

	@SuppressWarnings("unchecked")
	@After
	public void tearDown() {
		try {
			// Delete collection
			lookupFlowConstruct("drop-collection").process(getTestEvent(testObjects));
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}
	
	@Category({ RegressionTests.class })
	@Test
	public void testCountObjectsUsingQueryMap_without_map() {
		int numObjects = (Integer) testObjects.get("numObjects");
		insertObjects(getEmptyDBObjects(numObjects));

		MuleEvent response = null;
		try {
			MessageProcessor countFlow = lookupFlowConstruct("count-objects-using-query-map-without-query");
			response = countFlow.process(getTestEvent(testObjects));
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
		assertEquals(new Long(numObjects), response.getMessage().getPayload());
	}

	@Category({ RegressionTests.class })
	@Test
	public void testCountObjectsUsingQueryMap_with_map() {
		List<DBObject> list = getEmptyDBObjects(2);

		String queryAttribKey = testObjects.get("queryAttribKey").toString();
		String queryAttribVal = testObjects.get("queryAttribVal").toString();
		
		DBObject dbObj = new BasicDBObject();
		dbObj.put(queryAttribKey, queryAttribVal);
		list.add(dbObj);

		insertObjects(list);

		MuleEvent response = null;
		try {
			MessageProcessor countFlow = lookupFlowConstruct("count-objects-using-query-map-with-query");
			response = countFlow.process(getTestEvent(testObjects));
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
		assertEquals(new Long(1), response.getMessage().getPayload());
	}

}
