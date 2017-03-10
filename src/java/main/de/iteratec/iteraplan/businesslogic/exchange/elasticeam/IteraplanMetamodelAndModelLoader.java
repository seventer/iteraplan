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

import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import org.hibernate.CacheMode;
import org.hibernate.FlushMode;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.context.ApplicationContext;
import org.springframework.orm.hibernate3.SessionFactoryUtils;
import org.springframework.orm.hibernate3.SessionHolder;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import de.iteratec.iteraplan.businesslogic.exchange.elasticmi.IteraplanMiHibernateAccessor;
import de.iteratec.iteraplan.common.DefaultSpringApplicationContext;
import de.iteratec.iteraplan.common.Logger;
import de.iteratec.iteraplan.common.UserContext;
import de.iteratec.iteraplan.elasticeam.ElasticeamProfile;
import de.iteratec.iteraplan.elasticeam.concurrent.ElasticeamLoadTask;
import de.iteratec.iteraplan.elasticeam.concurrent.MetamodelAndModelContainer;
import de.iteratec.iteraplan.elasticeam.exception.ElasticeamException;
import de.iteratec.iteraplan.elasticeam.model.ModelFactory;
import de.iteratec.iteraplan.model.user.Role;
import de.iteratec.iteraplan.model.user.User;


public class IteraplanMetamodelAndModelLoader extends ElasticeamLoadTask {

  private final static Logger      LOGGER = Logger.getLogger(IteraplanMetamodelAndModelLoader.class);
  private IteraplanMiHibernateAccessor hibernateAccessor;

  private IteraplanMapping         mapping;

  /**
   * Default constructor.
   * @param profile
   */
  public IteraplanMetamodelAndModelLoader(ElasticeamProfile profile, String dataSource) {
    super(profile, dataSource);
    ApplicationContext context = DefaultSpringApplicationContext.getSpringApplicationContext();
    this.hibernateAccessor = new IteraplanMiHibernateAccessor((SessionFactory) context.getBean("sessionFactory"));
  }

  @Override
  public MetamodelAndModelContainer call() throws ElasticeamException {
    MetamodelAndModelContainer result = null;

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
          throw new ElasticeamException(ElasticeamException.GENERAL_ERROR,
              "IteraQl model and data could not be loaded in iteraplan due to an exception in hibernate: \n " + e);
        }
      } finally {
        SessionFactoryUtils.closeSession(sessionHolder.getSession());
        TransactionSynchronizationManager.unbindResource(hibernateAccessor.getSessionFactory());
      }

      //Remove the temporary user context
      UserContext.detachCurrentUserContext();

    } catch (Exception ex) {
      LOGGER.error("IteraQl model and data loading failed", ex);
      throw new ElasticeamException(ElasticeamException.GENERAL_ERROR, "IteraQl model and data loading failed: \n" + ex, ex);
    }
    return result;
  }

  /**{@inheritDoc}**/
  @Override
  protected void loadMetamodel() {
    IteraplanMetamodelLoader metamodelLoader = (IteraplanMetamodelLoader) profile.getValue("iteraplan.metamodel.loader");
    this.mapping = metamodelLoader.loadConceptualMetamodelMapping();
    this.metamodel = mapping.getMetamodel();
  }

  /**{@inheritDoc}**/
  @Override
  protected void loadModel() {
    this.model = ModelFactory.INSTANCE.createModel(mapping.getMetamodel());
    ModelLoader modelLoader = (ModelLoader) profile.getValue("iteraplan.model.loader");
    modelLoader.load(model, mapping);
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

    UserContext userContext = new UserContext("ElasticeamLoader", roles, Locale.US, user);
    userContext.setDataSource(dataSource);
    UserContext.setCurrentUserContext(userContext);
  }

}
