package obix.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import obix.Int;
import obix.Obj;
import obix.Ref;
import obix.Uri;

import org.junit.Before;
import org.junit.Test;

public class ObjTest {
	private Obj root, obix, about, sensor, sensorRef, val;
	
	@Before
	public void setUp() {
		root = new Obj();
		root.setHref(new Uri("/"));
		
		obix = new Obj("obix");
		obix.setHref(new Uri("obix"));
		root.add(obix);
		
		about = new Obj("about");
		about.setHref(new Uri("about"));
		obix.add(about);
		
		sensor = new Obj("sensor");
		sensor.setHref(new Uri("sensor"));
		root.add(sensor);
		
		sensorRef = new Ref("sensor", new Uri("/sensor"));
		obix.add(sensorRef);
		
		val = new Int("value", 42);
		val.setHref(new Uri("value"));
		sensor.add(val);
	}
	
	
	@Test
	public void testGetChildByHref() {
		assertEquals(obix, root.getChildByHref(new Uri("obix")));
		assertEquals(sensor, root.getChildByHref(new Uri("sensor")));
		assertEquals(about,  obix.getChildByHref(new Uri("about")));
	}
	
	@Test
	public void testGetChildByUnknownHrefShouldReturnNull() {
		assertEquals(null, root.getChildByHref(new Uri("none")));
		assertEquals(null, root.getChildByHref(new Uri("obix/about")));
		assertEquals(null,  obix.getChildByHref(new Uri("sensor")));
	}
	
	@Test
	public void testGetByHref() {
		assertEquals(about, root.getByHref(new Uri("obix/about")));
		assertEquals(null,  root.getByHref(new Uri("obix/sensor")));
		
		assertEquals(sensor,  root.getByHref(new Uri("sensor")));
		assertEquals(val,  root.getByHref(new Uri("sensor/value")));
	}
	
	@Test
	public void testByUnknownHrefShouldReturnNull() {
		assertEquals(null,  root.getByHref(new Uri("obix/sensor")));
		assertEquals(null,  root.getByHref(new Uri("/something")));
		assertEquals(null,  root.getByHref(new Uri("/sensor/history")));
	}
	
	@Test
	public void testGetByAbsoluteHref() {
		assertEquals(about, root.getByHref(new Uri("/obix/about")));
		assertEquals(about, obix.getByHref(new Uri("/obix/about")));
		assertEquals(about, about.getByHref(new Uri("/obix/about")));
		assertEquals(val,   root.getByHref(new Uri("/sensor/value")));
	}
	
	@Test
	public void testGetByHrefWithTrailingSlash() {
		assertEquals(about, root.getByHref(new Uri("/obix/about/")));
		assertEquals(about, obix.getChildByHref(new Uri("about/")));
	}
	
	@Test
	public void testGetByHrefNullHrefShouldReturnNull() {
		assertNull(root.getChildByHref(null));
		assertNull(root.getByHref(null));
		
		assertNull(about.getChildByHref(new Uri("/sensor")));
	}
}
