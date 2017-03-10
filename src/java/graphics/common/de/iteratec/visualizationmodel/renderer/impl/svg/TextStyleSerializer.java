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

/**
 * Serializer for textually representing the style of a text.
 * 
 * @author Christian M. Schweda
 *
 * @version 6.0
 */
public class TextStyleSerializer implements ISerializer {

	public Object transform(Object source) throws SerializationException {
		boolean[] style = (boolean[]) source;
		StringBuffer result = new StringBuffer(77);
		if (style[0]) { //bold
			result.append("font-weight = \"bold\" ");
		}
		if (style[1]) { //underlined
			result.append("text-decoration = \"underline\"");
		}
		if (style[2]) { //italic
			result.append("font-slant = \"italic\"");
		}
		return result.toString();	
	}
}