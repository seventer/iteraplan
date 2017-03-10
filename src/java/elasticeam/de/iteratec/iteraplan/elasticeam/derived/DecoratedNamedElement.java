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
package de.iteratec.iteraplan.elasticeam.derived;

import java.util.Locale;

import de.iteratec.iteraplan.elasticeam.ElasticeamContext;
import de.iteratec.iteraplan.elasticeam.metamodel.NamedExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.UniversalTypeExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.derived.DerivedNamedExpression;


public abstract class DecoratedNamedElement<N extends NamedExpression> implements NamedExpression, DerivedNamedExpression {

  protected DecoratedNamedElement(N wrapped) {
    this.wrapped = wrapped;
  }

  private final N wrapped;

  final N getWrapped() {
    return this.wrapped;
  }

  /**{@inheritDoc}**/
  public final String getAbbreviation() {
    return this.wrapped.getAbbreviation();
  }

  /**{@inheritDoc}**/
  public final String getAbbreviation(Locale locale) {
    return this.wrapped.getAbbreviation(locale);
  }

  /**{@inheritDoc}**/
  public final void setAbbreviation(String abbreviation) {
    this.wrapped.setAbbreviation(abbreviation);
  }

  /**{@inheritDoc}**/
  public final void setAbbreviation(String abbreviation, Locale locale) {
    this.wrapped.setAbbreviation(abbreviation, locale);
  }

  /**{@inheritDoc}**/
  public final String getPersistentName() {
    return this.wrapped.getPersistentName();
  }

  /**{@inheritDoc}**/
  public final String getName() {
    return this.wrapped.getName();
  }

  /**{@inheritDoc}**/
  public final String getName(Locale locale) {
    return this.wrapped.getName(locale);
  }

  /**{@inheritDoc}**/
  public final void setName(String name) {
    this.wrapped.setName(name);
  }

  /**{@inheritDoc}**/
  public final void setName(String name, Locale locale) {
    this.wrapped.setName(name, locale);
  }

  /**{@inheritDoc}**/
  public final String getDescription() {
    return this.wrapped.getDescription();
  }

  /**{@inheritDoc}**/
  public String getDescription(Locale locale) {
    return this.wrapped.getDescription(locale);
  }

  /**{@inheritDoc}**/
  public final void setDescription(String description) {
    this.wrapped.setDescription(description);
  }

  /**{@inheritDoc}**/
  public final void setDescription(String description, Locale locale) {
    this.wrapped.setDescription(description, locale);
  }

  /**{@inheritDoc}**/
  @Override
  public final String toString() {
    return this.wrapped.toString();
  }

  /**{@inheritDoc}**/
  @Override
  public final boolean equals(Object obj) {
    return getWrapped().equals(obj);
  }

  /**{@inheritDoc}**/
  @Override
  public final int hashCode() {
    return getWrapped().hashCode();
  }

  /**{@inheritDoc}**/
  public final String getAbbreviation(ElasticeamContext ctx) {
    return getAbbreviation();
  }

  /**{@inheritDoc}**/
  public final void setAbbreviation(ElasticeamContext ctx, String abbreviation) {
    setAbbreviation(abbreviation);
  }

  /**{@inheritDoc}**/
  public final String getName(ElasticeamContext ctx) {
    return getName();
  }

  /**{@inheritDoc}**/
  public final void setName(ElasticeamContext ctx, String name) {
    setName(name);
  }

  /**{@inheritDoc}**/
  public final String getDescription(ElasticeamContext ctx) {
    return getDescription();
  }

  /**{@inheritDoc}**/
  public final void setDescription(ElasticeamContext ctx, String description) {
    setDescription(description);
  }

  /**{@inheritDoc}**/
  public final UniversalTypeExpression getParent() {
    return wrapped instanceof DerivedNamedExpression ? ((DerivedNamedExpression) wrapped).getParent() : null;
  }
}