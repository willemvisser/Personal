			<%@page import="za.co.willemvisser.wpvhomecontroller.config.dto.XbeeConfigDeviceDTO"%>
<%@page import="za.co.willemvisser.wpvhomecontroller.xbee.XbeeController"%>
<%@page import="za.co.willemvisser.wpvhomecontroller.config.dto.XbeeConfigDTO"%>
<%@page import="com.rapplogic.xbee.api.XBeeAddress64"%>
<%@page import="java.util.HashMap"%>
<div class="menu_box">
		    		 	 <h3>Quick Switch</h3>
		    		 	   <div class="menu_box_list">
					      		<ul>
					      		<%					      							      		
					      		HashMap<XBeeAddress64, XbeeConfigDTO> xbeeMap = XbeeController.INSTANCE.getXbeeDeviceMap();
					      		for (XbeeConfigDTO xbeeConfigDTO : xbeeMap.values()) {
					      			for (XbeeConfigDeviceDTO xbeeConfigDeviceDTO : xbeeConfigDTO.getDeviceList()) {
					      				if (xbeeConfigDeviceDTO.getId().contains("_LED") || 
					      						xbeeConfigDeviceDTO.getName().equals("Pool Pump")) {
						      				%>
							      			<div id="<%=xbeeConfigDeviceDTO.getId()%>" class="<%
							      				if (xbeeConfigDeviceDTO.isEnabled()) {
							      					out.print("simplemenu_green");					      					
							      				} else {
							      					out.print("simplemenu_black");
							      				}
							      				%>
							      				"><h4 id="<%=xbeeConfigDeviceDTO.getId()%>"><%
							      					if (xbeeConfigDeviceDTO.isDigital()) {
							      						out.print(xbeeConfigDeviceDTO.getName()); 
							      					} 
							      				%></h4></div>
							      				<script>
							      					$( "#<%=xbeeConfigDeviceDTO.getId()%>" ).click(function() {
							      						$.get( "xbee/xdevice/<%=xbeeConfigDeviceDTO.getId()%>", function( data ) {
							      							//For now do nothing as the update will happen on the next refresh
							      						});
	   												});
							      				</script>
							      			<%
					      				}
					      			}
					      		}
					      		%>
					    		</ul>
					      </div>
		    	</div>
		    	
		    	