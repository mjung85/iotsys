/*******************************************************************************
 * Copyright (c) 2013
 * Institute of Computer Aided Automation, Automation Systems Group, TU Wien.
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 * 3. Neither the name of the Institute nor the names of its contributors
 *    may be used to endorse or promote products derived from this software
 *    without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE INSTITUTE AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED.  IN NO EVENT SHALL THE INSTITUTE OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS
 * OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT
 * LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY
 * OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * 
 * This file is part of the IoTSyS project.
 ******************************************************************************/

package at.ac.tuwien.auto.iotsys.gateway.util;

import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import org.openexi.schema.EXISchema;
import org.openexi.scomp.EXISchemaFactory;
import org.openexi.scomp.EXISchemaFactoryErrorHandler;
import org.openexi.scomp.EXISchemaFactoryException;
import org.xml.sax.InputSource;

/**
 * Helper class to create a EXI schema from the XSD schema.
 */
public class ExiSchemaHelper {
	public static void main(String[] args) {
	
			fromXSDtoESD("res/obix.xsd", "res/obix.esd");
	
	}

	public static void fromXSDtoESD(String xsdFileName, String esdFileName)  {
		FileInputStream fis=null;
		InputSource is;
		EXISchema schema;
		EXISchemaFactory factory;
		FileOutputStream fos = null;
		DataOutputStream dos = null;

		try{
			fis = new FileInputStream(xsdFileName);
			is = new InputSource(fis);
			// Process a new schema.
			factory = new EXISchemaFactory();
			EXISchemaFactoryExceptionHandlerSample esfe = new EXISchemaFactoryExceptionHandlerSample();
			factory.setCompilerErrorHandler(esfe);
			schema = factory.compile(is);
			// Write the results to a file.
			fos = new FileOutputStream(esdFileName);
			dos = new DataOutputStream(fos);
			schema.writeOut(dos);
			fos.close();
			dos.close();
			fis.close();
		}		
		catch(Exception e){
			e.printStackTrace();
		}
		
	}
}

class EXISchemaFactoryExceptionHandlerSample implements
		EXISchemaFactoryErrorHandler {
	public EXISchemaFactoryExceptionHandlerSample() {
		super();
	}

	public void warning(EXISchemaFactoryException eXISchemaFactoryException)
			throws EXISchemaFactoryException {
		eXISchemaFactoryException.printStackTrace();
	}

	public void error(EXISchemaFactoryException eXISchemaFactoryException)
			throws EXISchemaFactoryException {
		eXISchemaFactoryException.printStackTrace();
	}

	public void fatalError(EXISchemaFactoryException eXISchemaFactoryException)
			throws EXISchemaFactoryException {
		eXISchemaFactoryException.printStackTrace();
	}
}
