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
public class DeleteServlet extends ServletBase {

	@Override
	protected void execute(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		UserService userService = UserServiceFactory.getUserService();
		User currentUser = userService.getCurrentUser();
		
		GCSUtil.deleteFile(currentUser.getUserId(),req.getParameter("file"));
		
		req.setAttribute("msg", "Your file '"+req.getParameter("file")+"' has been deleted!");
										
		req.getRequestDispatcher("/WEB-INF/jsp/home.jsp").forward(req, resp);
	}

}
