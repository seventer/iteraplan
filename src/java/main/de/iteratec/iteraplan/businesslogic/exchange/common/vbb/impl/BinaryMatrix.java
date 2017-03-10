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

import java.util.LinkedList;
import java.util.List;
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
import de.iteratec.visualizationmodel.Rectangle;
import de.iteratec.visualizationmodel.Text;


/**
 * VBB creating a binary matrix, i.e. a diagram showing relationships between elements designated in columns and rows.
 */
public class BinaryMatrix extends AbstractBaseVBB implements BaseVBB {

  // abstract viewmodel object names
  public static final String                                           CLASS_XAXIS   = "column";
  public static final String                                           CLASS_YAXIS   = "row";
  public static final String                                           REFERENCE_X2Y = "column2row";
  public static final String                                           REFERENCE_Y2X = VBBUtil.PREFIX4OPTIONAL + "row2column";

  //abstract viewmodel objects
  private SubstantialTypeExpression                                    xAxis;
  private SubstantialTypeExpression                                    yAxis;
  private RelationshipEndExpression                                    x2y;

  // VBBs
  private final CreateLabeledPlanarSymbol<ALabeledVisualizationObject> xAxisCreateSymbol;
  private final CreateLabeledPlanarSymbol<ALabeledVisualizationObject> yAxisCreateSymbol;
  private final CreateLabeledPlanarSymbol<ALabeledVisualizationObject> x2yCreateSymbol;
  private final CreateVisualizationObject<BaseMapSymbol>               baseMapCreateSymbol;

  private static final float                                           WIDTH         = 80f;
  private static final float                                           HEIGHT        = 30f;

  /**
   * Default constructor.
   */
  public BinaryMatrix() {
    this.xAxisCreateSymbol = new CreateLabeledPlanarSymbol<ALabeledVisualizationObject>();
    this.xAxisCreateSymbol.setVObjectClass(Rectangle.class);
    this.xAxisCreateSymbol.setBorderColor(Color.BLACK);

    this.yAxisCreateSymbol = new CreateLabeledPlanarSymbol<ALabeledVisualizationObject>();
    this.yAxisCreateSymbol.setVObjectClass(Rectangle.class);
    this.yAxisCreateSymbol.setBorderColor(Color.BLACK);

    this.x2yCreateSymbol = new CreateLabeledPlanarSymbol<ALabeledVisualizationObject>();
    this.x2yCreateSymbol.setVObjectClass(Rectangle.class);
    this.x2yCreateSymbol.setFillColor(Color.BLACK);
    this.x2yCreateSymbol.setTextSize(12);

    putChild(CLASS_XAXIS, this.xAxisCreateSymbol);
    putChild(CLASS_YAXIS, this.yAxisCreateSymbol);

    this.baseMapCreateSymbol = new CreateVisualizationObject<BaseMapSymbol>();
    this.baseMapCreateSymbol.setVObjectClass(BaseMapSymbol.class);
  }

  /**{@inheritDoc}**/
  public ASymbol transform(Model model, ViewpointConfiguration vpConfig) {
    this.xAxisCreateSymbol.setBaseUrl(getBaseUrl());
    this.yAxisCreateSymbol.setBaseUrl(getBaseUrl());

    BaseMapSymbol baseMap = baseMapCreateSymbol.transform(null, model, vpConfig);
    baseMap.setTitle(getTitle());
    baseMap.setGeneratedInformation(createGeneratedInformationString());

    SubstantialTypeExpression xAxisMapping = (SubstantialTypeExpression) vpConfig.getMappingFor(xAxis);
    SubstantialTypeExpression yAxisMapping = (SubstantialTypeExpression) vpConfig.getMappingFor(yAxis);
    RelationshipEndExpression x2yMapping = (RelationshipEndExpression) vpConfig.getMappingFor(x2y);
    float x = WIDTH * 1.5f;
    boolean first = true;
    for (InstanceExpression xAxisElement : model.findAll(xAxisMapping)) {
      APlanarSymbol xAxisSymbol = this.xAxisCreateSymbol.transform(xAxisElement, model, vpConfig);
      xAxisSymbol.setXpos(x);
      xAxisSymbol.setYpos(HEIGHT * 0.5f);
      float y = HEIGHT * 1.5f;
      baseMap.getChildren().add(xAxisSymbol);
      for (InstanceExpression yAxisElement : model.findAll(yAxisMapping)) {
        if (first) {
          APlanarSymbol yAxisSymbol = this.yAxisCreateSymbol.transform(yAxisElement, model, vpConfig);
          yAxisSymbol.setXpos(WIDTH * 0.5f);
          yAxisSymbol.setYpos(y);
          baseMap.getChildren().add(yAxisSymbol);
        }
        if (xAxisElement.getConnecteds(x2yMapping).contains(yAxisElement)) {
          // FIXME Hacked together to make it work again
          List<UniversalModelExpression> tmp = new LinkedList<UniversalModelExpression>(xAxisElement.getConnecteds(x2yMapping));
          ALabeledVisualizationObject x2ySymbol = this.x2yCreateSymbol.transform(tmp.get(tmp.indexOf(yAxisElement)), model, vpConfig);
          // FIXME The text isn't shown anymore at the proper place
          x2ySymbol.setText(new Text("X"));
          x2ySymbol.setXpos(x);
          x2ySymbol.setYpos(y);
          baseMap.getChildren().add(x2ySymbol);
        }
        y += HEIGHT;
      }
      first = false;
      x += WIDTH;
    }
    return baseMap;
  }

  /**{@inheritDoc}**/
  @Override
  protected void computeMyAbstractViewmodel(EditableMetamodel viewmodel, ViewpointConfiguration vpConfig, String prefix) {
    this.xAxis = (SubstantialTypeExpression) viewmodel.findTypeByPersistentName(CLASS_XAXIS);
    this.yAxis = (SubstantialTypeExpression) viewmodel.findTypeByPersistentName(CLASS_YAXIS);
    RelationshipExpression x2yRelationship = viewmodel.createRelationship(REFERENCE_X2Y, xAxis, REFERENCE_X2Y, 0,
        RelationshipEndExpression.UNLIMITED, yAxis, REFERENCE_Y2X, 0, RelationshipEndExpression.UNLIMITED);
    this.x2y = x2yRelationship.findRelationshipEndByPersistentName(REFERENCE_X2Y);
  }

  /**{@inheritDoc}**/
  public void applyFilters(Map<String, String> vpConfigMap, ViewpointConfiguration vpConfig, Metamodel metamodel) {
    // Nothing to do here
  }
}
