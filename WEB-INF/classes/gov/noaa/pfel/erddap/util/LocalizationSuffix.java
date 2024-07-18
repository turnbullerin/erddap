package gov.noaa.pfel.erddap.util; 

import java.util.Locale;
import java.util.ArrayList;
import java.util.List;

/**
 * This class maps a suffix for an attribute to the relevant locale. Note that
 * the locale may be null, which indicates that the locale is not known.
 * 
 */
public class LocalizationSuffix {
    
    /** A suffix that is appended to an attribute name to indicate a localized
     * version of that attribute.
     */
    protected String localizedSuffix;
    
    /** The locale that describes text in attributes with the associated suffix. */
    protected Locale localeTag;
    
    /** Default constructor, for use when there is no localization attribute. */
    public LocalizationSuffix() {
        localizedSuffix = "";
        localeTag = null;
    }
    
    /** Constructor.
     * 
     * @param suffix The suffix used on the attribute.
     * @param locale The locale referenced by the suffix.
     */
    public LocalizationSuffix(String suffix, Locale locale) {
        localizedSuffix = suffix;
        localeTag = locale;
    }
    
    /** The suffix that is appended to attribute names to indicate a localized version 
     * @return The suffix for localized attributes.
     */
    public String suffix() {
        return localizedSuffix;
    }
    
    /** The locale of text in suffixed attributes. 
     * @return The locale indicated by the suffix.
     */
    public Locale locale() {
        return localeTag;
    }
        
    /**
     * Parse a CF localizations tag for a list of localization suffixes.
     * 
     * A CF localizations tag is a map of suffixes to BCP 47 locales. The special suffix
     * "default" indicates the default locale for non-suffixed attributes. For example, 
     * a value of "default: en-US _fr: fr-CA _es: es-MX" indicates that the default locale for
     * localizable attributes should be "en-US", and that versions of those attributes may
     * exist with the suffix "_fr" for Canadian French or "_es" for Mexican Spanish.
     *
     * The result is an ArrayList the same length as the number of languages in ERDDAP.
     * Each entry in the ArrayList is itself an ArrayList of LocalizationSuffix objects
     * in priority order from most relevant to least relevant for the given language
     * code in TranslateMessages.languageCodeList.
     * 
     * Text can be extracted for a given language number n by looping through the n-th
     * entry in the outer list and finding the first inner list with a relevant value.
     * 
     * @param localizations The localizations string in CF format.
     * @return An ArrayList with one entry per languageCode in TranslateMessages.languageCodeList,
     * in the same order as that list. Each entry in the ArrayList is itself an ArrayList
     * which contain a prioritized list of LocalizationSuffix objects from most relevant to
     * least.
     */
    public static ArrayList<ArrayList<LocalizationSuffix>> parseLocalizations(String localizations) {
        int nLanguages = TranslateMessages.languageCodeList.length;
        ArrayList<ArrayList<LocalizationSuffix>> localePriorityList = new ArrayList<>();
        ArrayList<LocalizationSuffix> suffixes = localizationSuffixes(localizations);
        for (int k = 0; k < nLanguages; k++) {
            localePriorityList.add(buildPriorityListForLanguage(TranslateMessages.languageCodeList[k], suffixes));
        }
        return localePriorityList;
    }
    
    protected static ArrayList<LocalizationSuffix> buildPriorityListForLanguage(String languageTag, ArrayList<LocalizationSuffix> suffixes) {
        LocalizationSuffix defaultOption = null;
        ArrayList<LocalizationSuffix> priorityList = new ArrayList<>();
        ArrayList<Locale> localesAvailable = new ArrayList<>();
        for (int k = 0; k < suffixes.size(); k++) {
            LocalizationSuffix current = suffixes.get(k);
            if (current.suffix().equals("")) {
                defaultOption = current;
            }
            else {
                localesAvailable.add(current.locale());
            }
        }
        // TODO: verify that the default matching done by Locale.filter() is sufficient for our needs
        if (!localesAvailable.isEmpty()) {
            List<Locale.LanguageRange> ranges = Locale.LanguageRange.parse(languageTag + ";q=1.0");
            List<Locale> results = Locale.filter(ranges, localesAvailable);
            for (int k = 0; k < results.size(); k++) {
                for (int j = 0; j < suffixes.size(); j++) {
                    LocalizationSuffix current = suffixes.get(j);
                    if (results.get(k).equals(current.locale())) {
                        priorityList.add(current);
                    }
                }
            }
        }
        // make sure default option is LAST in the list
        if (defaultOption != null) {
            priorityList.add(defaultOption);
        }
        // if no default option provided, default to an unknown locale with no suffix.
        else {
            priorityList.add(new LocalizationSuffix());
        }
        return priorityList;
    }
    
    protected static ArrayList<LocalizationSuffix> localizationSuffixes(String localizations) {
        ArrayList<LocalizationSuffix> suffixes = new ArrayList<>();
        if (localizations != null) {
            String localization_map[] = localizations.trim().split(" ");
            for (int i = 0; i < localization_map.length; i += 2) {
                if (i+1 >= localization_map.length) {
                    // TODO: this is an error condition.
                    break;
                }
                String languageTag = localization_map[i+1].trim();
                // TODO: we should enforce a better removal of ":" to ensure the string is well formatted
                String suffix = localization_map[i].replace(":", " ").trim();
                if (suffix.equals("default")) {
                    suffixes.add(new LocalizationSuffix("", Locale.forLanguageTag(languageTag)));
                }
                else {
                    suffixes.add(new LocalizationSuffix(suffix, Locale.forLanguageTag(languageTag)));
                }
            }
        }
        return suffixes;
    }
    
}
