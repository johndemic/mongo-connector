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

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
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
		}
		catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}
	
	@After
	public void tearDown() {
		MessageProcessor dropCollectionFlow = lookupFlowConstruct("drop-collection-for-drop-restore");
		try {
			dropCollectionFlow.process(getTestEvent(testObjects));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}	
	
	
	// When DropDatabaseTestCases is executed by itself, the test passes. When executing all test cases, it fails...
	@Category({ RegressionTests.class })
	@Test
//	@Ignore
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
