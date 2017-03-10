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

import java.util.Iterator;
import java.util.Map;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.metadata.ClassMetadata;

import com.google.common.collect.Maps;

import de.iteratec.iteraplan.businesslogic.exchange.elasticeam.MetamodelExport.TypeOfExchange;
import de.iteratec.iteraplan.common.Logger;
import de.iteratec.iteraplan.common.util.Preconditions;
import de.iteratec.iteraplan.elasticeam.metamodel.Metamodel;
import de.iteratec.iteraplan.elasticeam.metamodel.loader.HbMappedClass;
import de.iteratec.iteraplan.persistence.dao.BuildingBlockTypeDAO;


/**
 * Implementation of {@link IteraplanMetamodelLoader}
 */
public class IteraplanMetamodelLoaderImpl implements IteraplanMetamodelLoader {

  private static final Logger  LOGGER = Logger.getIteraplanLogger(IteraplanMetamodelLoader.class);

  private Configuration        configuration;
  private SessionFactory       sessionFactory;
  private BuildingBlockTypeDAO buildingBlockTypeDAO;

  /**
   * Default constructor.
   * @param sessionFactory the hibernate session factory used to access the hibernate mappings.
   * @param configuration the current hibernate mapping configuration.
   * @param bbtDAO the DAO for querying existing building block types.
   */
  public IteraplanMetamodelLoaderImpl(SessionFactory sessionFactory, Configuration configuration, BuildingBlockTypeDAO bbtDAO) {
    this.configuration = Preconditions.checkNotNull(configuration);
    this.sessionFactory = Preconditions.checkNotNull(sessionFactory);
    this.buildingBlockTypeDAO = Preconditions.checkNotNull(bbtDAO);
  }

  /**{@inheritDoc}**/
  public Metamodel loadConceptualMetamodel() {
    return loadConceptualMetamodelMapping().getMetamodel();
  }

  public IteraplanMapping loadConceptualMetamodelMapping() {
    long time = -System.currentTimeMillis();
    LOGGER.debug("Started Metamodel LoadTask");
    Map<String, HbMappedClass> hbClassData = getHbMappedClasses();
    MetamodelExport metamodelExport = new MetamodelExport(hbClassData, buildingBlockTypeDAO, sessionFactory);
    IteraplanMapping result = metamodelExport.loadMetamodel(TypeOfExchange.CONCEPTUAL);
    time += System.currentTimeMillis();
    LOGGER.debug("Loaded metamodel in {0} ms.", Long.valueOf(time));
    return result;
  }

  /**
   * Create necessary information (={@link HbMappedClass}) about all persisted classes
   * 
   * @return {@link Map} of {@link HbMappedClass} for all persisted model classes
   */
  private Map<String, HbMappedClass> getHbMappedClasses() {

    Map<String, ClassMetadata> allClassMetadata = sessionFactory.getAllClassMetadata();
    Map<String, HbMappedClass> hbMappedClasses = Maps.newHashMap();

    Iterator<PersistentClass> pcs = configuration.getClassMappings();
    PersistentClass persistentClass = null;
    ClassMetadata cm = null;
    while (pcs.hasNext()) {
      persistentClass = pcs.next();
      cm = allClassMetadata.get(persistentClass.getEntityName());
      if (cm != null) {
        HbMappedClass hbmClass = new HbMappedClass(hbMappedClasses, cm, persistentClass);
        hbMappedClasses.put(cm.getEntityName(), hbmClass);
      }
      else {
        // abstract class
        HbMappedClass hbmClass = new HbMappedClass(hbMappedClasses, persistentClass);
        hbMappedClasses.put(persistentClass.getEntityName(), hbmClass);
      }
    }

    return hbMappedClasses;
  }

}
