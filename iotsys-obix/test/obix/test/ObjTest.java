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
	private Obj root, obix, about, sensor, sensorRef, val, history, count;
	
	@Before
	public void setUp() {
		root = new Obj();
		root.setHref(new Uri("http://localhost/"));
		
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
		
		history = new Obj("history");
		history.setHref(new Uri("value/history"));
		sensor.add(history);
		
		count = new Int("count");
		history.add(count);
	}
	
	
	@Test
	public void testGetChildByHref() {
		assertEquals(obix, root.getChildByHref(new Uri("obix")));
		assertEquals(obix, root.getChildByHref(new Uri("obix/")));
		assertEquals(obix, root.getChildByHref(new Uri("obix//")));
		assertEquals(sensor, root.getChildByHref(new Uri("sensor/")));
		assertEquals(about,  obix.getChildByHref(new Uri("about")));
	}
	
	@Test
	public void testGetChildByUnknownHrefShouldReturnNull() {
		assertEquals(null, root.getChildByHref(new Uri("none")));
		
		assertEquals(null,  root.getChildByHref(new Uri("/obix")));
		assertEquals(null,  obix.getChildByHref(new Uri("/about")));
		
		assertEquals(null, root.getChildByHref(new Uri("obix/about")));
		assertEquals(null,  obix.getChildByHref(new Uri("sensor")));
	}
	
	@Test
	public void testGetByHref() {
		assertEquals(about, root.getByHref(new Uri("obix/about")));
		assertEquals(about, root.getByHref(new Uri("/obix/about")));
		assertEquals(about, root.getByHref(new Uri("/obix/about/")));
		assertEquals(about, root.getByHref(new Uri("obix/about/")));
		
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
	
	@Test
	public void testGetByCompoundHref() {
		assertEquals(history,   root.getByHref(new Uri("/sensor/value/history")));
		assertEquals(history, sensor.getByHref(new Uri("value/history")));
		assertEquals(history,    val.getByHref(new Uri("history")));
	}
	
	@Test
	public void testGetByHrefChildAbsolute() {
		about.setHref(new Uri("/obix/about"));
		assertEquals(about, root.getByHref(new Uri("/obix/about")));
		assertEquals(about, root.getByHref(new Uri("/obix/about/")));
		
		about.setHref(new Uri("/obix/about/"));
		assertEquals(about, root.getByHref(new Uri("/obix/about")));
		assertEquals(about, root.getByHref(new Uri("/obix/about/")));
		
		obix.setHref(new Uri("/obix"));
		assertEquals(obix,  root.getByHref(new Uri("/obix")));
		assertEquals(about, root.getByHref(new Uri("/obix/about")));
	}
	
	@Test
	public void testGetByHrefWithDotSegments() {
		assertEquals(obix, obix.getByHref(new Uri(".")));
		assertEquals(obix, about.getByHref(new Uri("..")));
		
		assertEquals(about, about.getByHref(new Uri("../about")));
		assertEquals(about, about.getByHref(new Uri("../about/.")));
		assertEquals(sensor, about.getByHref(new Uri("../../sensor/")));
		
		assertEquals(sensor, root.getByHref(new Uri("/obix/../sensor")));
		assertEquals(about, root.getByHref(new Uri("obix/./about")));
		assertEquals(sensor, root.getByHref(new Uri("./sensor")));
		
		assertEquals(null, root.getByHref(new Uri("../sensor")));
		assertEquals(null, about.getByHref(new Uri("../../../../sensor")));
	}
	
	@Test
	public void testGetByUnnormalizedHref() {
		assertEquals(about, about.getByHref(new Uri("../about?query#fragment")));
		assertEquals(sensor, about.getByHref(new Uri("../../sensor/#value")));
		assertEquals(val, obix.getByHref(new Uri("/sensor/value?v=30")));
	}
	
	@Test
	public void testGetByHrefRootless() {
		obix.removeThis();
		sensor.removeThis();
		
		assertEquals(obix, obix.getByHref(new Uri(".")));
		assertEquals(about, obix.getByHref(new Uri("about")));
		assertEquals(history, sensor.getByHref(new Uri("value/history")));
		
		// assumes highest reachable object as root
		assertEquals(sensor, val.getByHref(new Uri("/")));
		assertEquals(history, val.getByHref(new Uri("/value/history")));
	}
	
	
	
	@Test
	public void testGetFullContextPath() {
		assertEquals("/obix", obix.getFullContextPath());
		assertEquals("/sensor/value", val.getFullContextPath());
		assertEquals("/sensor/value/history", history.getFullContextPath());
		assertEquals("/sensor/value/history/", count.getFullContextPath());
	}
	
	@Test
	public void testGetFullContextPathRootless() {
		obix.removeThis();
		sensor.removeThis();
		
		assertEquals("obix", obix.getFullContextPath());
		assertEquals("sensor/value", val.getFullContextPath());
		assertEquals("sensor/value/history", history.getFullContextPath());
		assertEquals("sensor/value/history/", count.getFullContextPath());
	}
	
	@Test
	public void testGetNormalizedHref() {
		assertEquals("http://localhost/obix", obix.getNormalizedHref().get());
		assertEquals("http://localhost/sensor/value", val.getNormalizedHref().get());
		assertEquals("http://localhost/sensor/value/history", history.getNormalizedHref().get());
	}
	
	@Test
	public void testGetNormalizedHrefWithEmptyHrefShouldReturnNull() {
		assertEquals(null, count.getNormalizedHref());
	}
}
