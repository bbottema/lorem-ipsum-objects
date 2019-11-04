package org.bbottema.loremipsumobjects.helperutils;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Builder
@Getter
@EqualsAndHashCode
@ToString
public class ProcessStatus {
    private boolean success;
    private String code;
    private String message;
}