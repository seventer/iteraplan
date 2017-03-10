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
package de.iteratec.iteraplan.presentation.dialog.IteraQl;

import java.util.List;

import de.iteratec.iteraplan.presentation.dialog.common.PagePosition;


public class IteraQlQueryResult implements PagePosition {

  private String                   pagePositionY;
  private String                   pagePositionX;

  private String                   type1Name;
  private String                   type2Name;

  private int                      type1Size;
  private int                      type2Size;

  private boolean                  bindingSetResult;

  private long                     execTime;

  private List<IteraQlResultEntry> resultEntries;

  public String getType1Name() {
    return type1Name;
  }

  public void setType1Name(String type1Name) {
    this.type1Name = type1Name;
  }

  public String getType2Name() {
    return type2Name;
  }

  public void setType2Name(String type2Name) {
    this.type2Name = type2Name;
  }

  public boolean isBindingSetResult() {
    return bindingSetResult;
  }

  public void setBindingSetResult(boolean bindingSetResult) {
    this.bindingSetResult = bindingSetResult;
  }

  public long getExecTime() {
    return execTime;
  }

  public void setExecTime(long execTime) {
    this.execTime = execTime;
  }

  public List<IteraQlResultEntry> getResultEntries() {
    return resultEntries;
  }

  public void setResultEntries(List<IteraQlResultEntry> resultEntries) {
    this.resultEntries = resultEntries;
  }

  public int getType1Size() {
    return type1Size;
  }

  public void setType1Size(int type1Size) {
    this.type1Size = type1Size;
  }

  public int getType2Size() {
    return type2Size;
  }

  public void setType2Size(int type2Size) {
    this.type2Size = type2Size;
  }

  /**{@inheritDoc}**/
  public String getPagePositionX() {
    return this.pagePositionY;
  }

  /**{@inheritDoc}**/
  public void setPagePositionX(String pagePositionX) {
    this.pagePositionX = pagePositionX;
  }

  /**{@inheritDoc}**/
  public String getPagePositionY() {
    return this.pagePositionX;
  }

  /**{@inheritDoc}**/
  public void setPagePositionY(String pagePositionY) {
    this.pagePositionY = pagePositionY;
  }

}
