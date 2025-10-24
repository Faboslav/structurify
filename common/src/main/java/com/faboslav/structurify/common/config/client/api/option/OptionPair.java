package com.faboslav.structurify.common.config.client.api.option;

import dev.isxander.yacl3.api.Option;

public record OptionPair<K extends Option<?>, V extends Option<?>>(K firstOption, V secondOption)
{
}
