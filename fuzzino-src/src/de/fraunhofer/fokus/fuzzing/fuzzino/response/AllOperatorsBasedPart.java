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
package de.fraunhofer.fokus.fuzzing.fuzzino.response;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>All Operators Based Part</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link de.fraunhofer.fokus.fuzzing.fuzzino.response.AllOperatorsBasedPart#getOperatorParts <em>Operator Parts</em>}</li>
 * </ul>
 * </p>
 *
 * @see de.fraunhofer.fokus.fuzzing.fuzzino.response.ResponsePackage#getAllOperatorsBasedPart()
 * @model extendedMetaData="name='operatorBased_._type' kind='elementOnly'"
 * @generated
 */
public interface AllOperatorsBasedPart extends EObject {
	/**
	 * Returns the value of the '<em><b>Operator Parts</b></em>' containment reference list.
	 * The list contents are of type {@link de.fraunhofer.fokus.fuzzing.fuzzino.response.OperatorPart}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Operator Parts</em>' containment reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Operator Parts</em>' containment reference list.
	 * @see de.fraunhofer.fokus.fuzzing.fuzzino.response.ResponsePackage#getAllOperatorsBasedPart_OperatorParts()
	 * @model containment="true" required="true"
	 *        extendedMetaData="kind='element' name='operator' namespace='##targetNamespace'"
	 * @generated
	 */
	EList<OperatorPart> getOperatorParts();

} // AllOperatorsBasedPart
