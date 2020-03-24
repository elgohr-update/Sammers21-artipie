/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2020 artipie.com
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.artipie;

import com.amihaiemil.eoyaml.StrictYamlMapping;
import com.amihaiemil.eoyaml.Yaml;
import com.amihaiemil.eoyaml.YamlMapping;
import com.artipie.asto.Storage;
import com.artipie.asto.fs.FileStorage;
import io.vertx.reactivex.core.Vertx;
import java.io.IOException;
import java.nio.file.Path;

/**
 * Settings built from YAML.
 *
 * @since 0.1
 */
public final class YamlSettings implements Settings {

    /**
     * YAML file content.
     */
    private final String content;

    /**
     * Ctor.
     *
     * @param content YAML file content.
     */
    public YamlSettings(final String content) {
        this.content = content;
    }

    @Override
    public Storage storage() throws IOException {
        final YamlMapping yaml =
            new StrictYamlMapping(
                Yaml.createYamlInput(this.content)
                .readYamlMapping()
                .yamlMapping("meta")
                .yamlMapping("storage")
            );
        final String type =  yaml.string("type");
        if ("fs".equals(type)) {
            return new FileStorage(Path.of(yaml.string("path")), Vertx.vertx().fileSystem());
        }
        throw new IllegalStateException(String.format("Unsupported storage type: '%s'", type));
    }
}