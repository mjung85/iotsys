/*
  	Copyright (c) 2013 - IotSyS Gateway
 	Institute of Computer Aided Automation, Automation Systems Group, TU Wien.
  	All rights reserved.

    This program is free software; you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation; either version 2 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program; if not, write to the Free Software
    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/
package at.ac.tuwien.auto.iotsys.commons.persistent.models;

/**
 * @author Nam Giang - zang at kaist dot ac dot kr
 *
 */
public class CanvasObject {

	// The objects are identified through the
	// oBIX URI and the x and y position are simple integers
	
	String objUri;
	String objName;
	CanvasObjectPlacement objPlacement;
	
	
	public String getObjUri() {
		return objUri;
	}


	public void setObjUri(String objUri) {
		this.objUri = objUri;
	}


	public String getObjName() {
		return objName;
	}


	public void setObjName(String objName) {
		this.objName = objName;
	}


	public CanvasObjectPlacement getObjPlacement() {
		return objPlacement;
	}


	public void setObjPlacement(CanvasObjectPlacement objPlacement) {
		this.objPlacement = objPlacement;
	}


	private class CanvasObjectPlacement {
		int left, top;

		public int getLeft() {
			return left;
		}

		public void setLeft(int left) {
			this.left = left;
		}

		public int getTop() {
			return top;
		}

		public void setTop(int top) {
			this.top = top;
		}
	}
}
