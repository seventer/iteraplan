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
package de.iteratec.iteraplan.businesslogic.exchange.common.vbb.impl.util;

import java.util.Calendar;

import de.iteratec.iteraplan.businesslogic.exchange.common.vbb.ViewpointConfiguration;
import de.iteratec.iteraplan.common.UserContext;
import de.iteratec.iteraplan.elasticeam.model.Model;
import de.iteratec.iteraplan.model.RuntimePeriod;
import de.iteratec.visualizationmodel.ALabeledVisualizationObject;
import de.iteratec.visualizationmodel.Color;
import de.iteratec.visualizationmodel.CompositeSymbol;
import de.iteratec.visualizationmodel.Rectangle;
import de.iteratec.visualizationmodel.Text;


/**
 * Inner VBB creating a timeline symbol, i.e. a time axis.
 */
public class CreateTimelineSymbol {

  private static final float                                     UNIT_HEIGHT = 20f;
  private static final float                                     UNIT_WIDTH  = 15f;

  private CreateLabeledPlanarSymbol<ALabeledVisualizationObject> yearCreateSymbol;
  private CreateLabeledPlanarSymbol<ALabeledVisualizationObject> monthCreateSymbol;
  private CreateVisualizationObject<CompositeSymbol>             containerCreateSymbol;

  /**
   * Default constructor.
   */
  public CreateTimelineSymbol() {
    this.yearCreateSymbol = new CreateLabeledPlanarSymbol<ALabeledVisualizationObject>();
    this.yearCreateSymbol.setVObjectClass(Rectangle.class);
    this.yearCreateSymbol.setHeight(UNIT_HEIGHT);
    this.yearCreateSymbol.setFillColor(Color.WHITE);
    this.yearCreateSymbol.setBorderColor(Color.BLACK);

    this.monthCreateSymbol = new CreateLabeledPlanarSymbol<ALabeledVisualizationObject>();
    this.monthCreateSymbol.setVObjectClass(Rectangle.class);
    this.monthCreateSymbol.setHeight(UNIT_HEIGHT);
    this.monthCreateSymbol.setFillColor(Color.WHITE);
    this.monthCreateSymbol.setBorderColor(Color.BLACK);

    this.containerCreateSymbol = new CreateVisualizationObject<CompositeSymbol>();
    this.containerCreateSymbol.setVObjectClass(CompositeSymbol.class);
  }

  public CompositeSymbol transform(RuntimePeriod timespan, Model model, ViewpointConfiguration config) {
    CompositeSymbol container = this.containerCreateSymbol.transform(null, model, config);

    Calendar cal = Calendar.getInstance(UserContext.getCurrentLocale());
    cal.setTime(timespan.getStart());

    Calendar endCal = Calendar.getInstance();
    endCal.setTime(timespan.getEnd());

    int totalMonthCount = 0;
    while (cal.before(endCal)) {
      int year = cal.get(Calendar.YEAR);

      ALabeledVisualizationObject yearSymbol = this.yearCreateSymbol.transform(null, model, config);
      yearSymbol.setText(new Text(Integer.toString(year)));
      yearSymbol.setYpos(UNIT_HEIGHT / 2);
      yearSymbol.setFillColor(Color.WHITE);
      yearSymbol.setBorderColor(Color.BLACK);
      container.getChildren().add(yearSymbol);

      int monthCount = 0;
      for (; cal.get(Calendar.MONTH) <= cal.getActualMaximum(Calendar.MONTH) && cal.get(Calendar.YEAR) == year && cal.before(endCal); cal.add(
          Calendar.MONTH, 1), monthCount++, totalMonthCount++) {
        int month = cal.get(Calendar.MONTH) + 1; // Month starts from 0

        ALabeledVisualizationObject monthSymbol = this.monthCreateSymbol.transform(null, model, config);
        monthSymbol.setText(new Text(Integer.toString(month)));
        monthSymbol.setYpos(UNIT_HEIGHT + UNIT_HEIGHT / 2);
        monthSymbol.setXpos(UNIT_WIDTH * totalMonthCount + UNIT_WIDTH / 2);
        container.getChildren().add(monthSymbol);

        monthSymbol.setWidth(UNIT_WIDTH);
      }
      yearSymbol.setWidth(UNIT_WIDTH * monthCount + 1);
      yearSymbol.setXpos(UNIT_WIDTH * totalMonthCount - UNIT_WIDTH * monthCount / 2);
    }
    return container;
  }
}
