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
package de.iteratec.iteraplan.businesslogic.service;

import de.iteratec.iteraplan.common.error.IteraplanBusinessException;
import de.iteratec.iteraplan.common.error.IteraplanErrorMessages;
import de.iteratec.iteraplan.elasticeam.ElasticeamManager;
import de.iteratec.iteraplan.elasticeam.exception.ElasticeamException;
import de.iteratec.iteraplan.elasticeam.iteraql2.compile.CompiledQuery;
import de.iteratec.iteraplan.elasticeam.iteraql2.result.QueryResult;
import de.iteratec.iteraplan.elasticeam.metamodel.Metamodel;
import de.iteratec.iteraplan.elasticeam.model.Model;


/**
 * A simple implementation of the {@link ElasticeamService}.
 */
public class ElasticeamServiceImpl implements ElasticeamService {

  private ElasticeamManager manager;

  public ElasticeamServiceImpl(ElasticeamManager manager) {
    this.manager = manager;
  }

  /**{@inheritDoc}**/
  public Metamodel getMetamodel() {
    try {
      return manager.getMetamodel();
    } catch (ElasticeamException e) {
      throw new IteraplanBusinessException(IteraplanErrorMessages.ITERAQL_FRAMEWORK_EXCEPTION, e, e.getMessage());
    }
  }

  /**{@inheritDoc}**/
  public Model getModel() {
    try {
      return manager.getModel();
    } catch (ElasticeamException e) {
      throw new IteraplanBusinessException(IteraplanErrorMessages.ITERAQL_FRAMEWORK_EXCEPTION, e, e.getMessage());
    }
  }

  /**{@inheritDoc}**/
  public void initOrReload() {
    this.manager.reInitialize();
  }

  /**{@inheritDoc}**/
  public QueryResult executeQuery(String query) {
    try {
      return manager.executeQuery(query);
    } catch (ElasticeamException e) {
      throw new IteraplanBusinessException(IteraplanErrorMessages.SYNTAX_ERROR_IN_QUERY, e, e.getMessage());
    }
  }

  /**{@inheritDoc}**/
  public QueryResult executeQuery(CompiledQuery query) {
    try {
      return manager.executeQuery(query);
    } catch (ElasticeamException e) {
      throw new IteraplanBusinessException(IteraplanErrorMessages.SYNTAX_ERROR_IN_QUERY, e, e.getMessage());
    }
  }

  /**{@inheritDoc}**/
  public CompiledQuery compile(String queryString) {
    try {
      return manager.compile(queryString);
    } catch (ElasticeamException e) {
      throw new IteraplanBusinessException(IteraplanErrorMessages.SYNTAX_ERROR_IN_QUERY, e, e.getMessage());
    }
  }

  public void setManager(ElasticeamManager manager) {
    this.manager = manager;
    this.manager.reInitialize();
  }

}
