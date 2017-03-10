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
package de.iteratec.iteraplan.businesslogic.exchange.elasticmi.write.metamodel;

import java.util.List;

import de.iteratec.iteraplan.businesslogic.exchange.common.SimpleMessage;
import de.iteratec.iteraplan.businesslogic.exchange.elasticmi.write.metamodel.diff.AtsApplicableChange;
import de.iteratec.iteraplan.businesslogic.exchange.elasticmi.write.metamodel.diff.IteraplanAttributeDiffer;
import de.iteratec.iteraplan.businesslogic.exchange.elasticmi.write.metamodel.util.VirtualAttributeType;
import de.iteratec.iteraplan.businesslogic.service.AttributeTypeGroupService;
import de.iteratec.iteraplan.businesslogic.service.AttributeTypeService;
import de.iteratec.iteraplan.businesslogic.service.BuildingBlockTypeService;
import de.iteratec.iteraplan.elasticmi.messages.Message.Severity;
import de.iteratec.iteraplan.elasticmi.messages.MessageListener;


public final class AttributeTypeImporter {

  private final AttributeTypeReader       reader;
  private final MessageListener           messageListener;

  private final AttributeTypeGroupService attributeTypeGroupService;
  private final AttributeTypeService      attributeTypeService;
  private final BuildingBlockTypeService  buildingBlockTypeService;

  private boolean                         changesApplied;

  public AttributeTypeImporter(AttributeTypeReader reader, MessageListener messageListener, AttributeTypeGroupService attributeTypeGroupService,
      AttributeTypeService attributeTypeService, BuildingBlockTypeService buildingBlockTypeService) {
    this.reader = reader;
    this.messageListener = messageListener;
    this.attributeTypeGroupService = attributeTypeGroupService;
    this.attributeTypeService = attributeTypeService;
    this.buildingBlockTypeService = buildingBlockTypeService;
  }

  public boolean diff() {
    List<AtsApplicableChange> diffs = getDiffs();
    if (diffs.size() == 0) {
      messageListener.onMessage(new SimpleMessage(Severity.INFO, "No Metamodel changes necessary."));
    }
    else {
      messageListener.onMessage(new SimpleMessage(Severity.INFO, "There are changes to the current Metamodel necessary to proceed:"));
      for (AtsApplicableChange diff : diffs) {
        messageListener.onMessage(diff.getMessage());
      }
    }
    //currently, no error detection is available
    return true;
  }

  public boolean merge() {
    List<AtsApplicableChange> diffs = getDiffs();
    if (diffs.size() == 0) {
      messageListener.onMessage(new SimpleMessage(Severity.INFO, "No Metamodel changes had to be applied."));
    }
    else {
      messageListener.onMessage(new SimpleMessage(Severity.INFO, "Following changes were applied to the current Metamodel:"));

      AttributeTypeWriter writer = new AttributeTypeWriter(attributeTypeService);
      writer.write(diffs, messageListener);

      for (AtsApplicableChange diff : diffs) {
        messageListener.onMessage(diff.getMessage());
      }
      this.changesApplied = true;
    }
    //currently, no error detection is available
    return true;
  }

  public boolean wereChangesApplied() {
    return this.changesApplied;
  }

  private List<AtsApplicableChange> getDiffs() {
    List<VirtualAttributeType> virtualAttributeTypes = reader.readVirtualAttributes();

    IteraplanAttributeDiffer iad = new IteraplanAttributeDiffer(attributeTypeGroupService, attributeTypeService, buildingBlockTypeService, messageListener);
    return iad.findDifferences(virtualAttributeTypes);
  }

}
