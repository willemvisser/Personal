	<%
	String active = request.getParameter("active");
	%>

					<div class="menu_box">
		    		 	 <h3>Actions</h3>
		    		 	   <div class="menu_box_list">
					      		<ul>
							  		<li<%if (active.equals("Today")) {out.write(" class=\"active\"");}%>><a href="events.jsp" class="account_settings"><span>Today</span></a></li>
							  		<li<%if (active.equals("Add Event")) {out.write(" class=\"active\"");}%>><a href="eventAddSelectType.jsp" class="messages"><span>Add Event</span><div class="clear"></div></a></li>
							  		<li<%if (active.equals("Cancel Event")) {out.write(" class=\"active\"");}%>><a href="#" class="invites"><span>Cancel Current Event</span><div class="clear"></div></a></li>							  		
							  		<li<%if (active.equals("Detail")) {out.write(" class=\"active\"");}%>><a href="irrigationDetail.jsp" class="statistics"><span>Detailed List</span></a></li>
							  		<li<%if (active.equals("DirectEdit")) {out.write(" class=\"active\"");}%>><a href="eventsDirectEdit.jsp" class="account_settings"><span>Direct Edit</span></a></li>						  	
					    		</ul>
					      </div>
		    		 </div>