package com.company.data.api.util;

import org.springframework.stereotype.Component;

@Component
public class Normalizer {

    public String normalizeDomain(String website) {
        if (website == null || website.isEmpty()) {
            return null;
        }
        String w = website.trim()
                .replaceFirst("^https?://", "")
                .replaceFirst("^www\\.", "")
                .replaceAll("/.*$", "");
        w = w.replaceFirst("^https?://", "");
        return w.toLowerCase();
    }

    public String normalizeFacebook(String urlOrHandle) {
        if (urlOrHandle == null) return null;
        String s = urlOrHandle.trim();
        s = s.replaceFirst("^https?://(www\\.)?facebook\\.com/", "");
        s = s.replaceAll("/.*$", "");
        return s.toLowerCase();
    }

    public String normalizeName(String name) {
        if (name == null) return null;
        return name.toLowerCase().replaceAll("\\s+", " ").trim();
    }

    public String normalizePhone(String raw) {
        if (raw == null) return null;
        // cut usually extenstion
        String s = raw.replaceAll("(?i)\\s*(ext|x)\\s*\\d+$", "");
        // keep only digits
        s = s.replaceAll("\\D", "");
        if (s.isEmpty()) return null;

        if (s.length() == 10) {
            return "+1" + s;                  // ex: (786) 426-3492 -> +17864263492
        } else if (s.length() == 11 && s.startsWith("1")) {
            return "+" + s;                   // ex: 17864263492 -> +17864263492
        } else if (s.startsWith("+") && s.length() > 1) {
            return s;
        } else {
            // keep fallback: with +1 if it seems valid
            return s.length() >= 10 ? "+1" + s.substring(s.length() - 10) : null;
        }
    }
}
