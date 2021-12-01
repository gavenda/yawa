/*
 * This file is part of adventure-text-minimessage, licensed under the MIT License.
 *
 * Copyright (c) 2018-2021 KyoriPowered
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package net.kyori.adventure.text.minimessage.transformation.inbuild;

import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.parser.ParsingException;
import net.kyori.adventure.text.minimessage.parser.node.TagPart;
import net.kyori.adventure.text.minimessage.transformation.Inserting;
import net.kyori.adventure.text.minimessage.transformation.Transformation;
import net.kyori.examination.ExaminableProperty;
import org.jetbrains.annotations.NotNull;

/**
 * A transformation that inserts a key binding component.
 *
 * @since 4.1.0
 */
public final class KeybindTransformation extends Transformation implements Inserting {
  /**
   * Create a new keybind transformation from a tag.
   *
   * @param name the tag name
   * @param args the tag arguments
   * @return a new transformation
   * @since 4.2.0
   */
  public static KeybindTransformation create(final String name, final List<TagPart> args) {
    if (args.size() != 1) {
      throw new ParsingException("Doesn't know how to turn token with name '" + name + "' and arguments " + args + " into a keybind component", args);
    }
    return new KeybindTransformation(args.get(0).value());
  }

  private final String keybind;

  private KeybindTransformation(final String keybind) {
    this.keybind = keybind;
  }

  @Override
  public Component apply() {
    return Component.keybind(this.keybind);
  }

  @Override
  public @NotNull Stream<? extends ExaminableProperty> examinableProperties() {
    return Stream.of(ExaminableProperty.of("keybind", this.keybind));
  }

  @Override
  public boolean equals(final Object other) {
    if (this == other) return true;
    if (other == null || this.getClass() != other.getClass()) return false;
    final KeybindTransformation that = (KeybindTransformation) other;
    return Objects.equals(this.keybind, that.keybind);
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.keybind);
  }
}
