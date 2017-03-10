/*
 * iteraplan is an IT Governance web application developed by iteratec, GmbH
 * Copyright (C) 2004 - 2014 iteratec, GmbH
 *
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License version 3 as published by
 * the Free Software Foundation with the addition of the following permission
 * added to Section 15 as permitted in Section 7(a): FOR ANY PART OF THE COVERED
 * WORK IN WHICH THE COPYRIGHT IS OWNED BY ITERATEC, ITERATEC DISCLAIMS THE
 * WARRANTY OF NON INFRINGEMENT  OF THIRD PARTY RIGHTS.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program; if not, see http://www.gnu.org/licenses or write to
 * the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA 02110-1301 USA.
 *
 * You can contact iteratec GmbH headquarters at Inselkammerstr. 4
 * 82008 Munich - Unterhaching, Germany, or at email address info@iteratec.de.
 *
 * The interactive user interfaces in modified source and object code versions
 * of this program must display Appropriate Legal Notices, as required under
 * Section 5 of the GNU Affero General Public License version 3.
 *
 * In accordance with Section 7(b) of the GNU Affero General Public License
 * version 3, these Appropriate Legal Notices must retain the display of the
 * "iteraplan" logo. If the display of the logo is not reasonably
 * feasible for technical reasons, the Appropriate Legal Notices must display
 * the words "Powered by iteraplan".
 */
package de.iteratec.iteraplan.businesslogic.exchange.common.vbb.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Map;

import de.iteratec.iteraplan.businesslogic.exchange.common.vbb.BaseVBB;
import de.iteratec.iteraplan.businesslogic.exchange.common.vbb.ViewpointConfiguration;
import de.iteratec.iteraplan.businesslogic.exchange.common.vbb.impl.util.AbstractBaseVBB;
import de.iteratec.iteraplan.businesslogic.exchange.common.vbb.impl.util.CreateLabeledPlanarSymbol;
import de.iteratec.iteraplan.businesslogic.exchange.common.vbb.impl.util.CreatePlanarSymbol;
import de.iteratec.iteraplan.businesslogic.exchange.common.vbb.impl.util.CreateTimelineSymbol;
import de.iteratec.iteraplan.businesslogic.exchange.common.vbb.impl.util.CreateVisualizationObject;
import de.iteratec.iteraplan.businesslogic.exchange.common.vbb.impl.util.RuntimePeriodCalculator;
import de.iteratec.iteraplan.businesslogic.exchange.common.vbb.impl.util.VisualVariableHelper.VisualVariable;
import de.iteratec.iteraplan.elasticeam.metamodel.EditableMetamodel;
import de.iteratec.iteraplan.elasticeam.metamodel.Metamodel;
import de.iteratec.iteraplan.elasticeam.metamodel.PropertyExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.SubstantialTypeExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.builtin.BuiltinPrimitiveType;
import de.iteratec.iteraplan.elasticeam.model.InstanceExpression;
import de.iteratec.iteraplan.elasticeam.model.Model;
import de.iteratec.iteraplan.model.RuntimePeriod;
import de.iteratec.visualizationmodel.ALabeledVisualizationObject;
import de.iteratec.visualizationmodel.APlanarSymbol;
import de.iteratec.visualizationmodel.ASymbol;
import de.iteratec.visualizationmodel.BaseMapSymbol;
import de.iteratec.visualizationmodel.Color;
import de.iteratec.visualizationmodel.CompositeSymbol;
import de.iteratec.visualizationmodel.Point;
import de.iteratec.visualizationmodel.Polygon;
import de.iteratec.visualizationmodel.Rectangle;


public class Timeline extends AbstractBaseVBB implements BaseVBB {

  // abstract viewmodel object names
  public static final String                                     CLASS_RUNTIME_ELEMENT    = "runtimeElement";
  public static final String                                     ATTRIBUTE_RUNTIME_PERIOD = "runtimePeriod";

  private static final float                                     RT_ELEMENT_WIDTH         = 250f;
  private static final float                                     RT_ELEMENT_HEIGHT        = 20f;
  private static final float                                     PERIOD_HEIGHT            = 15f;
  private static final float                                     ARROW_DISTANCE           = 4f;
  private static final float                                     ARROW_LENGTH             = 10f;

  // abstract viewmodel objects
  private SubstantialTypeExpression                              runtimeElement;
  private PropertyExpression<?>                                  runtimeElementPeriod;

  // VBBs
  private CreateLabeledPlanarSymbol<ALabeledVisualizationObject> runtimeElementCreateSymbol;
  private CreateVisualizationObject<CompositeSymbol>             compositeCreateSymbol;
  private CreateVisualizationObject<BaseMapSymbol>               baseMapCreateSymbol;
  private CreateTimelineSymbol                                   timelineCreateSymbol;
  private CreatePlanarSymbol<APlanarSymbol>                      runtimePeriodCreateSymbol;
  private CreatePlanarSymbol<Polygon>                            arrowCreateSymbol;

  // visual variables
  private Date                                                   start;
  private Date                                                   end;

  /**
   * Default constructor.
   */
  public Timeline() {
    // Configure ...CreateSymbols
    this.runtimeElementCreateSymbol = new CreateLabeledPlanarSymbol<ALabeledVisualizationObject>();
    this.runtimeElementCreateSymbol.setVObjectClass(Rectangle.class);
    this.runtimeElementCreateSymbol.setWidth(RT_ELEMENT_WIDTH);
    this.runtimeElementCreateSymbol.setHeight(RT_ELEMENT_HEIGHT);
    this.runtimeElementCreateSymbol.setBorderColor(Color.BLACK);

    this.compositeCreateSymbol = new CreateVisualizationObject<CompositeSymbol>();
    this.compositeCreateSymbol.setVObjectClass(CompositeSymbol.class);

    this.baseMapCreateSymbol = new CreateVisualizationObject<BaseMapSymbol>();
    this.baseMapCreateSymbol.setVObjectClass(BaseMapSymbol.class);

    this.timelineCreateSymbol = new CreateTimelineSymbol();

    this.runtimePeriodCreateSymbol = new CreatePlanarSymbol<APlanarSymbol>();
    this.runtimePeriodCreateSymbol.setVObjectClass(Rectangle.class);
    this.runtimePeriodCreateSymbol.setHeight(PERIOD_HEIGHT);
    this.runtimePeriodCreateSymbol.setFillColor(new Color(169, 33, 142));
    this.runtimePeriodCreateSymbol.setBorderWidth(0f);

    this.arrowCreateSymbol = new CreatePlanarSymbol<Polygon>();
    this.arrowCreateSymbol.setVObjectClass(Polygon.class);
    this.arrowCreateSymbol.setFillColor(this.runtimePeriodCreateSymbol.getFillColor());

    putChild(CLASS_RUNTIME_ELEMENT, this.runtimeElementCreateSymbol);
  }

  /** {@inheritDoc}. */
  public ASymbol transform(Model model, ViewpointConfiguration vpConfig) {
    this.runtimeElementCreateSymbol.setBaseUrl(getBaseUrl());

    SubstantialTypeExpression runtimeElementMapping = (SubstantialTypeExpression) vpConfig.getMappingFor(this.runtimeElement);
    PropertyExpression<?> runtimePeriodMapping = (PropertyExpression<?>) vpConfig.getMappingFor(this.runtimeElementPeriod);

    BaseMapSymbol baseMap = this.baseMapCreateSymbol.transform(null, model, vpConfig);
    baseMap.setTitle(getTitle());
    baseMap.setGeneratedInformation(createGeneratedInformationString());

    CompositeSymbol runtimeContainer = this.compositeCreateSymbol.transform(null, model, vpConfig);
    Collection<InstanceExpression> runtimeElements = model.findAll(runtimeElementMapping);

    // Determine timespan
    RuntimePeriod selectedTimespan = determineSelectedTimespan(runtimeElements, runtimePeriodMapping);

    // Generate timeline
    CompositeSymbol timelineSymbol = this.timelineCreateSymbol.transform(selectedTimespan, model, vpConfig);
    float symbolWidth = timelineSymbol.getWidth();
    timelineSymbol.setXpos(RT_ELEMENT_WIDTH + ARROW_DISTANCE * 2 + ARROW_LENGTH + symbolWidth / 2);
    timelineSymbol.setYpos(timelineSymbol.getHeight() / 2);
    baseMap.getChildren().add(timelineSymbol);

    // Generate runtime symbols
    float timespanDelta = selectedTimespan.getEnd().getTime() - selectedTimespan.getStart().getTime();
    int i = 0;
    for (InstanceExpression rtElement : runtimeElements) {
      // Generate symbols for runtime elements
      APlanarSymbol rtElementSymbol = this.runtimeElementCreateSymbol.transform(rtElement, model, vpConfig);
      rtElementSymbol.setXpos(RT_ELEMENT_WIDTH / 2);
      rtElementSymbol.setYpos(RT_ELEMENT_HEIGHT * i + RT_ELEMENT_HEIGHT / 2);
      runtimeContainer.getChildren().add(rtElementSymbol);

      RuntimePeriod period = (RuntimePeriod) rtElement.getValue(runtimePeriodMapping);
      if (period == null) { // If no period is set, treat it as if the runtime would be all the time.
        period = new RuntimePeriod();
      }

      RuntimePeriod visibleRuntimePeriod = determineVisiblePeriod(period, selectedTimespan);

      if (period.overlapsPeriod(selectedTimespan)) {
        float periodDelta = visibleRuntimePeriod.getEnd().getTime() - visibleRuntimePeriod.getStart().getTime();
        float startDelta = visibleRuntimePeriod.getStart().getTime() - selectedTimespan.getStart().getTime();

        // Calculate width and shift of current runtime period
        float periodWidth = (periodDelta / timespanDelta) * symbolWidth;
        float shift = (startDelta / timespanDelta) * symbolWidth;

        // Generate symbol for runtime period
        APlanarSymbol periodSymbol = this.runtimePeriodCreateSymbol.transform(null, model, vpConfig);
        periodSymbol.setYpos(RT_ELEMENT_HEIGHT * i + RT_ELEMENT_HEIGHT / 2);
        periodSymbol.setXpos(RT_ELEMENT_WIDTH + ARROW_DISTANCE * 2 + ARROW_LENGTH + shift + periodWidth / 2);
        periodSymbol.setWidth(periodWidth);
        runtimeContainer.getChildren().add(periodSymbol);
      }

      // Show an arrow if period starts before displayed period
      if (period.getStart() == null || period.getStart().before(visibleRuntimePeriod.getStart())) {
        // ArrowDecorator?
        Polygon startsBeforeSymbol = this.arrowCreateSymbol.transform(null, model, vpConfig);
        layoutStartArrow(i, startsBeforeSymbol);
        runtimeContainer.getChildren().add(startsBeforeSymbol);
      }

      // Show an arrow if period ends after displayed period
      if (period.getEnd() == null || period.getEnd().after(visibleRuntimePeriod.getEnd())) {
        Polygon endsAfterSymbol = this.arrowCreateSymbol.transform(null, model, vpConfig);
        layoutEndArrow(symbolWidth, i, endsAfterSymbol);
        runtimeContainer.getChildren().add(endsAfterSymbol);
      }

      i++;
    }

    // Move runtime elements under timeline
    runtimeContainer.setYpos(timelineSymbol.getHeight() + runtimeContainer.getHeight() / 2);
    baseMap.getChildren().add(runtimeContainer);

    return baseMap;
  }

  private static void layoutStartArrow(int i, Polygon startsBeforeSymbol) {
    float xBase = RT_ELEMENT_WIDTH + ARROW_DISTANCE;
    float yBase = RT_ELEMENT_HEIGHT * i + RT_ELEMENT_HEIGHT / 2;

    startsBeforeSymbol.setPoints(new ArrayList<Point>(3));
    startsBeforeSymbol.getPoints().add(new Point(xBase + ARROW_LENGTH, yBase - PERIOD_HEIGHT / 2));
    startsBeforeSymbol.getPoints().add(new Point(xBase + ARROW_LENGTH, yBase + PERIOD_HEIGHT / 2));
    startsBeforeSymbol.getPoints().add(new Point(xBase, yBase));
  }

  private static void layoutEndArrow(float symbolWidth, int i, Polygon endsAfterSymbol) {
    float xBase = RT_ELEMENT_WIDTH + ARROW_DISTANCE * 2 + ARROW_LENGTH + symbolWidth + ARROW_DISTANCE;
    float yBase = RT_ELEMENT_HEIGHT * i + RT_ELEMENT_HEIGHT / 2;

    endsAfterSymbol.setPoints(new ArrayList<Point>(3));
    endsAfterSymbol.getPoints().add(new Point(xBase, yBase - PERIOD_HEIGHT / 2));
    endsAfterSymbol.getPoints().add(new Point(xBase, yBase + PERIOD_HEIGHT / 2));
    endsAfterSymbol.getPoints().add(new Point(xBase + ARROW_LENGTH, yBase));
  }

  private static RuntimePeriod determineVisiblePeriod(RuntimePeriod objectRuntimePeriod, RuntimePeriod selectedTimespan) {
    Date periodStart;
    // TODO Check this again!
    if (objectRuntimePeriod.getStart() != null && objectRuntimePeriod.getStart().after(selectedTimespan.getStart())
        && objectRuntimePeriod.getStart().before(selectedTimespan.getEnd())) {
      periodStart = objectRuntimePeriod.getStart();
    }
    else {
      periodStart = selectedTimespan.getStart();
    }

    Date periodEnd;
    if (objectRuntimePeriod.getEnd() != null && objectRuntimePeriod.getEnd().before(selectedTimespan.getEnd())
        && objectRuntimePeriod.getEnd().after(selectedTimespan.getStart())) {
      periodEnd = objectRuntimePeriod.getEnd();
    }
    else {
      periodEnd = selectedTimespan.getEnd();
    }

    return new RuntimePeriod(periodStart, periodEnd);
  }

  private RuntimePeriod determineSelectedTimespan(Collection<InstanceExpression> runtimeElements, PropertyExpression<?> runtimePeriodMapping) {
    RuntimePeriod selectedTimespan;
    if (this.start == null || this.end == null) {
      RuntimePeriodCalculator calc = new RuntimePeriodCalculator();
      for (InstanceExpression rtElement : runtimeElements) {
        RuntimePeriod period = (RuntimePeriod) rtElement.getValue(runtimePeriodMapping);
        calc.addRuntimePeriod(period);
      }

      if (this.start == null && this.end == null) {
        selectedTimespan = calc.getGlobalRuntimePeriod();
      }
      else if (this.start != null) {
        selectedTimespan = new RuntimePeriod(this.start, calc.getGlobalRuntimePeriod().getEnd());
      }
      else {
        selectedTimespan = new RuntimePeriod(calc.getGlobalRuntimePeriod().getStart(), this.end);
      }
    }
    else {
      selectedTimespan = new RuntimePeriod(this.start, this.end);
    }

    return selectedTimespan;
  }

  /** {@inheritDoc} */
  @Override
  protected void computeMyAbstractViewmodel(EditableMetamodel viewmodel, ViewpointConfiguration vpConfig, String prefix) {
    /* <pre>                                                    +---------------------------------+
     * +---------------------------------+      <<optional>>     |           <<optional>>          |
     * |          runtimeElement         |  elementRelationship  |        subRuntimeElement        |
     * +---------------------------------+-----------------------+---------------------------------+
     * | + runtimePeriod : RuntimePeriod |                       | + runtimePeriod : RuntimePeriod |
     * +---------------------------------+                       +---------------------------------+
     * </pre> 
     */
    this.runtimeElement = (SubstantialTypeExpression) viewmodel.findTypeByPersistentName(CLASS_RUNTIME_ELEMENT);
    this.runtimeElementPeriod = viewmodel.createProperty(runtimeElement, ATTRIBUTE_RUNTIME_PERIOD, 1, 1, BuiltinPrimitiveType.DURATION);
  }

  /**
   * @return the minimum date considered in the timeline, i.e. the left end of the time-axis.
   */
  @VisualVariable
  public Date getStart() {
    return start == null ? null : new Date(start.getTime());
  }

  /**
   * Sets the minimum date considered in the timeline.
   * @param start the minimum date considered in the timeline.
   */
  public void setStart(Date start) {
    this.start = new Date(start.getTime());
  }

  /**
   * @return the maximum date considered in the timeline, i.e. the right end of the time-axis.
   */
  @VisualVariable
  public Date getEnd() {
    return end == null ? null : new Date(end.getTime());
  }

  /**
   * Sets the maximum date considered in the timeline.
   * @param end the maximum date considered in the timeline.
   */
  public void setEnd(Date end) {
    this.end = new Date(end.getTime());
  }

  /**{@inheritDoc}**/
  public void applyFilters(Map<String, String> vpConfigMap, ViewpointConfiguration vpConfig, Metamodel metamodel) {
    // Nothing to do here
  }
}
