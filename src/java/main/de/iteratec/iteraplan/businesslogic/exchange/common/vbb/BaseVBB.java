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

import java.util.Map;

import de.iteratec.iteraplan.elasticeam.metamodel.Metamodel;
import de.iteratec.iteraplan.elasticeam.model.Model;
import de.iteratec.visualizationmodel.ASymbol;


/**
 * Interface for VBBs that can act as base-VBB of a viewpoint.
 */
public interface BaseVBB extends VBB {
  String FILTER_SUFFIX = "Filter";

  /**
   * Transforms a resource given the specified configuration.
   * @param model The resource to be transformed.
   * @param vpConfig The configuration of the VBB and its children-VBBs.
   * @return The base-map symbol resulting from applying the VBB's transformation.
   */
  ASymbol transform(Model model, ViewpointConfiguration vpConfig);

  void applyFilters(Map<String, String> vpConfigMap, ViewpointConfiguration vpConfig, Metamodel metamodel);
}
