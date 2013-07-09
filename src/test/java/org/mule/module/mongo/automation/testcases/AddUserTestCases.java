/**
 * Copyright (c) MuleSoft, Inc. All rights reserved. http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.md file.
 */

package org.mule.module.mongo.automation.testcases;

import static org.junit.Assert.assertEquals;
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
	@Category({RegressionTests.class})
	@Test
	public void testAddUser() {
		try {
			testObjects = (HashMap<String, Object>) context.getBean("addUser");
			
			MessageProcessor flow = lookupFlowConstruct("add-user");
			MuleEvent response = flow.process(getTestEvent(testObjects));
			
			HashMap<String, String> result = (HashMap<String, String>) response.getMessage().getPayload();
			assertEquals(testObjects.get("newUsername"), result.get("newUsername"));
			assertEquals(testObjects.get("newPassword"), result.get("newPassword"));
			
			// This is how it was, but now, add-user message processor is not returning WriteResult but a HashMap instead...
//			WriteResult result = (WriteResult) response.getMessage().getPayload();
//			assertTrue(result.getLastError().ok());
//			assertTrue(result.getError() == null);
//			assertTrue(result.getN() == 1);
		}
		catch (Exception e) {
			e.printStackTrace();
			fail();
		}
		
	}
	
}
