import java.net.MalformedURLException;

/**
 * A class designed to make fetching the results of different HTTP operations
 * easier. This particular class handles the HEAD operation.
 * 
 * @see HTTPFetcher
 * @see HTMLFetcher
 * @see HeaderFetcher
 */
public class HeaderFetcher extends HTTPFetcher {

	/**
	 * Initializes this fetcher. Must call {@link #fetch()} to actually start
	 * the process.
	 * 
	 * @param url
	 *            - the link to fetch from the webserver
	 * @throws MalformedURLException
	 *             if unable to parse URL
	 */
	public HeaderFetcher(String url) throws MalformedURLException {
		super(url);
	}

	/**
	 * Crafts the HTTP HEAD request from the URL.
	 * 
	 * @return HTTP request
	 */
	@Override
	protected String craftRequest() {
		String host = this.getURL().getHost();
		String resource = this.getURL().getFile().isEmpty() ? "/" : this
				.getURL().getFile();

		StringBuffer output = new StringBuffer();
		output.append("HEAD " + resource + " HTTP/1.1\n");
		output.append("Host: " + host + "\n");
		output.append("Connection: close\n");
		output.append("\r\n");

		return output.toString();
	}

	public static void main(String[] args) throws MalformedURLException {
		new HeaderFetcher("http://www.cs.usfca.edu").fetch();
	}
}