package com.codetrails.data;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import cc.recommenders.names.IMethodName;
import cc.recommenders.names.ITypeName;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;

public class EnclosingMethodContext {
	private IMethodName name;
	@Nullable
	private ITypeName introducedBy;
	@Nullable
	private ITypeName superclass;
	private ITypeName[] implementedBy;
	private ITypeName[] annotations;

	public IMethodName getName() {
		return name;
	}

	public void setName(final IMethodName name) {
		this.name = name;
	}

	public ITypeName getSuperclass() {
		return superclass;
	}

	public void setSuperclass(final ITypeName superclass) {
		this.superclass = superclass;
	}

	public ITypeName getIntroducedBy() {
		return introducedBy;
	}

	public void setIntroducedBy(final ITypeName introducedBy) {
		this.introducedBy = introducedBy;
	}

	public ITypeName[] getImplementors() {
		return implementedBy;
	}

	public void setImplementors(final ITypeName[] implementors) {
		this.implementedBy = implementors;
	}

	public ITypeName[] getAnnotations() {
		return annotations;
	}

	public void setAnnotations(final ITypeName[] annotations) {
		this.annotations = annotations;
	}

	public boolean isInit() {
		return name.isInit();
	}

	@Override
	public boolean equals(Object obj) {
		return EqualsBuilder.reflectionEquals(this, obj);
	}

	@Override
	public int hashCode() {
		return HashCodeBuilder.reflectionHashCode(this);
	}

	public String toString() {
		ToStringHelper _stringHelper = Objects.toStringHelper(this);
		IMethodName _name = this.getName();
		ToStringHelper _add = _stringHelper.add("name", _name);
		ITypeName[] _implementors = this.getImplementors();
		ToStringHelper _add_1 = _add.add("implementors", _implementors.length);
		String _string = _add_1.toString();
		return _string;
	}
}
