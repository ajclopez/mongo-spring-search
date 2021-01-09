package io.github.ajclopez.mss.model;

public class SearchCriteria {

	private Boolean prefix;
	private String key;
	private SearchOperation operation;
	private String value;
	private CastType caster;
	
	public SearchCriteria() {
		
	}
	
	public SearchCriteria(Boolean prefix, String key, SearchOperation operation, String value, CastType caster) {
		this.prefix = prefix;
		this.key = key;
		this.operation = operation;
		this.value = value;
		this.caster = caster;
	}

	public Boolean getPrefix() {
		return prefix;
	}

	public void setPrefix(Boolean prefix) {
		this.prefix = prefix;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public SearchOperation getOperation() {
		return operation;
	}

	public void setOperation(SearchOperation operation) {
		this.operation = operation;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public CastType getCaster() {
		return caster;
	}

	public void setCaster(CastType caster) {
		this.caster = caster;
	}

	@Override
	public String toString() {
		return "SearchCriteria [prefix=" + prefix + ", key=" + key + ", operation=" + operation + ", value=" + value
				+ ", caster=" + caster + "]";
	}
	
}
