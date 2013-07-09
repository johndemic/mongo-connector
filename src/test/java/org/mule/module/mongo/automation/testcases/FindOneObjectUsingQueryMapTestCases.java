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
import org.mule.api.processor.MessageProcessor;

import com.mongodb.DBObject;

public class FindOneObjectUsingQueryMapTestCases extends MongoTestParent {

	@SuppressWarnings("unchecked")
	@Before
	public void setUp() {
		try {
			testObjects = (HashMap<String, Object>) context.getBean("findOneObjectUsingQueryMap");
			MessageProcessor flow = lookupFlowConstruct("create-collection");
			flow.process(getTestEvent(testObjects));
			
			flow = lookupFlowConstruct("save-object-from-map");
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
	
	@Category({SmokeTests.class, RegressionTests.class})
	@Test
	public void testFindOneObjectUsingQueryMap() {
		try {
			String key = testObjects.get("key").toString();
			String value = testObjects.get("value").toString();
			
			MessageProcessor flow = lookupFlowConstruct("find-one-object-using-query-map");
			MuleEvent response = flow.process(getTestEvent(testObjects));
			
			DBObject dbObject = (DBObject) response.getMessage().getPayload();
			assertTrue(dbObject.get(key).equals(value));
		}
		catch (Exception ex) {
			ex.printStackTrace();
			fail();
		}
	}
	
}
