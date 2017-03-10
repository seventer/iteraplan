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
package de.iteratec.iteraplan.elasticeam.metamodel.impl;

import de.iteratec.iteraplan.elasticeam.metamodel.FeatureExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.NamedExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.TypeExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.UniversalTypeExpression;
import de.iteratec.iteraplan.elasticeam.util.CompareUtil;


/**
 *
 */
public abstract class FeatureImpl<T extends TypeExpression> extends NamedExpressionImpl implements FeatureExpression<T> {

  private final String                  persistentName;
  private final int                     lower;
  private final int                     upper;
  private final UniversalTypeExpression holder;
  private final UniversalTypeExpression origin;
  private final T                       type;

  protected FeatureImpl(String persistentName, UniversalTypeExpression holder, UniversalTypeExpression origin, int lower, int upper, T type) {
    this.persistentName = persistentName;
    this.holder = holder;
    this.origin = origin;
    this.lower = lower;
    this.upper = upper;
    this.type = type;
    super.setName(persistentName);
    super.setAbbreviation(persistentName);
    super.setDescription(persistentName);
  }

  protected FeatureImpl(String persistentName, UniversalTypeExpression holder, int lower, int upper, T type) {
    this(persistentName, holder, holder, lower, upper, type);
  }

  /**{@inheritDoc}**/
  public final String getPersistentName() {
    return this.persistentName;
  }

  /**{@inheritDoc}**/
  public final int compareTo(FeatureExpression<T> o) {
    return CompareUtil.compareTo(this, o);
  }

  /**{@inheritDoc}**/
  public final int getLowerBound() {
    return this.lower;
  }

  /**{@inheritDoc}**/
  public final int getUpperBound() {
    return this.upper;
  }

  /**{@inheritDoc}**/
  public final UniversalTypeExpression getSource() {
    return getOrigin();
  }

  /**{@inheritDoc}**/
  public final T getType() {
    return this.type;
  }

  /**{@inheritDoc}**/
  public final UniversalTypeExpression getOwner() {
    return getHolder();
  }

  /**{@inheritDoc}**/
  public final UniversalTypeExpression getOrigin() {
    return this.origin;
  }

  /**{@inheritDoc}**/
  public final UniversalTypeExpression getHolder() {
    return this.holder;
  }

  /**{@inheritDoc}**/
  public final Class<? extends NamedExpression> getMetaType() {
    return FeatureExpression.class;
  }
}
