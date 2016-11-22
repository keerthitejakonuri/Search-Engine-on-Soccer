<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
<%@ page import="java.io.*,java.util.*"%>
<%@ page import="java.io.BufferedReader"%>
<%@ page import="utd.edu.ir.*"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>Soccer Search Engine</title>
<link href="bootstrap.min.css" rel="stylesheet">
<link href="1.css" rel="stylesheet" type="text/css">
<link href="2.css" rel="stylesheet" type="text/css">

<link rel="stylesheet"
	href="http://maxcdn.bootstrapcdn.com/bootstrap/3.3.5/css/bootstrap.min.css">
<script src="jquery.min.js"></script>

<script type="text/javascript">
	$(function() {
		$("#keywords_input").focus();

		$("form input").keypress(function(e) {
			if ((e.which && e.which == 13) || (e.keyCode && e.keyCode == 13)) {
				var search = $("#keywords_input").val();
				if (search == "") {
					alert("Search Query is Missing");
					return false;
				}

				$('#my_form').submit();
				//return false;
			}
		});

		$("#search_button").click(function(e) {
			var search = $("#keywords_input").val();
			if (search == "") {
				alert("Search Query is Missing");
				return false;
			}
			$('#my_form').submit();

		});

	});
</script>


<style type="text/css">
.imageStyle {
	background: url(soccer.jpg) no-repeat center center fixed;
	-webkit-background-size: cover;
	-moz-background-size: cover;
	-o-background-size: cover;
	background-size: cover;
}

table {
	width: 100%;
}

table tr:nth-child(even) {
	background-color: #eee;
}

table tr:nth-child(odd) {
	background-color: #fff;
}

table th {
	color: blue;
	background-color: white;
}
</style>
</head>
<body>

	<br />




	<header class="header wrapper" id="header">

	<div class="col-s-16">

		<a class="logo logo--header brl" href="#"> <img
			aria-label="Search Home" src="fifa-logo.jpg">
		</a>


		<section class="header-navigation hom"> <section
			class="login-navigation" id="login-navigation">

		<h2 align="center">Soccer Search Engine</h2>



		</section> </section>



		<!-- end header-navigation -->
	</div>
	<!-- end col-s-16 --> </header>

	<div class="fix-position"></div>
	<div id="resp-search-container" class="search-box-area"></div>
	<div class="wrapper bd-txt-bg">

		<div class="h-city-main-en p-relative row"
			style="background-position: 0 0; background-image: url('soccer.jpg');">
			<h1 class="h-city-home-title"></h1>
			<div class="dark-mask-absolute"></div>


			<div class="logged-in-home-search" style="opacity: 0.9;">


				<div id="search_main_container" class="full_search wrapper plr20 "
					style="margin-left: 200px">
					<div id="search_bar_wrapper" class="search_bar" role="form">
						<form id="my_form" method="POST" action="MainController"
							method="POST">
							<div class=" col-l-10 col-s-16 col-m-12 plr0i">
								<div id="keywords_container" class="col-s-16">
									<div id="keywords_pretext">
										<div class="k-pre-1 hidden"
											style="height: 100%; overflow: hidden; display: none;">
											<span class="search-bar-icon mr10" data-icon="Æ¹"></span>

											<div class="keyword_placeholder">
												<div class="keyword_div">Search</div>
											</div>
										</div>
										<div class="k-pre-2  w100" style="display: inline-block;">
											<span class="glyphicon glyphicon-search"></span> <label
												id="label_search_res" class="hdn">Search for
												restaurant, cuisine or a dish</label> <input id="keywords_input"
												class="discover-search" name="query" placeholder="Search">

										</div>
									</div>

								</div>
							</div>
							<div class=" col-l-2 col-s-16 col-m-2 plr0i">
								<div role="button" aria-flowto="search-start" tabindex="0"
									id="search_button" class="left ttupper btn btn--red">
									Search</div>
								<div class="hidden search-button-loading left">
									<img width="20" alt=""
										src="https://c.zmtcdn.com/images/loading-transparent.gif">
								</div>
							</div>

						</form>

						<div class="clear"></div>
					</div>

				</div>
				<div class="clear"></div>
			</div>

		</div>
	</div>

	<%
	    String q = (String) request.getAttribute("resp");
	%>
	<%
	    String expandedQuery = (String) request.getAttribute("expandedQuery");
	%>
	<%
	    if (q != null && !("".equals(q))) {
	%>
	<h3 align="center">
		<b>Your query is: <%=q%></b>
	</h3>
	<%
	    }
	%>
	<br>
	<%
	    if (expandedQuery != null && !("".equals(expandedQuery))) {
	%>
	<h3 align="center">
		<b>Your expanded query is: <%=expandedQuery%></b>
	</h3>
	<%
	    }
	%>


	<!--  google and bing results    -->
	<br />
	<%
	    ArrayList<String> googleSearchList = (ArrayList<String>) request.getAttribute("googleSearchList");
	%>
	<%
	    ArrayList<String> bingList = (ArrayList<String>) request.getAttribute("bingList");
	%>
	<%
	    List<RankDoc> rankDoc = (List<RankDoc>) request.getAttribute("rankDoc");
	%>
	<%
	    List<RankDoc> rankPage = (List<RankDoc>) request.getAttribute("rankPage");
	%>
	<%
	    List<RankDoc> rankTopicPage = (List<RankDoc>) request.getAttribute("rankTopicPage");
	%>
	<%
	    List<RankDoc> rankHub = (List<RankDoc>) request.getAttribute("rankHub");
	%>
	<%
	    List<RankDoc> rankAuthority = (List<RankDoc>) request.getAttribute("rankAuthority");
	%>
	<%
	    List<RankDoc> rankQuery = (List<RankDoc>) request.getAttribute("rankQuery");
	%>
	<%
	    List<String> clusterResult = (List<String>) request.getAttribute("clusterResult");
	%>



	<%
	    int index1 = 0;
	%>
	<%
	    String[] a = null;
	%>
	<%
	    String[] b = null;
	%>
	<%
	    int max = 0;
	%>
	<%
	    if (rankDoc != null)
	        max = Math.min(Math.min(Math.min(Math.min(rankDoc.size(), rankPage.size()), rankTopicPage.size()),
	                rankHub.size()), rankAuthority.size());
	%>
	<%
	    if ((googleSearchList != null && googleSearchList.size() >= 50)
	            && (bingList != null && bingList.size() >= 50)) {
	%>
	<!--   <h3 align="center">Google Results</h3>   -->
	<table align="center" cellpadding="10" cellspacing="10" border="1"
		width="80%">
		<tr>
			<th>Top</th>
			<th>Google Results</th>
			<th>Bing Results</th>
			<th>Without PageRank</th>
			<th>RankPage</th>
			<th>RankTopicPage</th>
			<th>RankHub</th>
			<th>RankAuthority</th>
			<th>Expanded Query</th>

		</tr>
		<%
		    for (int i = 0; i < max; i++) {
		            String data = "";
		            String data2 = "";
		            a = googleSearchList.get(i).split(" ");
		            for (int j = 1; j < a.length; j++) {
		                data += a[j] + " ";
		            }
		            b = bingList.get(i).split(" ");
		            for (int k = 1; k < b.length; k++) {
		                data2 += b[k] + " ";
		            }
		%>

		<tr>
			<td><%=index1 + 1%></td>
			<td><a href='<%=a[0]%>' target="_blank"><%=data%></a></td>
			<td><a href='<%=b[0]%>' target="_blank"><%=data2%></a></td>
			<td><a href='<%=rankDoc.get(i).url%>' target="_blank"><%=rankDoc.get(i).title%></a>
			</td>
			<td><a href='<%=rankPage.get(i).url%>' target="_blank"><%=rankPage.get(i).title%></a>
			</td>
			<td><a href='<%=rankTopicPage.get(i).url%>' target="_blank"><%=rankTopicPage.get(i).title%></a>
			</td>
			<td><a href='<%=rankHub.get(i).url%>' target="_blank"><%=rankHub.get(i).title%></a>
			</td>
			<td><a href='<%=rankAuthority.get(i).url%>' target="_blank"><%=rankAuthority.get(i).title%></a>
			</td>
			<%
			    if (i < rankQuery.size()) {
			%>
			<td><a href='<%=rankQuery.get(i).url%>' target="_blank"><%=rankQuery.get(i).title%></a>
			</td>
			<%
			    } else {
			%>
			<td></td>
			<%
			    }
			%>

		</tr>
		<%
		    index1++;
		%>
		<%
		    }
		%>
	</table>

	<%
	    }
	%>
	<br />

	<!--  Clusters   -->


	<%
	    int index2 = 0;
	%>
	<%
	    if (clusterResult != null) {
	%>
	<h3 align="center">
		<b>Clustering Results Shown: </b>
	</h3>
	<table align="center" cellpadding="10" cellspacing="10" border="1"
		width="80%">
		<tr>
			<th>Top</th>
			<th>Related Results</th>
		</tr>

		<%
		    for (int i = 0; i < clusterResult.size(); i++) {
		%>
		<tr>
			<td><%=index2 + 1%></td>
			<td><a href='<%=clusterResult.get(i)%>' target="_blank"><%=clusterResult.get(i)%></a>
			</td>
			<%
			    index2++;
			%>
		</tr>
		<%
		    }
		%>

	</table>
	<%
	    }
	%>
	<br />

</body>
</html>