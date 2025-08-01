package de.uol.swp.common.map;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.io.Serializable;

/**
 * Class representing a city with all information about it
 */
@AllArgsConstructor
@EqualsAndHashCode
@Getter
public class City implements Serializable {
    private String name;
    private String information;
}
