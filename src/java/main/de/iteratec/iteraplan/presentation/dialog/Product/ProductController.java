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
package de.iteratec.iteraplan.presentation.dialog.Product;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.support.PagedListHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import de.iteratec.iteraplan.businesslogic.service.BuildingBlockTypeService;
import de.iteratec.iteraplan.businesslogic.service.ProductService;
import de.iteratec.iteraplan.common.Dialog;
import de.iteratec.iteraplan.model.Product;
import de.iteratec.iteraplan.model.TypeOfBuildingBlock;
import de.iteratec.iteraplan.model.sorting.HierarchicalEntityCachingComparator;
import de.iteratec.iteraplan.presentation.SessionConstants;
import de.iteratec.iteraplan.presentation.ajax.DojoUtils;
import de.iteratec.iteraplan.presentation.dialog.GuiSearchController;
import de.iteratec.iteraplan.presentation.memory.SearchDialogMemory;


@Controller
public class ProductController extends GuiSearchController<ProductDialogMemory> {

  @Autowired
  private ProductService           productService;

  @Autowired
  private BuildingBlockTypeService bbtService;

  @Autowired
  public ProductController(ProductService productService) {
    this.productService = productService;
  }

  @Override
  protected ProductDialogMemory getDialogMemory() {
    return new ProductDialogMemory();
  }

  @Override
  protected String getDialogName() {
    return Dialog.PRODUCT.getDialogName();
  }

  @Override
  protected String getBaseViewMapping() {
    return "product";
  }

  @Override
  protected TypeOfBuildingBlock getTob() {
    return TypeOfBuildingBlock.PRODUCT;
  }

  /**
   * This method executes the search for specific product
   * 
   * @param dialogMemory
   *          contains search parameters
   * @param model
   *          Spring model
   * @return view ID of the result page
   */
  @Override
  public PagedListHolder<?> searchAll(ProductDialogMemory dialogMemory, ModelMap model) {

    List<Product> pList = productService.getEntityResultsBySearch(dialogMemory.toProduct());
    PagedListHolder<Product> results = new PagedListHolder<Product>(pList);

    return results;
  }

  @Override
  protected void addBuildingBlockTypeToModel(ModelMap model) {
    model.addAttribute(SessionConstants.MVC_BB_TYPE, bbtService.getBuildingBlockTypeByType(getTob()));
  }

  @Override
  protected void initHierarchicalTopElement(SearchDialogMemory searchDialogMemory) {
    searchDialogMemory.setHierarchicalTopElement(productService.getFirstElement());
  }

  protected void setBbtService(BuildingBlockTypeService bbtService) {
    this.bbtService = bbtService;
  }

  @RequestMapping
  public void list(@RequestParam(value = "escapeHtml", defaultValue = "false") boolean escapeHtml, HttpServletRequest request,
                   HttpServletResponse response) throws IOException {
    //ensure IE is not caching the list
    response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate"); // HTTP 1.1
    response.setHeader("Pragma", "no-cache"); // HTTP 1.0
    response.setDateHeader("Expires", 0); // Proxies.

    List<Product> elements = productService.loadElementList();
    Collections.sort(elements, new HierarchicalEntityCachingComparator<Product>());
    Object jsonData = DojoUtils.convertToMap(elements, escapeHtml, request);
    DojoUtils.write(jsonData, response);
  }
}
