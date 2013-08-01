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
import org.mule.api.MuleMessage;
import org.mule.api.processor.MessageProcessor;

public class InsertObjectTestCases extends MongoTestParent {

	@SuppressWarnings("unchecked")
	@Before
	public void setUp() {
		try {
			testObjects = (HashMap<String, Object>) context.getBean("insertObject");
			MessageProcessor flow = lookupFlowConstruct("create-collection");
			flow.process(getTestEvent(testObjects));
		} catch (Exception ex) {
			ex.printStackTrace();
			fail();
		}
	}

	@Category({SmokeTests.class, RegressionTests.class})
	@Test
	public void testInsertObject() {
		try {
			MessageProcessor flow = lookupFlowConstruct("insert-object");
			MuleMessage message = flow.process(getTestEvent(testObjects)).getMessage();
			String objectID = message.getPayload().toString();
			
			assertTrue(objectID != null && !objectID.equals("") && !objectID.trim().equals(""));
		}
		catch (Exception ex) {
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

}
