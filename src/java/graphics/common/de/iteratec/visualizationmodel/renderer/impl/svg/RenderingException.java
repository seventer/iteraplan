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
 * Class representing an error during the execution of a rendering process.
 * 
 * @author Christian M. Schweda
 *
 * @version 6.0
 */
public final class RenderingException extends Exception {
	
	private static final long serialVersionUID = 1L;

	public RenderingException() {
		super();
	}

	public RenderingException(String message, Throwable cause) {
		super(message, cause);
	}

	public RenderingException(String message) {
		super(message);
	}

	public RenderingException(Throwable cause) {
		super(cause);
	}
}