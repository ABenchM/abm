package de.fraunhofer.abm.zenodo.impl;

import java.io.IOException;
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


import de.fraunhofer.abm.zenodo.API;
import de.fraunhofer.abm.zenodo.Deposition;
import de.fraunhofer.abm.zenodo.DepositionFile;
import de.fraunhofer.abm.zenodo.FileMetadata;
import de.fraunhofer.abm.zenodo.Metadata;
import de.fraunhofer.abm.zenodo.ZenodoAPI;


@Component
public class ZenodoAPIImpl implements ZenodoAPI {

	
	static Map<String, String> header = new HashMap<>();
	 
	
	 private static String baseURL = "https://sandbox.zenodo.org/";
	 private static String token = "HWiH1QCdIj81fj0a9vB9knBzfH8puk55NXiEZqkumpILavP2BHgKnjgUEyc9";
			
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
		System.out.println("Token value" + token);
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


	@Override
	public Deposition createDeposition(Metadata m) throws UnsupportedOperationException, IOException {
		// TODO Auto-generated method stub
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
	public DepositionFile uploadFile(FileMetadata f, Integer depositionId)
			throws UnsupportedOperationException, IOException {
		// TODO Auto-generated method stub
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

	
	
	

}
