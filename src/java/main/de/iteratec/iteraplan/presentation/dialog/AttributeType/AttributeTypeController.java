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
package de.iteratec.iteraplan.presentation.dialog.AttributeType;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.support.PagedListHolder;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJacksonHttpMessageConverter;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.google.common.collect.Maps;

import de.iteratec.iteraplan.businesslogic.service.AttributeTypeService;
import de.iteratec.iteraplan.businesslogic.service.wiki.WikiParserService;
import de.iteratec.iteraplan.common.Dialog;
import de.iteratec.iteraplan.model.TypeOfBuildingBlock;
import de.iteratec.iteraplan.model.attribute.AttributeType;
import de.iteratec.iteraplan.model.attribute.AttributeValue;
import de.iteratec.iteraplan.model.attribute.EnumAT;
import de.iteratec.iteraplan.model.attribute.EnumAV;
import de.iteratec.iteraplan.presentation.PresentationHelper;
import de.iteratec.iteraplan.presentation.dialog.GuiSearchController;
import de.iteratec.iteraplan.presentation.memory.SearchDialogMemory;
import de.iteratec.iteraplan.presentation.tags.TagUtils;


@Controller
public class AttributeTypeController extends GuiSearchController<AttributeTypeDialogMemory> {

  private MappingJacksonHttpMessageConverter converter = new MappingJacksonHttpMessageConverter();
  private WikiParserService                  wikiParserService;

  @Autowired
  public AttributeTypeController(AttributeTypeService attributeTypeService) {
    super();
    this.setAttributeTypeService(attributeTypeService);
  }

  @Autowired
  public void setWikiParserService(WikiParserService wikiParserService) {
    this.wikiParserService = wikiParserService;
  }

  @Override
  protected String getDialogName() {
    return Dialog.ATTRIBUTE_TYPE.getDialogName();
  }

  @Override
  protected String getBaseViewMapping() {
    return "attributetype";
  }

  @Override
  protected TypeOfBuildingBlock getTob() {
    // no building block controller
    return null;
  }

  @Override
  protected AttributeTypeDialogMemory getDialogMemory() {
    return new AttributeTypeDialogMemory();
  }

  @Override
  public PagedListHolder<?> searchAll(AttributeTypeDialogMemory dialogMemory, ModelMap model) {

    List<AttributeType> atList = getAttributeTypeService().getAttributeBySearch(dialogMemory.toAttributeType());
    PagedListHolder<AttributeType> results = new PagedListHolder<AttributeType>(atList);

    return results;
  }

  @RequestMapping
  public void loadAttributeTypeDescription(@RequestParam(value = "id", required = true) String attId,
                                           @RequestParam(value = "avs", required = false) String[] avs, HttpServletResponse response)
      throws IOException {
    AttributeType attr = getAttributeTypeService().loadObjectById(PresentationHelper.parseId(attId));
    List<String> result = new ArrayList<String>();
    String desc = attr.getDescription();
    desc = wikiParserService.convertWikiText(desc);
    desc = TagUtils.breaksAndSpaces(desc);
    result.add(desc);
    if (avs != null && avs.length > 0 && attr instanceof EnumAT) {
      EnumAT en = (EnumAT) attr;
      Map<String, AttributeValue> idToAVMap = createIdToAVMap(en.getAllAttributeValues());
      for (String avIdAsString : avs) {
        if (idToAVMap.containsKey(avIdAsString) && idToAVMap.get(avIdAsString) instanceof EnumAV) {
          EnumAV av = (EnumAV) idToAVMap.get(avIdAsString);
          result.add(av.getDescription());
        }
        else {
          result.add("");
        }
      }
    }
    converter.write(result, MediaType.APPLICATION_JSON, new ServletServerHttpResponse(response));
  }

  /**{@inheritDoc}**/
  @Override
  protected void initHierarchicalTopElement(SearchDialogMemory searchDialogMemory) {
    searchDialogMemory.setHierarchicalTopElement(null); // no hierarchy => set top element to null     
  }

  private Map<String, AttributeValue> createIdToAVMap(Collection<? extends AttributeValue> avs) {
    Map<String, AttributeValue> idToAVMap = Maps.newHashMap();
    for (AttributeValue av : avs) {
      idToAVMap.put(String.valueOf(av.getId()), av);
    }
    return idToAVMap;
  }
}
