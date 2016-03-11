import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletHandler;
import org.eclipse.jetty.servlet.ServletHolder;

public class HTTPHeaderServer {

	public static final int PORT = 8080;

	/**
	 * Starts a Jetty server on port 8080, and maps /check requests to the
	 * {@link HeaderServlet}.
	 * 
	 * @param args
	 *            - unused
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {

		Server server = new Server(PORT);

		ServletHandler handler = new ServletHandler();
		handler.addServletWithMapping(new ServletHolder(new HeaderServlet()),
				"/check");

		server.setHandler(handler);
		server.start();
		server.join();
	}

	/**
	 * Returns the HTTP headers for a URL, or null if unable to connect to the
	 * URL.
	 */
	public static String getHeaders(String link) {
		URL url = null;
		StringBuilder buffer = new StringBuilder();

		try {
			System.out.println(link);
			url = new URL(link);
		} catch (MalformedURLException e) {
			System.out.println("No Valid URL Entered. " + link);
		}
		try (Socket socket = new Socket(url.getHost(), 80);
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(socket.getInputStream()));
				PrintWriter writer = new PrintWriter(socket.getOutputStream());) {
			HTMLFetcher fetcher = new HTMLFetcher(link);
			boolean head = true;
			String request = fetcher.craftRequest();

			writer.println(request);
			writer.flush();

			String line = reader.readLine();

			while (line != null) {
				if (head) {
					buffer.append(line);
					buffer.append("\n");
					if (line.trim().isEmpty()) {
						head = false;
					}
				}
				line = reader.readLine();
			}
		} catch (Exception ex) {
			System.out.println("No valid URL. " + link);
		}
		return buffer.toString();
	}

	/**
	 * Servlet to GET handle requests to /check.
	 */
	// @SuppressWarnings("serial") ??
	public static class HeaderServlet extends HttpServlet {

		/**
		 * Displays a form where users can enter a URL to check. When the button
		 * is pressed, submits the URL back to /check as a GET request.
		 * 
		 * If a URL was included as a parameter in the GET request, fetch and
		 * display the HTTP headers of that URL.
		 */
		@Override
		protected void doGet(HttpServletRequest request,
				HttpServletResponse response) throws ServletException,
				IOException {

			response.setContentType("text/html");
			response.setStatus(HttpServletResponse.SC_OK);

			PrintWriter out = response.getWriter();
			out.printf("<html>%n");
			out.printf("<head><title>%s</title></head>%n", "Link Checker");
			out.printf("<body>%n");

			out.printf("<h1>HeaderServer</h1>%n%n");

			printForm(request, response);

			String url = request.getParameter("url");
			out.printf("<pre>" + getHeaders(url) + "%n</pre>");

			out.printf("</body>%n");
			out.printf("</html>%n");

			response.setStatus(HttpServletResponse.SC_OK);
		}
	}

	private static void printForm(HttpServletRequest request,
			HttpServletResponse response) throws IOException {

		PrintWriter out = response.getWriter();
		out.printf("<form method=\"get\" action=\"%s\">%n",
				request.getServletPath());
		out.printf("<table cellspacing=\"0\" cellpadding=\"2\"%n");
		out.printf("<tr>%n");
		out.printf("\t<td nowrap>url:</td>%n");
		out.printf("\t<td>%n");
		out.printf("\t\t<input type=\"text\" name=\"url\" maxlength=\"100\" size=\"60\">%n");
		out.printf("\t</td>%n");
		out.printf("</tr>%n");
		out.printf("</table>%n");
		out.printf("<p>Please Enter A Link To Check</p>%n%n");
		out.printf("<p><input type=\"submit\" value=\"Check Link\"></p>\n%n");
		out.printf("</form>\n%n");
	}
}