package de.fraunhofer.abm.zenodo.impl;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.util.ISO8601DateFormat;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.ObjectMapper;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.mashape.unirest.request.GetRequest;
import com.mashape.unirest.request.HttpRequestWithBody;
import com.mashape.unirest.request.body.RequestBodyEntity;

import de.fraunhofer.abm.domain.ProjectObjectDTO;
import de.fraunhofer.abm.domain.VersionDTO;
import de.fraunhofer.abm.http.client.HttpUtils;
import de.fraunhofer.abm.zenodo.API;
import de.fraunhofer.abm.zenodo.Deposition;
import de.fraunhofer.abm.zenodo.DepositionFile;
import de.fraunhofer.abm.zenodo.FileMetadata;
import de.fraunhofer.abm.zenodo.Metadata;
import de.fraunhofer.abm.zenodo.ZenodoAPI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class ZenodoAPIImpl implements ZenodoAPI {

	private static final transient Logger logger = LoggerFactory.getLogger(ZenodoAPIImpl.class);
	static Map<String, String> header = new HashMap<>();
	 
	
	 private static String baseURL = "";
	 private static String token = "";
			
	 private abstract class MyObjectMapper implements ObjectMapper {
			public abstract <T> T readValue(String value, TypeReference<T> valueType);
		}

	private final MyObjectMapper objectMapper;
	 
	 public ZenodoAPIImpl(String baseURL, String token) {
			this.baseURL = baseURL;
			this.token = token;

			objectMapper = new MyObjectMapper() {
				final ISO8601DateFormat dateFormat = new ISO8601DateFormat() {
					@Override
					public Date parse(String source) throws ParseException {
						if (!source.endsWith("+0000") && !source.endsWith("+00:00"))
							source = source + "+0000";
						return super.parse(source);
					}
				};
				private com.fasterxml.jackson.databind.ObjectMapper jacksonObjectMapper = new com.fasterxml.jackson.databind.ObjectMapper();

				{
					jacksonObjectMapper.setDateFormat(dateFormat);
					jacksonObjectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
				}

				public <T> T readValue(String value, Class<T> valueType) {
					try {
						return jacksonObjectMapper.readValue(value, valueType);
					} catch (IOException e) {
						throw new RuntimeException(e);
					}
				}

				public <T> T readValue(String value, TypeReference<T> valueType) {
					try {
						return jacksonObjectMapper.readValue(value, valueType);
					} catch (IOException e) {
						throw new RuntimeException(e);
					}
				}

				public String writeValue(Object value) {
					try {
						return jacksonObjectMapper.writeValueAsString(value);
					} catch (JsonProcessingException e) {
						throw new RuntimeException(e);
					}
				}
			};
			Unirest.setObjectMapper(objectMapper);
		}
	
	@Override
	public boolean test() {
		GetRequest request = prepareGetRequest(baseURL + API.Deposit.Depositions);
		try {
			HttpResponse<String> response = request.asString();
			if (response.getStatus() == 200)
				return true;
		} catch (UnirestException e) {
		}

		return false;
	}


	@Override
	public Deposition getDeposition(Integer id) {
		GetRequest request = prepareGetRequest(baseURL + API.Deposit.Entity);
		request.routeParam("id", id.toString());
		try {
			HttpResponse<Deposition> response = request.asObject(Deposition.class);
			return response.getBody();
		} catch (UnirestException e) {
			e.printStackTrace();
		}
		return null;
	}


	@Override
	public List<Deposition> getDepositions() {
		ArrayList<Deposition> result = new ArrayList<Deposition>();
		GetRequest request = prepareGetRequest(baseURL + API.Deposit.Depositions);
		try {
			ArrayList<Deposition> response = fromJSON(new TypeReference<ArrayList<Deposition>>() {
			}, request.asString().getBody());
			return response;
		} catch (UnirestException e) {
			e.printStackTrace();
		}
		return null;
	}


	@Override
	public Deposition updateDeposition(Deposition deposition) {
		HttpRequestWithBody request = preparePutRequest(baseURL + API.Deposit.Entity);
		request.routeParam("id", deposition.id.toString());
		try {
			HttpResponse<Deposition> response = request.asObject(Deposition.class);
			return response.getBody();
		} catch (UnirestException e) {
			e.printStackTrace();
		}
		return null;
	}


	@Override
	public void deleteDeposition(Integer id) {
		HttpRequestWithBody request = prepareDeleteRequest(baseURL + API.Deposit.Entity);
		request.routeParam("id", id.toString());
		try {
			HttpResponse<String> response = request.asString();
		} catch (UnirestException e) {
			e.printStackTrace();
		}
	}

	public Deposition createDeposition() throws UnsupportedOperationException, IOException {
		return createDeposition(null);
	}

	@Override
	public Deposition createDeposition(final Metadata m) throws UnsupportedOperationException, IOException {
		HttpRequestWithBody post = preparePostRequest(baseURL + API.Deposit.Depositions);
		ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		String data = "{}";
		if (m != null)
			data = objectMapper.writeValue(new Object() {
				public Metadata metadata = m;
			});

		RequestBodyEntity completePost = post.body(data);
        completePost.getEntity().writeTo(bytes);
      
		try {
			HttpResponse<Deposition> response = completePost.asObject(Deposition.class);
			
			return response.getBody();

		} catch (UnirestException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Created by agupta on 19.11.18. to get the list of files for a particular
	 * deposition
	 */
	@Override
	public List<DepositionFile> getFiles(Integer depositionId) {
		GetRequest request = prepareGetRequest(baseURL + API.Deposit.Files);
		request.routeParam("id", depositionId.toString());
		try {
			ArrayList<DepositionFile> response = fromJSON(new TypeReference<ArrayList<DepositionFile>>() {
			}, request.asString().getBody());
			return response;
		} catch (UnirestException e) {
			e.printStackTrace();
		}
		return null;

	}

	
	@Override
	public DepositionFile uploadFile(String fileName, Integer depositionId) throws UnsupportedOperationException, IOException {
		try {
			HttpResponse<com.mashape.unirest.http.JsonNode> jsonResponse = Unirest.post(baseURL+API.Deposit.Files).routeParam("id", depositionId.toString())
		     		   .header("Authorization", "Bearer "+ token)
					  .header("accept", "application/json")
					  .field("filename", fileName)
					  .field("file", new File("/var/lib/abm/workspace/"+fileName))
					  .asJson();

		} catch (UnirestException e) {
			e.printStackTrace();

		}
       return null;
	}

	public boolean publish(Integer id) {
		HttpRequestWithBody post = preparePostRequest(baseURL + API.Deposit.Publish).routeParam("id", id.toString());

		try {
			final HttpResponse<String> response = post.asString();
		
			if (response.getStatus() == 202)
				return true;
		} catch (UnirestException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	@Override
	public boolean discard(Integer id) {
		HttpRequestWithBody post = preparePostRequest(baseURL + API.Deposit.Discard).routeParam("id", id.toString());

		try {
			final HttpResponse<String> response = post.asString();
			if (response.getStatus() == 201)
				return true;
		} catch (UnirestException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	private <T> T fromJSON(final TypeReference<T> type, final String jsonPacket) {
		T data = null;
		try {
			data = objectMapper.readValue(jsonPacket, type);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return data;
	}
	
	private HttpRequestWithBody preparePostRequest(String url) {
		return Unirest.post(url).header("Content-Type", "application/json").header("Authorization", "Bearer " + token);
	}

	private HttpRequestWithBody preparePostFileRequest(String url) {
		return Unirest.post(url).header("Content-Type", "application/octet-stream").header("Authorization",
				"Bearer " + token);
	}
	
	private GetRequest prepareGetRequest(String url) {
		return Unirest.get(url).header("Authorization", "Bearer " + token);
	}
	
	private HttpRequestWithBody preparePutRequest(String url) {
		return Unirest.put(url).header("Content-Type", "application/json").header("Authorization", "Bearer" + token);
	}

	private HttpRequestWithBody prepareDeleteRequest(String url) {
		return Unirest.delete(url).header("Content-Type", "application/json").header("Authorization",
				"Bearer " + token);
	}

	
	/*
	 * 
	 * @author - Ankur Gupta
	 * @see de.fraunhofer.abm.zenodo.ZenodoAPI#uploadCollectionToZenodo(de.fraunhofer.abm.domain.VersionDTO)
	 */
	@Override
	public Integer uploadCollectionToZenodo(VersionDTO version, String maven_base_url) throws UnsupportedOperationException, IOException {
		
		logger.debug("Starting publishing process on zenodo");
		 String artifactVersion = "";
		 String artifactId = "";
		 String artifactPath = "";
		 String projectUrl = "";
		Metadata collectionData =  new Metadata(Metadata.UploadType.DATASET	,
				new Date(),
				version.name,
				version.collectionId,
				version.id,
				Metadata.AccessRight.CLOSED,
				Metadata.Creator.AUTHOR
				);
		
		logger.info("Creating deposition for version " + version.id);
		
		Deposition deposition = this.createDeposition(collectionData); 
		
		logger.debug("Deposition created successfully for " + deposition.id);
		
		if(deposition.id != null) {
			
			logger.debug("Downloading artifacts for version " + version.id);
		    
			for (ProjectObjectDTO p: version.projects) {
      	      String project = p.project_id.substring(p.project_id.indexOf("maven2/:")+ "maven2/:".length());
      	      artifactId = project.substring(project.indexOf(":")+1,project.indexOf(":", project.indexOf(":")+1));
      	      artifactVersion = project.substring(project.indexOf(":", project.indexOf(":")+1)+1);
      	      artifactPath  = project.substring(0,project.indexOf(":")).replace(".", "/");
      	      projectUrl =   maven_base_url+artifactPath+"/"+artifactId+"/"+artifactVersion+"/"+artifactId+"-"+artifactVersion+".jar";
      	      HttpUtils.downloadJar(projectUrl, new File("/var/lib/abm/workspace/"+artifactId+"-"+artifactVersion+".jar"));
      	      
      	    logger.debug("uploading file " + artifactId+"-"+artifactVersion+".jar");
      	      DepositionFile newArtifact = uploadFile(artifactId+"-"+artifactVersion+".jar", deposition.id);
      	    logger.debug("File uploaded successfully " + artifactId+"-"+artifactVersion+".jar", deposition.id);
      	}
    
		if (publish(deposition.id) == true) {
            System.out.println("Successfully published version " + version.id);		
		Files.deleteIfExists(Paths.get("/var/lib/abm/workspace/"+artifactId+"-"+artifactVersion+".jar"));

		
		 File dir = new File("/var/lib/abm/workspace/");
		 
		    if (dir.listFiles().length == version.projects.size()) {
		    	logger.debug("All the artifacts for the version downloaded successfully " + version.id);
		    }

		    if(getFiles(deposition.id).size() == version.projects.size()) {
		    	logger.debug("All files uploaded successfully for deposition "+  deposition.id);
		    	if (publish(deposition.id) == true) {
		    	   logger.debug("Successfully published version " + version.id);
		    	   for (File file: dir.listFiles()) {
		   		    if (!file.isDirectory()) {
		   		        file.delete(); }}
		    	   
		    	}
		    }
		} 
		}
		return deposition.id;
	}
	
	

}
