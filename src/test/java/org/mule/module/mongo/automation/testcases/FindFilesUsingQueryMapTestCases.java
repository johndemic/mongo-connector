package org.mule.module.mongo.automation.testcases;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mule.api.MuleEvent;
import org.mule.api.processor.MessageProcessor;

import com.mongodb.DBObject;

public class FindFilesUsingQueryMapTestCases extends MongoTestParent {
	
	private static String DIFF_FILENAME_FOR_TEST = "diff_filename_for_test";

	@Before
	public void setUp() {
		try {
			assertEquals("There should be 0 files in total before setting up the test", 0, findFiles());
			createFileFromPayload(FILENAME_FOR_TEST);
			
			// create another file with a different name
			createFileFromPayload(DIFF_FILENAME_FOR_TEST);
			assertEquals("There should be 2 files in total after setting up the test", 2, findFiles());
		} catch (Exception ex) {
			ex.printStackTrace();
			fail();
		}
	}
	
	@After
	public void tearDown() {
		deleteFilesCreatedByCreateFileFromPayload();
	}

	@SuppressWarnings("unchecked")
	@Category({ SmokeTests.class, SanityTests.class })
	@Test
	public void testFindFilesUsingQueryMap() {
		try {
			MuleEvent response = null;
			try {
				MessageProcessor findFilesUsingQueryMapFlow = lookupFlowConstruct("find-files-using-query-map");
				testObjects.put("queryAttribKey", "filename");
				testObjects.put("queryAttribVal", DIFF_FILENAME_FOR_TEST);
				response = findFilesUsingQueryMapFlow.process(getTestEvent(testObjects));
				Iterable<DBObject> iterable = (Iterable<DBObject>) response.getMessage().getPayload();
				int filesFoundUsingQueryMap = iterableSize(iterable);
				
				assertEquals("There should be 1 file with the name " + DIFF_FILENAME_FOR_TEST, 1, filesFoundUsingQueryMap);
				assertEquals("There should be 2 files in total", 2, findFiles());
			} catch (Exception e) {
				e.printStackTrace();
				fail();
			}
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}
	
}
