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

import de.iteratec.visualizationmodel.HorizontalAlignment;


/**
 * Serializer for textually representing the horizonal alignment of a text.
 * 
 * @author Christian M. Schweda
 *
 * @version 6.0
 */
public class HorizontalAlignmentSerializer implements ISerializer {

	
	public Object transform(Object source) throws SerializationException {
		if (source == HorizontalAlignment.LEFT) {
			return "start";
		} else if (source == HorizontalAlignment.RIGHT) {
			return "end";
		} else {
			return "middle";
		}
	}
}