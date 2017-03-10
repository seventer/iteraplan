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
package de.iteratec.visualizationmodel.renderer.impl.svg;

import java.util.List;
import java.util.Locale;

import de.iteratec.visualizationmodel.Point;


/**
 * Serializer for textually representing the points of a polyline.
 * 
 * @author Christian M. Schweda
 *
 * @version 6.0
 */
public class PolylinePointsSerializer implements ISerializer {

	@SuppressWarnings({ "unchecked", "boxing" })
	public Object transform(Object source) throws SerializationException {
		if (source==null) {
			return null;
		} else {
			List<Point> points = (List<Point>)source;
			StringBuffer result = new StringBuffer();
			result.append(String.format(Locale.US, "M %.2f %.2f ", points.get(0).getX(), points.get(0).getY()));
			for (int i = 1; i<points.size(); i++) {
				result.append(String.format(Locale.US, "L %.2f %.2f ", points.get(i).getX(), points.get(i).getY()));
			}
			return result.toString();
		}
	}
}