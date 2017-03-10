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
package de.iteratec.iteraplan.businesslogic.service.wiki;

/**
 * Interface for an Parser Service which convert special interaplan Syntax into html text
 */
public interface IteraplanSyntaxParserService {

  /**
   * checks if iteraplan syntax is included
   * @param source
   * @return <b>true</b> if contains iteraplan syntax, otherwise false
   */
  boolean containsIteraplanSyntax(String source);
  
  /**
   *  Returns the html formated version of iteraplan syntax elements and wiki formated text.
   *  
   *  Treated text which contains a special interplan syntax and a wiki syntax.
   *  Firstly, it convert the special interaplan syntax into xhtml. After that,
   *  the mehtod {@link WikiParserService#convertWikiText(String)} is called. This convert the wiki syntax into
   *  html.
   *  
   * @param source
   * @param userAgent
   * @return String
   */
  String convertIteraplanSyntax(String source, String userAgent);
  
  /**
   *  Returns the html formated version of iteraplan syntax elements and wiki formated text.
   *  
   *  Treated text which contains a special interplan syntax and a wiki syntax.
   *  Firstly, it convert the special interaplan syntax into xhtml. After that,
   *  the mehtod {@link WikiParserService#convertWikiText(String)} is called. This convert the wiki syntax into
   *  html.
   *  
   * @param source
   * @param userAgent
   * @param refId
   * @return String
   */
  String convertIteraplanSyntax(String source, String userAgent, Integer refId);

  /**
   * Extract and convert iteraplan specific syntax elements.
   * @param source
   * @param userAgent
   * @param refId
   * @return WikiTextModel
   */
  WikiTextModel extractAndConvertIteraplanElements(String source, String userAgent, Integer refId);

  /**
   * brings together the data from the WikiTextModel 
   * @param source
   * @return String
   */
  String includeIterplanElements(WikiTextModel source);
}
