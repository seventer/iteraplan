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
package de.iteratec.iteraplan.businesslogic.exchange.common.vbb.impl;

import java.util.Collection;
import java.util.LinkedList;
import java.util.Map;

import de.iteratec.iteraplan.businesslogic.exchange.common.vbb.BaseVBB;
import de.iteratec.iteraplan.businesslogic.exchange.common.vbb.VBBUtil;
import de.iteratec.iteraplan.businesslogic.exchange.common.vbb.ViewpointConfiguration;
import de.iteratec.iteraplan.businesslogic.exchange.common.vbb.impl.util.AbstractBaseVBB;
import de.iteratec.iteraplan.businesslogic.exchange.common.vbb.impl.util.CreateLabeledPlanarSymbol;
import de.iteratec.iteraplan.businesslogic.exchange.common.vbb.impl.util.CreateVisualizationObject;
import de.iteratec.iteraplan.elasticeam.metamodel.EditableMetamodel;
import de.iteratec.iteraplan.elasticeam.metamodel.Metamodel;
import de.iteratec.iteraplan.elasticeam.metamodel.RelationshipEndExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.RelationshipExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.SubstantialTypeExpression;
import de.iteratec.iteraplan.elasticeam.model.InstanceExpression;
import de.iteratec.iteraplan.elasticeam.model.Model;
import de.iteratec.iteraplan.elasticeam.model.UniversalModelExpression;
import de.iteratec.visualizationmodel.ALabeledVisualizationObject;
import de.iteratec.visualizationmodel.APlanarSymbol;
import de.iteratec.visualizationmodel.ASymbol;
import de.iteratec.visualizationmodel.BaseMapSymbol;
import de.iteratec.visualizationmodel.Color;
import de.iteratec.visualizationmodel.CompositeSymbol;
import de.iteratec.visualizationmodel.Containment;
import de.iteratec.visualizationmodel.Rectangle;
import de.iteratec.visualizationmodel.VerticalAlignment;
import de.iteratec.visualizationmodel.layout.SquareBox;


/**
 * VBB for creating clustered visualizations.
 */
public class Cluster extends AbstractBaseVBB implements BaseVBB {

  // abstract viewmodel object names
  public static final String                                           CLASS_OUTER           = "outer";
  public static final String                                           CLASS_INNER           = "inner";
  public static final String                                           REFERENCE_OUTER2INNER = "outer2inner";
  public static final String                                           REFERENCE_INNER2OUTER = VBBUtil.PREFIX4OPTIONAL + "inner2outer";

  // abstract viewmodel objects
  private SubstantialTypeExpression                                    outer;
  private RelationshipEndExpression                                    outerToInner;

  // VBBs
  private final CreateLabeledPlanarSymbol<ALabeledVisualizationObject> innerCreateSymbol;
  private final CreateLabeledPlanarSymbol<ALabeledVisualizationObject> outerCreateSymbol;
  private final CreateVisualizationObject<BaseMapSymbol>               baseMapCreateSymbol;
  private final CreateVisualizationObject<CompositeSymbol>             outerContainerCreateSymbol;

  /**
   * Default constructor initializing the VBB and its children-VBBs.
   */
  public Cluster() {
    this.outerCreateSymbol = new CreateLabeledPlanarSymbol<ALabeledVisualizationObject>();
    this.outerCreateSymbol.setVObjectClass(Rectangle.class);
    this.outerCreateSymbol.setVerticalAlignment(VerticalAlignment.TOP);
    this.outerCreateSymbol.setBorderColor(Color.BLACK);

    this.innerCreateSymbol = new CreateLabeledPlanarSymbol<ALabeledVisualizationObject>();
    this.innerCreateSymbol.setVObjectClass(Rectangle.class);
    this.innerCreateSymbol.setBorderColor(Color.BLACK);

    putChild(CLASS_OUTER, this.outerCreateSymbol);
    putChild(CLASS_INNER, this.innerCreateSymbol);

    this.baseMapCreateSymbol = new CreateVisualizationObject<BaseMapSymbol>();
    this.baseMapCreateSymbol.setVObjectClass(BaseMapSymbol.class);
    this.outerContainerCreateSymbol = new CreateVisualizationObject<CompositeSymbol>();
    this.outerContainerCreateSymbol.setVObjectClass(CompositeSymbol.class);
  }

  /** {@inheritDoc} **/
  public ASymbol transform(Model model, ViewpointConfiguration vpConfig) {
    this.outerCreateSymbol.setBaseUrl(getBaseUrl());
    this.innerCreateSymbol.setBaseUrl(getBaseUrl());

    SubstantialTypeExpression outerMapping = (SubstantialTypeExpression) vpConfig.getMappingFor(outer);

    RelationshipEndExpression refMapping = (RelationshipEndExpression) vpConfig.getMappingFor(outerToInner);

    Collection<InstanceExpression> outers = model.findAll(outerMapping);

    BaseMapSymbol baseMap = baseMapCreateSymbol.transform(null, model, vpConfig);
    baseMap.setTitle(getTitle());
    baseMap.setGeneratedInformation(createGeneratedInformationString());

    Collection<Containment> containments = new LinkedList<Containment>();
    Collection<APlanarSymbol> outerSymbols = new LinkedList<APlanarSymbol>();

    for (InstanceExpression outerObject : outers) {
      CompositeSymbol outerContainer = outerContainerCreateSymbol.transform(outerObject, model, vpConfig);
      APlanarSymbol outerSymbol = outerCreateSymbol.transform(outerObject, model, vpConfig);

      Collection<UniversalModelExpression> inners = outerObject.getConnecteds(refMapping);

      for (UniversalModelExpression innerObject : inners) {
        APlanarSymbol innerSymbol = innerCreateSymbol.transform(innerObject, model, vpConfig);
        outerContainer.getChildren().add(innerSymbol);

        Containment containment = new Containment();
        containment.setInner(innerSymbol);
        containment.setOuter(outerSymbol);
        containments.add(containment);
      }

      SquareBox outerLayouter = new SquareBox(outerSymbol, outerContainer.getChildren());
      outerLayouter.layout();

      outerSymbols.add(outerSymbol);
      outerContainer.getChildren().add(0, outerSymbol);
      baseMap.getChildren().add(outerContainer);
    }

    SquareBox baseMapLayouter = new SquareBox(baseMap.getChildren());
    baseMapLayouter.layout();

    for (Containment containment : containments) {
      containment.getPotentialOuters().addAll(outerSymbols);
      containment.getPotentialOuters().remove(containment.getOuter());
    }

    return baseMap;
  }

  /**{@inheritDoc}**/
  @Override
  protected void computeMyAbstractViewmodel(EditableMetamodel viewmodel, ViewpointConfiguration vpConfig, String prefix) {
    /*
     * +--------+   outer2inner >    +--------+
     * + outer  +--------------------+ inner  |
     * +--------+ 1..*             * +--------+
     */
    SubstantialTypeExpression inner = (SubstantialTypeExpression) viewmodel.findUniversalTypeByPersistentName(CLASS_INNER);
    this.outer = (SubstantialTypeExpression) viewmodel.findUniversalTypeByPersistentName(CLASS_OUTER);

    RelationshipExpression relationship = viewmodel.createRelationship(REFERENCE_OUTER2INNER, this.outer, REFERENCE_OUTER2INNER, 0,
        RelationshipEndExpression.UNLIMITED, inner, REFERENCE_INNER2OUTER, 1, RelationshipEndExpression.UNLIMITED);
    this.outerToInner = relationship.findRelationshipEndByPersistentName(REFERENCE_OUTER2INNER);
  }

  /**{@inheritDoc}**/
  public void applyFilters(Map<String, String> vpConfigMap, ViewpointConfiguration vpConfig, Metamodel metamodel) {
    // Nothing to do here
  }
}
