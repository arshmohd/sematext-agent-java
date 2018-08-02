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
package com.sematext.spm.client;

import java.io.File;

import com.sematext.spm.client.sender.SenderUtil;

public final class StatValuesHelper {
  private static final Log LOG = LogFactory.getLog(StatValuesHelper.class);

  private StatValuesHelper() {
  }

  public static void fillHostTags(StatValues statValues, File propsFile) {
    try {
      statValues.getTags().put("os.host", SenderUtil.calculateHostParameterValue(propsFile));
    } catch (Throwable thr) {
      LOG.warn("Can't resolve os.host value, setting to unknown", thr);
      statValues.getTags().put("os.host", "unknown");
    }

    String dockerHostname = SenderUtil.getDockerHostname();
    if (dockerHostname != null) {
      statValues.getTags().put("container.host.hostname", dockerHostname);
    }
    try {
      String containerHostname = MonitorUtil.getContainerHostname(propsFile);
      if (containerHostname != null) {
        statValues.getTags().put("container.hostname", containerHostname);
      }
    } catch (Throwable thr) {
      LOG.warn("Can't resolve container.hostname value, leaving empty", thr);
    }
  }
}