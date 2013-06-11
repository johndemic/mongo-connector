package org.mule.module.mongo.automation.testcases;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.HashMap;
import java.util.Map;

import javax.validation.constraints.AssertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mule.api.MuleEvent;
import org.mule.api.processor.MessageProcessor;
import org.mule.construct.Flow;

public class ExistsCollectionTestCases extends MongoTestParent {

	@Before
	public void setUp() {
		try {			
			testObjects = (Map<String, Object>) context.getBean("existsCollection");
			MessageProcessor flow = lookupFlowConstruct("create-collection");
			MuleEvent response = flow.process(getTestEvent(testObjects));
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}

	@After
	public void tearDown() {
		try {
			MessageProcessor flow = lookupFlowConstruct("drop-collection");
			MuleEvent response = flow.process(getTestEvent(testObjects));
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}

	@Category({ SmokeTests.class, SanityTests.class })
	@Test
	public void testExistsCollection() {
		try {
			MessageProcessor flow = lookupFlowConstruct("exists-collection");
			MuleEvent response = flow.process(getTestEvent(testObjects));
			
			Object payload = response.getMessage().getPayload();
			assertTrue((Boolean)payload);
			
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}

}
