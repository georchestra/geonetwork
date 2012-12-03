package org.fao.geonet.kernel.security;

import javax.servlet.http.HttpServletRequest;

import org.springframework.security.web.authentication.preauth.RequestHeaderAuthenticationFilter;

public class GeorchestraSecurityHeaderAuthFilter extends
		RequestHeaderAuthenticationFilter {
	String rolesHeader = "sec-roles";

	@Override
	protected Object getPreAuthenticatedCredentials(HttpServletRequest request) {
		String roleString = request.getParameter(this.rolesHeader);
		if(roleString == null) {
			return "RegisteredUser";
		}
		String[] roles = roleString.split(",");
		
		String highestPriority = roles[0];
		int highestVal = calcValue(highestPriority);
		for (int i = 1; i < roles.length; i++) {
			String role = roles[i];
			int value = calcValue(role);
			if (value < highestVal) {
				highestPriority = role;
				highestVal = value;
			}
		}
		
		return highestPriority;
	}
	
	private int calcValue (String role) {
		if ("SV_ADMIN".equalsIgnoreCase(role)) {
			return 0;
		} else if ("SV_UserAdmin".equalsIgnoreCase(role)) {
			return 1;
		} else if ("SV_Reviewer".equalsIgnoreCase(role)) {
			return 2;
		} else if ("SV_Editor".equalsIgnoreCase(role)) {
			return 3;
		} else if ("SV_RegisteredUser".equalsIgnoreCase(role)) {
			return 4;
		} else if ("SV_Guest".equalsIgnoreCase(role)) {
			return 5;
		} else if ("SV_Monitor".equalsIgnoreCase(role)) {
			return 6;
		}
		return 100;
	}
	
	@Override
	public void setCredentialsRequestHeader(String credentialsRequestHeader) {
		super.setCredentialsRequestHeader(credentialsRequestHeader);
		this.rolesHeader = credentialsRequestHeader;
	}
}
