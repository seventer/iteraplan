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

import de.iteratec.iteraplan.elasticmi.ElasticMiContext;
import de.iteratec.iteraplan.elasticmi.M3CRepository;
import de.iteratec.iteraplan.elasticmi.iteraql2.IteraQlQuery;
import de.iteratec.iteraplan.elasticmi.metamodel.read.RMetamodel;
import de.iteratec.iteraplan.elasticmi.model.BindingSet;
import de.iteratec.iteraplan.elasticmi.model.Model;
import de.iteratec.iteraplan.elasticmi.model.ObjectExpression;
import de.iteratec.iteraplan.elasticmi.util.Either;
import de.iteratec.iteraplan.elasticmi.util.ElasticValue;


public class ElasticMiServiceImpl implements ElasticMiService {

  private M3CRepository m3cRepository;

  /**{@inheritDoc}**/
  @Override
  public void reload() {
    ElasticMiContext currentMiCtx = ElasticMiContext.getCurrentContext();
    if (currentMiCtx != null) {
      this.m3cRepository.updateMetamodelAndModelContainer(currentMiCtx.getCurrentStoreIdentifier());
    }
  }

  /**{@inheritDoc}**/
  @Override
  public Model getModel() {
    return ElasticMiContext.getCurrentContext().getContextModel();
  }

  /**{@inheritDoc}**/
  @Override
  public RMetamodel getRMetamodel() {
    return ElasticMiContext.getCurrentContext().getContextMetamodel();
  }

  /**{@inheritDoc}**/
  @Override
  public Either<ElasticValue<ObjectExpression>, BindingSet> executeQuery(String query) {
    return ElasticMiContext.getCurrentContext().compileAndExecuteQuery(query);
  }

  /**{@inheritDoc}**/
  @Override
  public Either<ElasticValue<ObjectExpression>, BindingSet> executeQuery(IteraQlQuery query) {
    return ElasticMiContext.getCurrentContext().executeQuery(query);
  }

  /**{@inheritDoc}**/
  @Override
  public IteraQlQuery compile(String queryString) {
    return ElasticMiContext.getCurrentContext().compile(queryString);
  }

  public void setM3CRepository(M3CRepository elasticMiExecution) {
    this.m3cRepository = elasticMiExecution;
  }

}
