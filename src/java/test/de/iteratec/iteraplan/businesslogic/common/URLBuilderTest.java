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
package de.iteratec.iteraplan.businesslogic.common;

import static org.junit.Assert.assertThat;
import static org.junit.matchers.JUnitMatchers.containsString;

import org.junit.Before;
import org.junit.Test;

import de.iteratec.iteraplan.model.BusinessMapping;
import de.iteratec.iteraplan.model.InformationSystemInterface;
import de.iteratec.iteraplan.model.InformationSystemRelease;
import de.iteratec.iteraplan.model.InfrastructureElement;
import de.iteratec.iteraplan.model.Product;
import de.iteratec.iteraplan.model.Tcr2IeAssociation;
import de.iteratec.iteraplan.model.TechnicalComponentRelease;
import de.iteratec.iteraplan.model.Transport;
import de.iteratec.iteraplan.model.attribute.AttributeTypeGroup;


public class URLBuilderTest {
  private BusinessMapping     mapping;
  private Product             prod;
  private Transport           trsp;
  private Tcr2IeAssociation   tcrIeAssoc;
  private AttributeTypeGroup  atg;

  private static final String LOCALHOST = "http://localhost";
  private static final String ITERAPLAN = "/iteraplan";

  @Before
  public void setUp() {
    prod = new Product();
    prod.setName("Product");
    prod.setId(Integer.valueOf(23));
    InformationSystemRelease isr = new InformationSystemRelease();
    InformationSystemInterface isi = new InformationSystemInterface();
    isr.setId(Integer.valueOf(41));

    mapping = new BusinessMapping();
    mapping.addProduct(prod);
    mapping.addInformationSystemRelease(isr);
    mapping.setId(Integer.valueOf(42));

    isi.setId(Integer.valueOf(13));
    isi.setDescription("isi desc");

    trsp = new Transport();
    trsp.setId(Integer.valueOf(12));
    isi.addTransport(trsp);

    TechnicalComponentRelease tcr = new TechnicalComponentRelease();
    tcr.setId(Integer.valueOf(16));
    InfrastructureElement ie = new InfrastructureElement();
    ie.setId(Integer.valueOf(17));
    tcrIeAssoc = new Tcr2IeAssociation(tcr, ie);
    tcrIeAssoc.setId(Integer.valueOf(18));

    atg = new AttributeTypeGroup();
    atg.setId(Integer.valueOf(25));
  }

  @Test
  public void testGetEntityURLIdentityEntityString() {
    String productUri = URLBuilder.getEntityURL(prod, LOCALHOST);
    assertThat("URI must contain ID", productUri, containsString("/23"));
    assertThat("URI must contain element type", productUri, containsString("/product/"));

    String mappingUri = URLBuilder.getEntityURL(mapping, LOCALHOST);
    assertThat("URI must contain the ISR ID", mappingUri, containsString("/41"));
    assertThat("Mapping must link to information system", mappingUri, containsString("/informationsystem/"));

    String transportUri = URLBuilder.getEntityURL(trsp, LOCALHOST);
    assertThat("URI must contain the interface ID", transportUri, containsString("/13"));
    assertThat("Mapping must link to interface", transportUri, containsString("/interface/"));

    String assocUri = URLBuilder.getEntityURL(tcrIeAssoc, LOCALHOST);
    assertThat("URI must contain TCR ID", assocUri, containsString("/16"));
    assertThat("Association must link to one of its ends", assocUri, containsString("/technicalcomponent/"));
  }

  @Test
  public void testGetRelativeURLforFlow() {
    String productUri = URLBuilder.getRelativeURLforFlow(ITERAPLAN, prod);
    assertThat("URI must contain ID", productUri, containsString("/23"));
    assertThat("URI must contain element type", productUri, containsString("/product/"));

    String mappingUri = URLBuilder.getRelativeURLforFlow(ITERAPLAN, mapping);
    assertThat("URI must contain the ISR ID", mappingUri, containsString("/41"));
    assertThat("Mapping must link to information system", mappingUri, containsString("/informationsystem/"));

    String transportUri = URLBuilder.getRelativeURLforFlow(ITERAPLAN, trsp);
    assertThat("URI must contain the interface ID", transportUri, containsString("/13"));
    assertThat("Mapping must link to interface", transportUri, containsString("/interface/"));

    String assocUri = URLBuilder.getRelativeURLforFlow(ITERAPLAN, tcrIeAssoc);
    assertThat("URI must contain TCR ID", assocUri, containsString("/16"));
    assertThat("Association must link to one of its ends", assocUri, containsString("/technicalcomponent/"));

  }

  @Test
  public void testGetRelativeUrlForMvc() throws Exception {
    String atgUri = URLBuilder.getRelativeURLforMVC(ITERAPLAN, atg);
    assertThat("URI must contain ID", atgUri, containsString("id=25"));
    assertThat("URI must contain element type", atgUri, containsString("/attributetypegroup/"));
  }

  // all other methods in URLBuilder require a ServletContext and do simple String concatenation, 
  // not really worth the effort of testing

}
