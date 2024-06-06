package io.github.ajclopez.mss.model;

import java.util.Map;

/**
 * <p>Class used for advanced options [optional].</p>
 *
 * <ul>
 * <li><b>casters:</b> object which map keys to casters ({@code BOOLEAN, NUMBER, PATTERN, DATE, STRING}).
 * <li><b>defaultLimit:</b> default value for {@code limit} key.
 * <li><b>maxLimit:</b> maximum value for {@code limit} key.
 * </ul>
 */
public record Configuration(Map<String, CastType> casters, Integer defaultLimit, Integer maxLimit) {

}
