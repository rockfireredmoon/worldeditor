package oembedcompressor;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Servlet implementation class OEmbedCompressor
 */
public class OEmbedCompressor extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	public OEmbedCompressor() {
		super();
	}
	
	static String esc(String in) {
		return in.replace(":", "\\:");
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		JSONObject obj = readJsonFromUrl(request.getParameter("url"));
		response.setContentType("text/plain");
		PrintWriter pw = response.getWriter();
		pw.println( ( obj.isNull("type") ? "video" : esc(obj.getString("type"))) + ":" +
				    ( obj.isNull("title") ? "" : esc(obj.getString("title"))) + ":" +
				    ( obj.isNull("author") ? "" : esc(obj.getString("author"))) + ":" +
				    ( obj.isNull("provider_name") ? "" : esc(obj.getString("provider_name"))) + ":" +
				    ( obj.isNull("html") ? "" : esc(obj.getString("html"))) );
		pw.flush();
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	}

	public static JSONObject readJsonFromUrl(String url) throws IOException, JSONException {
		return new JSONObject(IOUtils.toString(new URL(url)));
	}
}
