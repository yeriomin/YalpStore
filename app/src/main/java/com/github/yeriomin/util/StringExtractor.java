/*
 * Yalp Store
 * Copyright (C) 2018 Sergey Yeriomin <yeriomin@gmail.com>
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */

package com.github.yeriomin.util;

import android.annotation.TargetApi;
import android.os.Build;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

@TargetApi(Build.VERSION_CODES.FROYO)
public class StringExtractor {

    final static private String VALUES_DIR_PREFIX = "values-";

    final static private Map<String, String> stringNames = new HashMap<>();

    static {

        // Play Store
        stringNames.put("apps_by", "apps_by"); // Apps by %1$s
        stringNames.put("availability_restriction_carrier", "availability_restriction_carrier"); // "This item isn't available on your carrier."
        stringNames.put("availability_restriction_country", "availability_restriction_country"); // "This item isn't available in your country."
        stringNames.put("availability_restriction_country_or_carrier", "availability_restriction_country_or_carrier"); // "This item isn't available in your country or on your carrier."
        stringNames.put("availability_restriction_for_managed_account", "availability_restriction_for_managed_account"); // Your administrator has not given you access to this item.
        stringNames.put("availability_restriction_generic", "availability_restriction_generic"); // "This item isn't available."
        stringNames.put("availability_restriction_hardware", "availability_restriction_hardware"); // "Your device isn't compatible with this item."
        stringNames.put("availability_restriction_hardware_app", "availability_restriction_hardware_app"); // "Your device isn't compatible with this version."
        stringNames.put("availability_restriction_hardware_app_ram_generic", "availability_restriction_hardware_app_ram_generic"); // "This app requires more RAM than what's available on your device."
        stringNames.put("availability_restriction_missing_permission", "availability_restriction_missing_permission"); // Your administrator has not accepted permissions for this item.
        stringNames.put("availability_restriction_not_in_group", "availability_restriction_not_in_group"); // "You're not in the targeted group for this item."
        stringNames.put("check_for_updates", "list_check_updates"); // Check for updates
        stringNames.put("content_flagged", "content_flagged"); // Objection submitted.
        stringNames.put("delete_review", "delete"); // Delete
        stringNames.put("done", "done"); // Done
        stringNames.put("download", "details_download"); // Download
        stringNames.put("download_in_progress", "details_downloading"); // Downloading…
        stringNames.put("download_settings_value_wifi_only", "pref_background_update_wifi_only"); // On Wi-Fi only
        stringNames.put("early_access", "early_access"); // Early access
        stringNames.put("flag_graphic_violence", "flag_graphic_violence"); // Graphic violence
        stringNames.put("flag_harmful_prompt", "flag_harmful_prompt"); // Describe the harm caused to your device or data:
        stringNames.put("flag_harmful_to_device", "flag_harmful_to_device"); // Harmful to device or data
        stringNames.put("flag_hateful_content", "flag_hateful_content"); // Hateful or abusive content
        stringNames.put("flag_improper_content_rating", "flag_improper_content_rating"); // Improper content rating
        stringNames.put("flag_other_concern_prompt", "flag_other_concern_prompt"); // Describe objection
        stringNames.put("flag_other_objection", "flag_other_objection"); // Other objection
        stringNames.put("flag_page_description", "flag_page_description"); // The reason you find this content or app objectionable:
        stringNames.put("flag_pharma_content", "flag_pharma_content"); // Illegal prescription or other drug
        stringNames.put("flag_sexual_content", "flag_sexual_content"); // Sexual content
        stringNames.put("flagging_title", "flagging_title"); // Flag as inappropriate
        stringNames.put("install", "details_install"); // Install
        stringNames.put("installing", "details_installing"); // Installing…
        stringNames.put("label_wishlist_add_action", "action_wishlist_add"); // Add to wishlist
        stringNames.put("label_wishlist_remove_action", "action_wishlist_remove"); // Remove from wishlist
        stringNames.put("menu_wishlist", "action_wishlist"); // Wishlist
        stringNames.put("my_apps_tab_updates", "action_updates"); // Updates
        stringNames.put("my_apps_tab_updates", "pref_category_updates"); // Updates
        stringNames.put("my_apps_tab_updates", "activity_title_updates_only"); // Updates
        stringNames.put("not_now", "dialog_two_factor_cancel"); // Not now
        stringNames.put("search_menu_title", "action_search"); // Search
        stringNames.put("settings", "action_settings"); // Settings
        stringNames.put("settings_about_header", "action_about"); // About
        stringNames.put("setup_wizard_select_all_apps", "search_filter"); // All apps
        stringNames.put("share", "details_share"); // Share
        stringNames.put("submit", "submit"); // Submit
        stringNames.put("testing_program_opt_in", "testing_program_opt_in"); // join
        stringNames.put("testing_program_opt_out", "testing_program_opt_out"); // leave
        stringNames.put("testing_program_review_dialog_content_hint", "testing_program_review_dialog_content_hint"); // Enter feedback about the app
        stringNames.put("testing_program_section_opted_in_message", "testing_program_section_opted_in_message"); // "App updates will include beta versions. If you leave this app's beta program, you will no longer get beta updates."
        stringNames.put("testing_program_section_opted_in_propagating_message", "testing_program_section_opted_in_propagating_message"); // "In a few minutes, you'll be added to the beta program. You can then update to the beta version of this app, if available."
        stringNames.put("testing_program_section_opted_in_title", "testing_program_section_opted_in_title"); // "You're a beta tester"
        stringNames.put("testing_program_section_opted_out_message", "testing_program_section_opted_out_message"); // "Try new features before they're made public. Give your feedback directly to the developer."
        stringNames.put("testing_program_section_opted_out_propagating_message", "testing_program_section_opted_out_propagating_message"); // It can take a few minutes to remove you from the beta program.
        stringNames.put("testing_program_section_opted_out_title", "testing_program_section_opted_out_title"); // Become a beta tester
        stringNames.put("uninstall", "details_uninstall"); // Uninstall
        stringNames.put("update_all", "list_update_all"); // Update all
        stringNames.put("updating", "list_updating"); // Updating…

        // F-Droid
        stringNames.put("app_version_x_installed", "details_versionName"); // Version %1$s
        stringNames.put("menu_settings", "action_settings"); // Settings
        stringNames.put("menu_launch", "details_run"); // Run
        stringNames.put("menu_video", "details_video"); // Video
        stringNames.put("main_menu__categories", "action_categories"); // Categories
        stringNames.put("useTor", "pref_use_tor"); // Use Tor
        stringNames.put("useTorSummary", "pref_use_tor_summary"); // Force download traffic through Tor for increased privacy. Requires Orbot
        stringNames.put("proxy", "pref_category_proxy"); // Proxy
        stringNames.put("no_permissions", "no_permissions"); // No permissions are used.
        stringNames.put("theme", "pref_ui_theme"); // Theme
        stringNames.put("interval_1h", "pref_background_update_interval_hourly"); // Hourly
        stringNames.put("interval_1d", "pref_background_update_interval_daily"); // Daily
        stringNames.put("interval_1w", "pref_background_update_interval_weekly"); // Weekly
        stringNames.put("theme_light", "pref_ui_theme_light"); // Light
        stringNames.put("theme_dark", "pref_ui_theme_dark"); // Dark
        stringNames.put("menu_ignore_this", "action_ignore_this"); // Ignore This Update
    }

    private Map<String, String> englishStrings = new HashMap<>();

    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Path to the other project's res directory is expected");
            System.exit(128);
        }
        File fromRoot = new File(args[0]);
        if (!fromRoot.exists() || !fromRoot.isDirectory()) {
            System.out.println("Path to the other project's res directory is expected");
            System.exit(128);
        }
        new StringExtractor().extract(fromRoot);
    }

    private void extract(File fromRoot) {
        englishStrings = getEnglishStrings();
        for (File valuesDir: fromRoot.listFiles()) {
            File stringsFile = new File(valuesDir, "strings.xml");
            if (!valuesDir.isDirectory() || !valuesDir.getName().startsWith(VALUES_DIR_PREFIX) || !stringsFile.exists()) {
                continue;
            }
            System.out.println("Processing " + valuesDir.getName());
            Map<String, String> wantedStrings = getStrings(stringsFile);
            if (!wantedStrings.isEmpty()) {
                File localFile = getLocalFile(valuesDir.getName().substring(VALUES_DIR_PREFIX.length()));
                if (!localFile.exists()) {
                    create(localFile);
                }
                putStrings(wantedStrings, localFile);
            }
        }
    }

    private Map<String, String> getStrings(File from) {
        Map<String, String> wantedStrings = new HashMap<>();
        try {
            Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(from);
            doc.getDocumentElement().normalize();
            NodeList nodeList = doc.getElementsByTagName("resources").item(0).getChildNodes();
            for (int i = 0; i < nodeList.getLength(); i++) {
                Node stringNode = nodeList.item(i);
                if (!(stringNode instanceof Element) || !((Element) stringNode).getTagName().equals("string")) {
                    continue;
                }
                String stringName = ((Element) stringNode).getAttribute("name");
                if (!stringNames.keySet().contains(stringName)) {
                    continue;
                }
                String stringContent = stringNode.getTextContent();
                wantedStrings.put(stringNames.get(stringName), stringContent.startsWith("\"") ? stringContent : ("\"" + stringContent + "\""));
            }
        } catch (ParserConfigurationException | IOException | SAXException e) {
            System.out.println("Could not read xml document from " + from + ": " + e.getMessage());
        }
        return wantedStrings;
    }

    private Map<String, String> getEnglishStrings() {
        Map<String, String> wantedStrings = new HashMap<>();
        File from = new File("src\\main\\res\\values\\strings.xml");
        try {
            Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(from);
            doc.getDocumentElement().normalize();
            NodeList nodeList = doc.getElementsByTagName("resources").item(0).getChildNodes();
            for (int i = 0; i < nodeList.getLength(); i++) {
                Node stringNode = nodeList.item(i);
                if (!(stringNode instanceof Element) || !((Element) stringNode).getTagName().equals("string")) {
                    continue;
                }
                wantedStrings.put(((Element) stringNode).getAttribute("name"), stringNode.getTextContent());
            }
        } catch (ParserConfigurationException | IOException | SAXException e) {
            System.out.println("Could not read xml document from " + from + ": " + e.getMessage());
        }
        return wantedStrings;
    }

    private void putStrings(Map<String, String> wantedStrings, File to) {
        Set<String> existingStrings = new HashSet<>();
        try {
            Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(to);
            doc.getDocumentElement().normalize();
            Node resources = doc.getElementsByTagName("resources").item(0);
            NodeList nodeList = resources.getChildNodes();
            for (int i = 0; i < nodeList.getLength(); i++) {
                Node stringNode = nodeList.item(i);
                if (!(stringNode instanceof Element) || !((Element) stringNode).getTagName().equals("string")) {
                    continue;
                }
                String stringName = ((Element) stringNode).getAttribute("name");
                String stringContent = stringNode.getTextContent();
                existingStrings.add(stringName);
                if (sameAsEnglish(stringName, stringContent)) {
                    resources.removeChild(stringNode);
                }
            }
            for (String name: wantedStrings.keySet()) {
                if (existingStrings.contains(name) || sameAsEnglish(name, wantedStrings.get(name))) {
                    continue;
                }
                existingStrings.add(name);
                Element stringNode = doc.createElement("string");
                stringNode.setAttribute("name", name);
                stringNode.setTextContent(wantedStrings.get(name));
                resources.appendChild(stringNode);
            }
            writeXml(doc, to);
        } catch (ParserConfigurationException | IOException |SAXException e) {
            System.out.println("Could not read xml document from " + to + ": " + e.getMessage());
        }
    }

    private void create(File localFile) {
        if (!localFile.getParentFile().exists()) {
            localFile.getParentFile().mkdirs();
        }
        try {
            Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
            doc.appendChild(doc.createElement("resources"));
            writeXml(doc, localFile);
        } catch (ParserConfigurationException e) {
            System.out.println("Could not create xml document for " + localFile);
        }
    }

    private void writeXml(Document doc, File file) {
        try {
            TransformerFactory tranFactory = TransformerFactory.newInstance();
            Transformer aTransformer = tranFactory.newTransformer();
            aTransformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
            aTransformer.setOutputProperty(OutputKeys.METHOD, "xml");
            aTransformer.setOutputProperty(OutputKeys.INDENT, "yes");
            aTransformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            aTransformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "0");
            aTransformer.setOutputProperty("http://www.oracle.com/xml/is-standalone", "yes");
            doc.setXmlStandalone(true);
            Source src = new DOMSource(doc);
            Result dest = new StreamResult(file);
            aTransformer.transform(src, dest);
        } catch (TransformerException e) {
            System.out.println("Could not write xml to " + file);
        }
    }

    private File getLocalFile(String locale) {
        return new File("src\\main\\res\\values-" + locale + "\\strings.xml");
    }

    private boolean sameAsEnglish(String key, String value) {
        String englishValue = englishStrings.get(key);
        return isEmpty(value)
            || isEmpty(englishValue)
            || englishValue.equals(value)
            || englishValue.equals("\"" + value + "\"")
            || value.equals("\"" + englishValue + "\"")
            ;
    }

    private static boolean isEmpty(String str) {
        return str == null || str.length() == 0;
    }
}
