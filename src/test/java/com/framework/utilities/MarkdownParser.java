package com.framework.utilities;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Derives what a markdown string should look like once rendered, so UI steps can assert the page against the
 * markdown they submitted. Supports bold ({@code **text**}, {@code __text__}) and italics ({@code *text*},
 * {@code _text_}) only; nested or combined emphasis such as {@code ***text***} is not handled.
 */
public final class MarkdownParser {

    private static final Pattern BOLD = Pattern.compile("\\*\\*(.+?)\\*\\*|__(.+?)__");
    private static final Pattern ITALIC = Pattern.compile("\\*(.+?)\\*|(?<!\\w)_(.+?)_(?!\\w)");

    private MarkdownParser() {
    }

    /**
     * Texts expected inside {@code <strong>} elements, in document order.
     */
    public static List<String> boldTexts(String markdown) {
        return matches(BOLD, markdown);
    }

    /**
     * Texts expected inside {@code <em>} elements, in document order.
     */
    public static List<String> italicTexts(String markdown) {
        // strip bold first, otherwise **text** also matches the single-asterisk italic pattern
        return matches(ITALIC, strip(BOLD, markdown));
    }

    /**
     * The visible text of the rendered markdown, i.e. what {@code getText()} returns for it.
     */
    public static String renderedText(String markdown) {
        return strip(ITALIC, strip(BOLD, markdown));
    }

    public static List<String> renderedParagraphs(String markdown) {
        List<String> paragraphs = new ArrayList<>();
        String[] rawParagraphs = renderedText(markdown).split("\n\n");
        for (String paragraph : rawParagraphs) {
            paragraph = paragraph.trim();
            if (!paragraph.isEmpty()) {
                paragraphs.add(paragraph);
            }
        }
        return paragraphs;
    }

    private static List<String> matches(Pattern pattern, String markdown) {
        if (markdown == null) {
            return Collections.emptyList();
        }
        List<String> texts = new ArrayList<>();
        Matcher matcher = pattern.matcher(markdown);
        while (matcher.find()) {
            texts.add(contentOf(matcher));
        }
        return texts;
    }

    private static String strip(Pattern pattern, String markdown) {
        if (markdown == null) {
            return null;
        }
        // quoteReplacement keeps '$' and '\' in the content from being read as group references
        return pattern.matcher(markdown).replaceAll(match -> Matcher.quoteReplacement(contentOf(match)));
    }

    private static String contentOf(MatchResult match) {
        return match.group(1) != null ? match.group(1) : match.group(2);
    }
}
