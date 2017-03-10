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
package de.iteratec.iteraplan.model.user;

import java.io.Serializable;
import java.util.Date;

import de.iteratec.iteraplan.model.interfaces.IdEntity;


/**
 * This class holds information about a particular data source used in iteraplan, e.g. for access
 * via different database settings. A data source configuration is always identified by a unique 
 * lookup key.
 */
public class DataSource implements Serializable, IdEntity {

  private static final long serialVersionUID = 5647141087923561813L;

  /** The unique identifier. */
  private Integer           id;

  /** {@link #getKey()} */
  private String            key;

  /** {@link #getDriver()} */
  private String            driver;

  /** {@link #getUrl()} */
  private String            url;

  /** {@link #getUser()} */
  private String            user;

  /** {@link #getPassword()} */
  private String            password;

  /** {@link #getExpiryDate()} */
  private Date              expiryDate;

  public Integer getId() {
    return id;
  }

  /**
   * Returns the lookup key for this data source.
   * 
   * @return
   *    The lookup key.
   */
  public String getKey() {
    return key;
  }

  /**
   * Returns the driver class name for this data source. For instance, this is applicable for data 
   * sources that represent database connections.
   * 
   * @return
   *    The driver class name.
   */
  public String getDriver() {
    return driver;
  }

  /**
   * Returns the URL to this data source.
   * 
   * @return    
   *    The URL.
   */
  public String getUrl() {
    return url;
  }

  /**
   * Returns the user that connects to this data source. For instance, this is applicable for data
   * sources that represent database connections.
   * 
   * @return
   *    The user.
   */
  public String getUser() {
    return user;
  }

  /**
   * Returns the password to connect to this data source. For instance, this is applicable for data
   * sources that represent database connections.
   * 
   * @return    
   *    The password.
   */
  public String getPassword() {
    return password;
  }

  /**
   * Returns the expiry date of this data source.
   * 
   * @return
   *    The expiry date.
   */
  public Date getExpiryDate() {
    return expiryDate;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public void setKey(String key) {
    this.key = key;
  }

  public void setDriver(String driver) {
    this.driver = driver;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  public void setUser(String user) {
    this.user = user;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public void setExpiryDate(Date expiryDate) {
    this.expiryDate = expiryDate;
  }

}