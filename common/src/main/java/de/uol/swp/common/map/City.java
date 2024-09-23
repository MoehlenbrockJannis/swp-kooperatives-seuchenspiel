package de.uol.swp.common.map;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;

/**
 * Class representing a city with all information about it
 *
 * @author Tom Weelborg
 * @since 2024-09-02
 */
@AllArgsConstructor
@Getter
public class City implements Serializable {
    private String name;
    private String information;
}
