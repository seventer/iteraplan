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
package de.iteratec.iteraplan.businesslogic.reports.query.options;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.Lists;

import de.iteratec.iteraplan.model.queries.SavedQuery;
import de.iteratec.iteraplan.presentation.dialog.common.PagePosition;
import de.iteratec.iteraplan.presentation.responsegenerators.GraphicsResponseGenerator.Content;


public abstract class ReportMemBean implements PagePosition, Serializable {

  /** Serialization version. */
  private static final long serialVersionUID = -1071752293386302202L;

  /** Holds the if of a selected saved report. If null, the no saved report is loaded **/
  private Integer           savedQueryId     = null;
  /**
   * Holds the id if of a report is selected for delete. If null, than no saved report should be
   * deleted.
   **/
  private Integer           deleteQueryId    = null;

  /**
   * Content type of the report, defaults to attach
   */
  private Content           content          = Content.ATTACH;

  private String            xmlQueryName;
  private String            xmlQueryDescription;

  private String            xmlSaveAsQueryName;
  private String            xmlSaveAsQueryDescription;

  private boolean           saveAs           = false;

  /** A list of all available saved reports of the underlying type */
  private List<SavedQuery>  savedQueries     = new ArrayList<SavedQuery>();

  private String            pagePositionY;
  private String            pagePositionX;

  private List<String>      errors           = Lists.newArrayList();

  public Integer getSavedQueryId() {
    return savedQueryId;
  }

  public void setSavedQueryId(Integer savedQueryId) {
    this.savedQueryId = savedQueryId;
  }

  public List<SavedQuery> getSavedQueries() {
    return savedQueries;
  }

  public Integer getDeleteQueryId() {
    return deleteQueryId;
  }

  public void setDeleteQueryId(Integer deleteQueryId) {
    this.deleteQueryId = deleteQueryId;
  }

  public void setSavedQueries(List<SavedQuery> savedQueries) {
    this.savedQueries = savedQueries;
  }

  public String getXmlQueryName() {
    return xmlQueryName;
  }

  public void setXmlQueryName(String xmlSaveName) {
    this.xmlQueryName = xmlSaveName;
  }

  public String getXmlQueryDescription() {
    return xmlQueryDescription;
  }

  public void setXmlQueryDescription(String xmlSaveDescription) {
    this.xmlQueryDescription = xmlSaveDescription;
  }

  public String getXmlSaveAsQueryName() {
    return xmlSaveAsQueryName;
  }

  public void setXmlSaveAsQueryName(String xmlSaveAsQueryName) {
    this.xmlSaveAsQueryName = xmlSaveAsQueryName;
  }

  public String getXmlSaveAsQueryDescription() {
    return xmlSaveAsQueryDescription;
  }

  public void setXmlSaveAsQueryDescription(String xmlSaveAsQueryDescription) {
    this.xmlSaveAsQueryDescription = xmlSaveAsQueryDescription;
  }

  public String getPagePositionY() {
    return pagePositionY;
  }

  public void setPagePositionY(String pagePositionY) {
    this.pagePositionY = pagePositionY;
  }

  public String getPagePositionX() {
    return pagePositionX;
  }

  public void setPagePositionX(String pagePositionX) {
    this.pagePositionX = pagePositionX;
  }

  public Content getContent() {
    return content;
  }

  public void setContent(Content content) {
    this.content = content;
  }

  public List<String> getErrors() {
    return errors;
  }

  public void addError(String error) {
    errors.add(error);
  }

  public void clearErrors() {
    errors.clear();
  }

  public boolean isSaveAs() {
    return saveAs;
  }

  public void setSaveAs(boolean saveAs) {
    this.saveAs = saveAs;
  }
}