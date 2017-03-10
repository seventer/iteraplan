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

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.springframework.web.util.HtmlUtils;

import com.google.common.collect.Maps;

import de.iteratec.iteraplan.common.Logger;
import de.iteratec.iteraplan.elasticeam.iteraql2.IteraQl2Compiler;
import de.iteratec.iteraplan.elasticeam.iteraql2.compile.CompiledQuery;
import de.iteratec.iteraplan.elasticeam.iteraql2.compile.UniversalTypeCompilationUnit;
import de.iteratec.iteraplan.elasticeam.metamodel.EditableMetamodel;
import de.iteratec.iteraplan.elasticeam.metamodel.FeatureExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.Metamodel;
import de.iteratec.iteraplan.elasticeam.metamodel.PropertyExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.UniversalTypeExpression;


/**
 * Parses the contents of a set of key-value pairs to a viewpoint configuration.
 */
public class ViewpointConfigurationParser {

  private static final Logger LOGGER         = Logger.getIteraplanLogger(ViewpointConfigurationParser.class);

  private static final String EMPTYAVMPREFIX = "";

  private VBB                 vbb;
  private Metamodel           metamodel;
  private List<String>        errors;
  private EditableMetamodel   abstractViewmodel;

  /**
   * Initializes the parser with a given VBB (used to specify the abstract viewmodel) and an information model.
   * @param vbb the VBB, which should be configured.
   * @param metamodel the information model whose elements constitute the configuration
   */
  public ViewpointConfigurationParser(VBB vbb, Metamodel metamodel) {
    this.vbb = vbb;
    this.metamodel = metamodel;
    this.errors = new LinkedList<String>();
  }

  /**
   * Parses the contents of a key-value map to the provided viewpoint configuration.
   * @param config the key-value map.
   * @param vpConfig the viewpoint configuration whose values should be filled.
   */
  public void parse(Map<String, String> config, ViewpointConfiguration vpConfig) {
    Map<String, String> possibleVisualVariables = Maps.newHashMap(config);
    this.abstractViewmodel = vpConfig.createEmptyAVM();
    this.vbb.computeAbstractViewmodel(this.abstractViewmodel, vpConfig, EMPTYAVMPREFIX);

    // Iterate over classes of abstract viewmodel
    for (UniversalTypeExpression vbbVariable : this.abstractViewmodel.getUniversalTypes()) {
      possibleVisualVariables.remove(vbbVariable.getPersistentName());
      String classifierMappingName = HtmlUtils.htmlUnescape(config.get(vbbVariable.getPersistentName()));
      if (classifierMappingName == null || classifierMappingName.isEmpty()) {
        if (!VBBUtil.isOptional(vbbVariable)) {
          errors.add("Missing mapping for classifier '" + vbbVariable.getPersistentName() + "'");
        }
      }
      else {
        LOGGER.info("Parsing {0}.", classifierMappingName);
        CompiledQuery query = IteraQl2Compiler.compile(this.metamodel, classifierMappingName + ";");
        UniversalTypeExpression classifierMapping = (query instanceof UniversalTypeCompilationUnit) ? ((UniversalTypeCompilationUnit) query)
            .getCompilationResult() : null;
        if (VBBUtil.isOptional(vbbVariable) || classifierMapping != null) {
          vpConfig.setMappingFor(vbbVariable, classifierMapping);
          setFeatures(config, vpConfig, possibleVisualVariables, vbbVariable, classifierMapping);
        }
        else {
          errors.add("Did not find classifier '" + classifierMappingName + "' for mapping '" + vbbVariable.getPersistentName() + "'");
        }
      }
    }
    vpConfig.setVisualVariables(possibleVisualVariables);
  }

  private void setFeatures(Map<String, String> config, ViewpointConfiguration vpConfig, Map<String, String> possibleVisualVariables,
                           UniversalTypeExpression vbbVariable, UniversalTypeExpression classifierMapping) {
    // Iterate over StructuralFeatures (=attributes/references) of an abstract viewmodels class.

    List<FeatureExpression<?>> vbbFeatureVariables = new ArrayList<FeatureExpression<?>>();
    vbbFeatureVariables.addAll(vbbVariable.getProperties());
    vbbFeatureVariables.addAll(vbbVariable.getRelationshipEnds());

    for (FeatureExpression<?> vbbFeatureVariable : vbbFeatureVariables) {
      String fqnFeatureVariableName = vbbVariable.getPersistentName() + '.' + vbbFeatureVariable.getPersistentName();
      String featureMappingName = config.get(fqnFeatureVariableName);
      FeatureExpression<?> featureMapping = null;
      if (vbbFeatureVariable instanceof PropertyExpression) {
        featureMapping = classifierMapping.findPropertyByPersistentName(featureMappingName);
      }
      else {
        featureMapping = classifierMapping.findRelationshipEndByPersistentName(featureMappingName);
      }
      possibleVisualVariables.remove(fqnFeatureVariableName);
      if (featureMappingName == null && !VBBUtil.isOptional(vbbFeatureVariable)) {
        errors.add("Missing mapping for feature '" + vbbFeatureVariable.getPersistentName() + "'");
      }
      else if (featureMappingName != null && featureMapping == null && !VBBUtil.isOptional(vbbFeatureVariable)) {
        errors.add("Did not find feature '" + featureMappingName + "' in classifier '" + classifierMapping.getPersistentName() + "' for mapping '"
            + fqnFeatureVariableName + "'");
      }
      else if (featureMappingName != null && !featureMappingName.isEmpty()) { // e.g. isOptional and not set
        vpConfig.setMappingFor(vbbFeatureVariable, featureMapping);
      }
    }
  }

  /**
   * @return true, if parsing has not raised errors.
   */
  public boolean isComplete() {
    return this.errors.isEmpty();
  }

  /**
   * @return the list of errors that arose during parsing.
   */
  public List<String> getErrors() {
    return this.errors;
  }

  /**
   * @return the VBB's corresponding abstract viewmodel
   */
  public Metamodel getAbstractViewmodel() {
    return abstractViewmodel;
  }
}
