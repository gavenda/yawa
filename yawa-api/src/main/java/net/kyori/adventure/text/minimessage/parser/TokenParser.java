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
package net.kyori.adventure.text.minimessage.parser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;
import java.util.Locale;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.IntPredicate;
import net.kyori.adventure.text.minimessage.Template;
import net.kyori.adventure.text.minimessage.Tokens;
import net.kyori.adventure.text.minimessage.parser.node.ElementNode;
import net.kyori.adventure.text.minimessage.parser.node.RootNode;
import net.kyori.adventure.text.minimessage.parser.node.TagNode;
import net.kyori.adventure.text.minimessage.parser.node.TagPart;
import net.kyori.adventure.text.minimessage.parser.node.TemplateNode;
import net.kyori.adventure.text.minimessage.parser.node.TextNode;
import net.kyori.adventure.text.minimessage.template.TemplateResolver;
import net.kyori.adventure.text.minimessage.transformation.Inserting;
import net.kyori.adventure.text.minimessage.transformation.Transformation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Handles parsing a string into a list of tokens and then into a tree of nodes.
 *
 * @since 4.2.0
 */
public final class TokenParser {
  private TokenParser() {
  }

  /**
   * Parse a minimessage string into a tree of nodes.
   *
   * @param message the minimessage string to parse
   * @return the root of the resulting tree
   * @since 4.2.0
   */
  public static ElementNode parse(
    final @NotNull Function<TagNode, @Nullable Transformation> transformationFactory,
    final @NotNull BiPredicate<String, Boolean> tagNameChecker,
    final @NotNull TemplateResolver templateResolver,
    final @NotNull String message,
    final boolean strict
  ) {
    final List<Token> tokens = tokenize(message);

    return buildTree(transformationFactory, tagNameChecker, templateResolver, tokens, message, strict);
  }

  /**
   * Tokenize a minimessage string into a list of tokens.
   *
   * @param message the minimessage string to parse
   * @return the root tokens
   * @since 4.2.0
   */
  public static List<Token> tokenize(final String message) {
    final List<Token> tokens = parseFirstPass(message);
    parseSecondPass(message, tokens);
    return tokens;
  }

  /*
   * First pass over the text identifies valid tags and text blocks.
   */
  @SuppressWarnings("DuplicatedCode")
  private static List<Token> parseFirstPass(final String message) {
    final ArrayList<Token> elements = new ArrayList<>();

    FirstPassState state = FirstPassState.NORMAL;
    // If the current state is escaped then the next character is skipped
    boolean escaped = false;

    int currentTokenEnd = 0;
    // Marker is the starting index for the current token
    int marker = -1;
    char currentStringChar = 0;

    final int length = message.length();
    for (int i = 0; i < length; i++) {
      final int codePoint = message.codePointAt(i);
      if (!Character.isBmpCodePoint(codePoint)) {
        i++;
      }

      if (!escaped) {
        // if we're trying to escape and the next character exists
        if (codePoint == '\\' && i + 1 < message.length()) {
          final int nextCodePoint = message.codePointAt(i + 1);

          switch (state) {
            case NORMAL:
              // allow escaping open tokens
              escaped = nextCodePoint == Tokens.TAG_START;
              break;
            case STRING:
              // allow escaping closing string chars
              escaped = currentStringChar == nextCodePoint;
              break;
            case TAG:
            case PRE:
              break;
          }

          // only escape if we need to
          if (escaped) {
            continue;
          }
        }
      } else {
        escaped = false;
        continue;
      }

      switch (state) {
        case NORMAL:
          if (codePoint == Tokens.TAG_START) {
            // Possibly a tag
            marker = i;
            state = FirstPassState.TAG;
          }
          break;
        case TAG:
          switch (codePoint) {
            case Tokens.TAG_END:
              if (i == marker + 1) {
                // This is empty, <>, so it's not a tag
                state = FirstPassState.NORMAL;
                break;
              }

              // We found a tag
              if (currentTokenEnd != marker) {
                // anything not matched up to this point is normal text
                elements.add(new Token(currentTokenEnd, marker, TokenType.TEXT));
              }
              currentTokenEnd = i + 1;

              // closing tags start with </
              TokenType thisType = TokenType.OPEN_TAG;
              if (boundsCheck(message, marker, 1) && message.charAt(marker + 1) == Tokens.CLOSE_TAG) {
                thisType = TokenType.CLOSE_TAG;
              }
              elements.add(new Token(marker, currentTokenEnd, thisType));

              // <pre> tags put us into a state where we don't parse anything
              if (message.regionMatches(marker, "<" + Tokens.PRE + ">", 0, 5)) {
                state = FirstPassState.PRE;
              } else {
                state = FirstPassState.NORMAL;
              }
              break;
            case Tokens.TAG_START:
              // This isn't a tag, but we can re-start looking here
              marker = i;
              break;
            case '\'':
            case '"':
              state = FirstPassState.STRING;
              currentStringChar = (char) codePoint;
              break;
          }
          break;
        case PRE:
          if (codePoint == Tokens.TAG_START) {
            if (message.regionMatches(i, "</" + Tokens.PRE + ">", 0, 6)) {
              // Anything inside the <pre>...</pre> is text
              elements.add(new Token(currentTokenEnd, i, TokenType.TEXT));
              // the </pre> is still a closing tag though
              elements.add(new Token(i, i + 6, TokenType.CLOSE_TAG));
              i += 5;
              currentTokenEnd = i + 1;
              state = FirstPassState.NORMAL;
            }
          }
          break;
        case STRING:
          if (codePoint == currentStringChar) {
            state = FirstPassState.TAG;
          }
          break;
      }
    }

    // anything left over is plain text
    if (elements.isEmpty()) {
      elements.add(new Token(0, message.length(), TokenType.TEXT));
    } else {
      final int end = elements.get(elements.size() - 1).endIndex();
      if (end != message.length()) {
        elements.add(new Token(end, message.length(), TokenType.TEXT));
      }
    }

    return elements;
  }

  enum FirstPassState {
    NORMAL,
    TAG,
    PRE,
    STRING;
  }

  /*
   * Second pass over the tag tokens identifies tag parts
   */
  @SuppressWarnings("DuplicatedCode")
  private static void parseSecondPass(final String message, final List<Token> tokens) {
    for (final Token token : tokens) {
      final TokenType type = token.type();
      if (type != TokenType.OPEN_TAG && type != TokenType.CLOSE_TAG) {
        continue;
      }

      // Only look inside the tag <[/] and >
      final int startIndex = type == TokenType.OPEN_TAG ? token.startIndex() + 1 : token.startIndex() + 2;
      final int endIndex = token.endIndex() - 1;

      SecondPassState state = SecondPassState.NORMAL;
      boolean escaped = false;
      char currentStringChar = 0;

      // Marker is the starting index for the current token
      int marker = startIndex;

      for (int i = startIndex; i < endIndex; i++) {
        final int codePoint = message.codePointAt(i);
        if (!Character.isBmpCodePoint(i)) {
          i++;
        }

        if (!escaped) {
          // if we're trying to escape and the next character exists
          if (codePoint == '\\' && i + 1 < message.length()) {
            final int nextCodePoint = message.codePointAt(i + 1);

            switch (state) {
              case NORMAL:
                // allow escaping open tokens
                escaped = nextCodePoint == Tokens.TAG_START;
                break;
              case STRING:
                // allow escaping closing string chars
                escaped = currentStringChar == nextCodePoint;
                break;
            }

            // only escape if we need to
            if (escaped) {
              continue;
            }
          }
        } else {
          escaped = false;
          continue;
        }

        switch (state) {
          case NORMAL:
            // Values are split by : unless it's in a URL
            if (codePoint == Tokens.SEPARATOR) {
              if (boundsCheck(message, i, 2) && message.charAt(i + 1) == '/' && message.charAt(i + 2) == '/') {
                break;
              }
              if (marker == i) {
                // 2 colons side-by-side like <::> or <:text> or <text::text> would lead to this happening
                insert(token, new Token(i, i, TokenType.TAG_VALUE));
                marker++;
              } else {
                insert(token, new Token(marker, i, TokenType.TAG_VALUE));
                marker = i + 1;
              }
            } else if (codePoint == '\'' || codePoint == '"') {
              state = SecondPassState.STRING;
              currentStringChar = (char) codePoint;
            }
            break;
          case STRING:
            if (codePoint == currentStringChar) {
              state = SecondPassState.NORMAL;
            }
            break;
        }
      }

      // anything not matched is the final part
      if (token.childTokens() == null || token.childTokens().isEmpty()) {
        insert(token, new Token(startIndex, endIndex, TokenType.TAG_VALUE));
      } else {
        final int end = token.childTokens().get(token.childTokens().size() - 1).endIndex();
        if (end != endIndex) {
          insert(token, new Token(end + 1, endIndex, TokenType.TAG_VALUE));
        }
      }
    }
  }

  /*
   * Build a tree from the OPEN_TAG and CLOSE_TAG tokens
   */
  private static ElementNode buildTree(
    final @NotNull Function<TagNode, @Nullable Transformation> transformationFactory,
    final @NotNull BiPredicate<String, Boolean> tagNameChecker,
    final @NotNull TemplateResolver templateResolver,
    final @NotNull List<Token> tokens,
    final @NotNull String message,
    final boolean strict
  ) {
    final RootNode root = new RootNode(message);
    ElementNode node = root;

    for (final Token token : tokens) {
      final TokenType type = token.type();
      switch (type) {
        case TEXT:
          node.addChild(new TextNode(node, token, message));
          break;

        case OPEN_TAG:
          final TagNode tagNode = new TagNode(node, token, message, templateResolver);
          if (isReset(tagNode.name())) {
            // <reset> tags get special treatment and don't appear in the tree
            // instead, they close all currently open tags

            if (strict) {
              throw new ParsingException("<reset> tags are not allowed when strict mode is enabled", message, token);
            }
            node = root;
          } else if (tagNode.name().equals(Tokens.PRE)) {
            // <pre> tags also get special treatment and don't appear in the tree
            // anything inside <pre> is raw text, so just skip

            continue;
          } else {
            final Template template = templateResolver.resolve(tagNode.name());
            if (template instanceof Template.StringTemplate) {
              // String templates are inserted into the tree as raw text nodes, not parsed
              node.addChild(new TemplateNode(node, token, message, ((Template.StringTemplate) template).value()));
            } else if (tagNameChecker.test(tagNode.name(), true)) {
              final Transformation transformation = transformationFactory.apply(tagNode);
              if (transformation == null) {
                // something went wrong, ignore it
                // if strict mode is enabled this will throw an exception for us
                node.addChild(new TextNode(node, token, message));
              } else {
                // This is a recognized tag, goes in the tree
                tagNode.transformation(transformation);
                node.addChild(tagNode);
                if (!(transformation instanceof Inserting)) {
                  // this tag has children
                  node = tagNode;
                }
              }
            } else {
              // not recognized, plain text
              node.addChild(new TextNode(node, token, message));
            }
          }
          break; // OPEN_TAG

        case CLOSE_TAG:
          final List<Token> childTokens = token.childTokens();
          if (childTokens.isEmpty()) {
            throw new IllegalStateException("CLOSE_TAG token somehow has no children - " +
              "the parser should not allow this. Original text: " + message);
          }

          final ArrayList<String> closeValues = new ArrayList<>(childTokens.size());
          for (final Token childToken : childTokens) {
            closeValues.add(TagPart.unquoteAndEscape(message, childToken.startIndex(), childToken.endIndex()));
          }

          final String closeTagName = closeValues.get(0);
          if (isReset(closeTagName) || closeTagName.equals(Tokens.PRE)) {
            // These are synthetic nodes, closing them means nothing in the context of building a tree
            continue;
          }

          if (!tagNameChecker.test(closeTagName, false)) {
            // tag does not exist, so treat it as text
            node.addChild(new TextNode(node, token, message));
            continue;
          }

          ElementNode parentNode = node;
          while (parentNode instanceof TagNode) {
            final List<TagPart> openParts = ((TagNode) parentNode).parts();

            if (tagCloses(closeValues, openParts)) {
              if (parentNode != node && strict) {
                final String msg = "Unclosed tag encountered; " + ((TagNode) node).name() + " is not closed, because " +
                  closeValues.get(0) + " was closed first.";
                throw new ParsingException(msg, message, parentNode.token(), node.token(), token);
              }

              final ElementNode par = parentNode.parent();
              if (par != null) {
                node = par;
              } else {
                throw new IllegalStateException("Root node matched with close tag value, " +
                  "this should not be possible. Original text: " + message);
              }
              break;
            }

            parentNode = parentNode.parent();
          }
          if (parentNode == null || parentNode instanceof RootNode) {
            // This means the closing tag didn't match to anything
            // Since open tags which don't match to anything is never an error, neither is this
            node.addChild(new TextNode(node, token, message));
            break;
          }
          break; // CLOSE_TAG
      }
    }

    if (strict && root != node) {
      final ArrayList<TagNode> openTags = new ArrayList<>();
      {
        ElementNode n = node;
        while (n != null) {
          if (n instanceof TagNode) {
            openTags.add((TagNode) n);
          } else {
            break;
          }
          n = n.parent();
        }
      }

      final Token[] errorTokens = new Token[openTags.size()];

      final StringBuilder sb = new StringBuilder("All tags must be explicitly closed while in strict mode. " +
        "End of string found with open tags: ");

      int i = 0;
      final ListIterator<TagNode> iter = openTags.listIterator(openTags.size());
      while (iter.hasPrevious()) {
        final TagNode n = iter.previous();
        errorTokens[i++] = n.token();

        sb.append(n.name());
        if (iter.hasPrevious()) {
          sb.append(", ");
        }
      }

      throw new ParsingException(sb.toString(), message, errorTokens);
    }

    return root;
  }

  /**
   * Parse a minimessage string into another string, resolving only the string templates present in the message.
   *
   * @param message the minimessage string to parse
   * @param templateResolver the template resolver to use to find string placeholders
   * @return the message, with non-string templates still intact
   * @since 4.2.0
   */
  public static String resolveStringTemplates(
      final @NotNull String message,
      final @NotNull TemplateResolver templateResolver
  ) {
    final List<Token> tokens = tokenize(message);
    final StringBuilder sb = new StringBuilder();

    for (final Token token : tokens) {
      final TokenType type = token.type();
      switch (type) {
        case TEXT:
        case CLOSE_TAG:
          sb.append(token.get(message));
          break;

        case OPEN_TAG:
          if (token.childTokens() != null && token.childTokens().size() == 1) {
            final CharSequence name = token.childTokens().get(0).get(message);
            final Template template = templateResolver.resolve(name.toString().toLowerCase(Locale.ROOT));
            if (template instanceof Template.StringTemplate) {
              sb.append(((Template.StringTemplate) template).value());
              break;
            }
          }
          sb.append(token.get(message));
          break;
      }
    }

    return sb.toString();
  }

  private static boolean isReset(final String input) {
    return input.equalsIgnoreCase(Tokens.RESET) || input.equalsIgnoreCase(Tokens.RESET_2);
  }

  /**
   * Determine if a set of close string parts closes the given list of open tag parts. If the open parts starts with
   * the set of close parts, then this method returns {@code true}.
   *
   * @param closeParts The parts of the close tag
   * @param openParts The parts of the open tag
   * @return {@code true} if the given close parts closes the open tag parts.
   */
  private static boolean tagCloses(final List<String> closeParts, final List<TagPart> openParts) {
    if (closeParts.size() > openParts.size()) {
      return false;
    }
    // The tag name is case-insensitive, but the tag values are not
    if (!closeParts.get(0).equalsIgnoreCase(openParts.get(0).value())) {
      return false;
    }
    for (int i = 1; i < closeParts.size(); i++) {
      if (!closeParts.get(i).equals(openParts.get(i).value())) {
        return false;
      }
    }
    return true;
  }

  /**
   * Returns {@code true} if it's okay to check for characters up to the given length. Returns {@code false} if the
   * string is too short.
   *
   * @param text The string to check.
   * @param index The index to start from.
   * @param length The length to check.
   * @return {@code true} if the string's length is at least as long as {@code index + length}.
   */
  private static boolean boundsCheck(final String text, final int index, final int length) {
    return index + length < text.length();
  }

  /**
   * Optimized insert method for adding child tokens to the given {@code token}.
   *
   * @param token The token to add {@code value} as a child.
   * @param value The token to add to {@code token}.
   */
  private static void insert(final Token token, final Token value) {
    if (token.childTokens() == null) {
      token.childTokens(Collections.singletonList(value));
      return;
    }
    if (token.childTokens().size() == 1) {
      final ArrayList<Token> list = new ArrayList<>(3);
      list.add(token.childTokens().get(0));
      list.add(value);
      token.childTokens(list);
    } else {
      token.childTokens().add(value);
    }
  }

  enum SecondPassState {
    NORMAL,
    STRING;
  }

  /**
   * Removes escaping {@code '\`} characters from a substring where the subsequent character matches a given predicate.
   *
   * @param text the input text
   * @param startIndex the starting index of the substring
   * @param endIndex the ending index of the substring
   * @param escapes the predicate to determine if an escape happened
   * @return the output escaped substring
   * @since 4.2.0
   */
  public static String unescape(final String text, final int startIndex, final int endIndex, final IntPredicate escapes) {
    int from = startIndex;

    int i = text.indexOf('\\', from);
    if (i == -1 || i >= endIndex) {
      return text.substring(from, endIndex);
    }

    final StringBuilder sb = new StringBuilder(endIndex - startIndex);
    while (i != -1 && i + 1 < endIndex) {
      if (escapes.test(text.codePointAt(i + 1))) {
        sb.append(text, from, i);
        i++;

        if (i >= endIndex) {
          from = endIndex;
          break;
        }

        final int codePoint = text.codePointAt(i);
        sb.appendCodePoint(codePoint);

        if (Character.isBmpCodePoint(codePoint)) {
          i += 1;
        } else {
          i += 2;
        }

        if (i >= endIndex) {
          from = endIndex;
          break;
        }
      } else {
        i++;
        sb.append(text, from, i);
      }

      from = i;
      i = text.indexOf('\\', from);
    }

    sb.append(text, from, endIndex);

    return sb.toString();
  }
}
