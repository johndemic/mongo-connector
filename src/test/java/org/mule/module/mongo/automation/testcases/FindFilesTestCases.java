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

public class FindFilesTestCases extends MongoTestParent {
	
	@After
	public void tearDown() {
		deleteFilesCreatedByCreateFileFromPayload();
	}

	@Category({ SmokeTests.class, SanityTests.class })
	@Test
	public void testFindFiles() {
		try {
			assertEquals("There should be 0 files found before create-file-from-payload", 0, findFiles());
			
			createFileFromPayload(FILENAME_FOR_TEST);
			createFileFromPayload(FILENAME_FOR_TEST);
			
			assertEquals("There should be 2 files found", 2, findFiles());
			
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}
	
}
