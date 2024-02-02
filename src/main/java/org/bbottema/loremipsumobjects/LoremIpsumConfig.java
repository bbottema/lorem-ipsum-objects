package org.bbottema.loremipsumobjects;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Value;

@Builder
@Value
@SuppressFBWarnings(justification = "generated code")
public class LoremIpsumConfig {
	@Default private int retries = 0;
	@Default private int timeoutMillis = 250;
	@Default private int fixedBigdecimalScale = -1;
	@Default private ClassBindings classBindings = new ClassBindings();
}
