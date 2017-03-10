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
package de.iteratec.iteraplan.common.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import de.iteratec.iteraplan.common.Logger;


/**
 * Provides ZIP functionality.
 */
public final class ZipUtil {

  private ZipUtil() {
    // made private since this class provides only static methods
  }

  private static final Logger LOGGER = Logger.getIteraplanLogger(ZipUtil.class);

  private static final int    BUFFER = 2048;

  /**
   * Zips files and writes the zip file into an output stream.
   * 
   * @param f
   *          The files that are to be zipped.
   * @param dest
   *          The output stream into which the zip is written.
   * @throws IOException
   *           If something goes wrong.
   */
  public static void zipFilesToStream(File[] f, OutputStream dest) throws IOException {
    BufferedOutputStream stream = null;
    ZipOutputStream out = null;
    try {
      stream = new BufferedOutputStream(dest);
      out = new ZipOutputStream(stream);
      for (int i = 0; i < f.length; i++) {
        File file = f[i];
        if (!file.exists()) {
          LOGGER.info("Could not find file: " + file.getAbsolutePath() + ". The file was not added to the zip archive.");
          continue;
        }
        byte data[] = new byte[BUFFER];
        FileInputStream fi = null;
        BufferedInputStream origin = null;
        try {
          fi = new FileInputStream(file);
          origin = new BufferedInputStream(fi, BUFFER);
          ZipEntry entry = new ZipEntry(file.getName());
          out.putNextEntry(entry);
          int count = 0;
          count = origin.read(data, 0, BUFFER);
          while (count != -1) {
            out.write(data, 0, count);
            count = origin.read(data, 0, BUFFER);
          }
        } finally {
          out.closeEntry();
          if (origin != null) {
            origin.close();
          }
          if (fi != null) {
            fi.close();
          }
        }
      }
    } finally {
      if (out != null) {
        out.close();
      }
      if (stream != null) {
        stream.close();
      }
    }
  }

}
