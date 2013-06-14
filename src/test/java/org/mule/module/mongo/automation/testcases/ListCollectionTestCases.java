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

import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mule.api.MuleEvent;
import org.mule.api.MuleMessage;
import org.mule.api.processor.MessageProcessor;

public class ListCollectionTestCases extends MongoTestParent {

	private List<String> collectionNames;
	
	@Before
	public void setUp() {
		try {
			testObjects = new HashMap<String, Object>();
			collectionNames = (List<String>) context.getBean("listCollections");
			
			MessageProcessor flow = lookupFlowConstruct("create-collection");
			for (String collectionName : collectionNames) {
				testObjects.put("collectionName", collectionName);
				MuleEvent response = flow.process(getTestEvent(testObjects));
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			fail();
		}
	}

	@Category({SmokeTests.class, SanityTests.class})
	@Test
	public void testListCollections() {
		try {
			MessageProcessor flow = lookupFlowConstruct("list-collections");
			MuleEvent response = flow.process(getTestEvent(testObjects));
			MuleMessage message = response.getMessage();
			Collection<String> payload = (Collection<String>) message.getPayload();
			
			for (String collectionName : collectionNames) {
				assertTrue(payload.contains(collectionName));
			}
			
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

			for (String collectionName : collectionNames) {
				testObjects.put("collectionName", collectionName);
				MuleEvent response = flow.process(getTestEvent(testObjects));
			}
		}
		catch (Exception ex) {
			ex.printStackTrace();
			fail();
		}
	}

}
