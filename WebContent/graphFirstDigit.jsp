<%-- 
    Document   : graphFirstDigit
    Created on : 27/01/2016, 11:45:35
    Author     : Marcio - marcio.alexandre83@gmail.com
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>

<%
Integer one = Integer.parseInt(request.getParameter("one")==null?"0":request.getParameter("one"));
Integer two = Integer.parseInt(request.getParameter("two")==null?"0":request.getParameter("two"));
Integer three = Integer.parseInt(request.getParameter("three")==null?"0":request.getParameter("three"));
Integer four = Integer.parseInt(request.getParameter("four")==null?"0":request.getParameter("four"));
Integer five = Integer.parseInt(request.getParameter("five")==null?"0":request.getParameter("five"));
Integer six = Integer.parseInt(request.getParameter("six")==null?"0":request.getParameter("six"));
Integer seven = Integer.parseInt(request.getParameter("seven")==null?"0":request.getParameter("seven"));
Integer eight = Integer.parseInt(request.getParameter("eight")==null?"0":request.getParameter("eight"));
Integer nine = Integer.parseInt(request.getParameter("nine")==null?"0":request.getParameter("nine"));

%>
<html>

<head>  
	<script type="text/javascript">
	window.onload = function () {
		var chart = new CanvasJS.Chart("chartContainer", {

			title:{
				text:"LMDQL First Digit Analysis"				

			},
                        animationEnabled: true,
			axisX:{
				interval: 1,
				gridThickness: 0,
				labelFontSize: 10,
				labelFontStyle: "normal",
				labelFontWeight: "normal",
				labelFontFamily: "Lucida Sans Unicode"

			},
			axisY2:{
				interlacedColor: "rgba(1,77,101,.2)",
				gridColor: "rgba(1,77,101,.1)"

			},

			data: [
			{     
				type: "bar",
                name: "companies",
				axisYType: "secondary",
				color: "#014D65",				
				dataPoints: [
				
				{y: <%=one%>, label: "Digit 1"  },
				{y: <%=two%>, label: "Digit 2"  },
				{y: <%=three%>, label: "Digit 3"  },
				{y: <%=four%>, label: "Digit 4"  },
				{y: <%=five%>, label: "Digit 5"  },
				{y: <%=six%>, label: "Digit 6"  },
				{y: <%=seven%>, label: "Digit 7"  },
				{y: <%=eight%>, label: "Digit 8"  },
				{y: <%=nine%>, label: "Digit 9"  }

				]
			}
			
			]
		});

chart.render();
}
</script>
<script type="text/javascript" src="canvasjs.min.js"></script>
</head>
<body>
    
	<div id="chartContainer" style="height: 300px; width: 100%;">
	</div>
</body>
</html>
