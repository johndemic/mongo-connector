/**
 * Copyright (c) MuleSoft, Inc. All rights reserved. http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.md file.
 */

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

        MessageProcessor countFlow = lookupFlowConstruct("count-objects");
        testObjects.put("queryRef", new BasicDBObject());

        for (int i = 0; i < 32; i++) {
            try {
                countFlow.process(getTestEvent(testObjects));
            } catch (Exception e) {
                e.printStackTrace();
                fail();
            }
        }

        int newConnections = lookupFlowConstruct("count-open-connections").process(getTestEvent("")).getMessage().getPayload(Integer.class) - startingConnections;
        assertTrue("Too many new connections (" + newConnections + ", ", newConnections <= 2);
    }}
