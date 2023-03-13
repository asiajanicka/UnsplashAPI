package org.example.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TestUtils {

    public static String getNextPageLinkFromHeader(String header) {
        String pattern = ", <(.*)>; rel=\"next\"";

        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher(header);
        m.find();

        return m.group(1);
    }

    public static boolean isNextPageLinkAdded(String header){
        String pattern = "<(.*)>; rel=\"next\"";

        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher(header);
        return  m.find();
    }

    public static String getPrevPageLinkFromHeader(String header) {
        String pattern = "<(.*)>; rel=\"prev\"";

        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher(header);
        m.find();

        return m.group(1);
    }

    public static boolean isPrevPageLinkAdded(String header){
        String pattern = "<(.*)>; rel=\"prev\"";

        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher(header);
        return  m.find();
    }

    public static String getLastPageLinkFromHeader(String header) {
        String pattern = "<(.*)>; rel=\"last\"";

        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher(header);
        m.find();

        return m.group(1);
    }

    public static boolean isLastPageLinkAdded(String header){
        String pattern = "<(.*)>; rel=\"last\"";

        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher(header);
        return  m.find();
    }

    public static String getFirstPageLinkFromHeader(String header) {
        String pattern = "<(.*)>; rel=\"first\"";

        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher(header);
        m.find();

        return m.group(1);
    }

    public static boolean isFirstPageLinkAdded(String header){
        String pattern = "<(.*)>; rel=\"first\"";

        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher(header);
        return  m.find();
    }
}
