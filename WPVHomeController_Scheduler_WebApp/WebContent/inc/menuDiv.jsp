	
	<%
	String active = request.getParameter("active");
	%>
	
	<div class="wrap">	 
	      <div class="header">
	      	  <div class="header_top">
					  <div class="menu">
						  <a class="toggleMenu" href="#"><img src="images/nav.png" alt="" /></a>
							<ul class="nav">
								<li<%if (active.equals("Home")) {out.write(" class=\"active\"");}%>><a href="index.jsp"><i><img src="images/settings.png" alt="" /></i>Home</a></li>
								<li<%if (active.equals("Events")) {out.write(" class=\"active\"");}%>><a href="events.jsp"><i><img src="images/user.png" alt="" /></i>Events</a></li>
								<li<%if (active.equals("Cameras")) {out.write(" class=\"active\"");}%>><a href="cameras.jsp"><i><img src="images/views.png" alt="" /></i>Cameras</a></li>
								<li<%if (active.equals("Temperatures")) {out.write(" class=\"active\"");}%>><a href="temperatures.jsp"><i><img src="images/mail.png" alt="" /></i>Temperatures</a></li>								
								<li<%if (active.equals("Settings")) {out.write(" class=\"active\"");}%>><a href="settings.jsp"><i><img src="images/settings.png" alt="" /></i>Settings</a></li>
							
						    </ul>
							<script type="text/javascript" src="js/responsive-nav.js"></script>
				        </div>	
					  
		 		      <div class="clear"></div>				 
				   </div>
			</div>	  					     
	</div>