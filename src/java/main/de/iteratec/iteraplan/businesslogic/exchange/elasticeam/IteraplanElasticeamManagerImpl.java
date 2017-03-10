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
package de.iteratec.iteraplan.businesslogic.exchange.elasticeam;

import java.util.Collection;

import org.springframework.beans.factory.DisposableBean;

import de.iteratec.iteraplan.common.Logger;
import de.iteratec.iteraplan.common.UserContext;
import de.iteratec.iteraplan.elasticeam.ElasticeamManager;
import de.iteratec.iteraplan.elasticeam.ElasticeamProfile;
import de.iteratec.iteraplan.elasticeam.concurrent.GlobalMetamodelAndModelManager;
import de.iteratec.iteraplan.elasticeam.concurrent.MetamodelAndModelContainer;
import de.iteratec.iteraplan.elasticeam.exception.ElasticeamException;
import de.iteratec.iteraplan.elasticeam.iteraql2.IteraQl2Compiler;
import de.iteratec.iteraplan.elasticeam.iteraql2.IteraQl2Exception;
import de.iteratec.iteraplan.elasticeam.iteraql2.compile.CompileUtil;
import de.iteratec.iteraplan.elasticeam.iteraql2.compile.CompiledQuery;
import de.iteratec.iteraplan.elasticeam.iteraql2.qt.Query;
import de.iteratec.iteraplan.elasticeam.iteraql2.result.QueryResult;
import de.iteratec.iteraplan.elasticeam.iteraql2.result.RelationshipEndResult;
import de.iteratec.iteraplan.elasticeam.iteraql2.result.UniversalTypeResult;
import de.iteratec.iteraplan.elasticeam.metamodel.Metamodel;
import de.iteratec.iteraplan.elasticeam.metamodel.RelationshipEndExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.UniversalTypeExpression;
import de.iteratec.iteraplan.elasticeam.model.BindingSet;
import de.iteratec.iteraplan.elasticeam.model.Model;
import de.iteratec.iteraplan.elasticeam.model.UniversalModelExpression;


@edu.umd.cs.findbugs.annotations.SuppressWarnings({ "SC_START_IN_CTOR" })
public class IteraplanElasticeamManagerImpl implements ElasticeamManager, DisposableBean {

  private static final Logger            LOGGER = Logger.getIteraplanLogger(IteraplanElasticeamManagerImpl.class);

  /**
   * The metamodel and model manager which is responsible for the loading cycles and for holding the
   * current version of metamodel and model.
   */
  //  private MetamodelAndModelManager loadManager;

  private GlobalMetamodelAndModelManager dataSourceManager;

  public IteraplanElasticeamManagerImpl(ElasticeamProfile profile) {
    dataSourceManager = new GlobalMetamodelAndModelManager(profile);
    dataSourceManager.start();
  }

  /**{@inheritDoc}**/
  public void reInitialize() {
    LOGGER.debug("A reInitialize was called in the iteraQl manager.");
    dataSourceManager.doUpdate(UserContext.getActiveDatasource());
    LOGGER.debug("ReInitialize successfully submitted to the loader thread.");
  }

  /**{@inheritDoc}**/
  public Metamodel getMetamodel() {
    if (!checkIfInitialized()) {
      throw new ElasticeamException(ElasticeamException.GENERAL_ERROR,
          "The query framework has not been initialized. Please try again in a few minutes.");
    }
    return dataSourceManager.getMetamodelAndModel(UserContext.getActiveDatasource()).getMetamodel();
  }

  /**{@inheritDoc}**/
  public Model getModel() {
    if (!checkIfInitialized()) {
      throw new ElasticeamException(ElasticeamException.GENERAL_ERROR,
          "The query framework has not been initialized. Please try again in a few minutes.");
    }
    return dataSourceManager.getMetamodelAndModel(UserContext.getActiveDatasource()).getModel();
  }

  /**{@inheritDoc}**/
  public QueryResult executeQuery(String queryString) {
    if (!checkIfInitialized()) {
      LOGGER.debug("An attempt is made to execute a query while iteraQl is not initialized.");
      throw new ElasticeamException(ElasticeamException.GENERAL_ERROR,
          "No query can be executed since the metamodel and model are not initialized. Please try again in a few minutes.");
    }

    return executeStringQuery(queryString, dataSourceManager.getMetamodelAndModel(UserContext.getActiveDatasource()));
  }

  /**{@inheritDoc}**/
  public QueryResult executeQuery(Query query) {
    if (!checkIfInitialized()) {
      LOGGER.debug("An attempt is made to execute a query while iteraQl is not initialized.");
      throw new ElasticeamException(ElasticeamException.GENERAL_ERROR,
          "No query can be executed since the metamodel and model are not initialized. Please try again in a few minutes.");
    }

    return executeObjectQuery(query, dataSourceManager.getMetamodelAndModel(UserContext.getActiveDatasource()));
  }

  public QueryResult executeQuery(CompiledQuery query) {
    if (!checkIfInitialized()) {
      LOGGER.debug("An attempt is made to execute a query while iteraQl is not initialized.");
      throw new ElasticeamException(ElasticeamException.GENERAL_ERROR,
          "No query can be executed since the metamodel and model are not initialized. Please try again in a few minutes.");
    }

    return executeCompiledQuery(query, dataSourceManager.getMetamodelAndModel(UserContext.getActiveDatasource()).getModel());
  }

  /**{@inheritDoc}**/
  public CompiledQuery compile(String queryString) {
    if (!checkIfInitialized()) {
      LOGGER.debug("An attempt is made to parse a query while iteraQl is not initialized.");
      throw new ElasticeamException(ElasticeamException.GENERAL_ERROR,
          "No query can be parsed since the metamodel and model are not initialized. Please try again in a few minutes.");
    }
    return IteraQl2Compiler.compile(dataSourceManager.getMetamodelAndModel(UserContext.getActiveDatasource()).getMetamodel(), queryString);
  }

  public CompiledQuery compile(Query queryObject) {
    if (!checkIfInitialized()) {
      LOGGER.debug("An attempt is made to parse a query while iteraQl is not initialized.");
      throw new ElasticeamException(ElasticeamException.GENERAL_ERROR,
          "No query can be parsed since the metamodel and model are not initialized. Please try again in a few minutes.");
    }

    return IteraQl2Compiler.compile(dataSourceManager.getMetamodelAndModel(UserContext.getActiveDatasource()).getMetamodel(), queryObject);
  }

  public boolean isInitialized() {
    return checkIfInitialized();
  }

  private static QueryResult executeStringQuery(String queryString, MetamodelAndModelContainer container) {
    return executeCompiledQuery(IteraQl2Compiler.compile(container.getMetamodel(), queryString), container.getModel());
  }

  private static QueryResult executeObjectQuery(Query queryObject, MetamodelAndModelContainer container) {
    return executeCompiledQuery(IteraQl2Compiler.compile(container.getMetamodel(), queryObject), container.getModel());
  }

  private static QueryResult executeCompiledQuery(CompiledQuery query, Model model) {
    QueryResult result = null;
    if (CompileUtil.isUniversalTypeCompilationUnit(query)) {
      UniversalTypeExpression type = CompileUtil.asUniversalTypeCompilationUnit(query).getCompilationResult();
      Collection<UniversalModelExpression> results = model.findAll(type);
      result = new UniversalTypeResult(query, type, results);
    }
    else if (CompileUtil.isRelationshipEndCompilationUnit(query)) {
      RelationshipEndExpression relEnd = CompileUtil.asRelationshipEndCompilationUnit(query).getCompilationResult();
      BindingSet results = model.findAll(relEnd);
      result = new RelationshipEndResult(query, results);
    }
    else {
      throw new IteraQl2Exception(IteraQl2Exception.UNKNOWN_RESULT_TYPE, "Unknown kind of compiled query: " + query + ".");
    }

    return result;
  }

  private boolean checkIfInitialized() {
    if (dataSourceManager.getMetamodelAndModel(UserContext.getActiveDatasource()) != null) {
      return true;
    }
    return false;
  }

  /**
   * Implemented to ensure that allocated resources, like threads, are cleaned up when the Spring Application context is shut down
   */
  public void destroy() throws Exception { // NOPMD Exception is dictated by interface
    dataSourceManager.setStopManager();
  }
}
