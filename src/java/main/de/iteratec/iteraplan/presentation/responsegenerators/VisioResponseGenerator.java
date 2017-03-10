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
package de.iteratec.iteraplan.presentation.responsegenerators;

import java.io.IOException;
import java.io.OutputStream;

import de.iteratec.iteraplan.presentation.dialog.GraphicalReporting.GraphicExportBean;
import de.iteratec.visio.model.Document;


/**
 * Base class for Visio Response Generators. The document is written to the ServletOutputStream and
 * the content type and headers are set accordingly. <br>
 * The filename that is set into the header is determinined by parsing the state
 */
public class VisioResponseGenerator extends GraphicsResponseGenerator {

  /** MIME Content Type for Visio */
  private static final String MIME_TYPE_MSVISIO = "application/vnd.visio+xml";

  /** The key for the Bean containing the XML document as a String. */
  public static final String  XML_BEAN          = "VisioXMLExportBean";

  private static final String FILE_TYPE_MSVISIO = ".vdx";

  @Override
  protected String getContentType() {
    return MIME_TYPE_MSVISIO;
  }

  @Override
  protected String getFileType() {
    return FILE_TYPE_MSVISIO;
  }

  @Override
  protected void writeGraphics(GraphicExportBean graphicsBean, OutputStream outputStream) throws IOException {
    Document doc = graphicsBean.getVisioDocument();
    doc.write(outputStream);
  }

  /**
   * accepts all except cluster diagrams (they are not yet supported in visio)
   */
  @Override
  protected boolean savedQueryTypeSupported(GraphicalReport grep) {
    return (!GraphicalReport.CLUSTER.equals(grep));
  }

  @Override
  public GraphicsResponseGenerator getInstance() {
    return new VisioResponseGenerator();
  }

}
