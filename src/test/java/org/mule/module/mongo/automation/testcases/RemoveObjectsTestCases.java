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
import org.mule.module.mongo.api.MongoCollection;

// TODO: Depends on the insertObject bean
public class RemoveObjectsTestCases extends MongoTestParent {

	@Before
	public void setUp() {
		try {
			testObjects = (HashMap<String, Object>) context.getBean("removeObjects");
			MessageProcessor flow = lookupFlowConstruct("create-collection");
			MuleEvent response = flow.process(getTestEvent(testObjects));
			
			testObjects = (HashMap<String, Object>) context.getBean("insertObject");
			flow = lookupFlowConstruct("insert-object");
			response = flow.process(getTestEvent(testObjects));			
		}
		catch(Exception e) {
			e.printStackTrace();
			fail();
		}
	}
	
	@Category({SmokeTests.class, SanityTests.class})
	@Test
	public void testRemoveObjects() {
		try {
			testObjects = (HashMap<String, Object>) context.getBean("removeObjects");
			MessageProcessor flow = lookupFlowConstruct("remove-objects");
			MuleEvent response = flow.process(getTestEvent(testObjects));
			
			flow = lookupFlowConstruct("find-objects");
			response = flow.process(getTestEvent(testObjects));
			
			MongoCollection payload = (MongoCollection) response.getMessage().getPayload();
			assertTrue(payload.size() == 0);			
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
			MuleEvent response = flow.process(getTestEvent(testObjects));
		}
		catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}
	
}
