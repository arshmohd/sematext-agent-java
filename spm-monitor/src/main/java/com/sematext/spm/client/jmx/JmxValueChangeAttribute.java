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
package com.sematext.spm.client.jmx;

import com.sematext.spm.client.attributes.MetricType;
import com.sematext.spm.client.attributes.MetricValueHolder;
import com.sematext.spm.client.attributes.ValueChangeHolder;

/**
 * Tracks whether value of some attribute has changed since the last measurement.
 */
public class JmxValueChangeAttribute extends MBeanAttributeObservation {
  public JmxValueChangeAttribute() {
  }

  public JmxValueChangeAttribute(MBeanAttributeObservation original, String newAttributeName) {
    super(original, newAttributeName);
  }

  @Override
  public MetricValueHolder createHolder() {
    return new ValueChangeHolder();
  }

  @Override
  public MetricType getMetricType() {
    return MetricType.OTHER;
  }

  @Override
  public MBeanAttributeObservation getCopy(String newAttributeName) {
    return new JmxValueChangeAttribute(this, newAttributeName);
  }
}
