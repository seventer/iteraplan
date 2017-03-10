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
package de.iteratec.iteraplan.businesslogic.exchange.elasticmi;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.hibernate.CacheMode;
import org.hibernate.FlushMode;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.metadata.ClassMetadata;
import org.springframework.orm.hibernate3.SessionFactoryUtils;
import org.springframework.orm.hibernate3.SessionHolder;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import com.google.common.collect.BiMap;
import com.google.common.collect.Maps;

import de.iteratec.iteraplan.common.Logger;
import de.iteratec.iteraplan.common.UserContext;
import de.iteratec.iteraplan.elasticmi.exception.ElasticMiException;
import de.iteratec.iteraplan.elasticmi.load.ElasticMiLoadTask;
import de.iteratec.iteraplan.elasticmi.m3c.SimpleM3C;
import de.iteratec.iteraplan.elasticmi.metamodel.read.RMetamodel;
import de.iteratec.iteraplan.elasticmi.metamodel.write.WMetamodel;
import de.iteratec.iteraplan.elasticmi.model.Model;
import de.iteratec.iteraplan.elasticmi.model.ObjectExpression;
import de.iteratec.iteraplan.elasticmi.model.impl.ModelImpl;
import de.iteratec.iteraplan.model.user.Role;
import de.iteratec.iteraplan.model.user.User;
import de.iteratec.iteraplan.persistence.dao.AttributeTypeGroupDAO;


/**
 * iteraplan-specific implementation of an elasticMI Load Task.
 * <li>Not thread-safe!</li>
 * <li>Produces a metamodel mapping and an instance mapping also.</li> 
 */
public class IteraplanMiLoadTask extends ElasticMiLoadTask {

  private final static Logger                LOGGER = Logger.getLogger(IteraplanMiLoadTask.class);

  private final Configuration                configuration;
  private final AttributeTypeGroupDAO        attributeTypeGroupDAO;
  private final SessionFactory               sessionFactory;
  private final IteraplanMiHibernateAccessor hibernateAccessor;

  private ElasticMiIteraplanMapping          metamodelMapping;
  private BiMap<Object, ObjectExpression>    instanceMapping;
  private WMetamodel                         metamodel;
  private Model                              model;

  protected IteraplanMiLoadTask(String storeIdentifier, AttributeTypeGroupDAO attributeTypeGroupDAO, Configuration configuration,
      SessionFactory sessionFactory) {
    super(storeIdentifier);
    this.attributeTypeGroupDAO = attributeTypeGroupDAO;
    this.configuration = configuration;
    this.sessionFactory = sessionFactory;
    this.hibernateAccessor = new IteraplanMiHibernateAccessor(sessionFactory);
  }

  @Override
  public SimpleM3C call() {

    SimpleM3C result = null;
    if (UserContext.getCurrentUserContext() != null) {
      try {
        result = super.call();
      } catch (Exception ex) {
        LOGGER.error("IteraQl model and data loading failed", ex);
        throw new ElasticMiException(ElasticMiException.GENERAL_ERROR, "IteraQl model and data loading failed: \n" + ex, ex);
      }
    }
    else {
      try {
        //Create a temporary user context to avoid errors
        createTempUserContext();

        //Open a new session
        Session session = SessionFactoryUtils.getSession(hibernateAccessor.getSessionFactory(), hibernateAccessor.getEntityInterceptor(),
            hibernateAccessor.getJdbcExceptionTranslator());
        session.setFlushMode(FlushMode.MANUAL);
        session.setCacheMode(CacheMode.GET);
        SessionHolder sessionHolder = null;

        try {
          hibernateAccessor.applyFlushMode(session, false);
          sessionHolder = new SessionHolder(session);
          TransactionSynchronizationManager.bindResource(hibernateAccessor.getSessionFactory(), sessionHolder);
          Transaction t = hibernateAccessor.getSessionFactory().getCurrentSession().beginTransaction();
          try {

            //Once the session has been opened, trigger the actual model and data loading in the superclass
            result = super.call();
            //After model and data have been loaded, close the session.
            t.commit();
          } catch (HibernateException e) {
            t.rollback();
            throw new ElasticMiException(ElasticMiException.GENERAL_ERROR,
                "IteraQl model and data could not be loaded in iteraplan due to an exception in hibernate: \n " + e);
          }
        } finally {
          SessionFactoryUtils.closeSession(sessionHolder.getSession());
          TransactionSynchronizationManager.unbindResource(hibernateAccessor.getSessionFactory());
        }

      } catch (Exception ex) {
        LOGGER.error("IteraQl model and data loading failed", ex);
        throw new ElasticMiException(ElasticMiException.GENERAL_ERROR, "IteraQl model and data loading failed: \n" + ex, ex);
      } finally {
        //Remove the temporary user context
        UserContext.detachCurrentUserContext();
      }
    }
    return result;
  }

  /**{@inheritDoc}**/
  @Override
  public WMetamodel loadWMetamodel() {
    WMetamodelExport metamodelExport = new WMetamodelExport(getHbMappedClasses(), attributeTypeGroupDAO);
    metamodelMapping = metamodelExport.loadMetamodel();
    metamodel = metamodelMapping.getMetamodel();
    return metamodel;
  }

  /**{@inheritDoc}**/
  @Override
  public Model loadModel(WMetamodel wMetamodel, RMetamodel rMetamodel) {
    this.model = new ModelImpl();
    this.instanceMapping = new MiModelLoader().load(rMetamodel, metamodelMapping, model, sessionFactory);
    return model;
  }

  /**
   * Exposes the loaded WMetamodel.
   * If no WMetamodel is loaded yet, a load is triggered.
   * @return
   *    The loaded WMetamodel.
   */
  public WMetamodel getWMetamodel() {
    if (this.metamodel == null) {
      return loadWMetamodel();
    }
    else {
      return this.metamodel;
    }
  }

  /**
   * Exposes the loaded Model.
   * If no model is loaded yet, a load is triggered.
   * @return
   *    The loaded Model.
   */
  public Model getModel() {
    return this.model;
  }

  /**
   * Exposes the iteraplan metamodel mapping used for the
   * metamodel loading. If no mapping is loaded yet, a load is triggered.
   * @return
   *    The loaded iteraplan mapping.
   */
  public ElasticMiIteraplanMapping getMetamodelMapping() {
    return this.metamodelMapping;
  }

  /**
   * Exposes the loaded instance mapping created by the model loader.
   * If no model is loaded yet, a load is triggered.
   * @return
   *    The instance mapping produced by the model loader.
   */
  public BiMap<Object, ObjectExpression> getInstanceMapping() {
    return this.instanceMapping;
  }

  /**
   * Create a temporary user context.
   */
  private void createTempUserContext() {
    User user = new User();
    user.setLoginName("IteraQlLoader");

    Role superRole = new Role();
    superRole.setRoleName(Role.SUPERVISOR_ROLE_NAME);

    Set<Role> roles = new HashSet<Role>();
    roles.add(superRole);

    UserContext userContext = new UserContext("ElasticMiLoader", roles, Locale.US, user);
    userContext.setDataSource(getStoreIdentifier());
    UserContext.setCurrentUserContext(userContext);
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
