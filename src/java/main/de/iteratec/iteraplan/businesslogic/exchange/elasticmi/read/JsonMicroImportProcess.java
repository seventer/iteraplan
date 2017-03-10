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

import java.io.InputStream;
import java.math.BigInteger;

import de.iteratec.iteraplan.businesslogic.exchange.common.ImportProcessMessages;
import de.iteratec.iteraplan.elasticmi.metamodel.read.RStructuredTypeExpression;
import de.iteratec.iteraplan.elasticmi.model.ObjectExpression;


public interface JsonMicroImportProcess {

  /**
   * Create an new {@link ObjectExpression}.<br>
   * If the {@link ObjectExpression} was created, it will be returned.
   * The information about the success you get it also over {@link #getImportProcessMessages()}.<br>
   * The messages could contain the following messages:<br>
   * <ul>
   * <li>If appropriate permissions for creating are missing, the operation is canceled and the messages contains a warning.</li>
   * <li>In case of an exception the messages contains an error message.</li>
   * <li>In case of success, the messages contains an information message.</li>
   * </ul> 
   * 
   * @param mainType The {@link RStructuredTypeExpression} of the new {@link ObjectExpression}
   * @param in InpuStream with the json
   * 
   * @return the ID of the new {@link ObjectExpression}, null if there was an error
   */
  BigInteger create(RStructuredTypeExpression mainType, InputStream in);

  /**
   * Update an existing {@link ObjectExpression}.<br>
   * If the {@link ObjectExpression} was successful updated, the {@link ObjectExpression} will be returned.
   * The information about the success you get it also over {@link #getImportProcessMessages()}.<br>
   * The messages could contain the following messages:<br>
   * <ul>
   * <li>If appropriate permissions for updating are missing, the operation is canceled and the messages contains a warning.</li>
   * <li>In case of an exception the messages contains an error message.</li>
   * <li>In case of success, the messages contains an information message.</li>
   * </ul> 
   * 
   * @param id 
   * @param mainType The {@link RStructuredTypeExpression} of the new {@link ObjectExpression}
   * @param in InpuStream with the json
   */
  void update(BigInteger id, RStructuredTypeExpression mainType, InputStream in);

  /**
   * Delete the ObjectExcpressen with the given id.<br>
   * The information about the success you get it over the {@link #getImportProcessMessages()}.<br>
   * <ul>
   * <li>If appropriate permissions for deleting are missing, the operation is canceled and the messages contains an warning.</li>
   * <li>In case of an exception the messages contains an error message.</li>
   * <li>In case of success, the messages contains an information message.</li>
   * </ul> 
   */
  void delete(BigInteger id, RStructuredTypeExpression mainType);

  ImportProcessMessages getImportProcessMessages();

}
