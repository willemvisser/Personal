			<%@page import="za.co.willemvisser.wpvhomecontroller.config.dto.XbeeConfigDeviceDTO"%>
<%@page import="za.co.willemvisser.wpvhomecontroller.xbee.XbeeController"%>
<%@page import="za.co.willemvisser.wpvhomecontroller.config.dto.XbeeConfigDTO"%>
<%@page import="com.rapplogic.xbee.api.XBeeAddress64"%>
<%@page import="java.util.HashMap"%>
<div class="menu_box">
		    		 	 <h3>Device Status</h3>
		    		 	   <div class="menu_box_list">
					      		<ul>
					      		<%					      							      		
					      		HashMap<XBeeAddress64, XbeeConfigDTO> xbeeMap = XbeeController.INSTANCE.getXbeeDeviceMap();
					      		for (XbeeConfigDTO xbeeConfigDTO : xbeeMap.values()) {
					      			for (XbeeConfigDeviceDTO xbeeConfigDeviceDTO : xbeeConfigDTO.getDeviceList()) {
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
						      					} else {
						      						out.print(xbeeConfigDeviceDTO.getName());
						      						out.print(" (");
						      						if (xbeeConfigDeviceDTO.getAnalogValue() != null) {
						      							out.print(xbeeConfigDeviceDTO.getAnalogValue().intValue()/10.0);
						      						} else {
						      							out.print("n/a");
						      						}
						      						out.print(" �C)");
						      					}
						      				%></h4></div>
						      				<script>
						      					$( "#<%=xbeeConfigDeviceDTO.getId()%>" ).click(function() {
						      						$.get( "xbee/xdevice/<%=xbeeConfigDeviceDTO.getId()%>", function( data ) {
						      							//For now do nothing
						      							// $( ".result" ).html( data );
						      							//alert( "Load was performed." );
						      						});
   												});
						      				</script>
						      			<%
					      			}
					      		}
					      		%>
					    		</ul>
					      </div>
		    	</div>
		    	
		    	