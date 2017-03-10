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

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import com.google.common.base.Predicate;
import com.google.common.collect.Sets;

import de.iteratec.iteraplan.businesslogic.exchange.common.vbb.BaseVBB;
import de.iteratec.iteraplan.businesslogic.exchange.common.vbb.VBBUtil;
import de.iteratec.iteraplan.businesslogic.exchange.common.vbb.ViewpointConfiguration;
import de.iteratec.iteraplan.businesslogic.exchange.common.vbb.impl.util.AbstractBaseVBB;
import de.iteratec.iteraplan.businesslogic.exchange.common.vbb.impl.util.CreateLabeledPlanarSymbol;
import de.iteratec.iteraplan.businesslogic.exchange.common.vbb.impl.util.CreateVisualizationObject;
import de.iteratec.iteraplan.businesslogic.exchange.common.vbb.impl.util.VisualVariableHelper.VisualVariable;
import de.iteratec.iteraplan.businesslogic.exchange.common.vbb.legend.ColorLegend;
import de.iteratec.iteraplan.businesslogic.exchange.common.vbb.legend.LegendCreator;
import de.iteratec.iteraplan.businesslogic.exchange.common.vbb.legend.LegendCreator.LegendCreatorConfiguration;
import de.iteratec.iteraplan.common.MessageAccess;
import de.iteratec.iteraplan.common.UserContext;
import de.iteratec.iteraplan.elasticeam.metamodel.EditableMetamodel;
import de.iteratec.iteraplan.elasticeam.metamodel.Metamodel;
import de.iteratec.iteraplan.elasticeam.metamodel.RelationshipEndExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.RelationshipExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.SubstantialTypeExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.UniversalTypeExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.builtin.ComparisonOperators;
import de.iteratec.iteraplan.elasticeam.model.InstanceExpression;
import de.iteratec.iteraplan.elasticeam.model.Model;
import de.iteratec.iteraplan.elasticeam.model.UniversalModelExpression;
import de.iteratec.iteraplan.elasticeam.operator.count.CountProperty;
import de.iteratec.iteraplan.elasticeam.operator.filter.FilteredSubstantialType;
import de.iteratec.iteraplan.elasticeam.operator.filter.FilteredUniversalType;
import de.iteratec.iteraplan.elasticeam.operator.filter.ToFilteredRelationshipEnd;
import de.iteratec.iteraplan.elasticeam.operator.filter.predicate.FilterPredicates;
import de.iteratec.iteraplan.elasticeam.operator.fold.UnfoldRelationshipEnd;
import de.iteratec.iteraplan.elasticeam.operator.objectify.ObjectifyingSubstantialType;
import de.iteratec.visualizationmodel.ALabeledVisualizationObject;
import de.iteratec.visualizationmodel.APlanarSymbol;
import de.iteratec.visualizationmodel.ASymbol;
import de.iteratec.visualizationmodel.BaseMapSymbol;
import de.iteratec.visualizationmodel.Color;
import de.iteratec.visualizationmodel.CompositeSymbol;
import de.iteratec.visualizationmodel.LineStyle;
import de.iteratec.visualizationmodel.Rectangle;
import de.iteratec.visualizationmodel.VerticalAlignment;
import de.iteratec.visualizationmodel.layout.SquareBox;


/**
 * VBB used to create cluster visualizations that recursively work through self-relationships between outer elements / inner elements.
 */
public class RecursiveCluster extends AbstractBaseVBB implements BaseVBB {

  // abstract viewmodel object names
  public static final String                         CLASS_OUTER                   = "outer";
  public static final String                         CLASS_INNER                   = "inner";
  public static final String                         VV_FILL_COLOR                 = "fillColor";
  public static final String                         DEFAULT_OUTER_FILL_COLOR      = "#F2EEF1";
  public static final String                         DEFAULT_INNER_FILL_COLOR      = "#F2CBFE";
  public static final String                         OUTER_FILTER                  = CLASS_OUTER + FILTER_SUFFIX;
  public static final String                         INNER_FILTER                  = CLASS_INNER + FILTER_SUFFIX;

  private static final String                        REFERENCE_OUTER2INNER         = "outer2inner";
  private static final String                        REFERENCE_INNER2OUTER         = VBBUtil.PREFIX4OPTIONAL + "inner2outer";

  public static final String                         REFERENCE_OUTER2OUTER         = VBBUtil.PREFIX4OPTIONAL + "outer2outer";
  private static final String                        REFERENCE_OUTER2OUTER_INVERSE = REFERENCE_OUTER2OUTER + "inverse";

  public static final String                         REFERENCE_INNER2INNER         = VBBUtil.PREFIX4OPTIONAL + "inner2inner";
  private static final String                        REFERENCE_INNER2INNER_INVERSE = REFERENCE_INNER2INNER + "inverse";

  public static final String                         DISPLAY_ALL_INNER             = "showAllInner";

  // abstract viewmodel objects
  private SubstantialTypeExpression                  outer;
  private SubstantialTypeExpression                  inner;
  private RelationshipEndExpression                  outerToInner;
  private RelationshipEndExpression                  outerToOuter;
  private RelationshipEndExpression                  innerToInner;

  private String                                     outerFilter;
  private String                                     innerFilter;

  private MixedColorCodingDecorator                  innerColoredCreateSymbol;
  private MixedColorCodingDecorator                  outerColoredCreateSymbol;

  private CreateVisualizationObject<BaseMapSymbol>   baseMapCreateSymbol;
  private CreateVisualizationObject<CompositeSymbol> containerCreateSymbol;

  private boolean                                    showAllInner                  = false;

  /**
   * Default constructor.
   */
  public RecursiveCluster() {
    CreateLabeledPlanarSymbol<ALabeledVisualizationObject> outerCreateSymbolBase = new CreateLabeledPlanarSymbol<ALabeledVisualizationObject>();
    outerCreateSymbolBase.setVObjectClass(Rectangle.class);
    outerCreateSymbolBase.setVerticalAlignment(VerticalAlignment.TOP);
    outerCreateSymbolBase.setBorderColor(Color.BLACK);
    this.outerColoredCreateSymbol = new MixedColorCodingDecorator(outerCreateSymbolBase);

    CreateLabeledPlanarSymbol<ALabeledVisualizationObject> innerCreateSymbolBase = new CreateLabeledPlanarSymbol<ALabeledVisualizationObject>();
    innerCreateSymbolBase.setVObjectClass(Rectangle.class);
    innerCreateSymbolBase.setVerticalAlignment(VerticalAlignment.TOP);
    innerCreateSymbolBase.setBorderColor(Color.BLACK);
    this.innerColoredCreateSymbol = new MixedColorCodingDecorator(innerCreateSymbolBase);

    putChild(CLASS_OUTER, this.outerColoredCreateSymbol);
    putChild(CLASS_INNER, this.innerColoredCreateSymbol);

    this.baseMapCreateSymbol = new CreateVisualizationObject<BaseMapSymbol>();
    this.baseMapCreateSymbol.setVObjectClass(BaseMapSymbol.class);
    this.containerCreateSymbol = new CreateVisualizationObject<CompositeSymbol>();
    this.containerCreateSymbol.setVObjectClass(CompositeSymbol.class);
  }

  /**{@inheritDoc}**/
  public ASymbol transform(Model model, ViewpointConfiguration vpConfig) {
    outerColoredCreateSymbol.setBaseUrl(getBaseUrl());
    innerColoredCreateSymbol.setBaseUrl(getBaseUrl());

    // Get the root outer (hierarchical) elements from the model
    SubstantialTypeExpression outerMapping = (SubstantialTypeExpression) vpConfig.getMappingFor(outer);
    RelationshipEndExpression outerToOuterMapping = (RelationshipEndExpression) vpConfig.getMappingFor(outerToOuter);
    Set<UniversalModelExpression> fullOuters = getHierarchyRoot(model, outerMapping, outerToOuterMapping);

    // Transform the elements to a hierarchy of symbols
    Collection<ASymbol> symbols = outerSubTransform(model, vpConfig, fullOuters);

    // Create base map
    BaseMapSymbol baseMap = baseMapCreateSymbol.transform(null, model, vpConfig);
    baseMap.setTitle(getTitle());
    baseMap.setGeneratedInformation(createGeneratedInformationString());
    baseMap.getChildren().addAll(symbols);
    SquareBox compositeLayouter = new SquareBox(baseMap.getChildren());
    compositeLayouter.layout();

    addColorLegends(model, vpConfig, baseMap);

    return baseMap;
  }

  //TODO create discrete colour legends
  private void addColorLegends(Model model, ViewpointConfiguration vpConfig, BaseMapSymbol baseMap) {
    ColorLegend outerLegend = outerColoredCreateSymbol.getLegend();
    ColorLegend innerLegend = innerColoredCreateSymbol.getLegend();
    LegendCreator legendCreator = new LegendCreator();
    legendCreator.setConfiguration(new LegendCreatorConfiguration());
    List<ColorLegend> legends = new ArrayList<ColorLegend>();
    boolean hasLegends = false;
    if (hasInnerLegend()) {
      if (vpConfig.getMappingFor(inner) instanceof FilteredUniversalType) {
        innerLegend.getLegendInfo().setTypeName(
            ((FilteredUniversalType) vpConfig.getMappingFor(inner)).getBaseType().getName(UserContext.getCurrentUserContext().getLocale()));
      }
      else {
        innerLegend.getLegendInfo().setTypeName(vpConfig.getMappingFor(inner).getName(UserContext.getCurrentUserContext().getLocale()));
      }

      Collections.sort(innerLegend.getLegendEntries());
      legends.add(innerLegend);
      hasLegends = true;
    }
    if (hasOuterLegend()) {
      if (vpConfig.getMappingFor(outer) instanceof FilteredUniversalType) {
        outerLegend.getLegendInfo().setTypeName(
            ((FilteredUniversalType) vpConfig.getMappingFor(outer)).getBaseType().getName(UserContext.getCurrentUserContext().getLocale()));
      }
      else {
        outerLegend.getLegendInfo().setTypeName(vpConfig.getMappingFor(outer).getName(UserContext.getCurrentUserContext().getLocale()));
      }
      Collections.sort(outerLegend.getLegendEntries());
      legends.add(outerLegend);
      hasLegends = true;
    }
    if (hasLegends) {
      legendCreator.setLegends(legends);
      legendCreator.setDiagramBoundingBox(baseMap.getBoundingBox());
      CompositeSymbol transform = legendCreator.transform(model);
      int nrChildren = baseMap.getChildren().size();
      baseMap.getChildren().add(nrChildren, transform);
    }
  }

  private boolean hasInnerLegend() {
    ColorLegend innerLegend = innerColoredCreateSymbol.getLegend();
    if (innerLegend != null && !innerLegend.getLegendEntries().isEmpty()) {
      return true;
    }
    return false;
  }

  private boolean hasOuterLegend() {
    ColorLegend outerLegend = outerColoredCreateSymbol.getLegend();
    if (outerLegend != null && !outerLegend.getLegendEntries().isEmpty()) {
      return true;
    }
    return false;
  }

  private static Set<UniversalModelExpression> getHierarchyRoot(Model model, SubstantialTypeExpression clazz, RelationshipEndExpression ref) {
    Collection<InstanceExpression> outers = model.findAll(clazz);

    // Find the most outer objects. Removes all objects from the list of objects
    // with type outer that have an incoming outer2outer relation. This leaves only
    // the most outer objects in the list.
    HashSet<UniversalModelExpression> fullOuters = new HashSet<UniversalModelExpression>(outers);

    // As the hierarchy is optional, it might not be set
    if (ref != null) {
      //unfold the relationship to find the hierarchy roots even if some nodes in between paths to the root are filtered out
      UnfoldRelationshipEnd unfolded = new UnfoldRelationshipEnd(ref);
      for (InstanceExpression outerObject : outers) {
        if (!outerObject.getConnecteds(unfolded).isEmpty()) {
          fullOuters.removeAll(outerObject.getConnecteds(unfolded));
        }
      }
    }

    return fullOuters;
  }

  // TODO Merge outerSubTransform and innerSubTransform

  /**
   * Creates a collection of ASymbols for outer elements.
   * 
   * This method creates a Collection of ASymbols for all outer elements and an additional outer element
   * (orphan element) with all inner Elements they are not set in relationship with any outer elements.
   * @param model
   * @param vpConfig
   * @param outers
   * @return Collection of ASymbols for all outer elements and a additional orphan element.
   */
  private Collection<ASymbol> outerSubTransform(Model model, ViewpointConfiguration vpConfig, Collection<UniversalModelExpression> outers) {
    Collection<InstanceExpression> allOutersFiltered = model.findAll((SubstantialTypeExpression) vpConfig.getMappingFor(outer));
    Collection<InstanceExpression> allInnersFiltered = model.findAll((SubstantialTypeExpression) vpConfig.getMappingFor(inner));

    Collection<ASymbol> containerSymbols = outerSubTransformWithReleationship(model, vpConfig, outers, allOutersFiltered, allInnersFiltered);

    if (showAllInner) {
      CompositeSymbol container = innerSubTransformWithNoReleationship(model, vpConfig);
      if (container != null) {
        containerSymbols.add(container);
      }
    }

    return containerSymbols;
  }

  /**
   * Creates a collection of ASymbols for selected outer elements.
   * 
   * @param model
   * @param vpConfig
   * @param outers
   * @return a collection of ASymbols for selected outer elements
   */
  private Collection<ASymbol> outerSubTransformWithReleationship(Model model, ViewpointConfiguration vpConfig,
                                                                 Collection<UniversalModelExpression> outers,
                                                                 Collection<InstanceExpression> allOutersFiltered,
                                                                 Collection<InstanceExpression> allInnersFiltered) {

    RelationshipEndExpression outerToInnerMapping = (RelationshipEndExpression) vpConfig.getMappingFor(outerToInner);
    RelationshipEndExpression outerToOuterMapping = (RelationshipEndExpression) vpConfig.getMappingFor(outerToOuter);

    Collection<ASymbol> containerSymbols = new LinkedList<ASymbol>();

    for (UniversalModelExpression outerObject : outers) {
      CompositeSymbol container = containerCreateSymbol.transform(outerObject, model, vpConfig);
      APlanarSymbol containerSymbol = outerColoredCreateSymbol.transform(outerObject, model, vpConfig);

      // As it is optional, it might not be set
      if (outerToOuterMapping != null) {
        Collection<UniversalModelExpression> inOuters = outerObject.getConnecteds(outerToOuterMapping);
        Collection<ASymbol> subOuters = outerSubTransformWithReleationship(model, vpConfig, inOuters, allOutersFiltered, allInnersFiltered);
        if (allOutersFiltered.contains(outerObject)) {
          container.getChildren().addAll(subOuters);
        }
        else if (!subOuters.isEmpty()) {
          containerSymbols.addAll(subOuters);
        }
      }

      Collection<UniversalModelExpression> inners = outerObject.getConnecteds(outerToInnerMapping);
      Collection<ASymbol> innerSymbols = innerSubTransform(model, vpConfig, inners, allInnersFiltered);
      container.getChildren().addAll(innerSymbols);

      SquareBox layouter = new SquareBox(containerSymbol, container.getChildren());
      layouter.layout();

      container.getChildren().add(0, containerSymbol);
      if (allOutersFiltered.contains(outerObject)) {
        containerSymbols.add(container);
      }
    }

    return containerSymbols;
  }

  private Collection<ASymbol> innerSubTransform(Model model, ViewpointConfiguration vpConfig, Collection<UniversalModelExpression> inners,
                                                Collection<InstanceExpression> allInnersFiltered) {
    RelationshipEndExpression innerToInnerMapping = (RelationshipEndExpression) vpConfig.getMappingFor(innerToInner);

    Collection<ASymbol> containerSymbols = new LinkedList<ASymbol>();

    for (UniversalModelExpression innerObject : inners) {
      CompositeSymbol container = containerCreateSymbol.transform(innerObject, model, vpConfig);
      APlanarSymbol containerSymbol = innerColoredCreateSymbol.transform(innerObject, model, vpConfig);

      // As its optional, it might not be set
      if (innerToInnerMapping != null) {
        Collection<UniversalModelExpression> inInners = innerObject.getConnecteds(innerToInnerMapping);
        Collection<ASymbol> subInners = innerSubTransform(model, vpConfig, inInners, allInnersFiltered);
        if (allInnersFiltered.contains(innerObject)) {
          container.getChildren().addAll(subInners);
        }
        else if (!inInners.isEmpty()) {
          containerSymbols.addAll(subInners);
        }
      }

      SquareBox layouter = new SquareBox(containerSymbol, container.getChildren());
      layouter.layout();

      container.getChildren().add(0, containerSymbol);
      if (allInnersFiltered.contains(innerObject)) {
        containerSymbols.add(container);
      }
    }

    return containerSymbols;
  }

  /**
   * Inner Elements that are not set in relationship with any outer elements are grouped in a dedicated outer element.
   * @param model
   * @param vpConfig
   * @param usedInners
   * @return If inner elements with no relationship to any outer element exist a CompositeSymbol as dedicated outer element is returned.
   *         Otherwise null is returned.
   */
  private CompositeSymbol innerSubTransformWithNoReleationship(Model model, ViewpointConfiguration vpConfig) {
    SubstantialTypeExpression oldInner = (SubstantialTypeExpression) vpConfig.getMappingFor(inner);
    RelationshipEndExpression innerToInnerMapping = (RelationshipEndExpression) vpConfig.getMappingFor(innerToInner);

    CompositeSymbol container = null;

    //explicitly filter for orphans
    //take the opposite of the outer2inner relationship and filter for count=0
    RelationshipEndExpression outer2Inner = (RelationshipEndExpression) vpConfig.getMappingFor(outerToInner);
    Predicate<UniversalModelExpression> orphanPredicate = FilterPredicates.buildIntegerPredicate(ComparisonOperators.EQUALS, new CountProperty(
        outer2Inner.getRelationship().getOppositeEndFor(outer2Inner)), BigInteger.ZERO);
    SubstantialTypeExpression innerMapping = new FilteredSubstantialType(oldInner, orphanPredicate);

    // temporarily replace the mapping for the inner type
    vpConfig.setMappingFor(inner, innerMapping);

    Collection<UniversalModelExpression> orphans = getHierarchyRoot(model, innerMapping, innerToInnerMapping);

    if (!orphans.isEmpty()) {
      container = containerCreateSymbol.transform(null, model, vpConfig);
      APlanarSymbol containerSymbol = outerColoredCreateSymbol.transform(null, model, vpConfig);
      containerSymbol.setLineStyle(LineStyle.DASHED);
      if (containerSymbol instanceof Rectangle) {
        ((Rectangle) containerSymbol).getText().setText(MessageAccess.getString("graphicalExport.vbbCluster.noMatchingOuterElement"));
      }

      Collection<ASymbol> innerSymbols = innerSubTransform(model, vpConfig, orphans, model.findAll(innerMapping));
      container.getChildren().addAll(innerSymbols);

      SquareBox layouter = new SquareBox(containerSymbol, container.getChildren());
      layouter.layout();

      container.getChildren().add(0, containerSymbol);
    }

    vpConfig.setMappingFor(inner, oldInner);

    return container;
  }

  /**{@inheritDoc}**/
  @Override
  protected void computeMyAbstractViewmodel(EditableMetamodel viewmodel, ViewpointConfiguration vpConfig, String prefix) {
    /*
     * <<optional>>                              <<optional>>
     * outer2outer >                             inner2inner >
     *    +-----+                                  +-----+
     *    |     | *                              * |     |
     *    |   +-+------+   outer2inner >    +------+-+   |
     *    +---+ outer  +--------------------+ inner  +---+
     *   0..1 +--------+ 1                * +--------+ 0..1
     */

    // Are created via a createSymbol, each. See putChild() in the constructor.
    //    SubstantialTypeExpression inner = (SubstantialTypeExpression) viewmodel.findUniversalTypeByPersistentName(CLASS_INNER);
    this.inner = (SubstantialTypeExpression) viewmodel.findUniversalTypeByPersistentName(CLASS_INNER);
    this.outer = (SubstantialTypeExpression) viewmodel.findUniversalTypeByPersistentName(CLASS_OUTER);

    // reference outer2inner
    RelationshipExpression outerToInnerRelationship = viewmodel.createRelationship(REFERENCE_OUTER2INNER, outer, REFERENCE_OUTER2INNER, 0,
        RelationshipEndExpression.UNLIMITED, inner, REFERENCE_INNER2OUTER, 0, RelationshipEndExpression.UNLIMITED);
    this.outerToInner = outerToInnerRelationship.findRelationshipEndByPersistentName(REFERENCE_OUTER2INNER);

    // optional reference outer2outer
    RelationshipExpression outerToOuterRelationship = viewmodel.createRelationship(REFERENCE_OUTER2OUTER, outer, REFERENCE_OUTER2OUTER, 0,
        RelationshipEndExpression.UNLIMITED, outer, REFERENCE_OUTER2OUTER_INVERSE, 0, RelationshipEndExpression.UNLIMITED);
    this.outerToOuter = outerToOuterRelationship.findRelationshipEndByPersistentName(REFERENCE_OUTER2OUTER);

    // optional reference inner2inner
    RelationshipExpression innerToInnerRelationship = viewmodel.createRelationship(REFERENCE_INNER2INNER, inner, REFERENCE_INNER2INNER, 0,
        RelationshipEndExpression.UNLIMITED, inner, REFERENCE_INNER2INNER_INVERSE, 0, RelationshipEndExpression.UNLIMITED);
    this.innerToInner = innerToInnerRelationship.findRelationshipEndByPersistentName(REFERENCE_INNER2INNER);
  }

  @VisualVariable
  public String getOuterFilter() {
    return outerFilter;
  }

  public void setOuterFilter(String outerFilter) {
    this.outerFilter = outerFilter;
  }

  @VisualVariable
  public String getInnerFilter() {
    return innerFilter;
  }

  public void setInnerFilter(String innerFilter) {
    this.innerFilter = innerFilter;
  }

  @VisualVariable
  public boolean isShowAllInner() {
    return showAllInner;
  }

  public void setShowAllInner(boolean showAllInner) {
    this.showAllInner = showAllInner;
  }

  /**{@inheritDoc}**/
  public void applyFilters(Map<String, String> vpConfigMap, ViewpointConfiguration vpConfig, Metamodel metamodel) {
    String outerFilterStr = vpConfigMap.get(OUTER_FILTER);
    String innerFilterStr = vpConfigMap.get(INNER_FILTER);
    String outerStr = vpConfigMap.get(CLASS_OUTER);

    UniversalTypeExpression compiledOuter = null;
    String objectifySubString = ".objectify(";
    if (outerStr.contains(objectifySubString)) {
      Set<BigInteger> ids = createIdSet(innerFilterStr);
      String objectifyingPropPersistentName = outerStr.substring(outerStr.indexOf(objectifySubString) + 1 + objectifySubString.length(),
          outerStr.length() - 1);
      SubstantialTypeExpression innerType = (SubstantialTypeExpression) vpConfig.getMappingFor(inner);
      FilteredSubstantialType filteredInner = new FilteredSubstantialType(innerType, createFilterPredicate(ids));
      compiledOuter = new ObjectifyingSubstantialType(filteredInner.findPropertyByPersistentName(objectifyingPropPersistentName));

      vpConfig.setMappingFor(outerToInner, compiledOuter.findRelationshipEndByPersistentName("isValueOf"));
      vpConfig.setMappingFor(inner, compiledOuter.findRelationshipEndByPersistentName("isValueOf").getType());
    }
    else {
      Set<BigInteger> outerIds = createIdSet(outerFilterStr);
      SubstantialTypeExpression outerType = (SubstantialTypeExpression) vpConfig.getMappingFor(outer);
      compiledOuter = new FilteredSubstantialType(outerType, createFilterPredicate(outerIds));

      Set<BigInteger> innerIds = createIdSet(innerFilterStr);
      String innerStr = vpConfigMap.get(CLASS_INNER);
      if (!innerStr.contains(objectifySubString)) {
        RelationshipEndExpression outerToInnerRelation = (RelationshipEndExpression) vpConfig.getMappingFor(outerToInner);
        RelationshipEndExpression compiledToInner = new ToFilteredRelationshipEnd(outerToInnerRelation, createFilterPredicate(innerIds));

        vpConfig.setMappingFor(outerToInner, compiledToInner);
        vpConfig.setMappingFor(inner, compiledToInner.getType());
      }
    }
    vpConfig.setMappingFor(outer, compiledOuter);
  }

  /**
   * Creates a {@link Predicate} which filters out all instances whose ID is not contained in the {@code ids} collection.
   */
  private static Predicate<UniversalModelExpression> createFilterPredicate(final Collection<BigInteger> ids) {
    return new Predicate<UniversalModelExpression>() {
      public boolean apply(UniversalModelExpression input) {
        return ids.contains(input.getValue(UniversalTypeExpression.ID_PROPERTY));
      }
    };
  }

  private Set<BigInteger> createIdSet(String filter) {
    Set<BigInteger> result;

    if (StringUtils.isEmpty(filter)) {
      result = Sets.newHashSet(BigInteger.valueOf(-1L), BigInteger.ONE);
    }
    else {
      result = Sets.newHashSet();
      String[] idStrings = filter.split(",");
      for (String idString : idStrings) {
        result.add(new BigInteger(idString));
      }
    }

    return result;
  }

}
