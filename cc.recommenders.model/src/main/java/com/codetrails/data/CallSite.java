package com.codetrails.data;

import static com.codetrails.data.CallSiteKind.RECEIVER_CALL_SITE;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import cc.recommenders.names.IMethodName;
import cc.recommenders.names.Names;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;

public class CallSite {

	// make sure the naming is consistent to the hardcoded names in "UsageTypeAdapter"

	private CallSiteKind kind;
	private IMethodName call;
	private int argumentIndex;

	public CallSiteKind getKind() {
		return kind;
	}

	public void setKind(final CallSiteKind kind) {
		this.kind = kind;
	}

	public IMethodName getCall() {
		return call;
	}

	public void setCall(final IMethodName call) {
		this.call = call;
	}

	public void setArgumentIndex(int argumentIndex) {
		this.argumentIndex = argumentIndex;
	}

	public int getArgumentIndex() {
		return argumentIndex;
	}

	public int hashCode() {
		int _reflectionHashCode = HashCodeBuilder.reflectionHashCode(this);
		return _reflectionHashCode;
	}

	public boolean equals(final Object other) {
		boolean _reflectionEquals = EqualsBuilder.reflectionEquals(this, other);
		return _reflectionEquals;
	}

	public String toString() {
		String _xblockexpression = null;
		{
			String key = kind == RECEIVER_CALL_SITE ? "call" : "param";
			ToStringHelper _stringHelper = Objects.toStringHelper(this);
			IMethodName _call = this.getCall();
			String _sourceName = Names.vm2srcQualifiedMethod(_call);
			ToStringHelper _add = _stringHelper.add(key, _sourceName);
			String _string = _add.toString();
			_xblockexpression = (_string);
		}
		return _xblockexpression;
	}
}
