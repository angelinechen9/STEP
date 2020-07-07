<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="en">
  <head>
    <meta charset="UTF-8">
    <title>Angeline Chen</title>
    <link rel="stylesheet" href="https://fonts.googleapis.com/css?family=Roboto:300,400,500,700|Material+Icons">
    <link rel="stylesheet" href="https://unpkg.com/bootstrap-material-design@4.1.1/dist/css/bootstrap-material-design.min.css" integrity="sha384-wXznGJNEXNG1NFsbm0ugrLFMQPWswR3lds2VeinahP8N0zJw9VWSopbjv2x7WCvX" crossorigin="anonymous">
    <link rel="stylesheet" href="style.css">
    <link rel="stylesheet" href="https://use.fontawesome.com/releases/v5.2.0/css/all.css" integrity="sha384-hWVjflwFxL6sNzntih27bfxkr27PmbbK/iSvJ+a4+0owXq79v+lsFkW54bOGbiDQ" crossorigin="anonymous">
    <script src="https://code.jquery.com/jquery-3.3.1.slim.min.js" integrity="sha384-q8i/X+965DzO0rT7abK41JStQIAqVgRVzpbzo5smXKp4YfRvH+8abtTE1Pi6jizo" crossorigin="anonymous"></script>
    <script src="https://unpkg.com/popper.js@1.12.6/dist/umd/popper.js" integrity="sha384-fA23ZRQ3G/J53mElWqVJEGJzU0sTs+SvzG8fXVWP+kJQ1lwFAOkcUOysnlKJC33U" crossorigin="anonymous"></script>
    <script src="https://unpkg.com/bootstrap-material-design@4.1.1/dist/js/bootstrap-material-design.js" integrity="sha384-CauSuKpEqAFajSpkdjv3z9t8E7RlpJ1UP0lKM/+NdtSarroVKu069AlsRPKkFBz9" crossorigin="anonymous"></script>
    <script src="script.js"></script>
  </head>
  <body>
    <nav class="navbar navbar-expand-lg navbar-light bg-white">
        <a class="nav-link" href="/index.html">Home</a>
        <c:if test="${loginStatus == false}">
            <a class="nav-link" href="${loginUrl}">Login</a>
        </c:if>
        <c:if test="${loginStatus == true}">
            <a class="nav-link" href="${logoutUrl}">Logout</a>
        </c:if>
    </nav>
    <main>
        <c:if test="${loginStatus == true}">
            <%-- form that creates a comment --%>
            <form action="/data" method="POST">
                <div class="form-group">
                    <label for="nickname">Nickname</label>
                    <input class="form-control" name="nickname" value="${nickname}" readonly>
                    <a class="btn btn-primary" href="/nickname">Change Nickname</a>
                    <br>
                    <label for="comment">Comment</label>
                    <textarea class="form-control" name="comment" cols="50" rows="10"></textarea>
                    <br>
                    <input class="btn btn-primary" type="submit" value="Submit">
                </div>
            </form>
        </c:if>
        <c:if test="${comments.size() > 0}">
            <p>Comments</p>
            <div class="tab-content">
                <div class="tab-pane fade show active" id="content1" role="tabpanel" aria-labelledby="tab1">
                    <ul class="list-group list-group-flush">
                        <c:forEach varStatus="i" var="comment" items="${comments}">
                            <c:if test="${(i.index >= 0) && (i.index < 5)}">
                                <li class="list-group-item list-group-item-light">
                                    <p class="mb-1"><c:out value="${comment.getComment()}" /></p>
                                    <small><c:out value="${comment.getNickname()}" /></small>
                                    <form action="/delete-data?id=${comment.getId()}" method="POST">
                                        <button class="btn btn-primary" type="submit">
                                            <i class="far fa-trash-alt"></i>
                                        </button>
                                    </form>
                                </li>
                            </c:if>
                        </c:forEach>
                    </ul>
                </div>
                <% int count = 2; %>
                <c:forEach varStatus="i" begin="5" end="${comments.size()}" step="5">
                    <div class="tab-pane fade" id="content<%= count %>" role="tabpanel" aria-labelledby="tab<%= count %>">
                        <ul class="list-group list-group-flush">
                            <c:forEach varStatus="j" var="comment" items="${comments}">
                                <c:if test="${(j.index >= i.index) && (j.index < i.index + 5)}">
                                    <li class="list-group-item list-group-item-light">
                                        <p class="mb-1"><c:out value="${comment.getComment()}" /></p>
                                        <small><c:out value="${comment.getNickname()}" /></small>
                                        <form action="/delete-data?id=${comment.getId()}" method="POST">
                                            <button class="btn btn-primary" type="submit">
                                                <i class="far fa-trash-alt"></i>
                                            </button>
                                        </form>
                                    </li>
                                </c:if>
                            </c:forEach>
                        </ul>
                    </div>
                    <% count++; %>
                </c:forEach>
            </div>
            <ul class="nav nav-tabs pagination justify-content-center" role="tablist">
                <li class="page-item"><a class="page-link" id="tab1" data-toggle="tab" href="#content1" role="tab" aria-controls="content1" aria-selected="true">1</a></li>
                <c:if test="${comments.size() > 5}">
                    <% for (int i = 2; i < count; i++) { %>
                        <li class="page-item"><a class="page-link" id="tab<%= i %>" data-toggle="tab" href="#content<%= i %>" role="tab" aria-controls="content<%= i %>" aria-selected="false"><%= i %></a></li>
                    <% } %>
                </c:if>
            </ul>
        </c:if>
    </main>
  </body>
</html>
