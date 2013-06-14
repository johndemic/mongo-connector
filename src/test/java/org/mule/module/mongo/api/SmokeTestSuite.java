/**
 * Copyright (c) MuleSoft, Inc. All rights reserved. http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.md file.
 */

package org.mule.module.mongo.api;

import org.junit.experimental.categories.Categories;
import org.junit.experimental.categories.Categories.IncludeCategory;
import org.junit.runner.RunWith;
import org.junit.runners.Suite.SuiteClasses;
import org.mule.module.mongo.automation.testcases.AddUserTestCases;
import org.mule.module.mongo.automation.testcases.CreateCollectionTestCases;
import org.mule.module.mongo.automation.testcases.CreateIndexTestCases;
import org.mule.module.mongo.automation.testcases.DropIndexTestCases;
import org.mule.module.mongo.automation.testcases.ExistsCollectionTestCases;
import org.mule.module.mongo.automation.testcases.InsertObjectTestCases;
import org.mule.module.mongo.automation.testcases.ListCollectionTestCases;
import org.mule.module.mongo.automation.testcases.RemoveObjectsTestCases;
import org.mule.module.mongo.automation.testcases.SmokeTests;

@RunWith(Categories.class)
@IncludeCategory(SmokeTests.class)
@SuiteClasses({ AddUserTestCases.class, CreateCollectionTestCases.class,
		CreateIndexTestCases.class, DropIndexTestCases.class,
		ExistsCollectionTestCases.class, InsertObjectTestCases.class,
		ListCollectionTestCases.class, RemoveObjectsTestCases.class})
public class SmokeTestSuite {
}
