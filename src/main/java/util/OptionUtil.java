package util;

import org.apache.commons.cli.Option;

public class OptionUtil {
    /**
     * Creates an optional {@link Option} that takes no arguments with the given name and description.
     *
     * @param name        the given name
     * @param description the given description
     * @return the created option
     */
    public static Option createOptionalOptionNoArgument(String name, String description) {
        final Option option = new Option(name, false, description);
        option.setRequired(false);
        return option;
    }
}
