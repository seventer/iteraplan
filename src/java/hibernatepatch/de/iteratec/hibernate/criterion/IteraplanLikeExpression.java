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
package de.iteratec.hibernate.criterion;

import org.hibernate.Criteria;
import org.hibernate.EntityMode;
import org.hibernate.HibernateException;
import org.hibernate.criterion.CriteriaQuery;
import org.hibernate.criterion.LikeExpression;
import org.hibernate.engine.TypedValue;

import de.iteratec.iteraplan.common.Constants;
import de.iteratec.iteraplan.persistence.util.SqlHqlStringUtils;


/**
 * This class is an extension of the original org.hibernate.criterion.LikeExpression that replaces
 * the previously used Hibernate-Patch. The class adds an extra constructor which supports a custom
 * escape character.
 */
public class IteraplanLikeExpression extends LikeExpression {

  public IteraplanLikeExpression(String propertyName, String value, boolean ignoreCase) {
    super(propertyName, value, Constants.SQL_LIKE_OPERATOR_ESCAPE_CHAR, ignoreCase);
  }

  /**{@inheritDoc}**/
  @Override
  public TypedValue[] getTypedValues(Criteria criteria, CriteriaQuery criteriaQuery) throws HibernateException {
    // There is exactly one TypedValue, see super implementation.
    final TypedValue basic = super.getTypedValues(criteria, criteriaQuery)[0];

    // Value of the TypedValue is a String, because it is passed in the super constructor call above.    
    String value = SqlHqlStringUtils.escapeSqlLikeSquareBrackets((String) basic.getValue(), criteriaQuery.getFactory().getDialect());

    // EntityMode is always POJO, see super impl. of this method, CriteriaQueryTranslater as the only impl of CriteriaQuery, 
    // and all TypedValue constructions there.
    return new TypedValue[] { new TypedValue(basic.getType(), value, EntityMode.POJO) };
  }
}
