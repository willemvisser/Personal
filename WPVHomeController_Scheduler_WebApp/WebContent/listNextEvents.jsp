<%@page import="org.quartz.Trigger"%>
<%@page import="za.co.willemvisser.wpvhomecontroller.scheduler.job.dto.JobTriggerDTO"%>
<%@page import="java.util.List"%>
<%@ page import="java.util.Set" %>
<%@ page import="za.co.willemvisser.wpvhomecontroller.scheduler.WPVHomeControllerScheduler" %>
<html>
	<head>
		<title>List Schedule</title>
	</head>
	<body>
		<h2>Schedule</h2>
		<table border="1">
			<thead>
				<tr>
			    	<th>Group</th>
			     	<th>Job Name</th>
			     	<th>Next Fire Time</th>
			  	</tr>
			</thead>
		<%
		List<JobTriggerDTO> jobTriggerList = WPVHomeControllerScheduler.INSTANCE.listJobTriggers();
		for (int i=0; i<jobTriggerList.size(); i++) {
			JobTriggerDTO jobTriggerDTO = jobTriggerList.get(i);
			out.println("<tr><td>" + jobTriggerDTO.getGroupName() + "</td><td>" + 
					jobTriggerDTO.getJobName() + "</td><td>"); 
			
			out.println(jobTriggerDTO.getTrigger().getNextFireTime());
			
			out.println("</td></tr>");
		}
		%>
		</table>
	</body>
</html>