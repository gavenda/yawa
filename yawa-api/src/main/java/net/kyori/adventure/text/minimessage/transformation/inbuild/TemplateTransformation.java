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

import java.util.Objects;
import java.util.stream.Stream;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.Template;
import net.kyori.adventure.text.minimessage.transformation.Inserting;
import net.kyori.adventure.text.minimessage.transformation.Transformation;
import net.kyori.adventure.text.minimessage.transformation.TransformationFactory;
import net.kyori.examination.ExaminableProperty;
import org.jetbrains.annotations.NotNull;

/**
 * Inserts a formatted template component into the result.
 *
 * @since 4.1.0
 */
public final class TemplateTransformation extends Transformation implements Inserting {
  private final Template.@NotNull ComponentTemplate template;

  private TemplateTransformation(final Template.@NotNull ComponentTemplate template) {
    this.template = template;
  }

  /**
   * Create a new factory for template transformations applying {@code template}.
   *
   * @param template the template to apply
   * @since 4.1.0
   */
  public static @NotNull TransformationFactory<TemplateTransformation> factory(final Template.@NotNull ComponentTemplate template) {
    final TemplateTransformation instance = new TemplateTransformation(template);
    return (ctx, name, args) -> instance;
  }

  @Override
  public Component apply() {
    return this.template.value();
  }

  @Override
  public @NotNull Stream<? extends ExaminableProperty> examinableProperties() {
    return Stream.of(ExaminableProperty.of("template", this.template));
  }

  @Override
  public boolean equals(final Object other) {
    if (this == other) return true;
    if (other == null || this.getClass() != other.getClass()) return false;
    final TemplateTransformation that = (TemplateTransformation) other;
    return Objects.equals(this.template, that.template);
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.template);
  }

}
