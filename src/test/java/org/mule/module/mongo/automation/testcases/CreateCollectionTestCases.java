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
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mule.api.processor.MessageProcessor;

public class CreateCollectionTestCases extends MongoTestParent {

	@After
	public void tearDown() {
		try {
			MessageProcessor flow = lookupMessageProcessorConstruct("drop-collection");
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
	public void testCreateCollection() {
		
		try {
			testObjects = (HashMap<String, Object>) context.getBean("createCollection");
			
			MessageProcessor flow = lookupMessageProcessorConstruct("create-collection");
			flow.process(getTestEvent(testObjects));
			
			flow = lookupMessageProcessorConstruct("exists-collection");
			Boolean collection = (Boolean) flow.process(getTestEvent(testObjects)).getMessage().getPayload();
			assertTrue(collection);
		}
		catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}
		
}
