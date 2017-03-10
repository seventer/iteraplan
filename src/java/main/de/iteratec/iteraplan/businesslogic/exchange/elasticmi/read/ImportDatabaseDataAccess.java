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
package de.iteratec.iteraplan.businesslogic.exchange.elasticmi.read;

import com.google.common.collect.BiMap;

import de.iteratec.iteraplan.businesslogic.exchange.elasticmi.ElasticMiIteraplanMapping;
import de.iteratec.iteraplan.businesslogic.exchange.elasticmi.IteraplanMiLoadTask;
import de.iteratec.iteraplan.businesslogic.exchange.elasticmi.IteraplanMiLoadTaskFactory;
import de.iteratec.iteraplan.elasticmi.ElasticMiContext;
import de.iteratec.iteraplan.elasticmi.metamodel.read.RMetamodel;
import de.iteratec.iteraplan.elasticmi.metamodel.read.pojo.PojoRMetamodelCopyUtil;
import de.iteratec.iteraplan.elasticmi.metamodel.write.WMetamodel;
import de.iteratec.iteraplan.elasticmi.model.Model;
import de.iteratec.iteraplan.elasticmi.model.ObjectExpression;


/**
 * Encapsulates the process of loading current metamodel and model
 * from the iteraplan database for the purposes of importing metamodel
 * and model data into hibernate, allowing for controlled on-demand
 * and explicit re-loading.
 * <br><br>
 * <b>Important:</b><br>
 * <li>Not thread safe!</li>
 */
class ImportDatabaseDataAccess {

  private final IteraplanMiLoadTaskFactory loadTaskFactory;

  private RMetamodel                       rMetamodel       = null;
  private Model                            model            = null;
  private ElasticMiIteraplanMapping        metamodelMapping = null;
  private BiMap<Object, ObjectExpression>  instanceMapping  = null;

  private boolean                          initialized      = false;

  protected ImportDatabaseDataAccess(IteraplanMiLoadTaskFactory factory) {
    this.loadTaskFactory = factory;
  }

  private boolean isInitialized() {
    return initialized;
  }

  /**
   * Reloads the model synchronously from the database.
   * <br><br><b>IMPORTANT!!!</b><br>
   * <li>Side effect is the actualization of the model in the current elasticMI context.</li>
   * <li>Metamodel and mappings are NOT updated.</li>
   * <br>
   * <b>Use with sense.</b>
   */
  public void loadModelWithContext() {
    ElasticMiContext ctx = ElasticMiContext.getCurrentContext();
    ctx.synchronizeAndGetMetamodelAndModelContainer();
    model = ctx.getContextModel();
  }

  /**
   * Reloads metamodel, model and mappings locally. Does not effect the metamodel
   * and model of the current elasticMI context.
   */
  public void loadAllWithoutContext() {
    IteraplanMiLoadTask loadTask = loadTaskFactory.create(ElasticMiContext.getCurrentContext().getCurrentStoreIdentifier());
    WMetamodel wMetamodel = loadMetamodel(loadTask);
    model = loadTask.loadModel(wMetamodel, rMetamodel);
    instanceMapping = loadTask.getInstanceMapping();
    this.initialized = true;
  }

  public void loadMetamodel() {
    IteraplanMiLoadTask loadTask = loadTaskFactory.create(ElasticMiContext.getCurrentContext().getCurrentStoreIdentifier());
    loadMetamodel(loadTask);
  }

  private WMetamodel loadMetamodel(IteraplanMiLoadTask loadTask) {
    WMetamodel wMetamodel = loadTask.loadWMetamodel();
    rMetamodel = PojoRMetamodelCopyUtil.rMetamodelFor(wMetamodel);
    metamodelMapping = loadTask.getMetamodelMapping();
    return wMetamodel;
  }

  RMetamodel getRMetamodel() {
    if (!isInitialized()) {
      loadAllWithoutContext();
    }
    return this.rMetamodel;
  }

  Model getModel() {
    if (!isInitialized()) {
      loadAllWithoutContext();
    }
    return this.model;
  }

  ElasticMiIteraplanMapping getMetamodelMapping() {
    if (!isInitialized()) {
      loadAllWithoutContext();
    }
    return this.metamodelMapping;
  }

  BiMap<Object, ObjectExpression> getInstanceMapping() {
    if (!isInitialized()) {
      loadAllWithoutContext();
    }
    return this.instanceMapping;
  }
}
