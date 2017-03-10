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
package de.iteratec.iteraplan.businesslogic.exchange.elasticmi.write.metamodel.diff;

import java.util.List;

import org.easymock.EasyMock;

import com.google.common.collect.Lists;

import de.iteratec.iteraplan.businesslogic.service.AttributeTypeGroupService;
import de.iteratec.iteraplan.businesslogic.service.AttributeTypeGroupServiceImpl;
import de.iteratec.iteraplan.businesslogic.service.AttributeTypeService;
import de.iteratec.iteraplan.businesslogic.service.BuildingBlockTypeService;
import de.iteratec.iteraplan.elasticmi.messages.MessageListener;
import de.iteratec.iteraplan.model.BuildingBlockType;
import de.iteratec.iteraplan.model.TypeOfBuildingBlock;
import de.iteratec.iteraplan.model.attribute.AttributeType;
import de.iteratec.iteraplan.model.attribute.AttributeTypeGroup;


/**
 * Helper class to build {@link IteraplanAttributeDiffer} instances using Mock objects.
 */
public class MockedIteraplanAttributeDifferBuilder {
  private List<String>              attributeNames = Lists.newArrayList();
  private List<AttributeType>       attributeTypes = Lists.newArrayList();
  private List<BuildingBlockType>   bbts           = Lists.newArrayList();
  private List<TypeOfBuildingBlock> tobbs          = Lists.newArrayList();

  public MockedIteraplanAttributeDifferBuilder withAttribute(String attributeName, AttributeType attributeType) {
    this.attributeNames.add(attributeName);
    this.attributeTypes.add(attributeType);
    return this;
  }

  public MockedIteraplanAttributeDifferBuilder withBBT(TypeOfBuildingBlock tobb, BuildingBlockType bbt) {
    this.tobbs.add(tobb);
    this.bbts.add(bbt);
    return this;
  }

  public IteraplanAttributeDiffer build() {
    AttributeTypeGroupService atgService = EasyMock.createMock(AttributeTypeGroupServiceImpl.class);
    EasyMock.expect(atgService.getStandardAttributeTypeGroup()).andReturn(new AttributeTypeGroup());
    EasyMock.replay(atgService);

    AttributeTypeService attributeTypeService = EasyMock.createMock(AttributeTypeService.class);
    if (attributeNames != null) {
      for (int i = 0; i < attributeNames.size(); i++) {
        EasyMock.expect(attributeTypeService.getAttributeTypeByName(attributeNames.get(i))).andReturn(attributeTypes.get(i));
      }
    }
    EasyMock.replay(attributeTypeService);

    BuildingBlockTypeService buildingBlockTypeService = EasyMock.createMock(BuildingBlockTypeService.class);
    if (tobbs != null) {
      for (int i = 0; i < tobbs.size(); i++) {
        EasyMock.expect(buildingBlockTypeService.getBuildingBlockTypeByType(tobbs.get(i))).andReturn(bbts.get(i));
      }
    }
    EasyMock.replay(buildingBlockTypeService);

    return new IteraplanAttributeDiffer(atgService, attributeTypeService, buildingBlockTypeService, MessageListener.NOOP_LISTENER);
  }
}