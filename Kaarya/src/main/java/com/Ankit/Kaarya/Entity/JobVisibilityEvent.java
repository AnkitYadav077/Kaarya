package com.Ankit.Kaarya.Entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class JobVisibilityEvent {

    private Long jobId;

    private boolean visible;
}