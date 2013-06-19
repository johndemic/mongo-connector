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
import org.mule.module.mongo.api.IndexOrder;

import com.mongodb.DBObject;

public class CreateIndexTestCases extends MongoTestParent {

	@Before
	public void setUp() {
		try {
			testObjects = (HashMap<String, Object>) context.getBean("createIndex");
			MessageProcessor flow = lookupFlowConstruct("create-collection");
			MuleEvent response = flow.process(getTestEvent(testObjects));
		}
		catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}
	
	@Category({SmokeTests.class, SanityTests.class})
	@Test
	public void testCreateIndex() {
		try {

			String indexKey = testObjects.get("field").toString();
			IndexOrder indexOrder = IndexOrder.valueOf(testObjects.get("order").toString());
			String indexName = indexKey + "_" + indexOrder.getValue();
	
			MessageProcessor flow = lookupFlowConstruct("create-index");
			MuleEvent response = flow.process(getTestEvent(testObjects));
						
			flow = lookupFlowConstruct("list-indices");
			response = flow.process(getTestEvent(testObjects));
			List<DBObject> payload = (ArrayList<DBObject>) response.getMessage().getPayload();
								
			assertTrue(existsInList(payload, indexName));
		}
		catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}
	
	@After
	public void tearDown() {
		try {
			// Drop the created index
			String indexKey = testObjects.get("field").toString();
			IndexOrder indexOrder = IndexOrder.valueOf(testObjects.get("order").toString());
			
			String indexName = indexKey + "_" + indexOrder.getValue();
			testObjects.put("index", indexName);
			
			MessageProcessor flow = lookupFlowConstruct("drop-index");
			MuleEvent response = flow.process(getTestEvent(testObjects));
			
			// Drop the collection
			flow = lookupFlowConstruct("drop-collection");
			response = flow.process(getTestEvent(testObjects));
		}
		catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}

}
