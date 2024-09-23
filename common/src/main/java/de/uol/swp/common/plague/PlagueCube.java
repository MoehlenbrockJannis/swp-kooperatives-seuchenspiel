package de.uol.swp.common.plague;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;

@AllArgsConstructor
@Getter
public class PlagueCube implements Serializable {
    private Plague plague;
}
