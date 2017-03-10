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
package de.iteratec.iteraplan.elasticeam.emfimpl;

import java.util.Locale;
import java.util.Observable;

import de.iteratec.iteraplan.elasticeam.ElasticeamContext;
import de.iteratec.iteraplan.elasticeam.metamodel.NamedExpression;
import de.iteratec.iteraplan.elasticeam.util.CompareUtil;
import de.iteratec.iteraplan.elasticeam.util.ElasticeamContextUtil;
import de.iteratec.iteraplan.elasticeam.util.I18nString;


@edu.umd.cs.findbugs.annotations.SuppressWarnings({ "EQ_UNUSUAL" })
public abstract class EMFNamedExpression extends Observable implements NamedExpression {

  private I18nString         abbreviation;
  private I18nString         name;
  private I18nString         description;
  private final EMFMetamodel metamodel;

  /**
   * Default constructor.
   */
  public EMFNamedExpression(EMFMetamodel metamodel) {
    this.metamodel = metamodel;
    this.abbreviation = new I18nString();
    this.description = new I18nString();
    this.name = new I18nString();
  }

  final EMFMetamodel getMetamodelImpl() {
    return this.metamodel;
  }

  static final void copy(EMFNamedExpression source, EMFNamedExpression target) {
    target.name = new I18nString(source.name);
    target.description = new I18nString(source.description);
    target.abbreviation = new I18nString(source.abbreviation);
  }

  /**{@inheritDoc}**/
  public final String getAbbreviation() {
    return this.abbreviation.get(ElasticeamContextUtil.getCurrentContext().getLocale());
  }

  /**{@inheritDoc}**/
  public final String getAbbreviation(Locale locale) {
    return this.abbreviation.get(locale);
  }

  /**{@inheritDoc}**/
  public final void setAbbreviation(String abbreviation) {
    this.abbreviation.set(ElasticeamContextUtil.getCurrentContext().getLocale(), abbreviation);
  }

  /**{@inheritDoc}**/
  public final void setAbbreviation(String abbreviation, Locale locale) {
    this.abbreviation.set(locale, abbreviation);
  }

  /**{@inheritDoc}**/
  public final String getName() {
    return this.name.get(ElasticeamContextUtil.getCurrentContext().getLocale());
  }

  /**{@inheritDoc}**/
  public final String getName(Locale locale) {
    return this.name.get(locale);
  }

  /**{@inheritDoc}**/
  public final void setName(String name) {
    setChanged();
    Locale currentLocale = ElasticeamContextUtil.getCurrentContext().getLocale();
    notifyObservers(new NameChangeEvent(currentLocale, name));
    this.name.set(currentLocale, name);
  }

  /**{@inheritDoc}**/
  public final void setName(String name, Locale locale) {
    setChanged();
    this.name.set(locale, name);
    notifyObservers(new NameChangeEvent(locale, name));
  }

  /**{@inheritDoc}**/
  public final String getDescription() {
    return this.description.get(ElasticeamContextUtil.getCurrentContext().getLocale());
  }

  /**{@inheritDoc}**/
  public final String getDescription(Locale locale) {
    return this.description.get(locale);
  }

  /**{@inheritDoc}**/
  public final void setDescription(String description) {
    this.description.set(ElasticeamContextUtil.getCurrentContext().getLocale(), description);
  }

  /**{@inheritDoc}**/
  public final void setDescription(String description, Locale locale) {
    this.description.set(locale, description);
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
  @Override
  public final boolean equals(Object obj) {
    return CompareUtil.equals(this, obj, getMetaType());
  }

  /**{@inheritDoc}**/
  @Override
  public final int hashCode() {
    return CompareUtil.hashCode(this, getMetaType());
  }

  /**{@inheritDoc}**/
  @Override
  public final String toString() {
    return getPersistentName() + ":" + getMetaType().getSimpleName();
  }
}