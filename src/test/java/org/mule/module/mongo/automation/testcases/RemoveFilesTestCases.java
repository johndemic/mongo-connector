package org.mule.module.mongo.automation.testcases;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.File;
import java.util.HashMap;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.TemporaryFolder;
import org.mule.api.MuleEvent;
import org.mule.api.processor.MessageProcessor;

import com.mongodb.gridfs.GridFSInputFile;

public class RemoveFilesTestCases extends MongoTestParent {
	
	@Rule  
	public TemporaryFolder folder = new TemporaryFolder();  

	@SuppressWarnings("unchecked")
	@Before
	public void setUp() {
		try {
			// Create collection
			testObjects = (HashMap<String, Object>) context.getBean("createCollection");
			lookupFlowConstruct("create-collection").process(getTestEvent(testObjects));
		} catch (Exception ex) {
			ex.printStackTrace();
			fail();
		}
	}

	@SuppressWarnings("unchecked")
	@After
	public void tearDown() {
		try {
			// Delete collections
			testObjects = (HashMap<String, Object>) context.getBean("createCollection");
			lookupFlowConstruct("drop-collection").process(getTestEvent(testObjects));
			testObjects.put("collectionName", "fs.chunks");
			lookupFlowConstruct("drop-collection").process(getTestEvent(testObjects));
			testObjects.put("collectionName", "fs.files");
			lookupFlowConstruct("drop-collection").process(getTestEvent(testObjects));
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}

	@Category({ SmokeTests.class, SanityTests.class })
	@Test
	public void testRemoveFiles() {
		
		MuleEvent response = null;
		try {
			String filename = "fileForTest";
			File file = folder.newFile(filename);
			MessageProcessor createFileFromPayloadFlow = lookupFlowConstruct("create-file-from-payload");
			testObjects.put("filename", filename);
			MuleEvent event = getTestEvent(file);
			event.setSessionVariable("filename", filename);
			response = createFileFromPayloadFlow.process(event);
			GridFSInputFile res = (GridFSInputFile) response.getMessage().getPayload();
			assertEquals(filename, res.getFilename());
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}
	
}
