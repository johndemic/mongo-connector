/**
 * Copyright (c) MuleSoft, Inc. All rights reserved. http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.md file.
 */

package org.mule.module.mongo.automation.testcases;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;

import java.util.HashMap;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mule.api.MuleEvent;
import org.mule.api.processor.MessageProcessor;
import org.mule.module.mongo.api.IndexOrder;
import org.mule.module.mongo.api.automation.MongoHelper;

import com.mongodb.DBObject;

public class DropIndexTestCases extends MongoTestParent {

	@SuppressWarnings("unchecked")
	@Before
	public void setUp() {
		try {
			// Create the collection
			testObjects = (HashMap<String, Object>) context.getBean("dropIndex");
			MessageProcessor flow = lookupFlowConstruct("create-collection");
			flow.process(getTestEvent(testObjects));
			
			// Create the index
			flow = lookupFlowConstruct("create-index");
			flow.process(getTestEvent(testObjects));
		}
		catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}
	
	@SuppressWarnings("unchecked")
	@Category({SmokeTests.class, RegressionTests.class})
	@Test
	public void testDropIndexByName() {
		try {

			String indexKey = testObjects.get("field").toString();
			IndexOrder indexOrder = (IndexOrder) testObjects.get("order");
			
			String indexName = indexKey + "_" + indexOrder.getValue();
			
			testObjects.put("index", indexName);
			
			MessageProcessor flow = lookupFlowConstruct("drop-index");
			MuleEvent response = flow.process(getTestEvent(testObjects));
						
			flow = lookupFlowConstruct("list-indices");
			response = flow.process(getTestEvent(testObjects));
			List<DBObject> payload = (List<DBObject>) response.getMessage().getPayload();
						
			
			assertFalse(MongoHelper.indexExistsInList(payload, indexName));
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
