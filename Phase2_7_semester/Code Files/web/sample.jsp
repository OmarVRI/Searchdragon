<%@page import="com.company.Ranker" %>
<%@ page import="java.util.Vector" %>
<%@ page import="com.company.RankerDbhandler" %>
<%@ page import="com.company.Dbhandler" %>
<%@ page import="com.company.Helper" %>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
         pageEncoding="ISO-8859-1" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
    <title>results</title>
</head>
<body>
<%
    String inq = request.getParameter("search word");
    //here
    Ranker R = new Ranker(inq);
    Vector<Integer> V = R.getResults();
    Dbhandler D = new Dbhandler();
    Helper H = new Helper();
    for (int x : V) {
        String URL = D.getURL(x);
     //   String title = H.getTitle(URL);
        System.out.println(URL);
        //System.out.println(title);
        out.println("<a href=" + URL + ">" + URL + "</a>");
    }

%>
</body>
</html>