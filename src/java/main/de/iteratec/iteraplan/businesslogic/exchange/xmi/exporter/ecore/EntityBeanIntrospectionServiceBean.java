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
package de.iteratec.iteraplan.businesslogic.exchange.xmi.exporter.ecore;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.emf.ecore.EPackage;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.metadata.ClassMetadata;

import com.google.common.collect.Maps;

import de.iteratec.iteraplan.common.util.Preconditions;

/**
 * This class serves as a Bean that can be used to get the EPackages for the 
 * iteraplan entity classes
 */
public class EntityBeanIntrospectionServiceBean {
  
  private Configuration configuration;
  private SessionFactory sessionFactory;
  
  
  public EntityBeanIntrospectionServiceBean(SessionFactory sessionFactory, Configuration configuration) {
    this.configuration = Preconditions.checkNotNull(configuration);
    this.sessionFactory = Preconditions.checkNotNull(sessionFactory);
  }
  
  /**
   * Creates the EPackages and returns them without keeping the objects in 
   * the memory
   */
  public Collection<EPackage> getEPackages() {
    Map<String, HbMappedClass> hbClassData = getHbMappedClasses();
    EntityBeanIntrospection ebi = new EntityBeanIntrospection(hbClassData);
    return ebi.getEPackages();
  }
  
  private Map<String, HbMappedClass> getHbMappedClasses() {
    
    Map<String, ClassMetadata> allClassMetadata = sessionFactory.getAllClassMetadata();
    Map<String, HbMappedClass> hbMappedClasses = Maps.newHashMap();
    
    Iterator<PersistentClass> pcs = configuration.getClassMappings();
    PersistentClass persistentClass = null;
    ClassMetadata cm = null;
    while (pcs.hasNext()) {
      persistentClass = pcs.next();
      cm = allClassMetadata.get(persistentClass.getEntityName());
      if (cm != null && !cm.getEntityName().startsWith("HIST_")) {
        HbMappedClass hbmClass = new HbMappedClass(hbMappedClasses, cm, persistentClass);
        hbMappedClasses.put(cm.getEntityName(), hbmClass);
      }
    }
    
    for (HbMappedClass hbmClass : hbMappedClasses.values()) {
      hbmClass.cleanUpSuperProperties();
    }
    
    return hbMappedClasses;
  }
}
