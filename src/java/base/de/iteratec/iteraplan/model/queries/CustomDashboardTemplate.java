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
package de.iteratec.iteraplan.model.queries;

import java.io.Serializable;

import org.hibernate.search.annotations.DocumentId;

import de.iteratec.iteraplan.model.BuildingBlockType;
import de.iteratec.iteraplan.model.interfaces.IdEntity;


/**
 * Entity of a custom dashboard template
 * This are the templates for dashboard instances.
 */
public class CustomDashboardTemplate implements IdEntity, Serializable {

  private static final long serialVersionUID = 3354735098375814058L;

  @DocumentId
  private Integer           id;
  private Integer           olVersion;
  private String            name;
  private String            description;
  private String            content;
  private BuildingBlockType buildingBlockType;

  /**
   * Default constructor.
   * Needed for hibernate
   */
  public CustomDashboardTemplate() {
    // empty constructor
  }

  /**
   *  Custom Dashboard Template Constructor
   * 
   * @param buildingBlockType
   * @param description
   * @param content
   * @param name
   */
  public CustomDashboardTemplate(BuildingBlockType buildingBlockType, String description, String content, String name) {
    this.buildingBlockType = buildingBlockType;
    this.description = description;
    this.content = content;
    this.name = name;
  }

  /**{@inheritDoc}**/
  public Integer getId() {
    return id;
  }

  /**{@inheritDoc}**/
  public void setId(Integer id) {
    this.id = id;
  }

  /**
   * mutator to be used by hibernate and bank data import
   * 
   * @param name
   */
  public void setName(String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }

  /**
   * mutator to be used by hibernate and bank data import
   * 
   * @param description
   */
  public void setDescription(String description) {
    this.description = description;
  }

  public String getDescription() {
    return description;
  }

  /**
   * mutator to be used by hibernate and bank data import
   * 
   * @param content
   */
  public void setContent(String content) {
    this.content = content;
  }

  public String getContent() {
    return content;
  }

  public Integer getOlVersion() {
    return olVersion;
  }

  public void setOlVersion(Integer olVersion) {
    this.olVersion = olVersion;
  }

  public BuildingBlockType getBuildingBlockType() {
    return buildingBlockType;
  }

  public void setBuildingBlockType(BuildingBlockType buildingBlockType) {
    this.buildingBlockType = buildingBlockType;
  }
  /*
    public int compareTo(CustomDashboardTemplate o) {
      // compared using the building block types
      Locale locale = UserContext.getCurrentLocale();
      String a = MessageAccess.getString(this.getBuildingBlockType().getTypeOfBuildingBlock().toString(), locale);
      String b = MessageAccess.getString(o.getBuildingBlockType().getTypeOfBuildingBlock().toString(), locale);

      return a.compareToIgnoreCase(b);
    }
  */
}
