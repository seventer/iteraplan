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

import java.io.Serializable;
import java.util.Comparator;

import de.iteratec.visualizationmodel.AVisualizationObject;

/**
 * Comparator for comparing EObjects holding an id based on their id.
 * 
 * @author Christian M. Schweda
 *
 * @version 6.0
 */
public class IdComparator implements Comparator<AVisualizationObject>, Serializable{

	private static final long serialVersionUID = -6612619693546718818L;

	public int compare(AVisualizationObject o1, AVisualizationObject o2) {
		return compareStatic(o1, o2);
	}

	/**
	 * Compares two EObjects based on their ids.
	 * 
	 * @param o1 the first EObject
	 * @param o2 the second EObject
	 * 
	 * @return -1, if the first EObject has the lower id;0, if both EObjects have the same id; 1, if the first EObject has the higher id
	 */
	public static int compareStatic(AVisualizationObject o1, AVisualizationObject o2) {
		if (o1 == null) {
			if (o2 == null) {
				return 0;
			} else {
				return -1;
			}
		} else {
			if (o2 == null) {
				return 1;
			} else {
				String id1 = o1.getId();
				String id2 = o2.getId();
				
				return id1.compareTo(id2);
			}
		}
	}
}