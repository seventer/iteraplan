/*
 * Copyright 2011 Christian M. Schweda & iteratec
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.iteratec.visualizationmodel;

public abstract class APrimitiveSymbol extends ASymbol {

  private String link;
  private String linkTarget;

  /**
   * Gets the link.
   * 
   * @return the primitive's link
   */
  public String getLink() {
    return this.link;
  }

  /**
   * Sets the link.
   * 
   * @param link the primitive's associated link
   */
  public void setLink(String link) {
    this.link = link;
  }

  /**
   * Gets the link target.
   * 
   * @return the primitive's linkTarget
   */
  public String getLinkTarget() {
    return this.linkTarget;
  }

  /**
   * Sets the link target.
   * 
   * @param linkTarget the link target to set
   */
  public void setLinkTarget(String linkTarget) {
    this.linkTarget = linkTarget;
  }
}
