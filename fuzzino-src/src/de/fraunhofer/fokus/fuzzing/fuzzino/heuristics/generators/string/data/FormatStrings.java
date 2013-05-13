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
package de.fraunhofer.fokus.fuzzing.fuzzino.heuristics.generators.string.data;

import de.fraunhofer.fokus.fuzzing.fuzzino.util.StringUtil;

public class FormatStrings extends StringContainer {
	
	public static final FormatStrings INSTANCE = new FormatStrings();
	
	private FormatStrings() {
		super();
	}

	@Override
	protected void initValues() {
		add(StringUtil.repeat("%n", 100));
		add(StringUtil.repeat("%n", 500));
		add(StringUtil.repeat("\"%n\"", 500));
		add(StringUtil.repeat("%s", 100));
		add(StringUtil.repeat("%s", 500));
		add(StringUtil.repeat("\"%s\"", 500));
	}

}
