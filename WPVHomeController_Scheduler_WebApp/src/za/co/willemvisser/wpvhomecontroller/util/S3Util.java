package za.co.willemvisser.wpvhomecontroller.util;

import org.apache.log4j.Logger;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;

public enum S3Util {

	INSTANCE;
	
	static Logger log = Logger.getLogger(S3Util.class.getName());
	
	public static final String BUCKET_WPVHOMESCHEDULER = "wpvhomescheduler";
	
	public void writeStringToBucket(String bucketName, String key, String content) {
		
		AWSCredentials credentials = new ProfileCredentialsProvider().getCredentials();
        
		
		AmazonS3 s3Client = new AmazonS3Client(credentials);
		try {
			s3Client.putObject(bucketName, key, content);			
		} catch (Exception e) {
			log.error("Could not write to S3 bucket", e);
		}
	}
	
	public String getBucketAsString(String bucketName, String key) throws AmazonServiceException, AmazonClientException{
		log.info("Downloading the S3 object: " + bucketName + " \\ " + key);		
		AmazonS3 s3Client = new AmazonS3Client();        
        return s3Client.getObjectAsString(bucketName, key);
        
                    
	}
	
}
