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
package de.iteratec.visualizationmodel.renderer;

import java.util.HashMap;
import java.util.Map;

import de.iteratec.iteraplan.common.Logger;
import de.iteratec.visualizationmodel.renderer.impl.svg.SVGRenderer;

public final class RendererFactory {

	private static final Logger LOGGER = Logger.getLogger(RendererFactory.class);
	
	private static final Map<String, Class<?>> RENDERERS_MAP = new HashMap<String, Class<?>>();
	static {
		// extension + classes implementing renderers
		RENDERERS_MAP.put("svg", SVGRenderer.class);
	}
	
	private RendererFactory() {
	  //Do not instantiate
	}
	
	public static IRenderer getRenderer(String uriExtension) throws RendererNotFoundException {
		try {
			Class<?> rendererClass = RENDERERS_MAP.get(uriExtension.toLowerCase());
			if (rendererClass != null) {
				return (IRenderer) rendererClass.newInstance();
			} else {
				throw new RendererNotFoundException("Did not find a renderer for uriExtension '" + uriExtension + "'");
			}
		} catch (InstantiationException e) {
			LOGGER.error("Could not instanciate renderer", e);
		} catch (IllegalAccessException e) {
			LOGGER.error("Could not instanciate renderer", e);
		}
		return null;
	}
}
