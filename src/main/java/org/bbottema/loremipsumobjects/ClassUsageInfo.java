package org.bbottema.loremipsumobjects;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.Data;
import org.jetbrains.annotations.Nullable;

/**
 * Stores a <em>object</em> and keeps track of whether the instance has been populated with dummy values.
 * <p>
 * Used by {@link LoremIpsumObjectCreator} to avoid a recursive loop when populating an object that has a field of its own type (or contains a chain
 * that returns to its own type).
 *
 * @param <T> The type of the instance stored.
 */
@Data
@SuppressFBWarnings(justification = "Generated code")
public class ClassUsageInfo<T> {
	@Nullable
	private T instance = null;
	private boolean populated = false;
}