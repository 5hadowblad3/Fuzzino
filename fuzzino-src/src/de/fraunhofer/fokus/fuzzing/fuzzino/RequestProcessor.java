//   Copyright 2012-2013 Fraunhofer FOKUS
//
//   Licensed under the Apache License, Version 2.0 (the "License");
//   you may not use this file except in compliance with the License.
//   You may obtain a copy of the License at
//
//       http://www.apache.org/licenses/LICENSE-2.0
//
//   Unless required by applicable law or agreed to in writing, software
//   distributed under the License is distributed on an "AS IS" BASIS,
//   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//   See the License for the specific language governing permissions and
//   limitations under the License.
package de.fraunhofer.fokus.fuzzing.fuzzino;

import java.io.Serializable;
import java.util.Iterator;
import java.util.UUID;

import de.fraunhofer.fokus.fuzzing.fuzzino.exceptions.DeleteRequestProcessorFailedException;
import de.fraunhofer.fokus.fuzzing.fuzzino.heuristics.ComposedFuzzingHeuristic;
import de.fraunhofer.fokus.fuzzing.fuzzino.request.java.CloseRequest;
import de.fraunhofer.fokus.fuzzing.fuzzino.request.java.CommonRequest;
import de.fraunhofer.fokus.fuzzing.fuzzino.response.java.CommonResponse;
import de.fraunhofer.fokus.fuzzing.fuzzino.response.java.ResponseFactory;
import de.fraunhofer.fokus.fuzzing.fuzzino.response.java.WarningsSection;
import de.fraunhofer.fokus.fuzzing.fuzzino.util.ValidationResult;

/**
 * This abstract, generic class provides basic methods for type-specific request processors and
 * is a skeleton implementation
 * 
 * @author Martin Schneider (martin.schneider@fokus.fraunhofer.de)
 *
 * @param <T> Specifies the type of the fuzzed values that will be enventually generated by the RequestProcessor implementation.
 */
@SuppressWarnings("serial")
public abstract class RequestProcessor<T> extends ComposedFuzzingHeuristic<T> implements Serializable {

	/**
	 * A unique id that identifies this request processor. It is given in a response, used for serialization of the request
	 * processor, and can be used to request further values after an initial request.
	 */
	protected UUID id;
	/**
	 * Stores the iterator that is return for requesting values. It enables to request further values after
	 * serialization an deserialization.
	 */
	protected Iterator<FuzzedValue<T>> iterator;
	/**
	 * The maximal number of values to be returned by this RequestProcessor.
	 */
	protected transient int maxValues;
	/**
	 * Contains the warning for the response to a malformed request.
	 */
	protected transient WarningsSection warningsPart;
	
	/**
	 * @return A type-specific request that is processed by this RequestProcessor.
	 */
	public abstract CommonRequest getRequest();
	
	/**
	 * Sets the request to be processed by this RequestProcessor. 
	 * 
	 * @param request The request to be processed.
	 * @throws IllegalArgumentException This exception is thrown if the given {@code request} expects another type 
	 *                                  than the one that is processed by the type-specific RequestProcessor. 
	 */
	public abstract void setRequest(CommonRequest request) throws IllegalArgumentException;
	
	/**
	 * @return The response to a request generated by this RequestProcessor.
	 */
	public abstract CommonResponse getResponse();

	/**
	 * Deletes a serialized request processor when a request was closed by {@link CloseRequest}.
	 * 
	 * @throws DeleteRequestProcessorFailedException If the request could not be deleted, e.g. if the file is missing.
	 */
	public abstract void delete() throws DeleteRequestProcessorFailedException;
	
	/**
	 * Creates a new instance for a request that is identified by a given ID.
	 * 
	 * @param request The request to be processed by the request processor.
	 * @param id The ID that identifies the request (necessary for continued requests).
	 */
	public RequestProcessor(CommonRequest request, UUID id) {
		super(request.getSeed());
		request.setId(id.toString());
		maxValues = request.getMaxValues();
		this.id = id;
		setRequest(request);
		addRequestedHeuristics();
	}

	/**
	 * Instantiates and adds the fuzzing heuristics that are specified in the request.
	 */
	protected void addRequestedHeuristics() {
		addRequestedGenerators();
		addRequestedOperators();
	}

	/**
	 * Builds the response to the request and returns it.
	 * 
	 * @return The response to that request this RequestProcessor is created for.
	 */
	public CommonResponse buildResponse() {
		ValidationResult requestValidationResult = getRequest().validate();
		CommonResponse commonResponse = buildResponseHeader();
		if (requestValidationResult.isValid()) {
			buildResponseContents();
		}
		
		if (requestValidationResult.hasWarnings()) {
			getWarningsPart().add(requestValidationResult.getWarnings());
		}

		return commonResponse;
	}

	/**
	 * Continues a previously created request in order to obtain further values.
	 * 
	 * @param continuedRequest The continued request referring to an initial request using {@code continuedRequest.getId()}.
	 */
	public void continueRequest(CommonRequest continuedRequest) {
		// check if continuedRequest is valid by name and id
		if (!continuedRequest.getName().equals(getRequest().getName()) || !(continuedRequest.getId().equals(id.toString()))) {
			throw new IllegalArgumentException("The continued request is not a continuation of the stored one.");
		}

		maxValues = continuedRequest.getMaxValues();
	}

	/**
	 * Builds the header of the response consisting of the name provided by the request, an id generated by the 
	 * {@link RequestDispatcher} and the used seed.
	 * 
	 * @return The partial build response.
	 */
	protected CommonResponse buildResponseHeader() {
		CommonResponse commonResponse = getResponse();
		commonResponse.setName(getRequest().getName());
		commonResponse.setId(id.toString());
		commonResponse.setSeed(getSeed());
		
		return commonResponse;
	}
	
	/**
	 * Builds the contents of the response that consists of the fuzzed values generated by the requested fuzzing heuristics.
	 */
	protected abstract void buildResponseContents();

	/**
	 * Retrieves the list of generators from a request and adds them to this RequestProcessor.
	 */
	protected abstract void addRequestedGenerators();

	/**
	 * Retrieves the list of operators from a request and adds them to this RequestProcessor.
	 */
	protected abstract void addRequestedOperators();

	/**
	 * @return A {@link WarningsSection} instance. When called first, it returns a new, empty instance of WarningsSection.
	 */
	protected WarningsSection getWarningsPart() {
		if (warningsPart == null) {
			warningsPart = ResponseFactory.INSTANCE.createWarnings();
		}
		return warningsPart;
	}

	/**
	 * @return The ID of this RequestProcessor as a {@link UUID}.
	 */
	public UUID getId() {
		return id;
	}

	@Override
	public String getName() {
		return getRequest().getName();
	}
	
	/**
	 * Save this request processor including its state to a file.
	 */
	public abstract void serialize();
	
	
	@Override
	public String toString() {
		return "[RequestProcessor name:" + getName() + " request:" + getRequest() + "]";
	}

}
