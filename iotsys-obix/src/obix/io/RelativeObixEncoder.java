package obix.io;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.util.logging.Logger;

import obix.Obj;

public class RelativeObixEncoder extends ObixEncoder
{
	private static final Logger log = Logger.getLogger(RelativeObixEncoder.class.getName());

	private URI rootUri, baseUri;
	private String globalPrefix = "//localhost/";

	public static String toString(Obj obj, URI rootUri, URI baseUri)
	{
		return toString(obj, rootUri, baseUri, Obj.DEFAULT_LANGUAGE);
	}

	public static String toString(Obj obj, URI rootUri, URI baseUri, String language)
	{
		try
		{
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			RelativeObixEncoder encoder = new RelativeObixEncoder(out, rootUri, baseUri);
			encoder.encode(obj, language);
			encoder.flush();
			encoder.close();
			return new String(out.toByteArray());
		}
		catch (IOException e)
		{
			throw new RuntimeException(e.toString());
		}
	}

	public RelativeObixEncoder(OutputStream out, URI rootUri, URI baseUri) throws IOException
	{
		super(out);

		try
		{
			rootUri = rootUri.normalize();
			while (rootUri.getPath().endsWith("/"))
				rootUri = URI.create(rootUri.getPath().substring(0, rootUri.getPath().length() - 1));

			if (rootUri.getPath().isEmpty())
				rootUri = URI.create("/");
		}
		catch (IllegalArgumentException e)
		{
			log.warning("Invalid rootUri: " + rootUri);
			rootUri = URI.create("/");
		}

		try
		{
			baseUri = baseUri.normalize();
			while (baseUri.getPath().endsWith("/"))
				baseUri = URI.create(baseUri.getPath().substring(0, baseUri.getPath().length() - 1));

			if (baseUri.getPath().isEmpty())
				baseUri = URI.create("/");
		}
		catch (IllegalArgumentException e)
		{
			log.warning("Invalid baseUri: " + baseUri);
			baseUri = URI.create("/");

		}

		this.rootUri = rootUri;
		this.baseUri = baseUri;
	}

	@Override
	protected String encodedHref(Obj obj)
	{
		// no href
		if (obj.getHref() == null || obj.getHref().get().isEmpty())
			return null;

		String fullPath = obj.getFullContextPath();
		URI uri = URI.create(fullPath);

		// href relativized to base
		String href = baseUri.relativize(uri).getPath();

		// relative href can be returned
		if (href.isEmpty())
		{
			href = rootUri.relativize(uri).getPath();
			if (getIndentation() == 0 && !href.endsWith("/"))
				href += "/";
			return "/" + href;
		}

		if (!href.startsWith("/"))
		{
			if (getIndentation() == 0 && !href.endsWith("/"))
				href += "/";
			return href;
		}

		if (rootUri.getPath().equals("/"))
		{
			return href;
		}

		uri = URI.create(href);
		uri = rootUri.relativize(uri);

		if (uri.getPath().isEmpty())
			return "/";

		return globalPrefix + uri.getPath();
	}

	public URI getRootUri()
	{
		return rootUri;
	}

	public void setRootUri(URI rootUri)
	{
		this.rootUri = rootUri;
	}

	public URI getBaseUri()
	{
		return baseUri;
	}

	public void setBaseUri(URI baseUri)
	{
		this.baseUri = baseUri;
	}

	public String getGlobalPrefix()
	{
		return globalPrefix;
	}

	public void setGlobalPrefix(String globalPrefix)
	{
		this.globalPrefix = globalPrefix;
	}
}
