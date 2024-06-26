/*
 * Copyright (C) 2017-2019 Dremio Corporation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.dremio.exec.testing;

import com.dremio.exec.proto.CoordinationProtos.NodeEndpoint;
import java.util.concurrent.atomic.AtomicInteger;

/** The base class for all types of injections (currently, pause and exception). */
public abstract class Injection {

  protected final String address; // the address of the node on which to inject
  protected final int
      port; // user port of the node; useful when there are multiple nodes on same machine
  protected final Class<?> siteClass; // the class where the injection should happen
  protected final String
      desc; // description of the injection site; useful for multiple exception injections in a
  // single class
  private final AtomicInteger nSkip; // the number of times to skip the injection; starts >= 0
  private final AtomicInteger
      nFire; // the number of times to do the injection, after any skips; starts > 0

  protected Injection(
      final String address,
      final int port,
      final String siteClass,
      final String desc,
      final int nSkip,
      final int nFire)
      throws InjectionConfigurationException {
    if (desc == null || desc.isEmpty()) {
      throw new InjectionConfigurationException("Injection desc is null or empty.");
    }

    if (nSkip < 0) {
      throw new InjectionConfigurationException("Injection nSkip is not non-negative.");
    }

    if (nFire <= 0) {
      throw new InjectionConfigurationException("Injection nFire is non-positive.");
    }
    try {
      this.siteClass = Class.forName(siteClass);
    } catch (ClassNotFoundException e) {
      throw new InjectionConfigurationException("Injection siteClass not found.", e);
    }

    this.address = address;
    this.port = port;
    this.desc = desc;
    this.nSkip = new AtomicInteger(nSkip);
    this.nFire = new AtomicInteger(nFire);
  }

  /**
   * This function checks if it is the right time for the injection to happen.
   *
   * @return if the injection should be injected now
   */
  protected boolean injectNow() {
    return nSkip.decrementAndGet() < 0 && nFire.decrementAndGet() >= 0;
  }

  public String getDesc() {
    return desc;
  }

  public Class<?> getSiteClass() {
    return siteClass;
  }

  // If the address is null, the injection must happen on every node that reaches the specified
  // site.
  public final boolean isValidForBit(final NodeEndpoint endpoint) {
    return address == null
        || (address.equals(endpoint.getAddress()) && port == endpoint.getUserPort());
  }
}
