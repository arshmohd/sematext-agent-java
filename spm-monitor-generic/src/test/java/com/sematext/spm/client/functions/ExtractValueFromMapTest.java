/*
 * Licensed to Sematext Group, Inc
 *
 * See the NOTICE file distributed with
 * this work for additional information regarding copyright
 * ownership. Sematext Group, Inc licenses this file to you under
 * the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package com.sematext.spm.client.functions;

import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

public class ExtractValueFromMapTest {
  @Test
  public void testCalculate() {
    ExtractLongFromMap functionLong = new ExtractLongFromMap();
    ExtractDoubleFromMap functionDouble = new ExtractDoubleFromMap();
    
    Map<String, Object> metrics = new HashMap<String, Object>();
    metrics.put("Value", "{metric1=1, metric2=1.23, metric3=abc}");
    Assert.assertEquals(1l, functionLong.calculateAttribute(metrics, new Object [] {"Value", "metric1", "long"}));
    Assert.assertEquals(1.23d, functionDouble.calculateAttribute(metrics, new Object [] {"Value", "metric2", "double"}));
  }
}
