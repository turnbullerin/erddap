package gov.noaa.pfel.erddap.util; 

import java.util.Locale;
import com.cohort.util.XML;
import com.cohort.util.String2;

/**
 * This class represents a string and its associated locale. It should be used
 * when the locale is relevant to the display of the string, such as adding an
 * HTML tag with an appropriate lang attribute.
 */
public class LocalizedString {
    
    /** The localized string content */
    protected String localizedString;
    /** The localization of the string, or null if none is known. */
    protected Locale localeTag;
    
    /** Constructor.
     * 
     * @param text The localized text content
     * @param locale The locale of the text content
     */
    public LocalizedString(String text, Locale locale) {
        localizedString = text;
        localeTag = locale;
    }
    
    /**
     * Retrieve the localized text content.
     * 
     * @return The localized text content. 
     */
    public String text() {
        return localizedString;
    }
    
    /**
     * The locale of the text content, or null if no language tag was provided.
     * @return The locale of the text or null for an undefined locale.
     */
    public Locale locale() {
        return localeTag;
    }
    
    /**
     * Generate an HTML span tag if the locale doesn't match the current one.
     * 
     * @param pageLanguage The language of the page
     * @return The localized string, wrapped in an HTML element given by tagName
     *   with a lang attribute set if that lang attribute doesn't match the current
     *   page language.
     */
    public String htmlTag(int pageLanguage) {
        return htmlTag(pageLanguage, "span", -1, "");
    }
    
    /**
     * Generate an HTML wrapper element if the locale of the string doesn't match 
     * the locale of the page.
     * 
     * @param pageLanguage The language of the page
     * @param tagName The tag to use as a wrapper
     * @return The localized string, wrapped in an HTML element given by tagName
     *   with a lang attribute set if that lang attribute doesn't match the current
     *   page language.
     */
    public String htmlTag(int pageLanguage, String tagName) {
        return htmlTag(pageLanguage, tagName, -1, "");
    }
    
    /**
     * Generate an HTML wrapper element if the locale of the string doesn't match 
     * the locale of the page.
     * 
     * @param pageLanguage The language of the page
     * @param maxLineLength The maximum length of the string to show
     * @return The localized string, wrapped in an HTML element given by tagName
     *   with a lang attribute set if that lang attribute doesn't match the current
     *   page language.
     */
    public String htmlTag(int pageLanguage, int maxLineLength) {
        return htmlTag(pageLanguage, "span", maxLineLength, "");
    }
    
    /**
     * Generate an HTML wrapper element if the locale of the string doesn't match 
     * the locale of the page.
     * 
     * @param pageLanguage The language of the page
     * @param maxLineLength The maximum length of the string to show
     * @param linePrefix The prefix after a line break
     * @return The localized string, wrapped in an HTML element given by tagName
     *   with a lang attribute set if that lang attribute doesn't match the current
     *   page language.
     */
    public String htmlTag(int pageLanguage, int maxLineLength, String linePrefix) {
        return htmlTag(pageLanguage, "span", maxLineLength, linePrefix);
    }
    
    /**
     * Generate an HTML wrapper element if the locale of the string doesn't match 
     * the locale of the page.
     * 
     * @param pageLanguage The language of the page
     * @param tagName The tag to use as a wrapper
     * @param maxLineLength The maximum length of the string to show
     * @param linePrefix The prefix after a line break
     * @return The localized string, wrapped in an HTML element given by tagName
     *   with a lang attribute set if that lang attribute doesn't match the current
     *   page language.
     */
    public String htmlTag(int pageLanguage, String tagName, int maxLineLength, String linePrefix) {
        String languageTag = "";
        if (localeTag != null) {
            languageTag = localeTag.toLanguageTag();
        }
        String encString = localizedString;
        if (maxLineLength > 0) {
            encString = String2.noLongLinesAtSpace(encString, maxLineLength, linePrefix);
        }
        encString = XML.encodeAsHTML(encString).replaceAll("\n", "<br />");
        if (!languageTag.equals(TranslateMessages.languageCodeList[pageLanguage])) {
            return "<" + tagName + " lang=\"" + languageTag + "\">" + encString + "</" + tagName + ">";
        }
        else {
            return encString;
        }
    }
        
}
