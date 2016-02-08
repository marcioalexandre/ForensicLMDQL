<%@ page session="true" contentType="text/html; charset=ISO-8859-1" %>
<%@ taglib uri="http://www.tonbeller.com/jpivot" prefix="jp" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core" %>

<jp:mondrianQuery id="query01" jdbcDriver="com.mysql.jdbc.Driver" jdbcUrl="jdbc:mysql://localhost:3306/mydb" catalogUri="/WEB-INF/queries/CuboForense2.xml" jdbcUser="marcio" jdbcPassword="Ma@1983" connectionPooling="false">
select
   [entity].[facebook] on columns,
   non empty [element].children on rows
from [XbrlDataMart]
</jp:mondrianQuery>



<!-- 
Válidos:
WITH MEMBER [Measures].[com 10%] 
    AS 'AdicionaPorcentagem([Measures].[valor],10)'
select
  {[Measures].[com 10%]} on columns,
  {[Elemento].[Passivo]} ON rows
from [Cubo Forense]
where {([Tempo].[fevereiro], [Empresa].[Gbarbosa])}

WITH MEMBER [Measures].[com 10%] 
    AS 'AdicionaPorcentagem([Measures].[valor],10)'
select
  {[Measures].[Valor], [Measures].[com 10%]} on columns,
  {([Empresa],[Tempo],[Elemento])} ON rows
from [Cubo Forense]
Where [Tempo]

WITH MEMBER [Measures].[com 10%] 
    AS 'AdicionaPorcentagem([Measures].[valor],10)'
select
  {[Measures].[Valor], [Measures].[com 10%]} on columns,
  {([Empresa], [Elemento])} ON rows
from [Cubo Forense]


WITH MEMBER [Measures].[Unit Sales Plus One] 
    AS 'PlusOne([Measures].[Unit Sales])'
SELECT
    {[Measures].[Unit Sales]} ON COLUMNS,
    {[Gender].MEMBERS} ON ROWS
FROM [Sales]
 -->





<c:set var="title01" scope="session">Test Query - Forensic Mondrian OLAP</c:set>
