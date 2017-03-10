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

import java.util.LinkedList;
import java.util.List;

public class Containment {
	private APlanarSymbol outer;
	private APlanarSymbol inner;
	private List<APlanarSymbol> potentialOuters;
	
	public Containment() {
		this.potentialOuters = new LinkedList<APlanarSymbol>();
	}
	public APlanarSymbol getOuter() {
		return outer;
	}
	public void setOuter(APlanarSymbol outer) {
		this.outer = outer;
	}
	public APlanarSymbol getInner() {
		return inner;
	}
	public void setInner(APlanarSymbol inner) {
		this.inner = inner;
	}
	public List<APlanarSymbol> getPotentialOuters() {
		return potentialOuters;
	}
	public void setPotentialOuters(List<APlanarSymbol> potentialOuters) {
		this.potentialOuters = potentialOuters;
	}
	
	
}
