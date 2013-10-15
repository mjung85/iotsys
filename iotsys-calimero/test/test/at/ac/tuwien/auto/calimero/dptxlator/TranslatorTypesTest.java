/*
    Calimero - A library for KNX network access
    Copyright (C) 2006-2008 W. Kastner

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

package test.at.ac.tuwien.auto.calimero.dptxlator;

import java.util.HashMap;
import java.util.Map;

import at.ac.tuwien.auto.calimero.dptxlator.DPT;
import at.ac.tuwien.auto.calimero.dptxlator.DPTXlator;
import at.ac.tuwien.auto.calimero.dptxlator.DPTXlator2ByteFloat;
import at.ac.tuwien.auto.calimero.dptxlator.DPTXlatorBoolean;
import at.ac.tuwien.auto.calimero.dptxlator.TranslatorTypes;
import at.ac.tuwien.auto.calimero.dptxlator.TranslatorTypes.MainType;
import at.ac.tuwien.auto.calimero.exception.KNXException;
import at.ac.tuwien.auto.calimero.exception.KNXIllegalArgumentException;

import junit.framework.TestCase;

/**
 * @author B. Malinowsky
 */
public class TranslatorTypesTest extends TestCase
{
	private final MainType[] types =
		(MainType[]) TranslatorTypes.getAllMainTypes().values().toArray(new MainType[0]);

	/**
	 * @param name name for test case
	 */
	public TranslatorTypesTest(String name)
	{
		super(name);
	}

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception
	{
		super.setUp();
	}

	/**
	 * Test method for
	 * {@link at.ac.tuwien.auto.calimero.dptxlator.TranslatorTypes#getMainType(int)}.
	 * 
	 * @throws KNXException
	 */
	public final void testGetMainType() throws KNXException
	{
		for (int i = 0; i < 100; ++i) {
			if (TranslatorTypes.getMainType(i) == null
				&& TranslatorTypes.getAllMainTypes().containsKey(new Integer(i)))
				fail("not found but in type list");
		}

		for (int i = 0; i < types.length; ++i) {
			final MainType t = TranslatorTypes.getMainType(types[i].getMainNumber());
			assertEquals(t.getMainNumber(), types[i].getMainNumber());
			t.createTranslator(((DPT) t.getSubTypes().values().iterator().next())
				.getID());
		}
	}

	/**
	 * Test method for
	 * {@link at.ac.tuwien.auto.calimero.dptxlator.TranslatorTypes#getAllMainTypes()}.
	 * 
	 * @throws KNXException
	 */
	public final void testGetAllMainTypes() throws KNXException
	{
		assertTrue(TranslatorTypes.getAllMainTypes().size() > 7);
		final Map m = TranslatorTypes.getAllMainTypes();
		final Map copy = new HashMap(m);
		m.clear();
		assertTrue(TranslatorTypes.getAllMainTypes().isEmpty());
		DPTXlator t = null;
		try {
			t = TranslatorTypes.createTranslator(TranslatorTypes.TYPE_BOOLEAN,
				DPTXlatorBoolean.DPT_BOOL.getID());
			fail("map is empty - should fail");
		}
		catch (final KNXException e) {}
		assertNull(t);

		m.putAll(copy);
		assertFalse(TranslatorTypes.getAllMainTypes().isEmpty());
		try {
			t = TranslatorTypes.createTranslator(TranslatorTypes.TYPE_BOOLEAN,
				DPTXlatorBoolean.DPT_BOOL.getID());
		}
		catch (final KNXException e) {
			fail("map is filled - should not fail");
		}
		assertNotNull(t);

		newMainTypeFail(2000, Object.class);
		newMainTypeFail(2000, DPTXlator.class);
		final MainType mt = new MainType(2000, DPTXlatorBoolean.class, "DPTXlatorBoolean.class");
		TranslatorTypes.getAllMainTypes().put(new Integer(2000), mt);
		assertEquals(TranslatorTypes.getMainType(2000).createTranslator(
			DPTXlatorBoolean.DPT_ENABLE).getClass(), DPTXlatorBoolean.class);
	}

	private void newMainTypeFail(int mainNo, Class cl)
	{
		try {
			new MainType(mainNo, cl, "faulty main type");
			fail("main type illegal arg - should fail");
		}
		catch (final KNXIllegalArgumentException e) {}

	}

	/**
	 * Test method for
	 * {@link at.ac.tuwien.auto.calimero.dptxlator.TranslatorTypes#createTranslator(int, java.lang.String)}.
	 * 
	 * @throws KNXException
	 */
	public final void testCreateTranslator() throws KNXException
	{
		// with main number
		for (int i = 0; i < types.length; i++) {
			final int main = types[i].getMainNumber();
			final String dptID = ((DPT) TranslatorTypes.getMainType(main).getSubTypes()
				.values().iterator().next()).getID();
			TranslatorTypes.createTranslator(main, dptID);
		}

		// without main number
		for (int i = 0; i < types.length; i++) {
			final int main = types[i].getMainNumber();
			final String dptID = ((DPT) TranslatorTypes.getMainType(main).getSubTypes()
				.values().iterator().next()).getID();
			TranslatorTypes.createTranslator(0, dptID);
		}
		
		try {
			TranslatorTypes.createTranslator(0, "123");
			fail("not supported dptID");
		}
		catch (final Exception e) { }
		try {
			TranslatorTypes.createTranslator(0, ".12");
			fail("not supported dptID");
		}
		catch (final Exception e) { }
		
		TranslatorTypes.createTranslator(DPTXlatorBoolean.DPT_ACK);
		TranslatorTypes.createTranslator(DPTXlator2ByteFloat.DPT_HUMIDITY);
		try {
			TranslatorTypes.createTranslator(new DPT("1000.1000", "", "-1", "1"));
			fail("not existant DPT");
		}
		catch (final KNXException e) {}
	}
}
