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

import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mule.api.MuleEvent;
import org.mule.api.processor.MessageProcessor;

public class ExistsCollectionTestCases extends MongoTestParent {

	@SuppressWarnings("unchecked")
	@Before
	public void setUp() {
		try {			
			testObjects = (Map<String, Object>) context.getBean("existsCollection");
			MessageProcessor flow = lookupMessageProcessorConstruct("create-collection");
			flow.process(getTestEvent(testObjects));
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}

	@After
	public void tearDown() {
		try {
			MessageProcessor flow = lookupMessageProcessorConstruct("drop-collection");
			flow.process(getTestEvent(testObjects));
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}

	@Category({ SmokeTests.class, RegressionTests.class })
	@Test
	public void testExistsCollection() {
		try {
			MessageProcessor flow = lookupMessageProcessorConstruct("exists-collection");
			MuleEvent response = flow.process(getTestEvent(testObjects));
			
			Object payload = response.getMessage().getPayload();
			assertTrue((Boolean)payload);
			
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}

}
