package com.codetrails.data;

import cc.recommenders.names.ITypeName;

import com.codetrails.data.CallSite;
import com.codetrails.data.DefinitionKind;
import com.codetrails.data.DefinitionSite;
import com.codetrails.data.EnclosingMethodContext;
import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;
import com.google.common.collect.Sets;
import com.google.gson.annotations.SerializedName;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import com.codetrails.data.Uuidable;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

@SuppressWarnings("all")
public class ObjectUsage implements Uuidable {
	@SerializedName("context")
	private EnclosingMethodContext _context;

	public EnclosingMethodContext getContext() {
		return this._context;
	}

	public void setContext(final EnclosingMethodContext context) {
		this._context = context;
	}

	@SerializedName("uuid")
	private UUID _uuid;

	public UUID getUuid() {
		return this._uuid;
	}

	public void setUuid(final UUID uuid) {
		this._uuid = uuid;
	}

	public ITypeName getType() {
		DefinitionSite _def = this.getDef();
		return _def.getType();
	}

	@SerializedName("def")
	private DefinitionSite _Def;

	public DefinitionSite getDef() {
		return this._Def;
	}

	public void setDef(final DefinitionSite Def) {
		this._Def = Def;
	}

	@SerializedName("paths")
	private Set<List<CallSite>> _paths = Sets.<List<CallSite>> newHashSet();

	public Set<List<CallSite>> getPaths() {
		return this._paths;
	}

	public void setPaths(final Set<List<CallSite>> paths) {
		this._paths = paths;
	}

	public DefinitionKind getDefinitionKind() {
		DefinitionSite _def = this.getDef();
		return _def.getKind();
	}

	public boolean isThis() {
		DefinitionSite _def = this.getDef();
		DefinitionKind _kind = _def.getKind();
		boolean _equals = Objects.equal(DefinitionKind.THIS, _kind);
		return _equals;
	}

	public boolean isReturn() {
		DefinitionSite _def = this.getDef();
		DefinitionKind _kind = _def.getKind();
		boolean _equals = Objects.equal(DefinitionKind.RETURN, _kind);
		return _equals;
	}

	public boolean isNew() {
		DefinitionSite _def = this.getDef();
		DefinitionKind _kind = _def.getKind();
		boolean _equals = Objects.equal(DefinitionKind.NEW, _kind);
		return _equals;
	}

	public boolean isParam() {
		DefinitionSite _def = this.getDef();
		DefinitionKind _kind = _def.getKind();
		boolean _equals = Objects.equal(DefinitionKind.PARAM, _kind);
		return _equals;
	}

	public boolean isField() {
		DefinitionSite _def = this.getDef();
		DefinitionKind _kind = _def.getKind();
		boolean _equals = Objects.equal(DefinitionKind.FIELD, _kind);
		return _equals;
	}

	public boolean isConstant() {
		DefinitionSite _def = this.getDef();
		DefinitionKind _kind = _def.getKind();
		boolean _equals = Objects.equal(DefinitionKind.CONSTANT, _kind);
		return _equals;
	}

	public boolean isUnkown() {
		DefinitionSite _def = this.getDef();
		DefinitionKind _kind = _def.getKind();
		boolean _equals = Objects.equal(DefinitionKind.UNKNOWN, _kind);
		return _equals;
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
		ITypeName _type = this.getType();
		ToStringHelper _add = _stringHelper.add("type", _type);
		Set<List<CallSite>> _paths = this.getPaths();
		ToStringHelper _add_1 = _add.add("paths", _paths.size());
		String _string = _add_1.toString();
		return _string;
	}
}