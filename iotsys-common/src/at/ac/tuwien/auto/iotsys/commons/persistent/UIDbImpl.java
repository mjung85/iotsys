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
package at.ac.tuwien.auto.iotsys.commons.persistent;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import at.ac.tuwien.auto.iotsys.commons.persistent.models.CanvasObject;

/**
 * @author Nam Giang - zang at kaist dot ac dot kr
 *
 */
public class UIDbImpl implements UIDb {

	private static UIDb INSTANCE;
	private static final Logger log = Logger.getLogger(UIDbImpl.class.getName());

	public static UIDb getInstance(){
		INSTANCE = UIDbRepo.getInstance(); 
		if (INSTANCE == null)
			INSTANCE = new UIDbImpl();
		return INSTANCE;
	}
	
	@Override
	public List<CanvasObject> getCanvasObjects() {
		// TODO Auto-generated method stub
		return new ArrayList<CanvasObject>();
	}

	@Override
	public void deleteCanvasObject(String uri) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void updateCanvasObject(String uri, CanvasObject co) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void addCanvasObject(CanvasObject co) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void addCanvasObjects(List<CanvasObject> cos) {
		// TODO Auto-generated method stub
		
	}

}
