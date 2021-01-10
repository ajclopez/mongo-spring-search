package io.github.ajclopez.mss.model;

import java.util.Map;

/**
 * 
 * <p>Class used for advanced options [optional].</p>
 * 
 * <ul>
 * <li><b>casters:</b> object which map keys to casters ({@code BOOLEAN, NUMBER, PATTERN, DATE, STRING}).
 * <li><b>defaultLimit:</b> default value for {@code limit} key.
 * <li><b>maxLimit:</b> maximum value for {@code limit} key. 
 * </ul>
 * 
 */
public class Configuration {

	private Map<String, CastType> casters;
	private Integer defaultLimit;
	private Integer maxLimit;
	
	/**
	 * Creates a new {@link Configuration} with parameters applied.
	 * 
	 * @param casters object which map keys to casters ({@code BOOLEAN, NUMBER, PATTERN, DATE, STRING}).
	 * @param defaultLimit default value for {@code limit} key.
	 * @param maxLimit maximum value for {@code limit} key.
	 */
	public Configuration(Map<String, CastType> casters, Integer defaultLimit, Integer maxLimit) {
		this.casters = casters;
		this.defaultLimit = defaultLimit;
		this.maxLimit = maxLimit;
	}

	public Map<String, CastType> getCasters() {
		return casters;
	}

	public Integer getDefaultLimit() {
		return defaultLimit;
	}

	public Integer getMaxLimit() {
		return maxLimit;
	}
	
}
