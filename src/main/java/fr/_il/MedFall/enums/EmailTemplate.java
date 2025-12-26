package fr._il.MedFall.enums;

public enum EmailTemplate {
    RESET_PWD("mail/reset_pwd.html"),
    RESET_EMAIL("mail/reset_email.html");

    private final String templatePath;

    EmailTemplate(String templatePath) {
        this.templatePath = templatePath;
    }

    public String getTemplatePath() {
        return templatePath;
    }
}
