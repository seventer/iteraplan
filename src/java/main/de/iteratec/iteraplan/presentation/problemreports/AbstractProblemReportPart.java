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
package de.iteratec.iteraplan.presentation.problemreports;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.nio.charset.Charset;

import org.apache.commons.lang.StringEscapeUtils;


/**
 * Abstract base class for {@link ProblemReportPart}s
 */
abstract class AbstractProblemReportPart implements ProblemReportPart {

  private ByteArrayOutputStream buffer   = new ByteArrayOutputStream();
  private String                filename;

  protected static final char   PAD_CHAR = ' ';

  public AbstractProblemReportPart(String filename) {
    this.filename = filename;
  }

  public abstract String getReportPartIdentifier();

  public String getEscapedText() {
    Charset charset;
    try {
      charset = Charset.forName("UTF-8");
    } catch (Exception e) {
      charset = Charset.defaultCharset();
    }
    return StringEscapeUtils.escapeHtml(new String(getByteArray(), charset));
  }

  PrintWriter getWriter() {
    return new PrintWriter(buffer);
  }

  public int getRawSize() {
    return buffer.size();
  }

  public String getFilename() {
    return filename;
  }

  public byte[] getByteArray() {
    return buffer.toByteArray();
  }

  /**{@inheritDoc}**/
  @Override
  public String toString() {
    return buffer.toString();
  }
}
