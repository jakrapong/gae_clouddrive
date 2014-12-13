package com.jakrapong.clouddriveapp;

import java.io.IOException;
import java.io.InputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.jakrapong.clouddriveapp.util.GCSUtil;

@SuppressWarnings("serial")
public class UploadServlet extends ServletBase {

	@Override
	protected void execute(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		UserService userService = UserServiceFactory.getUserService();
		User currentUser = userService.getCurrentUser();

		if (req.getMethod().equalsIgnoreCase("post")) {
			try {
				ServletFileUpload upload = new ServletFileUpload();

				FileItemIterator iterator = upload.getItemIterator(req);
				while (iterator.hasNext()) {
					FileItemStream item = iterator.next();
					InputStream stream = item.openStream();

					if (item.isFormField()) {
					} else {
						// log.warning("Got an uploaded file: " +
						// item.getFieldName() +
						// ", name = " + item.getName());

						GCSUtil.saveFile(currentUser.getUserId(),
								item.getName(), stream);
						
						req.setAttribute("msg", "Your file '"+item.getName()+"' has been uploaded!");
					}
				}
			} catch (Exception ex) {
				throw new ServletException(ex);
			}
		}
		req.getRequestDispatcher("/WEB-INF/jsp/home.jsp").forward(req, resp);
	}

}
