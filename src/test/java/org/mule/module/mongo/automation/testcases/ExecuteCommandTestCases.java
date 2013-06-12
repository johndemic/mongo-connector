package org.mule.module.mongo.automation.testcases;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.HashMap;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mule.api.MuleEvent;
import org.mule.api.processor.MessageProcessor;

import com.mongodb.CommandResult;
import com.mongodb.WriteResult;

public class ExecuteCommandTestCases extends MongoTestParent {

	@SuppressWarnings("unused")
	@Before
	public void setUp() {
		try {
			// Get the collectionName and create a collection
			// Create user
			testObjects = (HashMap<String, Object>) context.getBean("executeCommand");
			MessageProcessor flow = lookupFlowConstruct("create-collection");
			MuleEvent response = flow.process(getTestEvent(testObjects));	
		}
		catch(Exception e) {
			e.printStackTrace();
			fail();
		}
	}
	
	@SuppressWarnings("unchecked")
	@Category({SmokeTests.class, SanityTests.class})
	@Test
	public void testExecuteCommand() {
		try {
			// Drop the collection using command
			testObjects = (HashMap<String, Object>) context.getBean("executeCommand");
			
			String collectionName = testObjects.get("collectionName").toString();
			
			MessageProcessor flow = lookupFlowConstruct("execute-command");
			MuleEvent response = flow.process(getTestEvent(testObjects));
			CommandResult cmdResult = (CommandResult) response.getMessage().getPayload();
			assertTrue(cmdResult.ok());
			
			flow = lookupFlowConstruct("exists-collection");
			response = flow.process(getTestEvent(testObjects));
			Boolean exists = (Boolean) response.getMessage().getPayload();
			assertFalse(exists);
		}
		catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}
		
}
