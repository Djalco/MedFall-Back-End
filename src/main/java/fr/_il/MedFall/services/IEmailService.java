package fr._il.MedFall.services;

public interface IEmailService {
    void sendResetPwdMail(
            String to,
            String user_name,
            String reset_code
    );

    void sendResetEmailMail(
            String to,
            String user_name,
            String reset_code
    );
}
