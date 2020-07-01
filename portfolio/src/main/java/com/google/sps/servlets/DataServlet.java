// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.sps.servlets;

import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import com.google.gson.Gson;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.sps.data.Comment;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

/** Servlet that creates and reads comments. */
@WebServlet("/data")
public class DataServlet extends HttpServlet {

    /**
    * Reads comments.
    * @param request the request message
    * @param response the response message
    * @throws IOException if there is an I/O error
    */
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        Query query = new Query("Comment").addSort("timestamp", SortDirection.DESCENDING);
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        PreparedQuery results = datastore.prepare(query);
        List<Comment> comments = new ArrayList<>();
        for (Entity entity : results.asIterable()) {
            Comment comment = new Comment(entity.getKey().getId(), (String) entity.getProperty("nickname"), (String) entity.getProperty("comment"), (long) entity.getProperty("timestamp"));
            comments.add(comment);
        }
        request.setAttribute("comments", comments);
        UserService userService = UserServiceFactory.getUserService();
        if (!userService.isUserLoggedIn()) {
            String loginUrl = userService.createLoginURL("/data");
            request.setAttribute("loginStatus", false);
            request.setAttribute("loginUrl", loginUrl);
        }
        else {
            String nickname = getUserNickname(userService.getCurrentUser().getUserId());
            if (nickname == null) {
                response.sendRedirect("/nickname");
            }
            else {
                String logoutUrl = userService.createLogoutURL("/data");
                request.setAttribute("loginStatus", true);
                request.setAttribute("nickname", nickname);
                request.setAttribute("logoutUrl", logoutUrl);
            }
        }
        RequestDispatcher dispatcher = request.getRequestDispatcher("data.jsp");
        dispatcher.forward(request, response);
    }

    /**
    * Creates comments.
    * @param request the request message
    * @param response the response message
    * @throws IOException if there is an I/O error
    */
    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String nickname = getParameter(request, "nickname", "");
        String comment = getParameter(request, "comment", "");
        long timestamp = System.currentTimeMillis();
        Entity commentEntity = new Entity("Comment");
        commentEntity.setProperty("nickname", nickname);
        commentEntity.setProperty("comment", comment);
        commentEntity.setProperty("timestamp", timestamp);
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        datastore.put(commentEntity);
        response.sendRedirect("/data");
    }

    private String getParameter(HttpServletRequest request, String name, String defaultValue) {
        String value = request.getParameter(name);
        if (value == null) {
            return defaultValue;
        }
        return value;
    }

    private String getUserNickname(String id) {
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        Query query = new Query("UserInfo").setFilter(new Query.FilterPredicate("id", Query.FilterOperator.EQUAL, id));
        PreparedQuery results = datastore.prepare(query);
        Entity entity = results.asSingleEntity();
        if (entity == null) {
            return null;
        }
        String nickname = (String) entity.getProperty("nickname");
        return nickname;
    }
}
