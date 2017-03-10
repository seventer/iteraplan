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

import java.util.List;
import java.util.Map;

import com.google.common.collect.Maps;

import de.iteratec.iteraplan.elasticeam.ElasticeamContext;
import de.iteratec.iteraplan.elasticeam.metamodel.DataTypeExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.EnumerationExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.EnumerationLiteralExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.NamedExpression;
import de.iteratec.iteraplan.elasticeam.util.CompareUtil;
import de.iteratec.iteraplan.elasticeam.util.ElasticeamContextUtil;
import de.iteratec.iteraplan.elasticeam.util.I18nMap;


public abstract class EnumerationImpl extends NamedExpressionImpl implements EnumerationExpression {

  private final String                                    persistentName;
  private final I18nMap<EnumerationLiteralExpression>     literals;
  private final Map<String, EnumerationLiteralExpression> literalsByPersistentname;

  public EnumerationImpl(String persistentName) {
    this.persistentName = persistentName;
    this.literals = I18nMap.create();
    this.literalsByPersistentname = Maps.newHashMap();
  }

  protected void init() {
    for (EnumerationLiteralExpression literal : getLiterals()) {
      //TODO Add to i18nMap
      this.literalsByPersistentname.put(literal.getPersistentName(), literal);
    }
  }

  /**{@inheritDoc}**/
  public final String getPersistentName() {
    return this.persistentName;
  }

  /**{@inheritDoc}**/
  public final Class<? extends NamedExpression> getMetaType() {
    return EnumerationExpression.class;
  }

  /**{@inheritDoc}**/
  public final int compareTo(DataTypeExpression o) {
    return CompareUtil.compareTo(this, o);
  }

  /**{@inheritDoc}**/
  public final EnumerationLiteralExpression findLiteral(String name) {
    return this.literals.get(ElasticeamContextUtil.getCurrentContext().getLocale(), name);
  }

  /**{@inheritDoc}**/
  public final EnumerationLiteralExpression findLiteralByPersistentName(String pName) {
    return this.literalsByPersistentname.get(pName);
  }

  /**{@inheritDoc}**/
  public final List<EnumerationLiteralExpression> getLiterals(ElasticeamContext ctx) {
    return getLiterals();
  }

  /**{@inheritDoc}**/
  public final EnumerationLiteralExpression findLiteral(ElasticeamContext ctx, String name) {
    return findLiteral(name);
  }

}
