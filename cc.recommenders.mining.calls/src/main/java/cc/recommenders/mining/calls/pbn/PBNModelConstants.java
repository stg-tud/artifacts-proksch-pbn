/**
 * Copyright (c) 2010, 2011 Darmstadt University of Technology.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Sebastian Proksch - initial API and implementation
 */
package cc.recommenders.mining.calls.pbn;

import static java.lang.String.format;
import cc.recommenders.names.IFieldName;
import cc.recommenders.names.IMethodName;
import cc.recommenders.names.ITypeName;
import cc.recommenders.names.VmFieldName;
import cc.recommenders.names.VmMethodName;
import cc.recommenders.names.VmTypeName;
import cc.recommenders.usages.DefinitionSite;
import cc.recommenders.usages.DefinitionSites;
import cc.recommenders.usages.features.CallFeature;
import cc.recommenders.usages.features.ClassFeature;
import cc.recommenders.usages.features.DefinitionFeature;
import cc.recommenders.usages.features.FirstMethodFeature;
import cc.recommenders.usages.features.ParameterFeature;
import cc.recommenders.usages.features.UsageFeature;
import cc.recommenders.usages.features.UsageFeature.ObjectUsageFeatureVisitor;

public class PBNModelConstants {

	public static final String PATTERN_TITLE = "patterns";
	public static final String CLASS_CONTEXT_TITLE = "inClass";
	public static final String METHOD_CONTEXT_TITLE = "inMethod";
	public static final String DEFINITION_TITLE = "definition";

	public static final String STATE_TRUE = "t";
	public static final String STATE_FALSE = "f";

	public static final ITypeName DUMMY_TYPE = VmTypeName.get("LDummy");
	public static final IMethodName DUMMY_METHOD = VmMethodName.get("LDummy.dummy()V");
	public static final IFieldName DUMMY_FIELD = VmFieldName.get("LDummy.dummy;LDummy2");
	public static final DefinitionSite DUMMY_DEFINITION = DefinitionSites.createDefinitionByReturn(DUMMY_METHOD);

	public static final ITypeName UNKNOWN_TYPE = VmTypeName.get("LUnknown");
	public static final IMethodName UNKNOWN_METHOD = VmMethodName.get("LUnknown.unknown()V");
	public static final DefinitionSite UNKNOWN_DEFINITION = DefinitionSites.createUnknownDefinitionSite();

	public static final String CALL_PREFIX = "C_";
	public static final String PARAMETER_PREFIX = "P_";

	public static String newClassContext(ITypeName type) {
		return type.toString();
	}

	public static String newMethodContext(IMethodName method) {
		return method.toString();
	}

	public static String newDefinition(DefinitionSite definitionSite) {
		return definitionSite.toString();
		// DefinitionType type = definitionSite.type;
		// switch (type) {
		// case NEW:
		// case RETURN:
		// return definitionSite.method.toString();
		// case PARAM:
		// return format("%s#%d", type.toString(),
		// definitionSite.argumentIndex);
		// case FIELD:
		// return definitionSite.field.getIdentifier();
		// default:
		// return type.toString();
		// }
	}

	public static String newParameterSite(IMethodName param, int argNum) {
		return format("%s%s#%d", PARAMETER_PREFIX, param.toString(), argNum);
	}

	public static String newCallSite(IMethodName site) {
		return format("%s%s", CALL_PREFIX, site.toString());
	}

	public static String getTitle(UsageFeature f) {
		final String[] title = new String[] { "title is unset" };
		f.accept(new ObjectUsageFeatureVisitor() {
			@Override
			public void visit(CallFeature f) {
				title[0] = newCallSite(f.getMethodName());
			}

			@Override
			public void visit(ClassFeature f) {
				title[0] = newClassContext(f.getTypeName());
			}

			@Override
			public void visit(DefinitionFeature f) {
				title[0] = newDefinition(f.getDefinitionSite());

			}

			@Override
			public void visit(FirstMethodFeature f) {
				title[0] = newMethodContext(f.getMethodName());
			}

			@Override
			public void visit(ParameterFeature f) {
				title[0] = newParameterSite(f.getMethodName(), f.getArgNum());
			}
		});
		return title[0];
	}
}