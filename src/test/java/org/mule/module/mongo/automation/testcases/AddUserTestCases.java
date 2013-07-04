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

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mule.api.MuleEvent;
import org.mule.api.processor.MessageProcessor;

import com.mongodb.WriteResult;

public class AddUserTestCases extends MongoTestParent {
	
	@SuppressWarnings("unchecked")
	@Category({RegressionTests.class, SmokeTests.class})
	@Test
	public void testAddUser() {
		try {
			testObjects = (HashMap<String, Object>) context.getBean("addUser");
			
			MessageProcessor flow = lookupFlowConstruct("add-user");
			MuleEvent response = flow.process(getTestEvent(testObjects));
			
			WriteResult result = (WriteResult) response.getMessage().getPayload();
			assertTrue(result.getLastError().ok());
			assertTrue(result.getError() == null);
			assertTrue(result.getN() == 1);
		}
		catch (Exception e) {
			e.printStackTrace();
			fail();
		}
		
	}
	
}
