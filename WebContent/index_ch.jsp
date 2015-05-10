<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%
	String path = request.getContextPath();
	String basePath = request.getScheme() + "://"
			+ request.getServerName() + ":" + request.getServerPort()
			+ path + "/";
%>
<%
	String contextPath = request.getContextPath();
%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta charset="UTF-8">
<meta name="viewport"
	content="width=device-width, initial-scale=1.0, user-scalable=no">
<title>GDUFS Search Engine</title>
<link rel="stylesheet" type="text/css"
	href="./Hostrocket_files/css/bootstrap.min.css">
<link rel="stylesheet" type="text/css"
	href="./Hostrocket_files/css/bootstrap-theme.min.css">
<link rel="stylesheet" type="text/css"
	href="./Hostrocket_files/style.css">
	<link rel="stylesheet" type="text/css"
	href="./Hostrocket_files/css/index.css">
</head>

<body onload="init();">
<head>

</head>
<!--header start-->

	<div id="header" class="container-fluid row1">
	    <div class="container">
		<!-- title and logo area start -->
			<div class="title1">
   				 <table id="table_placement">
     			   <tr><td><br></td>
      			       <td><br></td>
      			       <td><br></td>
      			       <td><br><a href="index.jsp">English</a>  |  中文</td>
      			  </tr>
      			  <tr height="80px">
        		    <td><div class="form-top">广外搜索引擎</div></td>
        		    <td width="78%"></td>
					<td rowspan="2" width="2%">   <div class="logo_placement">
					        <img src="./Hostrocket_files/image/logo3.png" alt="#" align="right" style="margin-left: 0px; border="0px">
				        </div></td>
				    <td width="20%"></td>    
				        </tr>
       			 <tr id="header_bg">
       			 <td></td>
       			 <td></td>
       			 <td></td>
       			 </tr>
        
   </table>

</div>
<!-- title and logo area end -->
		
		   
<!-- Main part start -->

<!--Search start-->
	<div id="signup" class="container-fluid row2">
		<div class="container">
			<div class="row">
				<div class="col-md-6 col-sm-6 form-holder">
					<form action="<%=contextPath%>/search_ch" id="demoform"
						method="get">
						<div class="form-items-holder">
							<input type="text" name="reqStr" placeholder="请输入" value="<s:property value="reqStr" />">
							<input type="submit" value="搜索">
						</div>
					</form>
				</div>
				<div class="col-md-6 col-sm-6" style="padding-top: 50px;">
					<p class="big-text">
<!--  						<span style="color: #423852;">Search Engine</span><br> <span
							style="color: #999999;">
						</span>                         -->
					</p>
					<p style="font-size: 12px; color: #999999;"></p>
				</div>
			</div>
		</div>
	</div>
<!--search end-->
<!--show all start-->
	<s:if test="subResultPageList != null">
		<div id="trynow" class="container-fluid row3">
			<div class="container">
				<div class="row">
					<div class="col-md-6 col-sm-6 col-xs-12 trynow">
						<p class="result_count">
							共搜到 <span style="color: #ff0000"><s:property
									value="resultCount" /></span> 条结果
						</p>
						<br />
						<s:iterator value="subResultPageList" var="reulstl">
							<p class="result_title">
								<a target="_blank" href="<s:property value="#reulstl.url" />"><s:property
										value="#reulstl.title" escape="false" /></a>
							</p>
							<p class="result_body">
								<s:property value="#reulstl.body" escape="false" />
							</p>
							<p class="result_source">
								<a target="_blank" href="<s:property value="#reulstl.url" />"><s:property
										value="#reulstl.url" /></a>
							</p>
							<br />
						</s:iterator>
						<s:if test="hasPrev">
							<a href="<%=contextPath%>/search_ch?reqStr=<s:property value="reqStr" />&currentPage=1">首页</a>
							<a href="<%=contextPath%>/search_ch?reqStr=<s:property value="reqStr" />&currentPage=<s:property value="currentPage-1"/>">上一页</a>
						</s:if>
						<s:iterator value="viewPageNos"  var="viewPageNo">
						      <s:if test="#viewPageNo == currentPage">
						          <s:property value="currentPage" />
						      </s:if>
						      <s:else>
						          <a href="<%=contextPath%>/search_ch?reqStr=<s:property value="reqStr" />&currentPage=<s:property value="#viewPageNo"/>"><s:property value="#viewPageNo" /></a>
                              </s:else>
						</s:iterator>
                        <s:if test="hasNext">
                            <a href="<%=contextPath%>/search_ch?reqStr=<s:property value="reqStr" />&currentPage=<s:property value="currentPage+1"/>">下一页</a>
                            <a href="<%=contextPath%>/search_ch?reqStr=<s:property value="reqStr" />&currentPage=<s:property value="pageCount"/>">末页</a>
                        </s:if>
                        <br/>
                        <br/>
                        <br/>
                        <br/>
					</div>
				</div>
			</div>
		</div>
	</s:if>
	<!--shwo all end-->
<!-- Main part end -->
			
			<div class="row">
				<div class="col-md-7 col-sm-7 col-xs-8" style="padding-top: 50px;">
					<p class="big-text">
						<span style="color: #423852;">更快</span><br> <span
							style="color: #ffffff;">广外搜索引擎<br> 您好<br>
							欢迎使用
						</span>
					</p>
					<p class="small-text">更快更专注的 &amp; 广外搜索引擎</p>
					<br> <br>

				</div>
				 <div class="col-md-5 col-sm-5 col-xs-4">
					<canvas id="canvas" width="820px" height="350px"></canvas>
				</div> 
			</div>

		</div>
	</div>
	<!--header end-->
	
	
	<!--authority start
	<div id="testimonials" class="container-fluid row6">
		<div class="container">
			<div class="row">
				<div class="col-md-12 row-title2">
					<div>
						<span class="text">Authors</span><span class="dot">.</span>
					</div>
				</div>
			</div>
			<div class="row">
				<div class="col-md-12">
					<div id="myCarousel" class="carousel slide" data-ride="carousel">
						<!-- Indicators 
						<ol class="carousel-indicators">
							<li data-target="#myCarousel" data-slide-to="0" class="active"></li>
							<li data-target="#myCarousel" data-slide-to="1" class=""></li>
						</ol>

						<!-- Wrapper for slides
						<div class="carousel-inner" role="listbox">
							<div class="item active">
								<div class="testimonial">
									<img src="./Hostrocket_files/icoflag.png"
										style="height: 105px; width: 105px;" alt="">
									<div class="testimonial-title">Chao Jun Yang</div>
									<div class="testimonial-subtitle">GDUFS</div>
									<div class="testimonial-details">Brief introduction</div>
								</div>
							</div>

							<div class="item next">
								<div class="testimonial">
									<img src="./Hostrocket_files/icoflag.png"
										style="height: 105px; width: 105px;" alt="">
									<div class="testimonial-title">Amy Y Jiang</div>
									<div class="testimonial-subtitle">GDUFS</div>
									<div class="testimonial-details">Brief introduction</div>
								</div>
							</div>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
authority end -->
	<!--footer start
	<div id="footer" class="container-fluid row8">
		<div class="container">
			<div class="row">
				<div class="col-md-6 col-sm-6 col-xs-12 footer-left">
					All Rights reserved. 2015 Â© <strong>Chao Jun & Amy</strong>.
				</div>

			</div>
		</div>
	</div>-->
	<script src="./Hostrocket_files/jquery-1.11.2.min.js"></script>
	<script src="./Hostrocket_files/bootstrap.min.js"></script>

	<script src="./Hostrocket_files/easeljs-0.6.0.min.js"></script>
	<script src="./Hostrocket_files/tweenjs-0.4.0.min.js"></script>
	<script src="./Hostrocket_files/movieclip-0.6.0.min.js"></script>
	<script src="./Hostrocket_files/preloadjs-0.3.0.min.js"></script>
	<script src="./Hostrocket_files/rocketanimation.js"></script>
	<!--[if lt IE 9]>
<script src="http://html5shim.googlecode.com/svn/trunk/html5.js"></script>
<![endif]-->
	<!--footer end-->
	<script>
		"use strict";
		var canvas, stage, exportRoot;

		function init() {
			canvas = document.getElementById("canvas");
			images = images || {};

			var manifest = [ {
				src : "images/cloud.png",
				id : "cloud"
			}, {
				src : "images/rocket.png",
				id : "rocket_1"
			} ];

			var loader = new createjs.LoadQueue(false);
			loader.addEventListener("fileload", handleFileLoad);
			loader.addEventListener("complete", handleComplete);
			loader.loadManifest(manifest);
		}

		function handleFileLoad(evt) {
			if (evt.item.type == "image") {
				images[evt.item.id] = evt.result;
			}
		}

		function handleComplete() {
			exportRoot = new lib.rocket();

			stage = new createjs.Stage(canvas);
			stage.addChild(exportRoot);
			stage.update();

			createjs.Ticker.setFPS(30);
			createjs.Ticker.addEventListener("tick", stage);
		}
	</script>


</body>
</html>