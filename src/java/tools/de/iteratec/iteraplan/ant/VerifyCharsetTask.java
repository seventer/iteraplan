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
/*
# * iteraplan is an IT Governance web application developed by iteratec, GmbH
 * Copyright (C) 2008 iteratec, GmbH
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
 * You can contact iteratec GmbH headquarters at Inselkammerstraße 4
 * 82008 München - Unterhaching, Germany, or at email address info@iteratec.de.
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
package de.iteratec.iteraplan.ant;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CodingErrorAction;
import java.nio.charset.MalformedInputException;
import java.nio.charset.UnmappableCharacterException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.FileSet;
import org.apache.tools.ant.types.Resource;

/**
 * A simple Ant task that verifies for all passed FileSets that files are in the proper encoding.
 * Accepts the parameter <code>encoding</code> and nested <code>&lt;fileset&gt;</code> elements.
 */
public class VerifyCharsetTask extends Task {
  private String encoding = "UTF-8";
  private List<FileSet> fileSets = new ArrayList<FileSet>();

  public String getEncoding() {
    return encoding;
  }

  public void setEncoding(String encoding) {
    this.encoding = encoding;
  }

  public void addConfiguredFileset(FileSet filesForVerification) {
    fileSets.add(filesForVerification);
  }

  @Override
  @SuppressWarnings("rawtypes")
  public void execute() {
    
    CharsetDecoder charsetDecoder = Charset.forName(encoding).newDecoder();
    charsetDecoder.onMalformedInput(CodingErrorAction.REPORT);
    charsetDecoder.onUnmappableCharacter(CodingErrorAction.REPORT);
    
    boolean allEncodingsCorrect = true;
    
    for (FileSet fileset : fileSets) {
      for (Iterator it = fileset.iterator(); it.hasNext() ; ) {
        Resource file = (Resource) it.next();
        log("Verifying " + encoding + " encoding of " + file.getName(), Project.MSG_DEBUG);

        try {
          allEncodingsCorrect |= isCorrectlyEncoded(file, charsetDecoder);
          
        } catch (IOException ioe) {
          throw new BuildException("An unexpected error occurred", ioe);
        }
      }
    }
    
    if (!allEncodingsCorrect) {
      throw new BuildException("Found at least one file with incorrect encoding. Check the log messages.");
    }
  }
  
  private boolean isCorrectlyEncoded(Resource resource, CharsetDecoder charsetDecoder) throws IOException {
    InputStream fileStream = resource.getInputStream();
    Reader inputReader = new InputStreamReader(fileStream, charsetDecoder);
    LineNumberReader lineReader = new LineNumberReader(inputReader);
    char[] charBuf = new char[1024];
    
    try {
      while (lineReader.ready()) {
        int charsRead = lineReader.read(charBuf);  // NOPMD don't care about the return value, but findbugs says I should
      }
    } catch (MalformedInputException mie) {
      log("Found invalid byte sequence for encoding " + encoding + " in " + resource.getName(), Project.MSG_ERR);
      log("Invalid byte sequence was found around line " + lineReader.getLineNumber() + ". Last buffer contents were:\n" + String.valueOf(charBuf), mie, Project.MSG_DEBUG); // NOPMD
      
      return false;
    } catch (UnmappableCharacterException uce) {
      log("Found invalid byte sequence for encoding " + encoding + " in " + resource.getName(), Project.MSG_ERR);
      log("Invalid byte sequence was found around line " + lineReader.getLineNumber() + ". Last buffer contents were:\n" + String.valueOf(charBuf), uce, Project.MSG_DEBUG); // NOPMD
      
      return false;
    } finally {
      lineReader.close();
    }
    
    return true;
  }
  
}
