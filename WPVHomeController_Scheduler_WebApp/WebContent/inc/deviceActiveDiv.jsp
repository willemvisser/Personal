			<%@page import="za.co.willemvisser.wpvhomecontroller.config.ConfigController"%>
<%@page import="za.co.willemvisser.wpvhomecontroller.config.dto.XbeeConfigDeviceDTO"%>
<%@page import="za.co.willemvisser.wpvhomecontroller.xbee.XbeeController"%>
<%@page import="za.co.willemvisser.wpvhomecontroller.config.dto.XbeeConfigDTO"%>
<%@page import="com.rapplogic.xbee.api.XBeeAddress64"%>
<%@page import="java.util.HashMap"%>

<div class="menu_box">
		    		 	 
		    		 	 <h3>Tank Level: </h3>	
		    		 	 
		    		 	 <%
		    		 	int cm = -1;
		    		 	long percentage = -1;
		    		 	try {
		    	    		String deviceId = ConfigController.INSTANCE.getGeneralProperty(ConfigController.PROPERTY_WATERTANKLEVEL_ID).getValue();    		
		    	    		XbeeConfigDeviceDTO deviceDTO = XbeeController.INSTANCE.getDeviceWithID(deviceId);    		
		    	    		// deviceDTO.getPortAddress()  // should return 71
		    	    		XbeeConfigDTO xbeeConfigDTO = XbeeController.INSTANCE.getXbeeWithDeviceID(deviceId);
		    	    		cm = xbeeConfigDTO.getRxResponseMap().get(Integer.parseInt(deviceDTO.getPortAddress())); 
		    	    		percentage = (long) ((260.0 - cm) / 260.0 * 100);    		    	    
		    	    	} catch (Exception e) {
		    	    		//Do nothing
		    	    	}
		    		 	
		    		 	String percentageStr = percentage == -1 ? "ERR" : String.valueOf(percentage);
		    		 	 %>
		    		 	 
		    		 	 <div id="bar-1" class="bar-main-container yellow">
						    <div class="wrapPercentage">
						      <div class="bar-percentage" data-percentage="<%=percentageStr%>"> <%=percentageStr%> %</div>
						      <div class="bar-container">
						        <div class="bar" style="width: <%=percentageStr%>%"></div>
						      </div>
						    </div>
						  </div>
		    		 	 
		    		 	 <h3>Active Devices</h3>
		    		 	   <div class="menu_box_list">
					      		<ul>
					      		<%					      							      		
					      		HashMap<XBeeAddress64, XbeeConfigDTO> xbeeMap = XbeeController.INSTANCE.getXbeeDeviceMap();
					      		for (XbeeConfigDTO xbeeConfigDTO : xbeeMap.values()) {
					      			for (XbeeConfigDeviceDTO xbeeConfigDeviceDTO : xbeeConfigDTO.getDeviceList()) {
					      				if (!xbeeConfigDeviceDTO.getType().equals(XbeeConfigDeviceDTO.TYPE_TEMPERATURE)
					      						&& xbeeConfigDeviceDTO.isDigital() && xbeeConfigDeviceDTO.isEnabled()
					      						) {
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
							      						out.print(" °C)");
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
		    	
		    	