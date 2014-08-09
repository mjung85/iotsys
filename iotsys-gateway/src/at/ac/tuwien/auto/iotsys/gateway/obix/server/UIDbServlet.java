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
package at.ac.tuwien.auto.iotsys.gateway.obix.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import at.ac.tuwien.auto.iotsys.commons.persistent.models.User;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author Nam Giang - zang at kaist dot ac dot kr
 *
 */
public class UIDbServlet extends HttpServlet {
	
	private ObixServer os = null;
	private boolean enableAuthen = false;
	private ObjectMapper mapper = new ObjectMapper();

	public UIDbServlet(ObixServer obixServer) {
		this(false, obixServer);
	}

	public UIDbServlet(boolean enableAuthen, ObixServer obixServer) {
		super();
		this.enableAuthen = enableAuthen;
		this.os = obixServer;
	}

	@Override
	public void service(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		if (enableAuthen){
			HttpSession session = req.getSession(true);
			if ((session.getAttribute("authenticated") == null || Boolean
					.parseBoolean(session.getAttribute("authenticated")
							.toString()) != true)) {
				resp.sendRedirect("/");
			}
		}
		super.service(req, resp);
	}
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		
		PrintWriter w = resp.getWriter();

		String resource = req.getPathInfo();
		if (servletMatcher(resource, "/uistorage")){
			Map<String, String> uiStorage = os.getUidb().getUiStorage();
			String uiStorageJson = mapper.writeValueAsString(uiStorage);
			w.println(uiStorageJson);
		} else if (servletMatcher(resource, "/user/[a-zA-Z0-9]+")){
			String name = resource.split("/")[2];
			User u = os.getUidb().getUser(name);
			String userJson = mapper.writeValueAsString(u);
			w.println(userJson);
		}

		w.flush();
		w.close();
	}
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		PrintWriter w = resp.getWriter();
		
		String resource = req.getPathInfo();
		switch (resource) {
		case "/uistorage":
			Map<String, String> uiStorage = null;
			try {
				uiStorage = mapper.readValue(req.getInputStream(), Map.class);
				os.getUidb().updateBulkKeyValue(uiStorage);
				w.println("Server got: " + mapper.writeValueAsString(uiStorage));
			} catch (JsonParseException | JsonMappingException e){
				w.println("Wrong input!");
				return;
			}
			
			break;
		case "/users":
			User u = null;
			try {
				u = mapper.readValue(req.getInputStream(), User.class);
				os.getUidb().addUser(u);
				w.println(mapper.writeValueAsString(u));
			} catch (JsonParseException | JsonMappingException e){
				w.println("something wrong in adding new user");
			}
			break;
		default:
			w.println("NOT FOUND!");
			break;
		}
		
		w.flush();
		w.close();
	}
	
	@Override
	protected void doPut(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		PrintWriter w = resp.getWriter();
		
		String resource = req.getPathInfo();
		
		switch (resource) {
		case "/canvasobjects":
			break;
		default:
			w.println("NOT FOUND!");
			break;
		}
		
		w.flush();
		w.close();
	}
	@Override
	protected void doDelete(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		PrintWriter w = resp.getWriter();
		
		String resource = req.getPathInfo();
		
		switch (resource) {
		case "/canvasobjects":
			break;
		default:
			w.println("NOT FOUND!");
			break;
		}
		
		w.flush();
		w.close();
	}
	
	private String getRequestPayload(HttpServletRequest req) throws IOException{
		StringBuilder sb = new StringBuilder();
		BufferedReader br = req.getReader();
		String str = null;
		while( (str = br.readLine()) != null ){
			sb.append(str);
		}
		return str;
	}
	
	private boolean servletMatcher(String resourcePath, String pattern){
		if (resourcePath.length() == 0)
			return false;
		Pattern p = Pattern.compile(pattern);
		Matcher m = p.matcher(resourcePath);
		return m.matches();
	}
}