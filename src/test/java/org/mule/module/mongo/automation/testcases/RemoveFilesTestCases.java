/**
 * Copyright (c) MuleSoft, Inc. All rights reserved. http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.md file.
 */

package org.mule.module.mongo.automation.testcases;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.After;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mule.api.MuleEvent;
import org.mule.api.processor.MessageProcessor;

import com.mongodb.BasicDBObject;

public class RemoveFilesTestCases extends MongoTestParent {

	@After
	public void tearDown() {
		deleteFilesCreatedByCreateFileFromPayload();
	}

	@Category({ SmokeTests.class, SanityTests.class })
	@Test
	public void testRemoveFiles() {

		assertEquals(
				"There should be 0 files found before create-file-from-payload",
				0, findFiles());

		createFileFromPayload(FILENAME_FOR_TEST);

		assertEquals(
				"There should be 1 files found after create-file-from-payload",
				1, findFiles());

		try {
			MessageProcessor removeFilesFlow = lookupFlowConstruct("remove-files");
			testObjects.put("queryRef", new BasicDBObject());
			MuleEvent event = getTestEvent(testObjects);
			removeFilesFlow.process(event);

			assertEquals("There should be 0 files found after remove-files", 0,
					findFiles());
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}

	}

}
