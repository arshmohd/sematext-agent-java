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
package com.sematext.spm.client.tag;

import org.eclipse.collections.impl.map.mutable.UnifiedMap;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import com.sematext.spm.client.MonitorUtil;
import com.sematext.spm.client.MultipleStatsCollector;
import com.sematext.spm.client.Serializer;
import com.sematext.spm.client.StatValues;
import com.sematext.spm.client.StatValuesHelper;
import com.sematext.spm.client.StatsCollectorBadConfigurationException;
import com.sematext.spm.client.util.StringUtils;

public class MultipleTagsCollector extends MultipleStatsCollector<String> {
  private Set<String> trackedTags;

  private final String appToken;
  private String jvmNameForTags;
  private final File propsFile;

  public MultipleTagsCollector(Serializer serializer, String appToken, String jvmName, String subType,
                               File monitorPropsFile) {
    super(serializer);
    this.propsFile = monitorPropsFile;
    this.appToken = appToken;

    try {
      Properties monitorProperties = new Properties();
      monitorProperties.load(new FileInputStream(monitorPropsFile));
      jvmNameForTags = "true"
          .equalsIgnoreCase(MonitorUtil.stripQuotes(monitorProperties.getProperty("SPM_MONITOR_SEND_JVM_NAME").trim())
                                .trim()) ?
          (subType == null || subType.trim().equals("") ? jvmName : jvmName + "-" + subType) : null;
    } catch (IOException e) {
      // should never happen so just dummy handling
      jvmNameForTags = "default";
    }
  }

  public void refreshTagsDefinitions(Properties monitorProperties) throws StatsCollectorBadConfigurationException {
    Set<String> tags = TagsUtils
        .parseTags(StringUtils.removeQuotes(monitorProperties.getProperty("SPM_MONITOR_TAGS", "").trim()));
    Set<String> suppressTags = TagsUtils
        .parseTags(StringUtils.removeQuotes(monitorProperties.getProperty("SPM_SUPPRESS_TAGS", "").trim()));
    tags.removeAll(suppressTags);
    trackedTags = tags;
  }

  @Override
  protected Collection<String> getSlice(Map<String, Object> outerMetrics) {
    return trackedTags;
  }

  @Override
  protected void appendStats(String tag, StatValues statValues) {
    statValues.setMetrics(new UnifiedMap<String, Object>());
    statValues.getMetrics().put(tag.substring(0, tag.indexOf(":")), tag.substring(tag.indexOf(":") + 1));
    statValues.setTags(new UnifiedMap<String, String>());

    StatValuesHelper.fillHostTags(statValues, propsFile);
    if (jvmNameForTags != null) {
      statValues.getTags().put("jvm", jvmNameForTags);
    }
    statValues.setTimestamp(System.currentTimeMillis());
    statValues.setAppToken(appToken);
    statValues.setMetricNamespace("tag");
  }

  @Override
  public String getName() {
    return this.getClass().getName();
  }

  @Override
  public String getCollectorIdentifier() {
    return this.getClass().getName();
  }
}