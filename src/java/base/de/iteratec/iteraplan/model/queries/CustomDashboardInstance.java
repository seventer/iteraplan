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

import java.util.Date;

import de.iteratec.iteraplan.model.interfaces.IdEntity;


/**
 * Entity representing an instance of a custom dashboard
 */
public class CustomDashboardInstance implements IdEntity {
  private static final long       serialVersionUID = 1L;

  private Integer                 id;
  private Integer                 olVersion;
  private String                  name;
  private String                  description;
  private SavedQuery              query;
  private CustomDashboardTemplate template;
  private String                  author;
  private Date                    creationTime;
  private Date                    lastAccessTime;
  private String                  lastAccessUser;

  /**{@inheritDoc}**/
  public Integer getId() {
    return id;
  }

  /**
   * @return olVersion the olVersion
   */
  public Integer getOlVersion() {
    return olVersion;
  }

  public void setOlVersion(Integer olVersion) {
    this.olVersion = olVersion;
  }

  /**
   * @return name the name
   */
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  /**
   * @return description the description
   */
  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  /**
   * @return query the query
   */
  public SavedQuery getQuery() {
    return query;
  }

  public void setQuery(SavedQuery query) {
    this.query = query;
  }

  /**
   * @return the template
   */
  public CustomDashboardTemplate getTemplate() {
    return template;
  }

  public void setTemplate(CustomDashboardTemplate template) {
    this.template = template;
  }

  /**
   * @return author the author
   */
  public String getAuthor() {
    return author;
  }

  public void setAuthor(String author) {
    this.author = author;
  }

  /**
   * @return creationTime the creationTime
   */
  public Date getCreationTime() {
    return creationTime;
  }

  public void setCreationTime(Date creationTime) {
    this.creationTime = creationTime;
  }

  /**
   * @return lastAccessTime the lastAccessTime
   */
  public Date getLastAccessTime() {
    return lastAccessTime;
  }

  public void setLastAccessTime(Date lastAccessTime) {
    this.lastAccessTime = lastAccessTime;
  }

  /**
   * @return lastAccessUser the lastAccessUser
   */
  public String getLastAccessUser() {
    return lastAccessUser;
  }

  public void setLastAccessUser(String lastAccessUser) {
    this.lastAccessUser = lastAccessUser;
  }

  /**
   * @return serialVersionUID the serialVersionUID
   */
  public static long getSerialversionuid() {
    return serialVersionUID;
  }

  /**{@inheritDoc}**/
  public void setId(Integer id) {
    this.id = id;
  }
}
