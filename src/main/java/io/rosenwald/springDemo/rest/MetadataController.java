package io.rosenwald.springDemo.rest;

import java.io.IOException;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import io.rosenwald.springDemo.DemoApplication;

/**
 * REST Controller with endpoints that provide metadata on this deployment. Currently only provides the build version.
 * 
 * TODO: Provide better exception handling. 
 * 
 * @author Nathaniel Rosenwald
 *
 */
@RestController
public class MetadataController {
	
	private static final Logger logger = LoggerFactory.getLogger(DemoApplication.class);

	private String buildVersion;
	
	{
		Properties props = new Properties();
		
		try {
			props.load(new ClassPathResource("application.properties").getInputStream());;
			buildVersion = props.getProperty("build.version");
		} catch (IOException e) {
			logger.error("Failed to retrieve manifest attributes.");
			e.printStackTrace();
		}
	}
	
	@GetMapping("/version")
	public ResponseEntity<ApplicationMetadata> getVersion() {
		ApplicationMetadata meta = new ApplicationMetadata();
		meta.setBuildVersion(buildVersion);
		ResponseEntity<ApplicationMetadata> response = new ResponseEntity<ApplicationMetadata>(meta, HttpStatus.OK);
		return response;
	}
	
	private class ApplicationMetadata {
		private String buildVersion;

		@SuppressWarnings("unused")
		public String getBuildVersion() {
			return buildVersion;
		}

		public void setBuildVersion(String buildVersion) {
			this.buildVersion = buildVersion;
		}
	}
}
