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
package de.iteratec.iteraplan.elasticeam.metamodel;

import java.util.Locale;

import de.iteratec.iteraplan.elasticeam.ElasticeamContext;


/**
 * Basic interface for all metamodel-level expressions. Any expression holds a
 * localizable name and description as well as a name for persisting.
 */
public interface NamedExpression {

  /**
   * @return the abbreviated name for the current locale
   */
  String getAbbreviation();

  String getAbbreviation(Locale locale);

  /**
   * Sets the locale-specific abbreviated name.
   * @param abbreviation the locale-specific abbreviated name
   */
  void setAbbreviation(String abbreviation);

  void setAbbreviation(String abbreviation, Locale locale);

  /**
   * @return the locale-independent technical name
   */
  String getPersistentName();

  /**
   * @return the name for the current locale
   */
  String getName();

  String getName(Locale locale);

  /**
   * Sets the locale-specific name.
   * @param name the locale-specific name
   */
  void setName(String name);

  void setName(String name, Locale locale);

  /**
   * @return the description for the current locale
   */
  String getDescription();

  String getDescription(Locale locale);

  /**
   * Sets the locale-specific description.
   * @param description the locale-specific description
   */
  void setDescription(String description);

  void setDescription(String description, Locale locale);

  /**
   * The meta-type, i.e. IM2L-type, of this instance.
   * @return the meta-type
   */
  Class<? extends NamedExpression> getMetaType();

  class NameChangeEvent {
    private final Locale locale;
    private final String name;

    public NameChangeEvent(Locale locale, String name) {
      this.locale = locale;
      this.name = name;
    }

    public final Locale getLocale() {
      return this.locale;
    }

    public final String getName() {
      return this.name;
    }
  }

  /**
   * Method is deprecated. Use the method with the same name, which takes no {@link ElasticeamContext} as an argument.
   * @param ctx MetamodelContext, specifying locale
   * @return the locale-specific abbreviated name
   */
  @Deprecated
  String getAbbreviation(ElasticeamContext ctx);

  /**
   * Method is deprecated. Use the method with the same name, which takes no {@link ElasticeamContext} as an argument.
   * Sets the locale-specific abbreviated name.
   * @param ctx MetamodelContext, specifying locale
   * @param abbreviation the locale-specific abbreviated name
   */
  @Deprecated
  void setAbbreviation(ElasticeamContext ctx, String abbreviation);

  /**
   * Method is deprecated. Use the method with the same name, which takes no {@link ElasticeamContext} as an argument.
   * @param ctx MetamodelContext, specifying locale
   * @return the locale-specific name
   */
  @Deprecated
  String getName(ElasticeamContext ctx);

  /**
   * Method is deprecated. Use the method with the same name, which takes no {@link ElasticeamContext} as an argument.
   * Sets the locale-specific name.
   * @param ctx MetamodelContext, specifying locale
   * @param name the locale-specific name
   */
  @Deprecated
  void setName(ElasticeamContext ctx, String name);

  /**
   * Method is deprecated. Use the method with the same name, which takes no {@link ElasticeamContext} as an argument.
   * @param ctx MetamodelContext, specifying locale
   * @return the locale-specific description
   */
  @Deprecated
  String getDescription(ElasticeamContext ctx);

  /**
   * Method is deprecated. Use the method with the same name, which takes no {@link ElasticeamContext} as an argument.
   * Sets the locale-specific description.
   * @param ctx MetamodelContext, specifying locale
   * @param description the locale-specific description
   */
  @Deprecated
  void setDescription(ElasticeamContext ctx, String description);

}