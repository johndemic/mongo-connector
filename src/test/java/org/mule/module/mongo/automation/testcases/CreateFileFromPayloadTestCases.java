package org.mule.module.mongo.automation.testcases;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.After;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import com.mongodb.gridfs.GridFSInputFile;

public class CreateFileFromPayloadTestCases extends MongoTestParent {
	
	@After
	public void tearDown() {
		deleteFilesCreatedByCreateFileFromPayload();
	}

	@Category({ SmokeTests.class, SanityTests.class })
	@Test
	public void testCreateFileFromPayload() {
		try {
			assertEquals("There should be 0 files found before create-file-from-payload", 0, findFiles());
			
			GridFSInputFile res = createFileFromPayload();
			
			assertEquals("The created file should be named " + FILENAME_FOR_TEST, FILENAME_FOR_TEST, res.getFilename());
			assertEquals("There should be 1 files found after create-file-from-payload", 1, findFiles());
			
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}
	
}
