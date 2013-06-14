package org.mule.module.mongo.automation.testcases;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.After;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mule.api.MuleEvent;
import org.mule.api.processor.MessageProcessor;

import com.mongodb.BasicDBObject;

public class FindFilesUsingQueryMapTestCases extends MongoTestParent {
	
	@After
	public void tearDown() {
		deleteFilesCreatedByCreateFileFromPayload();
	}

	@Category({ SmokeTests.class, SanityTests.class })
	@Test
	public void testFindFilesUsingQueryMap() {
		try {
			assertEquals("There should be 0 files found before create-file-from-payload", 0, findFiles());
			
			createFileFromPayload();
		
			// create another file with query map
			
			MuleEvent response = null;
			try {
				MessageProcessor findFilesFlow = lookupFlowConstruct("find-files");
				MuleEvent event = getTestEvent(new BasicDBObject());
				response = findFilesFlow.process(event);
			} catch (Exception e) {
				e.printStackTrace();
				fail();
			}

			
			assertEquals("There should be 2 files found", 2, findFiles());
			
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}
	
}
