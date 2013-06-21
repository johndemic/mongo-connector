/**
 * Copyright (c) MuleSoft, Inc. All rights reserved. http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.md file.
 */

package org.mule.module.mongo.automation.testcases;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.HashMap;

import org.junit.After;
import org.junit.Before;
import org.junit.experimental.categories.Category;
import org.mule.api.MuleEvent;
import org.mule.api.processor.MessageProcessor;

public class DropDatabaseTestCases extends MongoTestParent {

	@SuppressWarnings("unchecked")
	@Before
	public void setUp() {
		try {
			// Create the collection
			testObjects = (HashMap<String, Object>) context.getBean("dropDatabase");
			MessageProcessor createCollectionFlow = lookupFlowConstruct("create-collection-for-drop-restore");
			createCollectionFlow.process(getTestEvent(testObjects));
			
			MessageProcessor flow = lookupFlowConstruct("exists-collection-for-drop-restore");
			MuleEvent response = flow.process(getTestEvent(testObjects));
			
			Object payload = response.getMessage().getPayload();
			assertTrue("After creating the collection " + testObjects.get("collection") + " it should exist", (Boolean)payload);
		}
		catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}
	
	@After
	public void tearDown() {
		MessageProcessor dropCollectionFlow = lookupFlowConstruct("drop-collection-for-drop-restore");
		MessageProcessor existsCollectionFlow = lookupFlowConstruct("exists-collection-for-drop-restore");
		Object payload = null;
		try {
			dropCollectionFlow.process(getTestEvent(testObjects));
			MuleEvent response = existsCollectionFlow.process(getTestEvent(testObjects));
			
			payload = response.getMessage().getPayload();
		} catch (Exception e) {
			e.printStackTrace();
		}
		assertFalse("After this test runs, there should not be a collection with name " + testObjects.get("collection"), (Boolean)payload);
	}	
	
	
	// When DropDatabaseTestCases is executed by itself, the test passes. When executing all test cases, it fails...
	@Category({ SmokeTests.class, SanityTests.class })
//	@Test
	public void testDropDatabase() {
		MuleEvent response = null;
		try {
			MessageProcessor dropDBFlow = lookupFlowConstruct("drop-database");
			dropDBFlow.process(getTestEvent(testObjects));
			
			MessageProcessor flow = lookupFlowConstruct("exists-collection-for-drop-restore");
			response = flow.process(getTestEvent(testObjects));
			
			Object payload = response.getMessage().getPayload();
			assertFalse("After dropping the database, the collection " + testObjects.get("collection") + " should not exist", (Boolean)payload);
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}
	
}
