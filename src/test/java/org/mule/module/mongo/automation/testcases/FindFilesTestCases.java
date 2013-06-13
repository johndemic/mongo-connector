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
			
			createFileFromPayload();
			createFileFromPayload();
			
			assertEquals("There should be 2 files found", 2, findFiles());
			
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}
	
}
