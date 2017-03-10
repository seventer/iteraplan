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
package de.iteratec.iteraplan.businesslogic.reports.query.options.GraphicalReporting.Portfolio;

import java.io.Serializable;

import org.apache.commons.lang.builder.ToStringBuilder;

import de.iteratec.iteraplan.businesslogic.reports.query.options.GraphicalReporting.GraphicalExportBaseOptions;
import de.iteratec.iteraplan.common.Constants;
import de.iteratec.iteraplan.common.error.IteraplanBusinessException;
import de.iteratec.iteraplan.common.error.IteraplanErrorMessages;
import de.iteratec.iteraplan.presentation.SpringGuiFactory;


public class PortfolioOptionsBean extends GraphicalExportBaseOptions implements Serializable, IPortfolioOptions {

  /** Serialization version. */
  private static final long serialVersionUID = 3059471565272465499L;
  private String            portfolioType    = PortfolioOptionsBean.TYPE_XY;
  private Integer           xAxisAttributeId = Integer.valueOf(-1);
  private Integer           yAxisAttributeId = Integer.valueOf(-1);
  private Integer           sizeAttributeId  = Integer.valueOf(-1);

  private boolean           scalingEnabled   = false;

  /** The user-selected building block type (string key). */
  private String            selectedBbType   = Constants.BB_INFORMATIONSYSTEMRELEASE_PLURAL;

  public PortfolioOptionsBean() {
    super();
    getColorOptionsBean().setAvailableColors(SpringGuiFactory.getInstance().getPortfolioColors());
  }

  public Integer getYAxisAttributeId() {
    return yAxisAttributeId;
  }

  /**
   * @param axisAttributeId
   *          The yAxisAttributeId to set.
   */
  public void setYAxisAttributeId(Integer axisAttributeId) {
    yAxisAttributeId = axisAttributeId;
  }

  @Override
  public String toString() {
    return ToStringBuilder.reflectionToString(this);
  }

  public String getPortfolioType() {
    return portfolioType;
  }

  /**
   * @param portfolioType
   *          The portfolioType to set.
   */
  public void setPortfolioType(String portfolioType) {
    this.portfolioType = portfolioType;
  }

  public Integer getXAxisAttributeId() {
    return xAxisAttributeId;
  }

  /**
   * @param axisAttributeId
   *          The xAxisAttributeId to set.
   */
  public void setXAxisAttributeId(Integer axisAttributeId) {
    xAxisAttributeId = axisAttributeId;
  }

  @Override
  public String getSelectedBbType() {
    return selectedBbType;
  }

  @Override
  public void setSelectedBbType(String selectedBbType) {
    this.selectedBbType = selectedBbType;
  }

  /**
   * Sets the scaling option for the portfolio graphic
   * 
   * @param scalingEnabled
   *          the state to set
   */
  public void setScalingEnabled(boolean scalingEnabled) {
    this.scalingEnabled = scalingEnabled;
  }

  /**
   * @return The current state of the scaling option
   */
  public boolean isScalingEnabled() {
    return this.scalingEnabled;
  }

  @Override
  public void validate() {
    // Both attributes chosen
    if (xAxisAttributeId.intValue() == -1 || yAxisAttributeId.intValue() == -1) {
      throw new IteraplanBusinessException(IteraplanErrorMessages.PORTFOLIO_NOT_ALL_ATTRIBUTES_CHOSEN);
    }

    // Different attributes
    if (xAxisAttributeId.intValue() == yAxisAttributeId.intValue()) {
      throw new IteraplanBusinessException(IteraplanErrorMessages.PORTFOLIO_IDENTICAL_ATTRIBUTES);
    }
  }

  public Integer getSizeAttributeId() {
    return sizeAttributeId;
  }

  /**
   * @param sizeAttributeId
   *          The sizeAttributeId to set.
   */
  public void setSizeAttributeId(Integer sizeAttributeId) {
    this.sizeAttributeId = sizeAttributeId;
  }

}