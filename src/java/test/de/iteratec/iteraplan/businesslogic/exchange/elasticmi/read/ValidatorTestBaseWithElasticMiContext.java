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

import org.junit.After;
import org.junit.Before;

import de.iteratec.iteraplan.elasticmi.ElasticMiContext;
import de.iteratec.iteraplan.elasticmi.ElasticMiExecution;
import de.iteratec.iteraplan.elasticmi.load.ElasticMiLoadTaskFactory;
import de.iteratec.iteraplan.elasticmi.metamodel.common.ElasticMiConstants;
import de.iteratec.iteraplan.elasticmi.metamodel.read.RPropertyExpression;
import de.iteratec.iteraplan.elasticmi.metamodel.read.RStructuredTypeExpression;
import de.iteratec.iteraplan.elasticmi.model.ObjectExpression;
import de.iteratec.iteraplan.elasticmi.model.ValueExpression;
import de.iteratec.iteraplan.elasticmi.permission.User;
import de.iteratec.iteraplan.elasticmi.permission.impl.StakeholderManagerImpl;
import de.iteratec.iteraplan.elasticmi.util.ElasticValue;


/**
 * Base class for Junit tests with {@link ElasticMiContext}
 */
public abstract class ValidatorTestBaseWithElasticMiContext {

  @Before
  public void onSetUp() {
    StakeholderManagerImpl mgr = new StakeholderManagerImpl();
    User user = mgr.createUser("testUser");
    ElasticMiContext ctx = new ElasticMiContext(user, "MASTER", new ElasticMiExecution(mgr, ElasticMiLoadTaskFactory.EMPTY_TASK_FACTORY));
    ElasticMiContext.setCurrentContext(ctx);
  }

  @After
  public void onTearDown() {
    ElasticMiContext.detachCurrentContext();
  }

  protected static boolean setNameValue(RStructuredTypeExpression type, ObjectExpression oe, String value) {
    if (type == null || oe == null) {
      return false;
    }

    RPropertyExpression nameProp = type.findPropertyByPersistentName(ElasticMiConstants.PERSISTENT_NAME_NAME);
    if (nameProp == null) {
      return false;
    }

    nameProp.set(ElasticValue.one(oe), ElasticValue.one(ValueExpression.create(value)));
    return true;
  }
}
