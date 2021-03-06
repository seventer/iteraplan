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
package de.iteratec.iteraplan.businesslogic.exchange.common.legend;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Maps;


/**
 * Dummy to be used when no names legend is desired (to increase robustness against NullPointerExceptions)
 */
public final class EmptyNamesLegend implements INamesLegend {

  private static final INamesLegend INSTANCE = new EmptyNamesLegend();

  private EmptyNamesLegend() {
    // do nothing
  }

  public static INamesLegend getInstance() {
    return INSTANCE;
  }

  public List<LogicalPage> createLegend() {
    return new ArrayList<LogicalPage>();
  }

  public boolean displayNamesLegend() {
    return false;
  }

  public String getScreenName(String ownName, String completeName, double fieldWidth, double fieldTextSizePt, int entryId) {
    return "";
  }

  public double getTextWidth(int textLength, double textSizePt) {
    return 0;
  }

  public double getLegendWidth() {
    return 0;
  }

  public int getNamesLegendHeight() {
    return 0;
  }

  public LegendMode getLegendMode() {
    return LegendMode.AUTO;
  }

  public void setLegendMode(LegendMode legendMode) {
    // do nothing
  }

  public void addOffset(double offsetAdd) {
    // do nothing
  }

  public void setTopMargin(double topMargin) {
    // do nothing
  }

  public double getLegendOffsetCount() {
    return 0;
  }

  public void setFrameSize(double frameWidth, double frameHeight, double frameX, double frameY) {
    // do nothing
  }

  public void setPageSize(double pageWidth, double pageHeight) {
    // do nothing
  }

  public void setLegendEntryHeight(double legendEntryHeight) {
    // do nothing
  }

  public boolean isEmpty() {
    return true;
  }

  public Map<String, List<NamesLegendEntry>> getLegendCategories() {
    return Maps.newHashMap();
  }

  public String addLegendEntry(String ownName, String completeName, String entryCategory, double fieldWidth, double fieldTextSizePt, String entryUrl) {
    return "";
  }

}
