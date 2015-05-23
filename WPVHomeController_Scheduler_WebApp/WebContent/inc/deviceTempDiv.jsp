			<%@page import="za.co.willemvisser.wpvhomecontroller.config.dto.XbeeConfigDeviceDTO"%>
<%@page import="za.co.willemvisser.wpvhomecontroller.xbee.XbeeController"%>
<%@page import="za.co.willemvisser.wpvhomecontroller.config.dto.XbeeConfigDTO"%>
<%@page import="com.rapplogic.xbee.api.XBeeAddress64"%>
<%@page import="java.util.HashMap"%>
<div class="menu_box">
		    		 	 <h3>Temperatures</h3>
		    		 	   <div class="menu_box_list">
					      		<ul>
					      		<%					      							      		
					      		HashMap<XBeeAddress64, XbeeConfigDTO> xbeeMap = XbeeController.INSTANCE.getXbeeDeviceMap();
					      		for (XbeeConfigDTO xbeeConfigDTO : xbeeMap.values()) {
					      			for (XbeeConfigDeviceDTO xbeeConfigDeviceDTO : xbeeConfigDTO.getDeviceList()) {
					      				if (xbeeConfigDeviceDTO.getType().equals(XbeeConfigDeviceDTO.TYPE_TEMPERATURE)) {
						      				%>
							      			<div id="<%=xbeeConfigDeviceDTO.getId()%>" class="simplemenu_black">
							      				<h4 id="<%=xbeeConfigDeviceDTO.getId()%>"><%							      					
							      					out.print(xbeeConfigDeviceDTO.getName());
							      					out.print(" (");
							      					if (xbeeConfigDeviceDTO.getAnalogValue() != null) {
							      						out.print(xbeeConfigDeviceDTO.getAnalogValue().intValue()/10.0);
							      					} else {
							      						out.print("n/a");
							      					}
							      					out.print(" °C)");							      					
							      				%></h4></div>							      				
							      			<%
					      				}
					      			}
					      		}
					      		%>
					    		</ul>
					      </div>
		    	</div>
		    	
		    	