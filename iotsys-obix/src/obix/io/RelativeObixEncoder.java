package obix.io;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;

import obix.Obj;

public class RelativeObixEncoder extends ObixEncoder { 
	private URI rootUri, baseUri;
	private String globalPrefix = "//localhost/";
	
	public static String toString(Obj obj, URI rootUri, URI baseUri) {
		try {
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			RelativeObixEncoder encoder = new RelativeObixEncoder(out, rootUri, baseUri);
			encoder.encode(obj);
			encoder.flush();
			encoder.close();
			return new String(out.toByteArray());
		} catch (IOException e) {
			throw new RuntimeException(e.toString());
		}
	}
	
	public RelativeObixEncoder(OutputStream out, URI rootUri, URI baseUri) throws IOException {
		super(out);
		
		if (baseUri.getPath().isEmpty())
			baseUri = URI.create("/");
		
		while (rootUri.getPath().endsWith("/"))
			rootUri = URI.create(rootUri.getPath().substring(0, rootUri.getPath().length()-1));
		
		while (baseUri.getPath().endsWith("/"))
			baseUri = URI.create(baseUri.getPath().substring(0, baseUri.getPath().length()-1));
		
		if (rootUri.getPath().isEmpty())
			rootUri = URI.create("/");
		
		this.rootUri = rootUri;
		this.baseUri = baseUri;
	}
	
	@Override
	protected String encodedHref(Obj obj) {
		// no href
		if (obj.getHref() == null || obj.getHref().get().isEmpty())
			return null;
		
		
		String fullPath = obj.getFullContextPath();
		URI uri = URI.create(fullPath);
		
		// href relativized to base
		String href = baseUri.relativize(uri).getPath();
		
		// relative href can be returned
		if (href.isEmpty())
			return "/" + rootUri.relativize(uri).getPath();
		
		if (!href.startsWith("/"))
			return href;
		
		if (rootUri.getPath().equals("/")) {
			return href;
		}
		
		uri = URI.create(href);
		uri = rootUri.relativize(uri);
		
		if (uri.getPath().isEmpty()) {
			return "/";
		} else {
			return globalPrefix + uri.getPath();
		}
	}

	
	public URI getRootUri() {
		return rootUri;
	}

	public void setRootUri(URI rootUri) {
		this.rootUri = rootUri;
	}

	public URI getBaseUri() {
		return baseUri;
	}

	public void setBaseUri(URI baseUri) {
		this.baseUri = baseUri;
	}

	public String getGlobalPrefix() {
		return globalPrefix;
	}

	public void setGlobalPrefix(String globalPrefix) {
		this.globalPrefix = globalPrefix;
	}
}
