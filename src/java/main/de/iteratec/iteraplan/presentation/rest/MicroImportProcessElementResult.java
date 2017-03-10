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
package de.iteratec.iteraplan.presentation.rest;

import java.math.BigInteger;

import com.google.gson.JsonObject;

import de.iteratec.iteraplan.businesslogic.exchange.common.ImportProcessMessages;
import de.iteratec.iteraplan.businesslogic.exchange.elasticmi.read.JsonMicroImportProcess;
import de.iteratec.iteraplan.elasticmi.io.mapper.json.JsonElementUriWriter;
import de.iteratec.iteraplan.elasticmi.metamodel.read.RStructuredTypeExpression;
import de.iteratec.iteraplan.elasticmi.model.ObjectExpression;


/**
 * The result of a {@link JsonMicroImportProcess} containing an {@link ObjectExpression} affected by the {@link JsonMicroImportProcess}. The json serialization contains the URI of the affected {@link ObjectExpression}.<br>
 * <br>
 * This is used for PUT and POST verbs in the micro REST API
 */
public class MicroImportProcessElementResult extends MicroImportProcessResult {
  private String                    rootUri;
  private RStructuredTypeExpression type;
  private BigInteger                oeID;

  /**
   * Default constructor.
   * @param rootUri
   * @param messages
   */
  public MicroImportProcessElementResult(String rootUri, ImportProcessMessages messages, RStructuredTypeExpression type, BigInteger oeID) {
    super(messages);
    this.rootUri = rootUri;
    this.type = type;
    this.oeID = oeID;
  }

  /**{@inheritDoc}**/
  @Override
  protected void preMessages(JsonObject target) {
    if (oeID != null) {
      new JsonElementUriWriter(rootUri, type).write(oeID, target);
    }
  }
}
