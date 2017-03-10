/*
 * Copyright 2011 Christian M. Schweda & iteratec
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
package de.iteratec.iteraplan.businesslogic.exchange.common.vbb;

import de.iteratec.iteraplan.businesslogic.exchange.common.vbb.impl.BinaryMatrix;
import de.iteratec.iteraplan.businesslogic.exchange.common.vbb.impl.Cluster;
import de.iteratec.iteraplan.businesslogic.exchange.common.vbb.impl.RecursiveCluster;
import de.iteratec.iteraplan.businesslogic.exchange.common.vbb.impl.Timeline;
import de.iteratec.iteraplan.common.Logger;


/**
 * Factory for creating the appropriate VBB-instance for a given viewpointConfiguration.
 */
public final class ViewpointFactory {

  private static final Logger LOGGER     = Logger.getIteraplanLogger(ViewpointFactory.class);

  /** Classes implementing VBB **/
  static final Class<?>[]     VIEWPOINTS = { Cluster.class, RecursiveCluster.class, Timeline.class, BinaryMatrix.class };

  private ViewpointFactory() {
    // Do not instantiate
  }

  /**
   * @param visConfig the {@link ViewpointConfiguration} for a viewpoint
   * @return A Viewpoint implementing the VBB or null if no viewpoint for the provided visConfig could be found 
   */
  public static BaseVBB getViewpoint(ViewpointConfiguration visConfig) {
    String viewpointName = visConfig.getViewpointName();

    for (Class<?> viewpoint : VIEWPOINTS) {
      if (viewpointName.equalsIgnoreCase(viewpoint.getSimpleName())) {
        try {
          BaseVBB vbb = (BaseVBB) viewpoint.newInstance();
          visConfig.initVisualVariableClass(vbb);

          return vbb;
        } catch (InstantiationException e) {
          LOGGER.error(e);
        } catch (IllegalAccessException e) {
          LOGGER.error(e);
        }
      }
    }
    return null;
  }
}
