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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import de.fraunhofer.fokus.fuzzing.fuzzino.exceptions.DeleteRequestProcessorFailedException;
import de.fraunhofer.fokus.fuzzing.fuzzino.exceptions.UnknownFuzzingHeuristicException;
import de.fraunhofer.fokus.fuzzing.fuzzino.heuristics.generators.IntegerGenerator;
import de.fraunhofer.fokus.fuzzing.fuzzino.heuristics.generators.IntegerGeneratorFactory;
import de.fraunhofer.fokus.fuzzing.fuzzino.heuristics.operators.IntegerOperator;
import de.fraunhofer.fokus.fuzzing.fuzzino.heuristics.operators.IntegerOperatorFactory;
import de.fraunhofer.fokus.fuzzing.fuzzino.request.java.Generator;
import de.fraunhofer.fokus.fuzzing.fuzzino.request.java.NumberRequest;
import de.fraunhofer.fokus.fuzzing.fuzzino.request.java.Operator;
import de.fraunhofer.fokus.fuzzing.fuzzino.request.java.ValidValuesSection;
import de.fraunhofer.fokus.fuzzing.fuzzino.response.java.IllegalGenerator;
import de.fraunhofer.fokus.fuzzing.fuzzino.response.java.IllegalOperator;
import de.fraunhofer.fokus.fuzzing.fuzzino.response.java.IntegerResponse;
import de.fraunhofer.fokus.fuzzing.fuzzino.response.java.ResponseFactory;

public class IntegerRequestProcessor extends NumberRequestProcessor<Long> {
	
	private static final long serialVersionUID = -4895581046104220308L;
	private static final String INTEGER_EXTENSION = ".integerProcessor";

	public IntegerRequestProcessor(NumberRequest request, UUID theUuid) {
		super(request, theUuid);
	}
	
	@Override
	public IntegerResponse getResponse() {
		if (response == null) {
			response = ResponseFactory.INSTANCE.createIntegerResponse();
			buildResponse();
		}
		return (IntegerResponse) response;
	}

	/**
	 * Retrieves the list of generators from the {@link #request} and adds them to
	 * {@link #generators}. 
	 */
	@Override
	protected void addRequestedGenerators() {
		List<Generator> allRequestedGenerators = request.getRequestedGenerators(); 
		if (allRequestedGenerators.isEmpty() && !request.useNoGenerators()) {
			List<IntegerGenerator> allIntegerGenerators = IntegerGeneratorFactory.INSTANCE.createAll(request.getSpecification(), seed);
			heuristics.addAll(allIntegerGenerators);
		} else {
			for (Generator requestedGenerator : allRequestedGenerators) {
				try {
					IntegerGenerator generator = IntegerGeneratorFactory.INSTANCE.create(requestedGenerator.getGeneratorName(), 
							                                                             requestedGenerator.getParameter(), 
							                                                             request.getSpecification(), 
							                                                             seed);
					heuristics.add(generator);
				} catch (UnknownFuzzingHeuristicException e) {
					IllegalGenerator illegalGenerator = ResponseFactory.INSTANCE.createIllegalGenerator(requestedGenerator.getGeneratorName(), "unknown generator");
					getWarningsPart().getIllegalGenerators().add(illegalGenerator);
				}
			}
		}
	}

	@Override
	protected void addRequestedOperators() {
		ValidValuesSection validValuesPart = request.getValidValuesSection();
		List<Long> validValues = parseValidValues();
		List<Operator> allRequestedOperators = (validValuesPart == null ?  Collections.<Operator>emptyList() : validValuesPart.getRequestedOperators());
		if (allRequestedOperators.isEmpty() && !validValues.isEmpty()) {
			List<IntegerOperator> allOperators = IntegerOperatorFactory.INSTANCE.createAll(validValues, request.getSpecification(), seed);
			heuristics.addAll(allOperators);
			return;
		}
		
		for (Operator requestedOperator : allRequestedOperators) {
			String requestedOperatorName = requestedOperator.getOperatorName();
			try {
				IntegerOperator operator = IntegerOperatorFactory.INSTANCE.create(requestedOperatorName, 
						validValues, 
						requestedOperator.getParameter(), 
						request.getSpecification(), 
						seed);
				heuristics.add(operator);
			} catch (UnknownFuzzingHeuristicException e) {
				IllegalOperator illegalOperator = 
						ResponseFactory.INSTANCE.createIllegalOperator(requestedOperatorName, "unknown operator");
				getWarningsPart().getIllegalOperators().add(illegalOperator);
			}
		}
	}
	
	private List<Long> parseValidValues() {
		ValidValuesSection validValues = request.getValidValuesSection();
		List<Long> result = new LinkedList<>();
		if (validValues != null) {
			List<String> stringValues = validValues.getValues();
			for (String str : stringValues) {
				try {
					result.add(Long.valueOf(str));
				} catch (NumberFormatException e) {
					// ignore value
				}
			}
		}
		
		return result;
	}
	
	public static IntegerRequestProcessor loadFromFile(String filename) throws IOException, ClassNotFoundException {
		FileInputStream file = new FileInputStream(filename + INTEGER_EXTENSION);
		ObjectInputStream in = new ObjectInputStream(file);
		IntegerRequestProcessor integerRequestProcessor = (IntegerRequestProcessor) in.readObject();
		in.close();
		file.close();
		return integerRequestProcessor;
	}	

	@Override
	public void delete() throws DeleteRequestProcessorFailedException {
		File serializedProcessor = new File(getRequest().getId() + INTEGER_EXTENSION);
		boolean serializedProcessorExists = serializedProcessor.exists();
		boolean deletionSuccessful = false;
		if (serializedProcessorExists) {
			deletionSuccessful = serializedProcessor.delete();
		}
		if (!serializedProcessorExists || !deletionSuccessful) {
			throw new DeleteRequestProcessorFailedException();
		}
	}

	@Override
	public void serialize() {
		try
		{
			FileOutputStream fos = new FileOutputStream(getId().toString() + INTEGER_EXTENSION);
			ObjectOutputStream out = new ObjectOutputStream(fos);
			out.writeObject(this);
			out.close();
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
	}
	
}
