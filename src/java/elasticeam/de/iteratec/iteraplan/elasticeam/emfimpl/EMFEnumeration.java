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

import java.util.List;
import java.util.Observable;
import java.util.Observer;

import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EEnumLiteral;

import com.google.common.collect.Lists;

import de.iteratec.iteraplan.common.Logger;
import de.iteratec.iteraplan.elasticeam.ElasticeamContext;
import de.iteratec.iteraplan.elasticeam.metamodel.DataTypeExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.EnumerationExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.EnumerationLiteralExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.NamedExpression;
import de.iteratec.iteraplan.elasticeam.util.CompareUtil;
import de.iteratec.iteraplan.elasticeam.util.ElasticeamContextUtil;
import de.iteratec.iteraplan.elasticeam.util.I18nMap;


public class EMFEnumeration extends EMFNamedElement<EEnum> implements EnumerationExpression, Observer {

  private static final Logger                         LOGGER = Logger.getIteraplanLogger(EMFEnumeration.class);

  private final I18nMap<EnumerationLiteralExpression> literals;

  /**
   * Default constructor.
   * @param wrapped
   * @param metamodel
   */
  EMFEnumeration(EEnum wrapped, EMFMetamodel metamodel) {
    super(wrapped, metamodel);
    this.literals = I18nMap.create();
  }

  private Iterable<EEnumLiteral> getAdmissibleLiterals() {
    LOGGER.debug("Accessing literals of {0} with context {1}.", getPersistentName(), ElasticeamContextUtil.getCurrentContext());
    //TODO Handle access control.
    return getWrapped().getELiterals();
  }

  protected final void removeLiteral(EnumerationLiteralExpression literal) {
    literals.remove(literal);
  }

  /**{@inheritDoc}**/
  public final List<EnumerationLiteralExpression> getLiterals() {
    List<EnumerationLiteralExpression> result = Lists.newArrayList();
    for (EEnumLiteral eEnumLiteral : getAdmissibleLiterals()) {
      if (!EMFEnumerationLiteral.NOT_SPECIFIED.equals(eEnumLiteral.getLiteral())) {
        result.add(getMetamodelImpl().encapsulate(eEnumLiteral));
      }
    }
    return result;
  }

  /**{@inheritDoc}**/
  public final EnumerationLiteralExpression findLiteral(String name) {
    EnumerationLiteralExpression candidate = this.literals.get(ElasticeamContextUtil.getCurrentContext().getLocale(), name);
    if (getLiterals().contains(candidate)) {
      return candidate;
    }
    else {
      return null;
    }
  }

  /**{@inheritDoc}**/
  public final EnumerationLiteralExpression findLiteralByPersistentName(String persistentName) {
    EEnumLiteral literal = getWrapped().getEEnumLiteral(persistentName);
    if (literal == null) {
      return null;
    }
    return getMetamodelImpl().encapsulate(literal);
  }

  /**{@inheritDoc}**/
  public final List<EnumerationLiteralExpression> getLiterals(ElasticeamContext ctx) {
    return getLiterals();
  }

  /**{@inheritDoc}**/
  public final EnumerationLiteralExpression findLiteral(ElasticeamContext ctx, String name) {
    return findLiteral(name);
  }

  /**{@inheritDoc}**/
  public final int compareTo(DataTypeExpression o) {
    return CompareUtil.compareTo(this, o);
  }

  /**{@inheritDoc}**/
  public final void update(Observable o, Object arg) {
    if (o instanceof EnumerationLiteralExpression && arg instanceof NameChangeEvent) {
      this.literals.set(((NameChangeEvent) arg).getLocale(), ((NameChangeEvent) arg).getName(), (EnumerationLiteralExpression) o);
    }
  }

  /**{@inheritDoc}**/
  public final Class<? extends NamedExpression> getMetaType() {
    return EnumerationExpression.class;
  }
}
