<%@ page session="true" contentType="text/html; charset=ISO-8859-1" %>
<%@ taglib uri="http://www.tonbeller.com/jpivot" prefix="jp" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core" %>

<jp:mondrianQuery id="query01" jdbcDriver="com.mysql.jdbc.Driver" jdbcUrl="jdbc:mysql://localhost:3306/producao" catalogUri="/WEB-INF/queries/producao.xml" jdbcUser="marcio" jdbcPassword="Ma@1983" connectionPooling="false">
select
   [entity].[facebook] on columns,
   non empty [element].children on rows
from [XbrlDataMart]
where [time].[2013].[12].[31]


</jp:mondrianQuery>

<c:set var="title01" scope="session">Test Query - Forensic Mondrian OLAP</c:set>
