			<%@page import="java.text.SimpleDateFormat"%>
<%@page import="java.util.Calendar"%>
<%@page import="java.util.Date"%>
<%@page import="za.co.willemvisser.wpvhomecontroller.config.dto.XbeeConfigDeviceDTO"%>
<%@page import="za.co.willemvisser.wpvhomecontroller.xbee.XbeeController"%>
<%@page import="za.co.willemvisser.wpvhomecontroller.config.dto.XbeeConfigDTO"%>
<%@page import="com.rapplogic.xbee.api.XBeeAddress64"%>
<%@page import="java.util.HashMap"%>

	<%
	    	final SimpleDateFormat sdf = new SimpleDateFormat("MM/dd HH:mm:ss");
	%>
		
		<div class="menu_box">
		    		 	 <h3>Xbee Status</h3>
		    		 	   <div class="menu_box_list">
					      		<ul>
					      		<%					      							      		
					      		Calendar oneMinuteBack = Calendar.getInstance();
					      		oneMinuteBack.add(Calendar.MINUTE, -1);
					      		
					      		HashMap<XBeeAddress64, XbeeConfigDTO> xbeeMap = XbeeController.INSTANCE.getXbeeDeviceMap();
					      		for (XbeeConfigDTO xbeeConfigDTO : xbeeMap.values()) {
					      			%>
				      				
				      				
						      		<div class="<%
						      			if (xbeeConfigDTO.getLastSync() != null && xbeeConfigDTO.getLastSync().after(oneMinuteBack.getTime()) ) {
						      				out.print("simplemenu_green");					      					
						      			} else {
						      				out.print("simplemenu_black");
						      			}
						      		%>
						      		"><h4><%=xbeeConfigDTO.getName() %> (
						      			<% 
				      							if (xbeeConfigDTO.getLastSync() != null) {
				      								out.println(sdf.format(xbeeConfigDTO.getLastSync()) );
				      							} else {
				      								out.println("n/a");
				      							}
						      			%>
						      		)</h4></div>
						      							      				
				      				
				      				<%
					      		}
					      		%>
					    		</ul>
					      </div>
		    	</div>