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

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mule.api.MuleEvent;
import org.mule.api.processor.MessageProcessor;

import com.mongodb.CommandResult;

public class ExecuteCommandTestCases extends MongoTestParent {

	@SuppressWarnings("unchecked")
	@Before
	public void setUp() {
		try {
			// Get the collectionName and create a collection
			testObjects = (HashMap<String, Object>) context.getBean("executeCommand");
			MessageProcessor flow = lookupMessageProcessorConstruct("create-collection");
			flow.process(getTestEvent(testObjects));	
		}
		catch(Exception e) {
			e.printStackTrace();
			fail();
		}
	}
	
	@Category({ RegressionTests.class})
	@Test
	public void testExecuteCommand() {
		try {
			// Drop the collection using command
			MessageProcessor flow = lookupMessageProcessorConstruct("execute-command");
			MuleEvent response = flow.process(getTestEvent(testObjects));
			CommandResult cmdResult = (CommandResult) response.getMessage().getPayload();
			assertTrue(cmdResult.ok());
			
			flow = lookupMessageProcessorConstruct("exists-collection");
			response = flow.process(getTestEvent(testObjects));
			Boolean exists = (Boolean) response.getMessage().getPayload();
			assertFalse(exists);
		}
		catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}
		
}
