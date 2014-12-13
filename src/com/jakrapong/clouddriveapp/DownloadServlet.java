package com.jakrapong.clouddriveapp;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.jakrapong.clouddriveapp.util.GCSUtil;

@SuppressWarnings("serial")
public class DownloadServlet extends ServletBase {

	@Override
	protected void execute(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		UserService userService = UserServiceFactory.getUserService();
		User currentUser = userService.getCurrentUser();

		resp.setContentType("application/octet-stream");
		resp.setHeader("Content-Disposition", "attachment; filename=\""+req.getParameter("file")+"\"");
		
		GCSUtil.readFile(currentUser.getUserId(),req.getParameter("file"),resp.getOutputStream());
											
	}

}
