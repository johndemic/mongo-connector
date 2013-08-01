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
import org.mule.api.MuleEvent;
import org.mule.api.MuleMessage;
import org.mule.api.processor.MessageProcessor;

import com.mongodb.DBObject;

public class InsertObjectFromMapTestCases extends MongoTestParent {

	@SuppressWarnings("unchecked")
	@Before
	public void setUp() {
		try {
			testObjects = (HashMap<String, Object>) context.getBean("insertObjectFromMap");
			MessageProcessor flow = lookupFlowConstruct("create-collection");
			flow.process(getTestEvent(testObjects));
		} catch (Exception ex) {
			ex.printStackTrace();
			fail();
		}
	}

	@Category({ RegressionTests.class})
	@Test
	public void testInsertObjectFromMap() {
		try {

			String key = testObjects.get("key").toString();
			String value = testObjects.get("value").toString();
			
			MessageProcessor flow = lookupFlowConstruct("insert-object-from-map");
			MuleEvent response = flow.process(getTestEvent(testObjects));
			MuleMessage message = response.getMessage();
			String objectID = message.getPayload().toString();
			
			assertTrue(objectID != null && !objectID.equals("") && !objectID.trim().equals(""));
			
			flow = lookupFlowConstruct("find-one-object-using-query-map");
			response = flow.process(getTestEvent(testObjects));
			
			DBObject object = (DBObject) response.getMessage().getPayload();
			assertTrue(object.containsField("_id"));
			assertTrue(object.containsField(key));
			
			assertTrue(object.get("_id").equals(objectID));
			assertTrue(object.get(key).equals(value));
		}
		catch (Exception ex) {
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
}
