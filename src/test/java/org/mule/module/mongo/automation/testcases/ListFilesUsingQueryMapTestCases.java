/**
 * Copyright (c) MuleSoft, Inc. All rights reserved. http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.md file.
 */

package org.mule.module.mongo.automation.testcases;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.HashMap;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mule.api.MuleEvent;
import org.mule.api.processor.MessageProcessor;
import org.mule.module.mongo.api.automation.MongoHelper;

import com.mongodb.DBObject;

public class ListFilesUsingQueryMapTestCases extends MongoTestParent {

	@SuppressWarnings("unchecked")
	@Before
	public void setUp() {
		testObjects = (HashMap<String, Object>) context.getBean("listFilesUsingQueryMap");

		createFileFromPayload(testObjects.get("filename1"));
		createFileFromPayload(testObjects.get("filename1"));
		createFileFromPayload(testObjects.get("filename2"));
	}

	@After
	public void tearDown() {
		deleteFilesCreatedByCreateFileFromPayload();
	}

	@SuppressWarnings("unchecked")
	@Category({ RegressionTests.class })
	@Test
	public void testListFilesUsingQueryMap_emptyQuery() {
		MessageProcessor listFilesFlow = lookupMessageProcessorConstruct("list-files-using-query-map-empty-query");
		MuleEvent response = null;
		try {
			response = listFilesFlow.process(getTestEvent(testObjects));
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}

		assertNotNull(response.getMessage());
		assertNotNull(response.getMessage().getPayload());
		assertTrue(response.getMessage().getPayload() instanceof Iterable);

		Iterable<DBObject> iterable = (Iterable<DBObject>) response
				.getMessage().getPayload();

		assertEquals(
				"An empty query map for the query should list all the files", 3,
				MongoHelper.getIterableSize(iterable));

	}

	@SuppressWarnings("unchecked")
	@Category({ RegressionTests.class })
	@Test
	public void testListFilesUsingQueryMap_nonemptyQuery() {
		MessageProcessor listFilesFlow = lookupMessageProcessorConstruct("list-files-using-query-map-non-empty-query");
		MuleEvent response = null;
		try {
			response = listFilesFlow.process(getTestEvent(testObjects));
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}

		assertNotNull(response.getMessage());
		assertNotNull(response.getMessage().getPayload());
		assertTrue(response.getMessage().getPayload() instanceof Iterable);

		Iterable<DBObject> iterable = (Iterable<DBObject>) response
				.getMessage().getPayload();

		assertEquals(
				"Listing files with a query with key " + testObjects.get("key")
						+ " and value " + testObjects.get("value")
						+ " should give 2 results", 2, MongoHelper.getIterableSize(iterable));

	}
}
