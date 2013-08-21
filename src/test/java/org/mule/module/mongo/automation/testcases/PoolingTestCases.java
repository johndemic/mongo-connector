package org.mule.module.mongo.automation.testcases;

import com.mongodb.BasicDBObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mule.api.MuleEvent;
import org.mule.api.MuleMessage;
import org.mule.api.processor.MessageProcessor;
import org.mule.api.transport.Connector;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.*;

public class PoolingTestCases extends MongoTestParent {

    @SuppressWarnings("unchecked")
    @Before
    public void setUp() {
        try {
            // Create collection
            testObjects = (HashMap<String, Object>) context.getBean("countObjects");
            lookupFlowConstruct("create-collection").process(getTestEvent(testObjects));
        } catch (Exception ex) {
            ex.printStackTrace();
            fail();
        }
    }

    @After
    public void tearDown() {
        try {
            // Delete collection
            lookupFlowConstruct("drop-collection").process(getTestEvent(testObjects));
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    @Category({ RegressionTests.class })
    @Test
    public void testPoolSizeDoesNotExceedConfiguration() throws Exception {

        int numObjects = (Integer) testObjects.get("numObjects");

        insertObjects(getEmptyDBObjects(numObjects));

        int startingConnections = lookupFlowConstruct("count-open-connections").process(getTestEvent("")).getMessage().getPayload(Integer.class);

        for (int i = 0; i < 32; i++) {
            try {
                MessageProcessor countFlow = lookupFlowConstruct("count-objects");
                testObjects.put("queryRef", new BasicDBObject());
                countFlow.process(getTestEvent(testObjects));
            } catch (Exception e) {
                e.printStackTrace();
                fail();
            }
        }


        Thread.sleep(2000);
        int newConnections = lookupFlowConstruct("count-open-connections").process(getTestEvent("")).getMessage().getPayload(Integer.class) - startingConnections;
        assertTrue("Too many new connections (" + newConnections + ", ", newConnections <= 2);
        Thread.sleep(20000);
    }}
