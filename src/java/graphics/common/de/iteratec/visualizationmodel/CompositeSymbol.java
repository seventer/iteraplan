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

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import de.iteratec.visualizationmodel.renderer.AVisualizationObjectVisitor;


public class CompositeSymbol extends ASymbol {

  private List<ASymbol> children = new LinkedList<ASymbol>();

  public List<ASymbol> getChildren() {
    return children;
  }

  public void setChildren(List<ASymbol> children) {
    this.children = children;
  }

  @Override
  public void visit(AVisualizationObjectVisitor visitor) {
    visitor.visit(this);
    for (ASymbol child : children) {
      child.visit(visitor);
    }
    visitor.endSymbol(this);
  }

  @Override
  public float getXpos() {
    float xMin = Float.MAX_VALUE;
    float xMax = Float.MIN_VALUE;

    if (children.size() == 0) {
      return 0f;
    }
    for (ASymbol child : children) {
      float childMinX = child.getXpos() - child.getWidth() / 2;
      if (childMinX < xMin) {
        xMin = childMinX;
      }

      float childMaxX = child.getXpos() + child.getWidth() / 2;
      if (childMaxX > xMax) {
        xMax = childMaxX;
      }
    }
    return xMin + (xMax - xMin) / 2f;
  }

  @Override
  public float getYpos() {
    float yMin = Float.MAX_VALUE;
    float yMax = Float.MIN_VALUE;

    if (children.size() == 0) {
      return 0f;
    }
    for (ASymbol child : children) {
      float childMinY = child.getYpos() - child.getHeight() / 2;
      if (childMinY < yMin) {
        yMin = childMinY;
      }

      float childMaxY = child.getYpos() + child.getHeight() / 2;
      if (childMaxY > yMax) {
        yMax = childMaxY;
      }
    }
    return yMin + (yMax - yMin) / 2f;
  }

  @Override
  public void setXpos(float newXpos) {
    float currentXpos = getXpos();

    if (currentXpos != newXpos) {
      float delta = newXpos - currentXpos;

      for (ASymbol child : children) {
        child.setXpos(delta + child.getXpos());
      }
    }
  }

  @Override
  public void setYpos(float newYpos) {
    float currentYpos = getYpos();

    if (currentYpos != newYpos) {
      float delta = newYpos - currentYpos;

      for (ASymbol child : children) {
        child.setYpos(delta + child.getYpos());
      }
    }
  }

  @Override
  public float getWidth() {
    float xMin = Float.MAX_VALUE;
    float xMax = Float.MIN_VALUE;

    for (ASymbol child : children) {
      float childMinX = child.getXpos() - child.getWidth() / 2;
      if (childMinX < xMin) {
        xMin = childMinX;
      }

      float childMaxX = child.getXpos() + child.getWidth() / 2;
      if (childMaxX > xMax) {
        xMax = childMaxX;
      }
    }

    return Math.max(0, xMax - xMin);
  }

  @Override
  public float getHeight() {
    float yMin = Float.MAX_VALUE;
    float yMax = Float.MIN_VALUE;

    for (ASymbol child : children) {
      float childMinY = child.getYpos() - child.getHeight() / 2;
      if (childMinY < yMin) {
        yMin = childMinY;
      }

      float childMaxY = child.getYpos() + child.getHeight() / 2;
      if (childMaxY > yMax) {
        yMax = childMaxY;
      }
    }

    return Math.max(0, yMax - yMin);
  }

  @Override
  public void setWidth(float width) {
    float currentWidth = getWidth();

    if (currentWidth != width) {
      float delta = width - currentWidth;

      for (ASymbol child : children) {
        child.setWidth(delta + child.getWidth());
      }
    }
  }

  @Override
  public void setHeight(float height) {
    float currentHeight = getHeight();

    if (currentHeight != height) {
      float delta = height - currentHeight;

      for (ASymbol child : children) {
        child.setHeight(delta + child.getHeight());
      }
    }
  }

  public void add(ASymbol child) {
    this.children.add(child);
  }

  public void addAll(Collection<? extends ASymbol> childrenCollection) {
    this.children.addAll(childrenCollection);
  }
}
