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
package de.iteratec.visualizationmodel.layout;

import java.util.Collection;

import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.ListMultimap;

import de.iteratec.visualizationmodel.ASymbol;


/**
 * Represents a rectangular object with give width and height.
 * 
 * @author Christian M. Schweda
 *
 * @version 6.0
 */
public class Bounds {
  private float width;
  private float height;

  /**
   * Default constructor, width=0, height=0
   */
  public Bounds() {
    //width = 0 and height = 0
  }

  /**
   * Constructor setting width and height
   * @param width the object's width
   * @param height the object's height
   */
  public Bounds(float width, float height) {
    this();
    this.width = width;
    this.height = height;
  }

  private Bounds(ASymbol visObj) {
    this(visObj.getWidth(), visObj.getHeight());
  }

  @Override
  public boolean equals(Object obj) {
    if (obj instanceof Bounds) {
      return (((Bounds) obj).width == this.width) && (((Bounds) obj).height == this.height);
    }
    else {
      return false;
    }
  }

  /**
   * Helper method for partitioning a list of planar map symbols into lists of symbols of a given size
   * 
   * @param symbols the planar map symbols to partition
   * 
   * @return a map from possible sizes to lists of symbols of that size
   */
  public static final ListMultimap<Bounds, ASymbol> deriveSymmetryBrokenProblem(Collection<ASymbol> symbols) {
    ListMultimap<Bounds, ASymbol> result = LinkedListMultimap.create();
    for (ASymbol symbol : symbols) {
      result.put(new Bounds(symbol), symbol);
    }

    return result;
  }

  /**
   * Helper method for calculating the minimum total area of the given planar map symbols including spacing.
   * 
   * @param symbols the planar map symbols to consider
   * @param spacing the spacing to take into account
   * 
   * @return the minimum total area needed by the map symbols respecting the spacing
   */
  public static final float deriveTotalArea(Collection<ASymbol> symbols, float spacing) {
    float result = 0f;
    for (ASymbol symbol : symbols) {
      result += (symbol.getWidth() + spacing) * (symbol.getHeight() + spacing);
    }
    return result;
  }

  @Override
  public int hashCode() {
    return Float.floatToIntBits(this.width) ^ Float.floatToIntBits(this.height);
  }

  public float getWidth() {
    return width;
  }

  public float getHeight() {
    return height;
  }
}